package uk.gov.hmcts.reform.amlib;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;

import java.io.IOException;

@State(Scope.Benchmark)
public class SpikeSetup {

    private final ObjectMapper mapper = new ObjectMapper();

    public JsonNode inputJson;

    @Setup(Level.Invocation)
    public void setup() throws IOException {
        inputJson = mapper.readTree(ClassLoader.getSystemResource("input.json"));

    }
}
