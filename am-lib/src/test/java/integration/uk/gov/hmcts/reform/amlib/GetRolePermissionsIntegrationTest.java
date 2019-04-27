package integration.uk.gov.hmcts.reform.amlib;

import com.fasterxml.jackson.core.JsonPointer;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
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

import java.util.AbstractMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.hmcts.reform.amlib.enums.AccessType.ROLE_BASED;
import static uk.gov.hmcts.reform.amlib.enums.Permission.CREATE;
import static uk.gov.hmcts.reform.amlib.enums.Permission.READ;
import static uk.gov.hmcts.reform.amlib.enums.RoleType.IDAM;
import static uk.gov.hmcts.reform.amlib.enums.SecurityClassification.*;
import static uk.gov.hmcts.reform.amlib.helpers.TestConstants.*;

class GetRolePermissionsIntegrationTest extends PreconfiguredIntegrationBaseTest {
    private static AccessManagementService service = initService(AccessManagementService.class);
    private static DefaultRoleSetupImportService importerService = initService(DefaultRoleSetupImportService.class);

    private Map<JsonPointer, Map.Entry<Set<Permission>, SecurityClassification>> attributePermissionsForRole;

    private String roleName;

    @BeforeEach
    void setUp() {
        roleName = UUID.randomUUID().toString();
    }

    @Test
    void whenGettingRolePermissionsShouldReturnPermissionsAndSecurityClassificationsForSpecifiedRole() {
        Map.Entry<Set<Permission>, SecurityClassification> readPermission = new Pair<>(ImmutableSet.of(READ), PUBLIC);

        attributePermissionsForRole = ImmutableMap.of(
            JsonPointer.valueOf("/child"), readPermission,
            JsonPointer.valueOf("/parent/age"), readPermission
        );

        importerService.addRole(roleName, IDAM, PUBLIC, ROLE_BASED);

        importerService.grantDefaultPermission(DefaultPermissionGrant.builder()
            .roleName(roleName)
            .resourceDefinition(ResourceDefinition.builder()
                .serviceName(SERVICE_NAME)
                .resourceType(RESOURCE_TYPE)
                .resourceName(RESOURCE_NAME)
                .build())
            .attributePermissions(attributePermissionsForRole)
            .build());

        RolePermissions rolePermissions =
            service.getRolePermissions(buildResource(SERVICE_NAME, RESOURCE_TYPE, RESOURCE_NAME), roleName);

        assertThat(rolePermissions.getPermissions())
            .hasSize(2)
            .containsEntry(JsonPointer.valueOf("/child"), ImmutableSet.of(READ))
            .containsEntry(JsonPointer.valueOf("/parent/age"), ImmutableSet.of(READ));

        assertThat(rolePermissions.getSecurityClassifications())
            .hasSize(2)
            .containsEntry(JsonPointer.valueOf("/child"), PUBLIC)
            .containsEntry(JsonPointer.valueOf("/parent/age"), PUBLIC);

        assertThat(rolePermissions.getRoleAccessType()).isEqualTo(ROLE_BASED);

        assertThat(rolePermissions.getRoleSecurityClassification()).isEqualTo(PUBLIC);
    }

    @Test
    void whenInsufficientRolePermissionsShouldReturnNull() {
        Map.Entry<Set<Permission>, SecurityClassification> readPermission =
            new Pair<>(ImmutableSet.of(READ), RESTRICTED);

        attributePermissionsForRole = ImmutableMap.of(
            JsonPointer.valueOf("/payment"), readPermission
        );

        importerService.addRole(roleName, IDAM, PUBLIC, ROLE_BASED);

        importerService.grantDefaultPermission(DefaultPermissionGrant.builder()
            .roleName(roleName)
            .resourceDefinition(ResourceDefinition.builder()
                .serviceName(SERVICE_NAME)
                .resourceType(RESOURCE_TYPE)
                .resourceName(RESOURCE_NAME)
                .build())
            .attributePermissions(attributePermissionsForRole)
            .build());

        RolePermissions rolePermissions =
            service.getRolePermissions(buildResource(SERVICE_NAME, RESOURCE_TYPE, RESOURCE_NAME), roleName);

        assertThat(rolePermissions).isNull();
    }

