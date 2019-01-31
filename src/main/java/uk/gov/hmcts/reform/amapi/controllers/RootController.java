package uk.gov.hmcts.reform.amapi.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.hmcts.reform.amlib.AccessManagementService;
import uk.gov.hmcts.reform.amlib.enums.Permissions;
import uk.gov.hmcts.reform.amlib.models.ExplicitPermissions;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.http.ResponseEntity.ok;

/**
 * Default endpoints per application.
 */
@RestController
public class RootController {

    @Autowired private ObjectMapper mapper;
    @Autowired private AccessManagementService am;

    /**
     * Root GET endpoint.
     *
     * <p>Azure application service has a hidden feature of making requests to root endpoint when
         * "Always On" is turned on.
     * This is the endpoint to deal with that and therefore silence the unnecessary 404s as a response code.
     *
             * @return Welcome message from the service.
     */
    @GetMapping("/")
    public ResponseEntity<String> welcome() {
        return ok("Welcome to AM Lib Testing Service!");
    }

    @SuppressWarnings("unchecked") // supressing compiler warning about casting from Object to List<String>
    @PostMapping("/create-resource-access")
    public void createResourceAccess(@RequestBody Map<String, Object> amData) {
        LinkedHashMap<String, List> rawExplicitPermissions = (LinkedHashMap) amData.get("explicitPermissions");
        List<String> userPermissions = rawExplicitPermissions.get("userPermissions");
        Permissions[] permissions = userPermissions.stream()
                .map(ep -> Permissions.valueOf(ep))
                .toArray(Permissions[]::new);

        ExplicitPermissions explicitPermissions = new ExplicitPermissions(permissions);

        am.createResourceAccess(amData.get("resourceId").toString(),
                amData.get("accessorId").toString(),
                explicitPermissions);
    }

    @PostMapping("/filter-resource")
    public JsonNode filterResource(@RequestBody Map<String, Object> amData) {
        JsonNode jsonNode = mapper.valueToTree(amData.get("resourceJson"));
        return am.filterResource(
            amData.get("userId").toString(),
            amData.get("resourceId").toString(),
            jsonNode
        );
    }
}
