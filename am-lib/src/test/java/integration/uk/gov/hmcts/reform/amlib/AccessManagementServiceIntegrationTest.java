package integration.uk.gov.hmcts.reform.amlib;

import org.junit.Test;
import uk.gov.hmcts.reform.amlib.enums.Permissions;
import uk.gov.hmcts.reform.amlib.models.ExplicitPermissions;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Java6Assertions.assertThat;

public class AccessManagementServiceIntegrationTest extends IntegrationBaseTest {

    private String resourceId;

    @Test
    public void whenCreatingResourceAccess_ResourceAccessAppearsInDatabase() {
        resourceId = UUID.randomUUID().toString();
        List<Permissions> permissions = Arrays.asList(Permissions.CREATE, Permissions.READ, Permissions.UPDATE);

        ams.createResourceAccess(resourceId, "dsa", new ExplicitPermissions(permissions));

        int count = jdbi.open().createQuery(
                "select count(1) from \"AccessManagement\" where \"resourceId\" = ?")
                .bind(0, resourceId)
                .mapTo(int.class)
                .findOnly();

        assertThat(count).isEqualTo(1);
    }
}
