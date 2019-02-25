package uk.gov.hmcts.reform.amlib;

import com.fasterxml.jackson.core.JsonPointer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import uk.gov.hmcts.reform.amlib.enums.Permission;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static uk.gov.hmcts.reform.amlib.enums.Permission.READ;

public class FilterService {

    JsonNode filterJson(JsonNode resourceJson, Map<JsonPointer, Set<Permission>> attributePermissions) {
        JsonNode resourceCopy = resourceJson.deepCopy();

        List<JsonPointer> nodesWithRead = attributePermissions.entrySet().stream()
            .filter(entry -> entry.getValue().contains(READ))
            .map(Map.Entry::getKey)
            .collect(Collectors.toList());

        System.out.println("> nodesWithRead = " + nodesWithRead);

        if (nodesWithRead.isEmpty()) {
            return null;
        }

        List<JsonPointer> uniqueNodesWithRead = nodesWithRead.stream()
            .sorted(Comparator.comparing(JsonPointer::toString))
            .reduce(new ArrayList<>(),
                (List<JsonPointer> result, JsonPointer pointerCandidate) -> {
                    if (result.stream().noneMatch(acceptedPointer -> { // already contains parent so no point adding
                        return acceptedPointer.toString().equals("")
                            || pointerCandidate.toString().startsWith(acceptedPointer.toString());
                    })) {
                        result.add(pointerCandidate);
                    }
                    return result;
                }, (firstPointer, secondPointer) -> firstPointer);

        System.out.println("> uniqueNodesWithRead = " + uniqueNodesWithRead);

        uniqueNodesWithRead.forEach(pointerCandidateForRetaining -> {
            if (pointerCandidateForRetaining.toString().isEmpty()) {
                return;
            }
            System.out.println(">> pointerCandidateForRetaining = " + pointerCandidateForRetaining);
            ObjectNode node = (ObjectNode) resourceCopy.at(pointerCandidateForRetaining.head());
            node.retain(pointerCandidateForRetaining.last().toString().substring(1));
        });

        List<JsonPointer> nodesWithoutRead = attributePermissions.entrySet().stream()
            .filter(entry -> !entry.getValue().contains(READ))
            .map(Map.Entry::getKey)
            .collect(Collectors.toList());

        System.out.println("> nodesWithoutRead = " + nodesWithoutRead);

        nodesWithoutRead.forEach(pointerCandidateForRemoval -> {
            System.out.println(">> pointerCandidateForRemoval = " + pointerCandidateForRemoval);
            List<JsonPointer> childPointersWithRead = nodesWithRead.stream()
                .filter(pointerWithRead -> pointerWithRead.toString().startsWith(pointerCandidateForRemoval.toString()))
                .collect(Collectors.toList());
            if (!childPointersWithRead.isEmpty()) {
                // retain node's children with READ
                ObjectNode node = (ObjectNode) resourceCopy.at(pointerCandidateForRemoval);
                node.retain(childPointersWithRead.stream().map(p -> p.last().toString().substring(1)).collect(Collectors.toList()));
            } else {
                // remove whole node
                ObjectNode node = (ObjectNode) resourceCopy.at(pointerCandidateForRemoval.head());
                node.remove(pointerCandidateForRemoval.last().toString().substring(1));
            }
        });

        return resourceCopy;
    }
}
