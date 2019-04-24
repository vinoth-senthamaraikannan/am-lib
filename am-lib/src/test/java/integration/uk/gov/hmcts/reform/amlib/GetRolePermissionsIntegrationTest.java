package integration.uk.gov.hmcts.reform.amlib;

import com.fasterxml.jackson.core.JsonPointer;
import com.google.common.collect.ImmutableMap;
import integration.uk.gov.hmcts.reform.amlib.base.PreconfiguredIntegrationBaseTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.gov.hmcts.reform.amlib.AccessManagementService;
import uk.gov.hmcts.reform.amlib.DefaultRoleSetupImportService;
import uk.gov.hmcts.reform.amlib.enums.Permission;
import uk.gov.hmcts.reform.amlib.enums.SecurityClassification;
import uk.gov.hmcts.reform.amlib.models.DefaultPermissionGrant;
import uk.gov.hmcts.reform.amlib.models.Pair;
import uk.gov.hmcts.reform.amlib.models.ResourceDefinition;
import uk.gov.hmcts.reform.amlib.models.RolePermissions;

import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.hmcts.reform.amlib.enums.AccessType.ROLE_BASED;
import static uk.gov.hmcts.reform.amlib.enums.SecurityClassification.PUBLIC;
import static uk.gov.hmcts.reform.amlib.helpers.TestConstants.OTHER_ROLE_NAME;
import static uk.gov.hmcts.reform.amlib.helpers.TestConstants.READ_PERMISSION;
import static uk.gov.hmcts.reform.amlib.helpers.TestConstants.RESOURCE_NAME;
import static uk.gov.hmcts.reform.amlib.helpers.TestConstants.RESOURCE_TYPE;
import static uk.gov.hmcts.reform.amlib.helpers.TestConstants.ROLE_NAME;
import static uk.gov.hmcts.reform.amlib.helpers.TestConstants.SERVICE_NAME;

class GetRolePermissionsIntegrationTest extends PreconfiguredIntegrationBaseTest {
    private static AccessManagementService service = initService(AccessManagementService.class);
    private static DefaultRoleSetupImportService importerService = initService(DefaultRoleSetupImportService.class);

    @BeforeEach
    void setUp() {
        Map.Entry<Set<Permission>, SecurityClassification> readPermission = new Pair<>(READ_PERMISSION, PUBLIC);

        Map<JsonPointer, Map.Entry<Set<Permission>, SecurityClassification>> attributePermissionsForRole =
            ImmutableMap.of(
                JsonPointer.valueOf("/child"), readPermission,
                JsonPointer.valueOf("/parent/age"), readPermission
            );

        importerService.grantDefaultPermission(
            DefaultPermissionGrant.builder()
                .roleName(ROLE_NAME)
                .resourceDefinition(ResourceDefinition.builder()
                    .serviceName(SERVICE_NAME)
                    .resourceType(RESOURCE_TYPE)
                    .resourceName(RESOURCE_NAME)
                    .build())
                .attributePermissions(attributePermissionsForRole)
                .build());

        Map<JsonPointer, Map.Entry<Set<Permission>, SecurityClassification>> attributePermissionsForOtherRole =
            ImmutableMap.of(
                JsonPointer.valueOf(""), readPermission,
                JsonPointer.valueOf("/address"), readPermission
            );

        importerService.grantDefaultPermission(DefaultPermissionGrant.builder()
            .roleName(OTHER_ROLE_NAME)
            .resourceDefinition(ResourceDefinition.builder()
                .serviceName(SERVICE_NAME)
                .resourceType(RESOURCE_TYPE)
                .resourceName(RESOURCE_NAME)
                .build())
            .attributePermissions(attributePermissionsForOtherRole)
            .build());
    }

    @Test
    void whenGettingRolePermissionsShouldReturnPermissionsAndSecurityClassificationsForSpecifiedRole() {
        RolePermissions rolePermissions =
            service.getRolePermissions(buildResource(SERVICE_NAME, RESOURCE_TYPE, RESOURCE_NAME), ROLE_NAME);

        assertThat(rolePermissions.getPermissions())
            .hasSize(2)
            .containsEntry(JsonPointer.valueOf("/child"), READ_PERMISSION)
            .containsEntry(JsonPointer.valueOf("/parent/age"), READ_PERMISSION);

        assertThat(rolePermissions.getSecurityClassification())
            .hasSize(2)
            .containsEntry(JsonPointer.valueOf("/child"), PUBLIC)
            .containsEntry(JsonPointer.valueOf("/parent/age"), PUBLIC);

        assertThat(rolePermissions.getAccessType()).isEqualTo(ROLE_BASED);
    }

    @Test
    void shouldReturnNullWhenServiceNameDoesNotExist() {
        RolePermissions rolePermissions = service.getRolePermissions(
            buildResource("Unknown Service", RESOURCE_TYPE, RESOURCE_NAME), ROLE_NAME);

        assertThat(rolePermissions).isNull();
    }

    @Test
    void shouldReturnNullWhenResourceTypeDoesNotExist() {
        RolePermissions rolePermissions = service.getRolePermissions(
            buildResource(SERVICE_NAME, "Unknown Resource Type", RESOURCE_NAME), ROLE_NAME);

        assertThat(rolePermissions).isNull();
    }

    @Test
    void shouldReturnNullWhenResourceNameDoesNotExist() {
        RolePermissions rolePermissions = service.getRolePermissions(
            buildResource(SERVICE_NAME, RESOURCE_TYPE, "Unknown Resource Name"), ROLE_NAME);

        assertThat(rolePermissions).isNull();
    }

    @Test
    void shouldReturnNullWhenDefaultRoleNameDoesNotExist() {
        RolePermissions rolePermissions = service.getRolePermissions(
            buildResource(SERVICE_NAME, RESOURCE_TYPE, RESOURCE_NAME), "Unknown Role");

        assertThat(rolePermissions).isNull();
    }

    private ResourceDefinition buildResource(String serviceName, String resourceType, String resourceName) {
        return ResourceDefinition.builder()
            .resourceName(resourceName)
            .resourceType(resourceType)
            .serviceName(serviceName)
            .build();
    }
}
