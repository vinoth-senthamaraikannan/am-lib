package integration.uk.gov.hmcts.reform.amlib;

import com.fasterxml.jackson.core.JsonPointer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.google.common.collect.ImmutableMap;
import integration.uk.gov.hmcts.reform.amlib.base.IntegrationBaseTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.gov.hmcts.reform.amlib.models.FilterResourceResponse;

import java.util.UUID;

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
    void whenRowExistsAndDoesNotHaveReadPermissionsReturnEnvelopeWithoutData() {
        ams.grantExplicitResourceAccess(createGrantForWholeDocument(resourceId, CREATE_PERMISSION));

        FilterResourceResponse result = ams.filterResource(ACCESSOR_ID, resourceId, DATA);

        assertThat(result).isEqualTo(FilterResourceResponse.builder()
            .resourceId(resourceId)
            .data(null)
            .permissions(ImmutableMap.of(JsonPointer.valueOf(""), CREATE_PERMISSION))
            .build());
    }

    @Test
    void whenRowExistsAndHaveReadPermissionsReturnEnvelopeWithData() {
        ams.grantExplicitResourceAccess(createGrant(
            resourceId, ACCESSOR_ID, createPermissions("", READ_PERMISSION)));

        JsonNode inputJson = mapper.valueToTree(
            JsonNodeFactory.instance.objectNode()
                .put("name", "John")
                .put("age", 18)
        );
        FilterResourceResponse result = ams.filterResource(ACCESSOR_ID, resourceId, inputJson);

        assertThat(result).isEqualTo(FilterResourceResponse.builder()
            .resourceId(resourceId)
            .data(inputJson)
            .permissions(ImmutableMap.of(JsonPointer.valueOf(""), READ_PERMISSION))
            .build());
    }

    @Test
    void whenNoRowExistsReturnNull() {
        String nonExistingUserId = "ijk";
        String nonExistingResourceId = "lmn";

        FilterResourceResponse result = ams.filterResource(nonExistingUserId, nonExistingResourceId, DATA);

        assertThat(result).isNull();
    }
}
