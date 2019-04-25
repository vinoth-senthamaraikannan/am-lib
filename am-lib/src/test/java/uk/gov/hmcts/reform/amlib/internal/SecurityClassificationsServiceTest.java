package uk.gov.hmcts.reform.amlib.internal;

import com.fasterxml.jackson.core.JsonPointer;
import org.junit.jupiter.api.Test;
import org.testcontainers.shaded.com.google.common.collect.ImmutableMap;
import org.testcontainers.shaded.com.google.common.collect.ImmutableSet;
import uk.gov.hmcts.reform.amlib.enums.Permission;
import uk.gov.hmcts.reform.amlib.enums.SecurityClassification;

import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.hmcts.reform.amlib.enums.Permission.READ;
import static uk.gov.hmcts.reform.amlib.enums.SecurityClassification.PRIVATE;
import static uk.gov.hmcts.reform.amlib.enums.SecurityClassification.PUBLIC;
import static uk.gov.hmcts.reform.amlib.enums.SecurityClassification.RESTRICTED;

class SecurityClassificationsServiceTest {

    private final SecurityClassificationService securityClassificationService = new SecurityClassificationService();

    @Test
    void shouldReturnJsonPointerWithSecurityClassificationAndPermissionWhenCalled() {
        Map<JsonPointer, Set<Permission>> permissions = ImmutableMap.of(
            JsonPointer.valueOf(""), ImmutableSet.of(READ));

        Map<JsonPointer, SecurityClassification> securityClassification = ImmutableMap.of(
            JsonPointer.valueOf(""), PUBLIC);

        Map<JsonPointer, Map<SecurityClassification, Set<Permission>>> result =
            securityClassificationService.removeEntriesWithInsufficientSecurityClassification(
                permissions, securityClassification, PUBLIC);

        assertThat(result).isEqualTo(
            ImmutableMap.of(JsonPointer.valueOf(""), ImmutableMap.of(PUBLIC, ImmutableSet.of(READ))));
    }

    @Test
    void whenRoleSecurityClassificationIsHighEnoughShouldNotRemoveAnything() {
        Map<JsonPointer, Set<Permission>> permissions = ImmutableMap.of(
            JsonPointer.valueOf(""), ImmutableSet.of(READ),
            JsonPointer.valueOf("/child"), ImmutableSet.of(READ),
            JsonPointer.valueOf("/address"), ImmutableSet.of(READ)
        );

        Map<JsonPointer, SecurityClassification> securityClassification = ImmutableMap.of(
            JsonPointer.valueOf(""), PUBLIC,
            JsonPointer.valueOf("/child"), PRIVATE,
            JsonPointer.valueOf("/address"), RESTRICTED
        );

        Map<JsonPointer, Map<SecurityClassification, Set<Permission>>> result =
            securityClassificationService.removeEntriesWithInsufficientSecurityClassification(
                permissions, securityClassification, RESTRICTED);

        assertThat(result).containsOnlyKeys(
            JsonPointer.valueOf(""),
            JsonPointer.valueOf("/child"),
            JsonPointer.valueOf("/address")
        );
    }

    @Test
    void whenRoleSecurityClassificationIsInsufficientShouldRemoveEntries() {
        Map<JsonPointer, Set<Permission>> permissions = ImmutableMap.of(
            JsonPointer.valueOf(""), ImmutableSet.of(READ));

        Map<JsonPointer, SecurityClassification> securityClassification = ImmutableMap.of(
            JsonPointer.valueOf(""), RESTRICTED);

        Map<JsonPointer, Map<SecurityClassification, Set<Permission>>> result =
            securityClassificationService.removeEntriesWithInsufficientSecurityClassification(
                permissions, securityClassification, PUBLIC);

        assertThat(result).isEmpty();
    }

    @Test
    void whenRoleSecurityClassificationIsInsufficientShouldRemoveOnlyInvalidEntries() {
        Map<JsonPointer, Set<Permission>> permissions = ImmutableMap.of(
            JsonPointer.valueOf(""), ImmutableSet.of(READ),
            JsonPointer.valueOf("/parent"), ImmutableSet.of(READ)
        );

        Map<JsonPointer, SecurityClassification> securityClassification = ImmutableMap.of(
            JsonPointer.valueOf(""), PUBLIC,
            JsonPointer.valueOf("/parent"), RESTRICTED
        );

        Map<JsonPointer, Map<SecurityClassification, Set<Permission>>> result =
            securityClassificationService.removeEntriesWithInsufficientSecurityClassification(
                permissions, securityClassification, PUBLIC);

        assertThat(result).containsOnlyKeys(JsonPointer.valueOf(""));
    }

    @Test
    void whenPermissionAndSecurityClassificationMapsDoNotMatchShouldReturnEmpty() {
        Map<JsonPointer, Set<Permission>> permissions = ImmutableMap.of(
            JsonPointer.valueOf("/parent"), ImmutableSet.of(READ));

        Map<JsonPointer, SecurityClassification> securityClassification = ImmutableMap.of(
            JsonPointer.valueOf("/children"), PUBLIC);


        Map<JsonPointer, Map<SecurityClassification, Set<Permission>>> result =
            securityClassificationService.removeEntriesWithInsufficientSecurityClassification(
                permissions, securityClassification, PUBLIC);

        assertThat(result).isEmpty();
    }
}
