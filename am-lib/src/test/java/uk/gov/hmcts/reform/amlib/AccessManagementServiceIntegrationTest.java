package integration.uk.gov.hmcts.reform.amlib;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import org.junit.Before;
import org.junit.Test;
import uk.gov.hmcts.reform.amlib.enums.Permissions;
import uk.gov.hmcts.reform.amlib.models.ExplicitPermissions;

import java.util.UUID;

import static org.assertj.core.api.Java6Assertions.assertThat;

public class AccessManagementServiceIntegrationTest extends IntegrationBaseTest {

    private String resourceId;
    private final JsonNode jsonObject = JsonNodeFactory.instance.objectNode();
    private ExplicitPermissions explicitReadCreateUpdatePermissions;

    @Before
    public void setupTest() {
        resourceId = UUID.randomUUID().toString();
        explicitReadCreateUpdatePermissions = new ExplicitPermissions(Permissions.CREATE,
                Permissions.READ, Permissions.UPDATE);
    }

    @Test
    public void createQuery_whenCreatingResourceAccess_ResourceAccessAppearsInDatabase() {
        ams.createResourceAccess(resourceId, "dsa", explicitReadCreateUpdatePermissions);

        int count = jdbi.open().createQuery(
                "select count(1) from \"AccessManagement\" where \"resourceId\" = ?")
                .bind(0, resourceId)
                .mapTo(int.class)
                .findOnly();

        assertThat(count).isEqualTo(1);
    }

    @Test
    public void filterResource_whenRowExistWithAccessorIdAndResourceId_ReturnPassedJsonObject() {
        String userId = UUID.randomUUID().toString();
        ams.createResourceAccess(resourceId, userId, explicitReadCreateUpdatePermissions);

        JsonNode result = ams.filterResource(userId, resourceId, jsonObject);

        assertThat(result).isEqualTo(jsonObject);
    }

    @Test
    public void filterResource_whenRowNotExistWithAccessorIdAndResourceId_ReturnNull() {
        String userId = "def";
        ams.createResourceAccess(resourceId, userId, explicitReadCreateUpdatePermissions);
        String nonExistingUserId = "ijk";
        String nonExistingResourceId = "lmn";

        JsonNode result = ams.filterResource(nonExistingUserId, nonExistingResourceId, jsonObject);

        assertThat(result).isNull();
    }

    @Test
    public void filterResource_whenRowExistsAndDoesntHaveReadPermissions_ReturnNull() {
        String userId = "def";
        ams.createResourceAccess(resourceId, userId,
                new ExplicitPermissions(Permissions.CREATE, Permissions.UPDATE)
        );

        JsonNode result = ams.filterResource(userId, resourceId, jsonObject);

        assertThat(result).isNull();
    }
}
