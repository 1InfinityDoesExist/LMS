package in.lms.sinchan.entity;

import java.util.Date;
import org.springframework.data.mongodb.core.mapping.Document;
import com.fasterxml.jackson.annotation.JsonInclude;

@Document(collection = "librarian")
@lombok.Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Librarian {
    private String id;
    private String firstName;
    private String lastName;
    private Date dateOfJoining;
    private String email;
    private String phone;
    private boolean isEmailVerified;
    private boolean isPhoneVerified;
    private String role;
}
