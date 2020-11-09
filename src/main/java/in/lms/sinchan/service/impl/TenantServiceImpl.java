package in.lms.sinchan.service.impl;

import java.util.List;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import com.amazonaws.util.StringUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import in.lms.sinchan.entity.Tenant;
import in.lms.sinchan.exception.InvalidInput;
import in.lms.sinchan.exception.TenantAlreadyExistException;
import in.lms.sinchan.exception.TenantNotFoundException;
import in.lms.sinchan.model.request.TenantCreateRequest;
import in.lms.sinchan.model.request.TenantUpdateRequest;
import in.lms.sinchan.model.response.TenantCreateResponse;
import in.lms.sinchan.repository.TenantRepository;
import in.lms.sinchan.service.TenantService;

@Component
public class TenantServiceImpl implements TenantService {

    @Autowired
    private TenantRepository tenantRepository;

    @Override
    public TenantCreateResponse persistTenantInDB(TenantCreateRequest tenantCreateRequest)
                    throws Exception {
        if (!StringUtils.isNullOrEmpty(tenantCreateRequest.getName())) {
            Tenant tenant = tenantRepository.getTenantByName(tenantCreateRequest.getName());
            if (!ObjectUtils.isEmpty(tenant)) {
                throw new TenantAlreadyExistException("Tenant already exist.");
            } else {
                tenant = new Tenant();
                tenant.setActive(true);
                tenant.setDescription(tenantCreateRequest.getDescription());
                tenant.setName(tenantCreateRequest.getName());
                tenant.setOrganizationName(tenantCreateRequest.getOrganizationName());
                tenant.setRegistratioNumber(tenantCreateRequest.getRegistratioNumber());
                tenantRepository.save(tenant);
                TenantCreateResponse tenantCreateResponse = new TenantCreateResponse();
                tenantCreateResponse.setId(tenant.getId());
                tenantCreateResponse.setMsg("Successfully persisted teannt in db.");
                return tenantCreateResponse;
            }
        } else {
            throw new InvalidInput("Name must not be null or empty.");
        }
    }

    @Override
    public Tenant getTenant(String id) throws Exception {
        Tenant tenant = null;
        if (!StringUtils.isNullOrEmpty(id)) {
            tenant = tenantRepository.findTenantById(id);
            if (!ObjectUtils.isEmpty(tenant)) {
                return tenant;
            } else {
                throw new TenantNotFoundException("Tenant details does not exit with id : " + id);
            }
        } else {
            throw new InvalidInput("Id must not be null or empty");
        }
    }


    @Override
    public List<Tenant> getAllTenants() {
        return tenantRepository.findTenantByIsActive(true);
    }

    @Override
    public void deleteTenant(String id) throws Exception {
        Tenant tenant = getTenant(id);
        tenant.setActive(false);
        tenantRepository.save(tenant);
        return;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void updateTenant(TenantUpdateRequest tenantUpdateRequest, String id) throws Exception {
        Tenant tenant = getTenant(id);
        JSONObject tenantFromDB =
                        (JSONObject) new JSONParser()
                                        .parse(new ObjectMapper().writeValueAsString(tenant));
        JSONObject tenantFromPayload = (JSONObject) new JSONParser()
                        .parse(new ObjectMapper().writeValueAsString(tenantUpdateRequest));
        for (Object obj : tenantFromPayload.keySet()) {
            String param = (String) obj;
            tenantFromDB.put(param, tenantFromPayload.get(param));
        }
        tenantRepository.save(
                        new ObjectMapper().readValue(tenantFromDB.toJSONString(), Tenant.class));
        return;
    }
}
