package integration.uk.gov.hmcts.reform.amlib;

import com.fasterxml.jackson.core.JsonPointer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import integration.uk.gov.hmcts.reform.amlib.base.IntegrationBaseTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.gov.hmcts.reform.amlib.enums.Permission;
import uk.gov.hmcts.reform.amlib.models.FilterResourceResponse;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.hmcts.reform.amlib.helpers.TestConstants.ACCESSOR_ID;
import static uk.gov.hmcts.reform.amlib.helpers.TestConstants.CREATE_PERMISSION;
import static uk.gov.hmcts.reform.amlib.helpers.TestConstants.DATA;
import static uk.gov.hmcts.reform.amlib.helpers.TestConstants.READ_PERMISSION;
import static uk.gov.hmcts.reform.amlib.helpers.TestDataFactory.createGrant;
import static uk.gov.hmcts.reform.amlib.helpers.TestDataFactory.createGrantForWholeDocument;
import static uk.gov.hmcts.reform.amlib.helpers.TestDataFactory.createPermissions;

class FilterResourceIntegrationTest extends IntegrationBaseTest {

    private String resourceId;
    private final ObjectMapper mapper = new ObjectMapper();

    private static final String TEST_CHILD_ATTRIBUTE = "/test";
    private static final String TEST_RESPONDENT_ATTRIBUTE = "/respondent";

    @BeforeEach
    void setupTest() {
        resourceId = UUID.randomUUID().toString();
    }

    @Test
    void whenRowExistWithAccessorIdAndResourceIdReturnPassedJsonObject() {
        ams.grantExplicitResourceAccess(createGrantForWholeDocument(resourceId, READ_PERMISSION));

        FilterResourceResponse result = ams.filterResource(ACCESSOR_ID, resourceId, DATA);

        assertThat(result).isEqualTo(FilterResourceResponse.builder()
            .resourceId(resourceId)
            .data(DATA)
            .permissions(createPermissions("", READ_PERMISSION))
            .build());
    }

    @Test
    void filterChildJson() throws IOException {
        ams.grantExplicitResourceAccess(createGrant(
            resourceId, ACCESSOR_ID, createPermissions(TEST_CHILD_ATTRIBUTE, READ_PERMISSION)));

        ams.grantExplicitResourceAccess(createGrant(
            resourceId, ACCESSOR_ID, createPermissions("/child/childName", READ_PERMISSION)));

        ams.grantExplicitResourceAccess(createGrant(
            resourceId, ACCESSOR_ID, createPermissions("/child/childAge", CREATE_PERMISSION)));

        final JsonNode inputJson = mapper.readTree("{\"child\": {\"childAge\": \"10\", \"childName\": \"James\"}}");
        final JsonNode expectedJson = mapper.readTree("{\"child\": {\"childName\": \"James\"}}");
        final FilterResourceResponse result = ams.filterResource(ACCESSOR_ID, resourceId, inputJson);

        Map<JsonPointer, Set<Permission>> attributePermissions = new ConcurrentHashMap<>();
        attributePermissions.put(JsonPointer.valueOf(TEST_CHILD_ATTRIBUTE), READ_PERMISSION);
        attributePermissions.put(JsonPointer.valueOf("/child/childName"), READ_PERMISSION);
        attributePermissions.put(JsonPointer.valueOf("/child/childAge"), CREATE_PERMISSION);

        assertThat(result).isEqualTo(FilterResourceResponse.builder()
            .resourceId(resourceId)
            .data(expectedJson)
            .permissions(attributePermissions)
            .build());
    }

    @Test
    void filterSimpleJson() throws IOException {
        ams.grantExplicitResourceAccess(createGrant(
            resourceId, ACCESSOR_ID, createPermissions(TEST_CHILD_ATTRIBUTE, READ_PERMISSION)));

        ams.grantExplicitResourceAccess(createGrant(
            resourceId, ACCESSOR_ID, createPermissions(TEST_RESPONDENT_ATTRIBUTE, CREATE_PERMISSION)));

        JsonNode inputJson = mapper.readTree("{\"child\": \"James\", \"respondent\": \"John\"}");

        JsonNode expectedJson = mapper.readTree("{\"child\": \"James\"}");

        FilterResourceResponse result = ams.filterResource(ACCESSOR_ID, resourceId, inputJson);

        Map<JsonPointer, Set<Permission>> attributePermissions = new ConcurrentHashMap<>();
        attributePermissions.put(JsonPointer.valueOf(TEST_CHILD_ATTRIBUTE), READ_PERMISSION);
        attributePermissions.put(JsonPointer.valueOf(TEST_RESPONDENT_ATTRIBUTE), CREATE_PERMISSION);

        assertThat(result).isEqualTo(FilterResourceResponse.builder()
            .resourceId(resourceId)
            .data(expectedJson)
            .permissions(attributePermissions)
            .build());
    }

    //TODO: This test does not yet pass. Need to implement retain function.
    @Test
    void filterJsonWithoutPermission() throws IOException {
        ams.grantExplicitResourceAccess(createGrant(
            resourceId, ACCESSOR_ID, createPermissions(TEST_CHILD_ATTRIBUTE, READ_PERMISSION)));

        ams.grantExplicitResourceAccess(createGrant(
            resourceId, ACCESSOR_ID, createPermissions(TEST_RESPONDENT_ATTRIBUTE, CREATE_PERMISSION)));

        JsonNode inputJson = mapper.readTree("{\"child\": \"James\", \"respondent\": \"John\", \"other\": \"stuff\"}");

        JsonNode expectedJson = mapper.readTree("{\"child\": \"James\"}");

        FilterResourceResponse result = ams.filterResource(ACCESSOR_ID, resourceId, inputJson);

        Map<JsonPointer, Set<Permission>> attributePermissions = new ConcurrentHashMap<>();
        attributePermissions.put(JsonPointer.valueOf(TEST_CHILD_ATTRIBUTE), READ_PERMISSION);
        attributePermissions.put(JsonPointer.valueOf(TEST_RESPONDENT_ATTRIBUTE), CREATE_PERMISSION);

        assertThat(result).isEqualTo(FilterResourceResponse.builder()
            .resourceId(resourceId)
            .data(expectedJson)
            .permissions(attributePermissions)
            .build());
    }

    @Test
    void whenRowNotExistWithAccessorIdAndResourceIdReturnNull() {
        String nonExistingUserId = "ijk";
        String nonExistingResourceId = "lmn";

        FilterResourceResponse result = ams.filterResource(nonExistingUserId, nonExistingResourceId, DATA);

        assertThat(result).isNull();
    }

    //TODO: this is failing because data is empty. When it hits the substring variables it errors. I don't think this could or should ever happen. We can't filter nothing.
    @Test
    void whenRowExistsAndDoesntHaveReadPermissionsReturnNull() {
        ams.grantExplicitResourceAccess(createGrantForWholeDocument(resourceId, CREATE_PERMISSION));

        FilterResourceResponse result = ams.filterResource(ACCESSOR_ID, resourceId, DATA);

        assertThat(result).isNull();
    }
}
