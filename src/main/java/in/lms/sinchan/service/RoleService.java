package in.lms.sinchan.service;

import java.util.List;
import org.springframework.stereotype.Service;
import in.lms.sinchan.entity.Role;
import in.lms.sinchan.model.request.RoleCreateRequest;
import in.lms.sinchan.model.request.RoleUpdateRequest;
import in.lms.sinchan.model.response.RoleCreateResponse;

@Service
public interface RoleService {

    public RoleCreateResponse persistRoleInDB(RoleCreateRequest roleCreateRequest) throws Exception;

    public Role getRole(String id) throws Exception;

    public List<Role> getAllRoles() throws Exception;

    public void deleteRole(String id) throws Exception;

    public void updateRole(RoleUpdateRequest roleUpdateRequest, String id) throws Exception;
}
