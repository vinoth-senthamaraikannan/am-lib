package uk.gov.hmcts.reform.amlib.models;

import java.beans.ConstructorProperties;

public class AccessManagement {

    private final int accessManagementId;
    private final String resourceId;
    private final String accessorId;
    private final int permissions;

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

    public int getAccessManagementId() {
        return accessManagementId;
    }

    public String getResourceId() {
        return resourceId;
    }

    public String getAccessorId() {
        return accessorId;
    }
}
