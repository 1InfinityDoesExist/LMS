package in.lms.sinchan.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import in.lms.sinchan.entity.Tenant;
import in.lms.sinchan.model.request.TenantCreateRequest;
import in.lms.sinchan.model.request.TenantUpdateRequest;
import in.lms.sinchan.model.response.TenantCreateResponse;
import in.lms.sinchan.service.TenantService;

@RestController("tenantController")
@RequestMapping(value = "/tenant")
public class TenantController {

    @Autowired
    private TenantService tenantService;

    @PostMapping(value = "/create", consumes = "application/json", produces = "application/json")
    public ResponseEntity<?> persistTenantIntoDB(
                    @RequestBody TenantCreateRequest teanntCreateRequest) throws Exception {
        TenantCreateResponse tenantCreateResponse =
                        tenantService.persistTenantInDB(teanntCreateRequest);
        return ResponseEntity.status(HttpStatus.CREATED)
                        .body(new ModelMap().addAttribute("response", tenantCreateResponse));
    }

    @GetMapping(value = "/get/{id}")
    public ResponseEntity<?> getTenantById(@PathVariable(value = "id", required = true) String id)
                    throws Exception {
        Tenant tenant = tenantService.getTenant(id);
        return ResponseEntity.status(HttpStatus.OK)
                        .body(new ModelMap().addAttribute("response", tenant));
    }

    @GetMapping(value = "/get")
    public ResponseEntity<?> getAllTenants() throws Exception {
        List<Tenant> listOfTenant = tenantService.getAllTenants();
        return ResponseEntity.status(HttpStatus.OK)
                        .body(new ModelMap().addAttribute("response", listOfTenant));
    }

    @DeleteMapping(value = "/delete/{id}")
    public ResponseEntity<?> deleteTenant(
                    @PathVariable(value = "id", required = true) String id) throws Exception {
        tenantService.deleteTenant(id);
        return ResponseEntity.status(HttpStatus.OK)
                        .body(new ModelMap().addAttribute("msg", "Successfully delete"));
    }

    public ResponseEntity<?> updateTenant(@RequestBody TenantUpdateRequest tenantUpdateRequest,
                    String id) throws Exception {
        tenantService.updateTenant(tenantUpdateRequest, id);
        return ResponseEntity.status(HttpStatus.OK)
                        .body(new ModelMap().addAttribute("msg", "Successfully updated."));
    }

}
