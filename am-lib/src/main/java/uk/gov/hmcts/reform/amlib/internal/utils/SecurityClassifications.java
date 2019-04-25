package uk.gov.hmcts.reform.amlib.internal.utils;

import uk.gov.hmcts.reform.amlib.enums.SecurityClassification;

import java.util.EnumSet;
import java.util.Set;

import static java.util.stream.Collectors.toSet;

public class SecurityClassifications {

    private SecurityClassifications() {
        //NO-OP
    }

    //TODO: write tests for fromValueOf method.
    public static Set<SecurityClassification> fromValueOf(int securityClassificationForRole) {
        return EnumSet.allOf(SecurityClassification.class)
            .stream()
            .filter(securityClassification ->
                securityClassification.isVisible(securityClassificationForRole))
            .collect(toSet());
    }
}
