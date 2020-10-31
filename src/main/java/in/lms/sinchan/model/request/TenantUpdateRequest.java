package in.lms.sinchan.model.request;

import org.springframework.stereotype.Component;

@Component
@lombok.Data
public class TenantUpdateRequest {
    private String name;
    private String description;
    private String organizationName;
    private String registratioNumber;

}
