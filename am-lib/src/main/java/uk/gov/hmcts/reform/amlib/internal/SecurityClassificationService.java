package uk.gov.hmcts.reform.amlib.internal;

import com.fasterxml.jackson.core.JsonPointer;
import lombok.extern.slf4j.Slf4j;
import uk.gov.hmcts.reform.amlib.enums.Permission;
import uk.gov.hmcts.reform.amlib.enums.SecurityClassification;

import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;

@Slf4j
public class SecurityClassificationService {

    public Map<JsonPointer, Map<SecurityClassification, Set<Permission>>>
        removeEntriesWithInsufficientSecurityClassification(Map<JsonPointer, Set<Permission>> permissions,
                                                            Map<JsonPointer, SecurityClassification> secClassifications,
                                                            SecurityClassification roleSecClassification) {
        Map<JsonPointer, Map<SecurityClassification, Set<Permission>>> attributeData = permissions.entrySet()
            .stream()
            .collect(groupingBy(Map.Entry::getKey,
                toMap(e -> secClassifications.get(e.getKey()), Map.Entry::getValue)
            ));

        Set<SecurityClassification> insufficientSecurityClassifications = EnumSet.allOf(SecurityClassification.class)
            .stream()
            .filter(securityClassification ->
                !securityClassification.isVisible(roleSecClassification.getHierarchy()))
            .collect(toSet());

        attributeData.entrySet().removeIf(entry ->
            entry.getValue().keySet().stream().anyMatch(insufficientSecurityClassifications::contains));

        attributeData.entrySet().removeIf(entry -> entry.getValue().keySet().contains(null));

        return attributeData;
    }
}
