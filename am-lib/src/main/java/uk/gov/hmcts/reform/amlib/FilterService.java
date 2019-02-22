package uk.gov.hmcts.reform.amlib;

import com.fasterxml.jackson.core.JsonPointer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import uk.gov.hmcts.reform.amlib.enums.Permission;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static uk.gov.hmcts.reform.amlib.enums.Permission.READ;

public class FilterService {

    JsonNode filterJson(JsonNode resourceJson, Map<JsonPointer, Set<Permission>> attributePermissions) {

        JsonNode resourceCopy = resourceJson.deepCopy();

        Map<JsonPointer, List<String>> pathsAndFieldnamesToRetain = new ConcurrentHashMap<>();
        List<String> fieldsWithReadPermission = new ArrayList<>();

        attributePermissions.forEach((attribute, permissions) -> {

            String jsonPointer = attribute.toString();

            if (jsonPointer.isEmpty()) {
                return;
            }

            String path = jsonPointer.substring(0, jsonPointer.lastIndexOf('/'));
            String fieldName = jsonPointer.substring(jsonPointer.lastIndexOf('/') + 1);

            if (permissions.contains(READ)) {
                fieldsWithReadPermission.add(fieldName);
                pathsAndFieldnamesToRetain.put(JsonPointer.valueOf(path), fieldsWithReadPermission);
            } else {
                ((ObjectNode) resourceCopy.at(JsonPointer.valueOf(path))).remove(fieldName);
            }
        });

        pathsAndFieldnamesToRetain.forEach((path, fieldNames) ->
            ((ObjectNode) resourceCopy.at(path)).retain(fieldNames));

        return resourceCopy;
    }
}
