package uk.gov.hmcts.reform.amlib.models;

import java.beans.ConstructorProperties;

public class AccessManagement {

    private int accessManagementId;
    private String resourceId;
    private String accessorId;
    private int permissions;

    @ConstructorProperties({"accessManagementId", "resourceId", "accessorId", "permissions"})
    public AccessManagement(int accessManagementId, String resourceId, String accessorId, int permissions) {
        this.accessManagementId = accessManagementId;
        this.resourceId = resourceId;
        this.accessorId = accessorId;
        this.permissions = permissions;
    }

    public int getPermissions() {
        return permissions;
    }
}
