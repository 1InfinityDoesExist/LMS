package in.lms.sinchan.service.impl;

import java.util.List;
import java.util.Optional;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import com.amazonaws.util.StringUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import in.lms.sinchan.entity.Role;
import in.lms.sinchan.entity.Tenant;
import in.lms.sinchan.exception.InvalidInput;
import in.lms.sinchan.exception.RoleNotFoundException;
import in.lms.sinchan.exception.TenantNotFoundException;
import in.lms.sinchan.model.request.RoleCreateRequest;
import in.lms.sinchan.model.request.RoleUpdateRequest;
import in.lms.sinchan.model.response.RoleCreateResponse;
import in.lms.sinchan.repository.RoleRepository;
import in.lms.sinchan.repository.TenantRepository;
import in.lms.sinchan.service.RoleService;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private TenantRepository tenantRepository;

    @Override
    public RoleCreateResponse persistRoleInDB(RoleCreateRequest roleCreateRequest)
                    throws Exception {
        if (!StringUtils.isNullOrEmpty(roleCreateRequest.getParentTenant())) {
            Optional<Tenant> tenant =
                            tenantRepository.findById(roleCreateRequest.getParentTenant());
            if (ObjectUtils.isEmpty(tenant)) {
                throw new TenantNotFoundException(
                                "Tenant does not exist. Please create tenant first.");
            }
        }
        Role role = new Role();
        role.setDescription(roleCreateRequest.getDescription());
        role.setName(roleCreateRequest.getName());
        role.setParentTenant(roleCreateRequest.getParentTenant());
        role.setActive(true);
        roleRepository.save(role);
        RoleCreateResponse response = new RoleCreateResponse();
        response.setId(role.getId());
        response.setMsg("Successfully persisted role in database.");
        return response;
    }

    @Override
    public Role getRole(String id) throws Exception {
        if (!StringUtils.isNullOrEmpty(id)) {
            Role role = roleRepository.findRoleById(id);
            if (!ObjectUtils.isEmpty(role)) {
                return role;
            } else {
                throw new RoleNotFoundException("Role details does not exit with id : " + id);
            }
        } else {
            throw new InvalidInput("Id must not be null or empty");
        }
    }

    @Override
    public List<Role> getAllRoles() throws Exception {
        List<Role> listOfRole = roleRepository.findRoleByIsActive(true);
        return listOfRole;
    }

    @Override
    public void deleteRole(String id) throws Exception {
        if (!ObjectUtils.isEmpty(id)) {
            Role role = roleRepository.findRoleById(id);
            if (!ObjectUtils.isEmpty(role)) {
                role.setActive(false);
                roleRepository.save(role);
            } else {
                throw new RoleNotFoundException("Role does not exist. Please crete a role first");
            }
        } else {
            throw new InvalidInput("Id must not be null or empty");
        }

    }

    @SuppressWarnings("unchecked")
    @Override
    public void updateRole(RoleUpdateRequest roleUpdateRequest, String id) throws Exception {
        Role role = roleRepository.findRoleById(id);
        if (ObjectUtils.isEmpty(role)) {
            throw new RoleNotFoundException(
                            "Role with id :" + id + " does not exist. Please create a role first.");
        }
        JSONObject roleFromDB = (JSONObject) new JSONParser()
                        .parse(new ObjectMapper().writeValueAsString(role));
        JSONObject roleFromPayload = (JSONObject) new JSONParser()
                        .parse(new ObjectMapper().writeValueAsString(roleUpdateRequest));
        for (Object obj : roleFromPayload.keySet()) {
            String param = (String) obj;
            if (param.equalsIgnoreCase("parentTenant")) {
                Tenant tenantFromDB = tenantRepository
                                .findTenantById((String) roleFromPayload.get(param));
                if (ObjectUtils.isEmpty(tenantFromDB)) {
                    throw new TenantNotFoundException(
                                    "Tenant does not exist. Please create tenant first.");
                } else {
                    roleFromDB.put(param, roleFromPayload.get(param));
                }
            } else {
                roleFromDB.put(param, roleFromPayload.get(param));
            }
        }
        Role updatedRole = new ObjectMapper().readValue(roleFromDB.toJSONString(), Role.class);
        roleRepository.save(updatedRole);
        return;
    }
}
