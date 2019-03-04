package uk.gov.hmcts.reform.amlib;

import com.fasterxml.jackson.core.JsonPointer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.spi.json.JacksonJsonNodeJsonProvider;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class TestJsonPathPointer {

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void test() {

    }

    @Test
    void testJsonPointer() throws IOException {
        JsonNode inputJson = mapper.readTree(ClassLoader.getSystemResource("FilterServiceResources/input.json"));

        long startTime = System.currentTimeMillis();

        JsonNode json = inputJson.at(JsonPointer.valueOf("/claimant"));

        long endTime = System.currentTimeMillis();

        System.out.println("duration = " + (endTime - startTime) + "ms");

        System.out.println("outputJson = " + json);
    }

    @Test
    void testJsonPath() throws IOException {
        JsonNode inputJson = mapper.readTree(ClassLoader.getSystemResource("FilterServiceResources/input.json"));

        long startTime = System.currentTimeMillis();

        JsonNode json = JsonPath.using(Configuration.builder()
            .jsonProvider(new JacksonJsonNodeJsonProvider())
            .build())
            .parse(inputJson).read("$.claimant");

        long endTime = System.currentTimeMillis();

        System.out.println("duration = " + (endTime - startTime) + "ms");

        System.out.println("outputJson = " + json);
    }


}
