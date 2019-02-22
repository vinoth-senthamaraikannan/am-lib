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

    @BeforeEach
    void setupTest() {
        resourceId = UUID.randomUUID().toString();
    }

    @Test
    void whenRowExistsAndDoesntHaveReadPermissionsReturnNull() {
        ams.grantExplicitResourceAccess(createGrantForWholeDocument(resourceId, CREATE_PERMISSION));

        FilterResourceResponse result = ams.filterResource(ACCESSOR_ID, resourceId, DATA);

        assertThat(result).isNull();
    }

    @Test
    void filterWithOnlyRootPermissions() throws IOException {
        ams.grantExplicitResourceAccess(createGrant(
            resourceId, ACCESSOR_ID, createPermissions("", READ_PERMISSION)));

        final JsonNode inputJson = mapper.readTree("{\"child\": {\"childAge\": \"10\", \"childName\": \"James\"}}");
        final FilterResourceResponse result = ams.filterResource(ACCESSOR_ID, resourceId, inputJson);

        Map<JsonPointer, Set<Permission>> attributePermissions = new ConcurrentHashMap<>();
        attributePermissions.put(JsonPointer.valueOf(""), READ_PERMISSION);

        assertThat(result).isEqualTo(FilterResourceResponse.builder()
            .resourceId(resourceId)
            .data(inputJson)
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
}
