package uk.gov.hmcts.reform.amlib.models;

import uk.gov.hmcts.reform.amlib.enums.Permissions;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ExplicitPermissions {
    private final Set<Permissions> userPermissions;

    public ExplicitPermissions(Set<Permissions> userPermissions) {
        this.userPermissions = userPermissions;
    }

    public ExplicitPermissions(Permissions...userPermissions) {
        this.userPermissions = Stream.of(userPermissions).collect(Collectors.toSet());
    }

    public Set<Permissions> getUserPermissions() {
        return userPermissions;
    }
}
