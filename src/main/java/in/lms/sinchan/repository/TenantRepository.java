package in.lms.sinchan.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import in.lms.sinchan.entity.Tenant;

@Repository
public interface TenantRepository extends MongoRepository<Tenant, String> {

    public Tenant getTenantByName(String name);

    public Tenant findTenantById(String id);

}
