package in.lms.sinchan.entity;

import org.springframework.data.mongodb.core.mapping.Document;
import com.fasterxml.jackson.annotation.JsonInclude;

@Document(collection = "tenant")
@lombok.Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Tenant {
    private String id;
    private String name;
    private String description;
    private boolean isActive;
    private String organizationName;
    private String registratioNumber;
}
