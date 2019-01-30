package uk.gov.hmcts.reform.amlib.repositories;

import org.jdbi.v3.sqlobject.config.RegisterBeanMapper;
import org.jdbi.v3.sqlobject.config.RegisterConstructorMapper;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;
import uk.gov.hmcts.reform.amlib.models.AccessManagement;

import java.util.List;

public interface AccessManagementRepository {

    @SqlUpdate("insert into \"AccessManagement\" (\"resourceId\", \"accessorId\", permissions) values (?, ?, ?)")
    void createAccessManagementRecord(String resourceId, String accessorId, int permissions);

    @SqlQuery("select * from \"AccessManagement\" where  \"accessorId\"=? and \"resourceId\"=? LIMIT 1")
    @RegisterConstructorMapper(AccessManagement.class)
    AccessManagement getExplicitAccess(String accessorId, String resourceId);
}
