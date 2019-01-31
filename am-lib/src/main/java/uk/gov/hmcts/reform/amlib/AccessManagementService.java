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
     * Returns void if method succeeds.
     * @param resourceId resource id
     * @param accessorId accessor id
     * @param explicitPermissions defines information about permissions given to the accessor
     */
    public void createResourceAccess(String resourceId, String accessorId, ExplicitPermissions explicitPermissions) {
        jdbi.useExtension(AccessManagementRepository.class,
            dao ->  {
                List<Permissions> userPermissions = explicitPermissions.getUserPermissions();

                dao.createAccessManagementRecord(resourceId, accessorId, Permissions.sumOf(userPermissions));
            });
    }

    /**
     * Returns `resourceJson` when record with userId and resourceId exist and has READ permissions, otherwise null.
     * @param userId (accessorId)
     * @param resourceId resource id
     * @param resourceJson json
     * @return resourceJson or null
     */
    public JsonNode filterResource(String userId, String resourceId, JsonNode resourceJson) {
        AccessManagement explicitAccess = jdbi.withExtension(AccessManagementRepository.class,
            dao -> dao.getExplicitAccess(userId, resourceId));

        if (explicitAccess == null) {
            return null;
        }

        boolean hasReadPermissions = (explicitAccess.getPermissions() & Permissions.READ.getValue())
                == Permissions.READ.getValue();

        return hasReadPermissions ? resourceJson : null;
    }
}
