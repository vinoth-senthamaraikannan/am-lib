package uk.gov.hmcts.reform.amlib.internal.utils;

import uk.gov.hmcts.reform.amlib.enums.SecurityClassification;

import java.util.EnumSet;
import java.util.Set;

import static java.util.stream.Collectors.toSet;

public final class SecurityClassifications {

    private SecurityClassifications() {
        //NO-OP
    }

    /**
     * Builds a set of security classifications including and below a given hierarchy.
     *
     * @param securityClassificationForRole integer value of security classification defined in
     * {@link SecurityClassification}
     * @return a set of security classifications
     */
    public static Set<SecurityClassification> fromValueOf(int securityClassificationForRole) {
        return EnumSet.allOf(SecurityClassification.class)
            .stream()
            .filter(securityClassification ->
                securityClassification.isVisible(securityClassificationForRole))
            .collect(toSet());
    }
}
