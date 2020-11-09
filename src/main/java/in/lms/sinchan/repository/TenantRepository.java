package in.lms.sinchan.repository;

import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import in.lms.sinchan.entity.Tenant;

@Repository
public interface TenantRepository extends MongoRepository<Tenant, String> {

    @Query(value = "{'isActive':true, 'name':?0}")
    public Tenant getTenantByName(String name);

    @Query(value = "{'isActive':true, 'id':?0}")
    public Tenant findTenantById(String id);

    public List<Tenant> findTenantByIsActive(boolean b);

}
