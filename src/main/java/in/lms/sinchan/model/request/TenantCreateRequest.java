package in.lms.sinchan.model.request;

import javax.validation.constraints.NotEmpty;
import org.springframework.stereotype.Component;

@Component
@lombok.Data
public class TenantCreateRequest {
    @NotEmpty
    private String name;
    private String description;
    private String organizationName;
    private String registratioNumber;
}
