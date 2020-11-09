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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import in.lms.sinchan.entity.Tenant;
import in.lms.sinchan.exception.InvalidInput;
import in.lms.sinchan.exception.TenantAlreadyExistException;
import in.lms.sinchan.exception.TenantNotFoundException;
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
        try {
            TenantCreateResponse tenantCreateResponse =
                            tenantService.persistTenantInDB(teanntCreateRequest);
            return ResponseEntity.status(HttpStatus.CREATED)
                            .body(new ModelMap().addAttribute("response", tenantCreateResponse));
        } catch (final TenantAlreadyExistException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                            .body(new ModelMap().addAttribute("msg", ex.getMessage()));
        } catch (final InvalidInput ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body(new ModelMap().addAttribute("msg", ex.getMessage()));
        }
    }

    @GetMapping(value = "/get/{id}")
    public ResponseEntity<?> getTenantById(@PathVariable(value = "id", required = true) String id)
                    throws Exception {
        try {
            Tenant tenant = tenantService.getTenant(id);
            return ResponseEntity.status(HttpStatus.OK)
                            .body(new ModelMap().addAttribute("response", tenant));
        } catch (final TenantNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(new ModelMap().addAttribute("msg", ex.getMessage()));
        } catch (final InvalidInput ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body(new ModelMap().addAttribute("msg", ex.getMessage()));
        }
    }

    @GetMapping(value = "/get")
    public ResponseEntity<?> getAllTenants() throws Exception {
        try {
            List<Tenant> listOfTenant = tenantService.getAllTenants();
            return ResponseEntity.status(HttpStatus.OK)
                            .body(new ModelMap().addAttribute("response", listOfTenant));
        } catch (final TenantNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body(new ModelMap().addAttribute("msg", ex.getMessage()));
        } catch (final InvalidInput ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body(new ModelMap().addAttribute("msg", ex.getMessage()));
        }
    }

    @DeleteMapping(value = "/delete/{id}")
    public ResponseEntity<?> deleteTenant(
                    @PathVariable(value = "id", required = true) String id) throws Exception {
        try {
            tenantService.deleteTenant(id);
            return ResponseEntity.status(HttpStatus.OK)
                            .body(new ModelMap().addAttribute("msg", "Successfully delete"));
        } catch (final TenantNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body(new ModelMap().addAttribute("msg", ex.getMessage()));
        } catch (final InvalidInput ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body(new ModelMap().addAttribute("msg", ex.getMessage()));
        }
    }

    @PutMapping(value = "/update/{id}")
    public ResponseEntity<?> updateTenant(@RequestBody TenantUpdateRequest tenantUpdateRequest,
                    @PathVariable(value = "id", required = true) String id) throws Exception {
        try {
            tenantService.updateTenant(tenantUpdateRequest, id);
            return ResponseEntity.status(HttpStatus.OK)
                            .body(new ModelMap().addAttribute("msg", "Successfully updated."));
        } catch (final TenantNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body(new ModelMap().addAttribute("msg", ex.getMessage()));
        } catch (final InvalidInput ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body(new ModelMap().addAttribute("msg", ex.getMessage()));
        }
    }

}
