package uk.gov.hmcts.reform.amlib.helpers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.google.common.collect.ImmutableSet;
import uk.gov.hmcts.reform.amlib.enums.Permission;

import java.util.Set;

import static uk.gov.hmcts.reform.amlib.enums.Permission.CREATE;
import static uk.gov.hmcts.reform.amlib.enums.Permission.READ;
import static uk.gov.hmcts.reform.amlib.enums.Permission.UPDATE;

public final class TestConstants {

    public static final String ACCESS_TYPE = "user";
    public static final String SERVICE_NAME = "Service 1";
    public static final String RESOURCE_TYPE = "Resource Type 1";
    public static final String RESOURCE_NAME = "resource";
    public static final String SECURITY_CLASSIFICATION = "Public";
    public static final String ACCESSOR_ID = "a";
    public static final String OTHER_ACCESSOR_ID = "b";
    public static final Set<Permission> EXPLICIT_READ_CREATE_UPDATE_PERMISSIONS = ImmutableSet.of(CREATE, READ, UPDATE);
    public static final Set<Permission> CREATE_PERMISSION = ImmutableSet.of(CREATE);
    public static final Set<Permission> READ_PERMISSION = ImmutableSet.of(READ);
    public static final JsonNode DATA = JsonNodeFactory.instance.objectNode();

    private TestConstants() {
        //NO-OP
    }
}
