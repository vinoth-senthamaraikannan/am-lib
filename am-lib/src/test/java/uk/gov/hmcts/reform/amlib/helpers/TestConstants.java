package uk.gov.hmcts.reform.amlib.helpers;

import com.fasterxml.jackson.core.JsonPointer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.google.common.collect.ImmutableSet;

import java.util.Set;

public final class TestConstants {
    public static final String SERVICE_NAME = "CMC";
    public static final String RESOURCE_TYPE = "Case";
    public static final String RESOURCE_NAME = "Claim";
    public static final JsonPointer ROOT_ATTRIBUTE = JsonPointer.valueOf("");
    public static final String ROLE_NAME = "Solicitor";
    public static final String OTHER_ROLE_NAME = "Local Authority";
    public static final Set<String> ROLE_NAMES = ImmutableSet.of(ROLE_NAME);
    public static final JsonNode DATA = JsonNodeFactory.instance.objectNode()
        .put("name", "John")
        .put("age", 18);

    private TestConstants() {
        //NO-OP
    }
}
