package in.lms.sinchan.model.request;

import java.util.Date;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.annotation.JsonInclude;
import in.lms.sinchan.model.response.LibrarianCreateResponse;

@Component
@lombok.Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LibrarianCreateRequest {
    private String firstName;
    private String lastName;
    private Date dateOfJoining;
    private String email;
    private String phone;
    /*
     * Name of the role ex. Manager, Clerk etc
     */
    private String role;
}
