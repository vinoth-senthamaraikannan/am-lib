package uk.gov.hmcts.reform.amlib;

import com.fasterxml.jackson.core.JsonPointer;
import com.fasterxml.jackson.databind.JsonNode;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.spi.json.JacksonJsonNodeJsonProvider;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Mode;

import java.util.List;
import java.util.stream.Collectors;

public class SpikeClass {

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    @Fork(value = 1, warmups = 1)
    public void jsonPointer(SpikeSetup setup) {

        setup.inputJson.at(JsonPointer.valueOf("/claimant"));
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    @Fork(value = 1, warmups = 1)
    public void jsonPath(SpikeSetup setup) {

        JsonPath.using(Configuration.builder()
            .jsonProvider(new JacksonJsonNodeJsonProvider())
            .build())
            .parse(setup.inputJson).read("$.claimant");
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    @Fork(value = 1, warmups = 1)
    public void jsonPointerItemInCollection(SpikeSetup setup) {

        String node = "/others[@id=1]";

        //   /others[@id=1]
        String attribute = node.substring(0, node.indexOf("[")).replace("/", "");
        //   others
        String path = node.substring(0, node.indexOf("="))
            .substring(node.indexOf("["))
            .replace("@", "")
            .replace("[", "");
        //    id
        String id = node.substring(node.indexOf("=") + 1).replace("]", "");
        //   "873b"

        //finds correct item in collection based on id.
        List<JsonNode> itemInCollection = setup.inputJson.findValue(attribute).findParents(path).stream()
            .filter(x -> x.findPath(path).toString().equals("\"" + id + "\""))
            .collect(Collectors.toList());
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    @Fork(value = 1, warmups = 1)
    public void jsonPathItemInCollection(SpikeSetup setup) {

        JsonPath.using(Configuration.builder()
            .jsonProvider(new JacksonJsonNodeJsonProvider())
            .build())
            .parse(setup.inputJson).read("$.others[?(@.id=='1')]");
    }
}

}
