package uk.gov.hmcts.reform.amlib.models;

import uk.gov.hmcts.reform.amlib.enums.Permissions;

import java.util.Arrays;
import java.util.List;

public class ExplicitPermissions {
    private List<Permissions> userPermissions;

    public ExplicitPermissions(List<Permissions> userPermissions) {
        this.userPermissions = userPermissions;
    }

    public ExplicitPermissions(Permissions ...userPermissions) {
        this.userPermissions = Arrays.asList(userPermissions);
    }

    public List<Permissions> getUserPermissions() {
        return userPermissions;
    }
}
