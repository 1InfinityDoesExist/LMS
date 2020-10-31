package in.lms.sinchan.model.request;

import org.springframework.stereotype.Component;

@Component
@lombok.Data
public class RoleUpdateRequest {
    private String description;
    private String parentTenant;
}
