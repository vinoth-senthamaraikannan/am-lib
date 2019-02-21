package uk.gov.hmcts.reform.amlib;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;
import uk.gov.hmcts.reform.amlib.enums.Permission;
import uk.gov.hmcts.reform.amlib.models.ExplicitAccessMetadata;
import uk.gov.hmcts.reform.amlib.models.ExplicitAccessRecord;
import uk.gov.hmcts.reform.amlib.models.FilterResourceResponse;
import uk.gov.hmcts.reform.amlib.repositories.AccessManagementRepository;
import uk.gov.hmcts.reform.amlib.utils.Permissions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static uk.gov.hmcts.reform.amlib.enums.Permission.READ;

public class AccessManagementService {
    private final Jdbi jdbi;

    public AccessManagementService(String url, String user, String password) {
        this.jdbi = Jdbi.create(url, user, password);

        this.jdbi.installPlugin(new SqlObjectPlugin());
    }

    /**
     * Grants explicit access to resource accordingly to record configuration.
     *
     * @param explicitAccessRecord a record that describes explicit access to resource
     */
    public void createResourceAccess(ExplicitAccessRecord explicitAccessRecord) {
        jdbi.useExtension(AccessManagementRepository.class,
            dao -> dao.createAccessManagementRecord(explicitAccessRecord));
    }

    /**
     * Removes explicit access to resource accordingly to record configuration.
     *
     * @param explicitAccessMetadata an object to remove a specific explicit access record.
     */
    public void revokeResourceAccess(ExplicitAccessMetadata explicitAccessMetadata) {
        jdbi.useExtension(AccessManagementRepository.class,
            dao -> dao.removeAccessManagementRecord(explicitAccessMetadata));
    }

    /**
     * Returns list of user ids who have access to resource or null if user has no access to this resource.
     *
     * @param userId     (accessorId)
     * @param resourceId resource Id
     * @return List of user ids (accessor id) or null
     */
    public List<String> getAccessorsList(String userId, String resourceId) {
        return jdbi.withExtension(AccessManagementRepository.class, dao -> {
            List<String> userIds = dao.getAccessorsList(userId, resourceId);

            return userIds.isEmpty() ? null : userIds;
        });
    }

    /**
     * Returns {@link FilterResourceResponse} when record with userId and resourceId exist and has READ permissions,
     * otherwise null.
     *
     * @param userId       (accessorId)
     * @param resourceId   resource id
     * @param resourceJson json
     * @return filtered resourceJson based on READ permissions or null if no READ permissions on resource.
     * Returns whole envelope with resource ID, filtered JSON and map of permissions.
     */
    public FilterResourceResponse filterResource(String userId, String resourceId, JsonNode resourceJson) {
        List<ExplicitAccessRecord> explicitAccess = jdbi.withExtension(AccessManagementRepository.class,
            dao -> dao.getExplicitAccess(userId, resourceId));

        if (explicitAccess.isEmpty()) {
            return null;
        }

        Map<String, Set<Permission>> attributePermissions = new ConcurrentHashMap<>();

        List<String> toKeep = new ArrayList<>();

        explicitAccess.forEach(explicitAccessRecord -> {
            attributePermissions.put(explicitAccessRecord.getAttribute(),
                Permissions.fromSumOf(explicitAccessRecord.getPermissions()));
            if (!READ.isGranted(explicitAccessRecord.getPermissions())) {
                ((ObjectNode)resourceJson).remove(explicitAccessRecord.getAttribute().replaceFirst("/", ""));

                System.out.println("explicitAccessRecord.getAttribute() = " + explicitAccessRecord.getAttribute());
                System.out.println("resourceJsonAfterRemove = " + resourceJson);
            } else {
                String test = explicitAccessRecord.getAttribute().replaceFirst("/", "");
//                toKeep.add(test.substring(0, test.indexOf("/")).trim());
//                toKeep.add(test.substring(test.indexOf("/")+1).trim());
                toKeep.add(test);

                System.out.println("explicitAccessRecord.getAttribute() = " + explicitAccessRecord.getAttribute());
                System.out.println("toKeep = " + toKeep);
            }
        });

        ((ObjectNode) resourceJson).retain(toKeep);

        System.out.println("resourceJsonAfterRetain = " + resourceJson);


        return FilterResourceResponse.builder()
            .resourceId(resourceId)
            .data(resourceJson)
            .permissions(attributePermissions)
            .build();
    }
}
