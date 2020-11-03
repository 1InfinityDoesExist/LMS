package in.lms.sinchan.model.request;

import org.springframework.stereotype.Component;
import com.fasterxml.jackson.annotation.JsonInclude;

@Component
@lombok.Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LibrarianUpdateRequest {
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String role;
}
