package uk.gov.hmcts.reform.amlib.enums;

import java.util.Set;


/**
 * Exposes a set of enum values used to set permissions for Access Management.
 * Each of the values is a power of two. The reason for that is that in Access Management
 * there might be multiple permissions that need to be assigned: ie. READ + CREATE + HIDE.
 * For convenience when saving a record into AM all the values are summed up (the 'sumOf' method) and saved as integer.
 * In order to determine which individual permissions a record has
 * the binary 'AND' operation is done (the 'hasPermissionTo' method).
 */
public enum Permissions {
    HIDE(0),
    CREATE(1),
    READ(2),
    UPDATE(4),
    SHARE(8),
    DELETE(16);

    private int value;

    Permissions(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static int sumOf(Set<Permissions> perms) {
        int sum = 0;

        for (Permissions permission: perms) {
            sum += permission.getValue();
        }

        return sum;
    }


    /**
     * Performs a binary AND operation to determine weather the 'permissions' value has suitable permissionToCheck.
     * @param permissions the decimal value of permissions defined in Permissions enum
     * @param permissionToCheck the permission to verify
     * @return Returns true if the binary AND of the provided 'permissions' and 'permissionToCheck' is true.
     */
    public static boolean hasPermissionTo(int permissions, Permissions permissionToCheck) {
        return (permissions & permissionToCheck.getValue()) == permissionToCheck.getValue();
    }
}
