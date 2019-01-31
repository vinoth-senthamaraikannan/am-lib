package uk.gov.hmcts.reform.amlib;

import org.junit.Test;
import uk.gov.hmcts.reform.amlib.enums.Permissions;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Java6Assertions.assertThat;

public class PermissionsTest {

    @Test
    public void sumOf_whenPassingPermissions_theSumOfValuesIsCalculated() {
        List<Permissions> permissions = Arrays.asList(Permissions.CREATE, Permissions.READ);

        int sum = Permissions.sumOf(permissions);

        int expectedSum = Permissions.CREATE.getValue() + Permissions.READ.getValue();
        assertThat(sum).isEqualTo(expectedSum);
    }

    @Test
    public void sumOf_whenPassingNoPermissions_theSumOfValuesIsZero() {
        List<Permissions> permissions = Arrays.asList();

        int sum = Permissions.sumOf(permissions);

        assertThat(sum).isEqualTo(0);
    }
}
