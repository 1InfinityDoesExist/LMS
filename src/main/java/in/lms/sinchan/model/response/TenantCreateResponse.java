package in.lms.sinchan.model.response;

import org.springframework.stereotype.Component;

@Component
@lombok.Data
public class TenantCreateResponse {
    private String id;
    private String msg;
}
