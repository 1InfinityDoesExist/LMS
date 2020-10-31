package in.lms.sinchan.model.response;

import org.springframework.data.mongodb.core.mapping.Document;

@Document
@lombok.Data
public class RoleCreateResponse {
    private String id;
    private String msg;
}
