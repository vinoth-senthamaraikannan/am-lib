package uk.gov.hmcts.reform.amlib;

import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;
import uk.gov.hmcts.reform.amlib.enums.Permissions;
import uk.gov.hmcts.reform.amlib.models.ExplicitPermissions;
import uk.gov.hmcts.reform.amlib.repositories.AccessManagementRepository;

import java.util.List;

public class AccessManagementService {
    private final Jdbi jdbi;

    public AccessManagementService(String url, String user, String password) {
        this.jdbi = Jdbi.create(url, user, password);

        this.jdbi.installPlugin(new SqlObjectPlugin());
    }

    public void createResourceAccess(String resourceId, String accessorId, ExplicitPermissions explicitPermissions) {
        List<Permissions> userPermissions = explicitPermissions.getUserPermissions();

        jdbi.useExtension(AccessManagementRepository.class,
            dao -> dao.createAccessManagementRecord(resourceId, accessorId, Permissions.sumOf(userPermissions)));
    }
}
