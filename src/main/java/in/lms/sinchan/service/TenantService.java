package in.lms.sinchan.service;

import java.util.List;
import org.springframework.stereotype.Service;
import in.lms.sinchan.entity.Tenant;
import in.lms.sinchan.model.request.TenantCreateRequest;
import in.lms.sinchan.model.request.TenantUpdateRequest;
import in.lms.sinchan.model.response.TenantCreateResponse;

@Service
public interface TenantService {

    public TenantCreateResponse persistTenantInDB(TenantCreateRequest tenantCreateRequest)
                    throws Exception;

    public Tenant getTenant(String id) throws Exception;

    public List<Tenant> getAllTenants() throws Exception;

    public void deleteTenant(String id) throws Exception;

    public void updateTenant(TenantUpdateRequest tenantUpdateRequest, String id) throws Exception;

}
