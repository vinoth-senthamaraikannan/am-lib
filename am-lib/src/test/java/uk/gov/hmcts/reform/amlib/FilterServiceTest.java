package uk.gov.hmcts.reform.amlib;

import com.fasterxml.jackson.core.JsonPointer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import uk.gov.hmcts.reform.amlib.enums.Permission;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.hmcts.reform.amlib.helpers.TestConstants.CREATE_PERMISSION;
import static uk.gov.hmcts.reform.amlib.helpers.TestConstants.READ_PERMISSION;
import static uk.gov.hmcts.reform.amlib.helpers.TestDataFactory.createPermissions;

public class FilterServiceTest {

    private final ObjectMapper mapper = new ObjectMapper();
    private final FilterService fs = new FilterService();

    @Test
    void filterWithRootReadPermissionReturnsUnfilteredResource() throws IOException {
        final JsonNode inputJson = mapper.readTree(ClassLoader.getSystemResource("FilterServiceResources/filterServiceTestInput.json"));

        JsonNode returnedJson = fs.filterJson(inputJson, createPermissions("", READ_PERMISSION));

        assertThat(returnedJson).isEqualTo(inputJson);
    }

    @Test
    void filterNestedJsonWhenRootExplicitlyHasNoReadButAnAttributeDoes() throws IOException {
        final JsonNode inputJson = mapper.readTree(ClassLoader.getSystemResource("FilterServiceResources/filterServiceTestInput.json"));
        final JsonNode expectedJson = mapper.readTree(ClassLoader.getSystemResource("FilterServiceResources/filterServiceTestExpected.json"));

        final Map<JsonPointer, Set<Permission>> attributePermissions = new ConcurrentHashMap<>();
        attributePermissions.put(JsonPointer.valueOf("/child"), CREATE_PERMISSION);
        attributePermissions.put(JsonPointer.valueOf("/child/childName"), READ_PERMISSION);

        JsonNode returnedJson = fs.filterJson(inputJson, attributePermissions);

        assertThat(returnedJson).isEqualTo(expectedJson);
    }

    @Test
    void filterNestedJsonWithNoPermissionsRemovesExpectedAttributes() throws IOException {
        final JsonNode inputJson = mapper.readTree(ClassLoader.getSystemResource("FilterServiceResources/filterServiceTestInput.json"));
        final JsonNode expectedJson = mapper.readTree(ClassLoader.getSystemResource("FilterServiceResources/filterServiceTestExpected.json"));

        JsonNode returnedJson = fs.filterJson(inputJson, createPermissions("/child/childName", READ_PERMISSION));

        assertThat(returnedJson).isEqualTo(expectedJson);
    }

    @Test
    void filterNestedJsonWithoutExplicitReadRemovesExpectedAttributes() throws IOException {
        final JsonNode inputJson = mapper.readTree(ClassLoader.getSystemResource("FilterServiceResources/filterServiceTestInput.json"));
        final JsonNode expectedJson = mapper.readTree(ClassLoader.getSystemResource("FilterServiceResources/filterServiceTestExpected.json"));

        final Map<JsonPointer, Set<Permission>> attributePermissions = new ConcurrentHashMap<>();
        attributePermissions.put(JsonPointer.valueOf("/child/childName"), READ_PERMISSION);
        attributePermissions.put(JsonPointer.valueOf("/child/childAge"), CREATE_PERMISSION);

        JsonNode returnedJson = fs.filterJson(inputJson, attributePermissions);

        assertThat(returnedJson).isEqualTo(expectedJson);
    }

    @Test
    void filterComplexNestedJson() throws IOException {
        final JsonNode inputJson = mapper.readTree(ClassLoader.getSystemResource("FilterServiceResources/filterServiceTestComplexInput.json"));
        final JsonNode expectedJson = mapper.readTree(ClassLoader.getSystemResource("FilterServiceResources/filterServiceTestComplexExpected.json"));

        final Map<JsonPointer, Set<Permission>> attributePermissions = new ConcurrentHashMap<>();
        attributePermissions.put(JsonPointer.valueOf("/child"), READ_PERMISSION);
        attributePermissions.put(JsonPointer.valueOf("/child/childAge"), CREATE_PERMISSION);
        attributePermissions.put(JsonPointer.valueOf("/child/childName/firstName"), READ_PERMISSION);
        attributePermissions.put(JsonPointer.valueOf("/child/childName/lastName"), CREATE_PERMISSION);

        JsonNode returnedJson = fs.filterJson(inputJson, attributePermissions);

        assertThat(returnedJson).isEqualTo(expectedJson);
    }

    @Test
    void filterRealisticData() throws IOException {
        final JsonNode inputJson = mapper.readTree(ClassLoader.getSystemResource("FilterServiceResources/ccdCaseDataInput.json"));
        final JsonNode expectedJson = mapper.readTree(ClassLoader.getSystemResource("FilterServiceResources/ccdCaseDataExpected.json"));

        final Map<JsonPointer, Set<Permission>> attributePermissions = new ConcurrentHashMap<>();
        attributePermissions.put(JsonPointer.valueOf(""), READ_PERMISSION);
        attributePermissions.put(JsonPointer.valueOf("/jurisdiction"), CREATE_PERMISSION);
        attributePermissions.put(JsonPointer.valueOf("/case_data/risks"), CREATE_PERMISSION);
        attributePermissions.put(JsonPointer.valueOf("/case_data/others/additionalOthers"), CREATE_PERMISSION);

        JsonNode returnedJson = fs.filterJson(inputJson, attributePermissions);

        assertThat(returnedJson).isEqualTo(expectedJson);
    }

    @Test
    void noReadPermissionsForAttributeReturnsEmptyArray() throws IOException {
        final JsonNode inputJson = mapper.readTree(ClassLoader.getSystemResource("FilterServiceResources/filterServiceTestInput.json"));

        JsonNode returnedJson = fs.filterJson(inputJson, createPermissions("/child", CREATE_PERMISSION));

        assertThat(returnedJson).isEmpty();
    }
}
