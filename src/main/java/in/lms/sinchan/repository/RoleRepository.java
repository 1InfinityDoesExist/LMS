package in.lms.sinchan.repository;

import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import in.lms.sinchan.entity.Role;

@Repository
public interface RoleRepository extends MongoRepository<Role, String> {

    @Query(value = "{'isActive':true, 'id':?0}")
    public Role findRoleById(String id);

    @Query(value = "{'isActive':true, 'name':?0}")
    public Role findRoleByName(String role);

    public List<Role> findRoleByIsActive(boolean b);

}
