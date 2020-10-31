package in.lms.sinchan.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import in.lms.sinchan.entity.Role;

@Repository
public interface RoleRepository extends MongoRepository<Role, String> {

    public Role findRoleById(String id);

}