    @Test
    void whenRolePermissionsHasSecurityClassificationShouldRemoveAttributesWithHigherSecurityClassification() {
        Map.Entry<Set<Permission>, SecurityClassification> readPermission = new Pair<>(ImmutableSet.of(READ), PUBLIC);
        Map.Entry<Set<Permission>, SecurityClassification> readPermissionRestricted =
            new Pair<>(ImmutableSet.of(READ), RESTRICTED);

        attributePermissionsForRole = ImmutableMap.of(
            JsonPointer.valueOf(""), readPermission,
            JsonPointer.valueOf("/orders"), readPermissionRestricted
        );

        importerService.addRole(roleName, IDAM, PUBLIC, ROLE_BASED);

        importerService.grantDefaultPermission(DefaultPermissionGrant.builder()
            .roleName(roleName)
            .resourceDefinition(ResourceDefinition.builder()
                .serviceName(SERVICE_NAME)
                .resourceType(RESOURCE_TYPE)
                .resourceName(RESOURCE_NAME)
                .build())
            .attributePermissions(attributePermissionsForRole)
            .build());

        RolePermissions rolePermissions =
            service.getRolePermissions(buildResource(SERVICE_NAME, RESOURCE_TYPE, RESOURCE_NAME), roleName);

        assertThat(rolePermissions.getPermissions())
            .hasSize(1)
            .containsEntry(JsonPointer.valueOf(""), ImmutableSet.of(READ));

        assertThat(rolePermissions.getSecurityClassifications())
            .hasSize(1)
            .containsEntry(JsonPointer.valueOf(""), PUBLIC);

        assertThat(rolePermissions.getRoleAccessType()).isEqualTo(ROLE_BASED);

        assertThat(rolePermissions.getRoleSecurityClassification()).isEqualTo(PUBLIC);
    }

    @Test
    void whenRolePermissionIsHighestThenShowAllAttribute() {

        Map.Entry<Set<Permission>, SecurityClassification> readPermission = new Pair<>(ImmutableSet.of(READ), PUBLIC);
        Map.Entry<Set<Permission>, SecurityClassification> readPermissionPrivate =
            new Pair<>(ImmutableSet.of(READ), PRIVATE);

        ImmutableSet multiPermission = ImmutableSet.builder().add(READ).add(CREATE).build();

        Map.Entry<Set<Permission>, SecurityClassification> readPermissionRestricted =
            new Pair(multiPermission, RESTRICTED);

        importerService.addRole(roleName, IDAM, RESTRICTED, ROLE_BASED);

        attributePermissionsForRole = ImmutableMap.of(
            JsonPointer.valueOf(""), readPermission,
            JsonPointer.valueOf("/address"), readPermissionPrivate,
            JsonPointer.valueOf("/address/line1"), readPermissionRestricted
        );

        importerService.grantDefaultPermission(DefaultPermissionGrant.builder()
            .roleName(roleName)
            .resourceDefinition(ResourceDefinition.builder()
                .serviceName(SERVICE_NAME)
                .resourceType(RESOURCE_TYPE)
                .resourceName(RESOURCE_NAME)
                .build())
            .attributePermissions(attributePermissionsForRole)
            .build());

        RolePermissions rolePermissions =
            service.getRolePermissions(buildResource(SERVICE_NAME, RESOURCE_TYPE, RESOURCE_NAME), roleName);


         assertThat(rolePermissions.getPermissions())
            .hasSize(3).
             contains(new AbstractMap.SimpleEntry<>(JsonPointer.valueOf("/address"), ImmutableSet.of(READ))).
             containsEntry(JsonPointer.valueOf("/address/line1"), multiPermission).
             contains(new AbstractMap.SimpleEntry<>(JsonPointer.valueOf(""), ImmutableSet.of(READ)));
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
