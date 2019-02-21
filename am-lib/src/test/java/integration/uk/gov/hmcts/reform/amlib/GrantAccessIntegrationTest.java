package integration.uk.gov.hmcts.reform.amlib;

import com.fasterxml.jackson.core.JsonPointer;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import integration.uk.gov.hmcts.reform.amlib.base.IntegrationBaseTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.gov.hmcts.reform.amlib.enums.Permission;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static uk.gov.hmcts.reform.amlib.helpers.TestConstants.ACCESSOR_ID;
import static uk.gov.hmcts.reform.amlib.helpers.TestConstants.EXPLICIT_READ_CREATE_UPDATE_PERMISSIONS;
import static uk.gov.hmcts.reform.amlib.helpers.TestConstants.READ_PERMISSION;
import static uk.gov.hmcts.reform.amlib.helpers.TestDataFactory.createGrant;
import static uk.gov.hmcts.reform.amlib.helpers.TestDataFactory.createGrantForWholeDocument;
import static uk.gov.hmcts.reform.amlib.helpers.TestDataFactory.createPermissionsForWholeDocument;

class GrantAccessIntegrationTest extends IntegrationBaseTest {

    private String resourceId;

    @BeforeEach
    void setupTest() {
        resourceId = UUID.randomUUID().toString();
    }

    @Test
    void noAttributesShouldThrowException() {
        Map<JsonPointer, Set<Permission>> noAttributes = ImmutableMap.of();

        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() ->
            ams.grantExplicitResourceAccess(createGrant(resourceId, ACCESSOR_ID, noAttributes)))
            .withMessage("At least one attribute is required");
    }

    @Test
    @SuppressWarnings("PMD")
    void noPermissionsForAttributesShouldThrowException() {
        Map<JsonPointer, Set<Permission>> attributeNoPermissions = createPermissionsForWholeDocument(ImmutableSet.of());

        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() ->
            ams.grantExplicitResourceAccess(createGrant(resourceId, ACCESSOR_ID, attributeNoPermissions)))
            .withMessage("At least one permission per attribute is required");
    }

    @Test
    void whenCreatingResourceAccessResourceAccessAppearsInDatabase() {
        ams.grantExplicitResourceAccess(createGrantForWholeDocument(resourceId, READ_PERMISSION));

        assertThat(countResourcesById(resourceId)).isEqualTo(1);
    }

    @Test
    void whenCreatingResourceAccessMultipleEntriesAppearInDatabase() {
        Map<JsonPointer, Set<Permission>> multipleAttributes = ImmutableMap.<JsonPointer, Set<Permission>>builder()
            .put(JsonPointer.valueOf(""), EXPLICIT_READ_CREATE_UPDATE_PERMISSIONS)
            .put(JsonPointer.valueOf("/name"), EXPLICIT_READ_CREATE_UPDATE_PERMISSIONS)
            .build();

        ams.grantExplicitResourceAccess(createGrant(resourceId, ACCESSOR_ID, multipleAttributes));

        assertThat(countResourcesById(resourceId)).isEqualTo(2);
    }

    @Test
    void whenCreatingDuplicateResourceAccessEntryIsOverwritten() {
        ams.grantExplicitResourceAccess(createGrantForWholeDocument(resourceId, READ_PERMISSION));
        ams.grantExplicitResourceAccess(createGrantForWholeDocument(resourceId, READ_PERMISSION));

        assertThat(countResourcesById(resourceId)).isEqualTo(1);
    }
}
