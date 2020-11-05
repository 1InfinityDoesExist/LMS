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
import in.lms.sinchan.entity.Role;
import in.lms.sinchan.exception.InvalidInput;
import in.lms.sinchan.exception.RoleNotFoundException;
import in.lms.sinchan.exception.TenantNotFoundException;
import in.lms.sinchan.model.request.RoleCreateRequest;
import in.lms.sinchan.model.request.RoleUpdateRequest;
import in.lms.sinchan.model.response.RoleCreateResponse;
import in.lms.sinchan.service.RoleService;

@RestController("roleController")
@RequestMapping(value = "/role")
public class RoleController {

    @Autowired
    private RoleService roleService;

    @PostMapping(value = "/create", consumes = "application/json", produces = "application/json")
    public ResponseEntity<?> persistRoleInDB(@RequestBody RoleCreateRequest roleCreateRequest)
                    throws Exception {
        try {
            RoleCreateResponse roleResponse = roleService.persistRoleInDB(roleCreateRequest);
            return ResponseEntity.status(HttpStatus.CREATED)
                            .body(new ModelMap().addAttribute("response", roleResponse));
        } catch (final TenantNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(new ModelMap().addAttribute("msg", ex.getMessage()));
        }

    }

    @GetMapping(value = "/get/{id}")
    public ResponseEntity<?> getRoleById(@PathVariable(value = "id", required = true) String id)
                    throws Exception {
        Role role;
        try {
            role = roleService.getRole(id);
            return ResponseEntity.status(HttpStatus.OK)
                            .body(new ModelMap().addAttribute("response", role));
        } catch (final RoleNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(new ModelMap().addAttribute("msg", ex.getMessage()));
        } catch (final InvalidInput ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body(new ModelMap().addAttribute("msg", ex.getMessage()));
        }
    }

    @GetMapping(value = "/getAll")
    public ResponseEntity<?> getAllRole() throws Exception {
        List<Role> listOfRole = roleService.getAllRoles();
        return ResponseEntity.status(HttpStatus.OK)
                        .body(new ModelMap().addAttribute("response", listOfRole));
    }

    @DeleteMapping(value = "/delete/{id}")
    public ResponseEntity<?> deleteRole(@PathVariable(value = "id", required = true) String id)
                    throws Exception {
        roleService.deleteRole(id);
        return ResponseEntity.status(HttpStatus.OK).body(
                        new ModelMap().addAttribute("msg",
                                        "Successfully deleted the role from db."));
    }

    @PutMapping(value = "/update/{id}")
    public ResponseEntity<?> updateRole(@RequestBody RoleUpdateRequest roleUpdateRequest,
                    @PathVariable(value = "id", required = true) String id) throws Exception {
        roleService.updateRole(roleUpdateRequest, id);
        return ResponseEntity.status(HttpStatus.OK)
                        .body(new ModelMap().addAttribute("response", "Successfully updated."));
    }
}
