package uk.gov.hmcts.reform.amlib;

import com.fasterxml.jackson.databind.JsonNode;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;
import uk.gov.hmcts.reform.amlib.enums.Permissions;
import uk.gov.hmcts.reform.amlib.models.AccessManagement;
import uk.gov.hmcts.reform.amlib.models.ExplicitPermissions;
import uk.gov.hmcts.reform.amlib.repositories.AccessManagementRepository;

import java.util.List;

public class AccessManagementService {
    private final Jdbi jdbi;

    public AccessManagementService(String url, String user, String password) {
        this.jdbi = Jdbi.create(url, user, password);

        this.jdbi.installPlugin(new SqlObjectPlugin());
    }

    /**
     * Returns void if method succeeds
     * @param resourceId
     * @param accessorId
     * @param explicitPermissions defines information about permissions given to the accessor
     */
    public void createResourceAccess(String resourceId, String accessorId, ExplicitPermissions explicitPermissions) {
        List<Permissions> userPermissions = explicitPermissions.getUserPermissions();

        jdbi.useExtension(AccessManagementRepository.class,
            dao -> dao.createAccessManagementRecord(resourceId, accessorId, Permissions.sumOf(userPermissions)));
    }

    /**
     * Returns `resourceJSON` when record with userId and resourceId exist and has READ permissions, otherwise null
     * @param userId (accessorId)
     * @param resourceId
     * @param resourceJSON
     * @return resourceJSON or null
     */
    public JsonNode filterResource(String userId, String resourceId, JsonNode resourceJSON) {
        AccessManagement accessRegardlessPermissions = jdbi.withExtension(AccessManagementRepository.class,
                dao -> dao.getExplicitAccess(userId, resourceId));

        if (accessRegardlessPermissions == null) {
            return null;
        }

        boolean hasReadPermissions = (accessRegardlessPermissions.getPermissions() & Permissions.READ.getValue())
                == Permissions.READ.getValue();

        return (hasReadPermissions) ? resourceJSON : null;
    }
}
