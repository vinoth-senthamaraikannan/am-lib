package uk.gov.hmcts.reform.amlib.enums;

import java.util.List;

public enum Permissions {
    HIDE(0),
    CREATE(1),
    READ(2),
    UPDATE(4),
    SHARE(8),
    DELETE(16)
    ;

    private int value;

    Permissions(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static int sumOf(List<Permissions> permissions) {
        int sum = 0;

        for(Permissions permission: permissions) {
            sum += permission.getValue();
        }

        return sum;
    }
}
