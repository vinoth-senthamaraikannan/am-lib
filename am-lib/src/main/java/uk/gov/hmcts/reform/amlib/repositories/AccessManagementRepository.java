package uk.gov.hmcts.reform.amlib.repositories;

import org.jdbi.v3.sqlobject.config.RegisterConstructorMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;
import uk.gov.hmcts.reform.amlib.models.AccessManagement;

import java.util.List;

public interface AccessManagementRepository {

    @SqlUpdate("insert into \"AccessManagement\" (\"resourceId\", \"accessorId\", permissions) values (?, ?, ?)")
    void createAccessManagementRecord(String resourceId, String accessorId, int permissions);

    @SqlQuery("select \"accessorId\" from \"AccessManagement\" where exists "
            + "(select 1 from \"AccessManagement\" where \"AccessManagement\".\"accessorId\" = :accessorId "
            + "and \"AccessManagement\".\"resourceId\" = :resourceId) "
            + "and \"AccessManagement\".\"resourceId\" = :resourceId")
    List<String> getAccessorsList(@Bind("accessorId") String accessorId, @Bind("resourceId") String resourceId);

    // The 'LIMIT 1' suffix was introduced because at the current database state (V2.2) there is a technical
    // possibility of returning multiple records with this query, but it's forbidden from business point of view.
    @SqlQuery("select * from \"AccessManagement\" where \"accessorId\"=? and \"resourceId\"=? LIMIT 1")
    @RegisterConstructorMapper(AccessManagement.class)
    AccessManagement getExplicitAccess(String accessorId, String resourceId);
}
