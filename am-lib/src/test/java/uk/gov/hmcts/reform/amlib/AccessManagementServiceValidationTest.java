package uk.gov.hmcts.reform.amlib;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import uk.gov.hmcts.reform.amlib.helpers.InvalidArgumentsProvider;
import uk.gov.hmcts.reform.amlib.models.ExplicitAccessGrant;
import uk.gov.hmcts.reform.amlib.models.ExplicitAccessMetadata;
import uk.gov.hmcts.reform.amlib.models.Resource;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static uk.gov.hmcts.reform.amlib.helpers.ValidationMessageRegexFactory.expectedValidationMessagesRegex;

@SuppressWarnings({"PMD"})
class AccessManagementServiceValidationTest {
    private final AccessManagementService service = new AccessManagementService("", "", "");

    @ParameterizedTest
    @ArgumentsSource(InvalidArgumentsProvider.class)
    void grantExplicitResourceAccessMethodShouldRejectInvalidArguments(ExplicitAccessGrant accessGrant) {
        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> service.grantExplicitResourceAccess(accessGrant))
            .withMessageMatching(expectedValidationMessagesRegex(
                "accessGrant - must not be null",
                "accessGrant.resourceId - must not be blank",
                "accessGrant.accessorIds - must not be empty",
                "accessGrant.accessType - must not be blank",
                "accessGrant.serviceName - must not be blank",
                "accessGrant.resourceType - must not be blank",
                "accessGrant.resourceName - must not be blank",
                "accessGrant.attributePermissions - must not be empty",
                "accessGrant.securityClassification - must not be null"
            ));
    }

    @ParameterizedTest
    @ArgumentsSource(InvalidArgumentsProvider.class)
    void revokeResourceAccessMethodShouldRejectInvalidArguments(ExplicitAccessMetadata accessMetadata) {
        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> service.revokeResourceAccess(accessMetadata))
            .withMessageMatching(expectedValidationMessagesRegex(
                "accessMetadata - must not be null",
                "accessMetadata.resourceId - must not be blank",
                "accessMetadata.accessorId - must not be blank",
                "accessMetadata.accessType - must not be blank",
                "accessMetadata.serviceName - must not be blank",
                "accessMetadata.resourceType - must not be blank",
                "accessMetadata.resourceName - must not be blank",
                "accessMetadata.attribute - must not be null",
                "accessMetadata.securityClassification - must not be null"
            ));
    }

    @ParameterizedTest
    @ArgumentsSource(InvalidArgumentsProvider.class)
    void filterResourceMethodShouldRejectInvalidArguments(String userId, Set<String> userRoles, Resource resource) {
        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> service.filterResource(userId, userRoles, resource))
            .withMessageMatching(expectedValidationMessagesRegex(
                "userId - must not be blank",
                "userRoles - must not be empty",
                "userRoles\\[\\].<iterable element> - must not be blank",
                "resource - must not be null",
                "resource.resourceId - must not be blank",
                "resource.type - must not be null",
                "resource.type.serviceName - must not be blank",
                "resource.type.resourceType - must not be blank",
                "resource.type.resourceName - must not be blank",
                "resource.resourceJson - must not be null"
            ));
    }

    @ParameterizedTest
    @ArgumentsSource(InvalidArgumentsProvider.class)
    void getRolePermissionsMethodShouldRejectInvalidArguments(String serviceName,
                                                              String resourceType,
                                                              String resourceName,
                                                              Set<String> roleNames) {
        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> service.getRolePermissions(serviceName, resourceType, resourceName, roleNames))
            .withMessageMatching(expectedValidationMessagesRegex(
                "serviceName - must not be blank",
                "resourceType - must not be blank",
                "resourceName - must not be blank",
                "roleNames - must not be empty",
                "roleNames\\[\\].<iterable element> - must not be blank"
            ));
    }

    @ParameterizedTest
    @ArgumentsSource(InvalidArgumentsProvider.class)
    void getResourceDefinitionsWithRootCreatePermissionMethodShouldRejectInvalidArguments(Set<String> userRoles) {
        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> service.getResourceDefinitionsWithRootCreatePermission(userRoles))
            .withMessageMatching(expectedValidationMessagesRegex(
                "userRoles - must not be empty",
                "userRoles\\[\\].<iterable element> - must not be blank"
            ));
    }
}