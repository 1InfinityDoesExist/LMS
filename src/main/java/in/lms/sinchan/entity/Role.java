package in.lms.sinchan.entity;

import org.springframework.data.mongodb.core.mapping.Document;
import com.fasterxml.jackson.annotation.JsonInclude;

@Document(collection = "role")
@lombok.Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Role {
    private String id;
    private String name;
    private String description;
    private String parentTenant;
    private boolean isActive;
}
