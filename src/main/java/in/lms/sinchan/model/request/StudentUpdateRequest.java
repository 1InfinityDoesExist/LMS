package in.lms.sinchan.model.request;

import java.util.Date;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.annotation.JsonInclude;
import in.lms.sinchan.model.Address;

@Component
@lombok.Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StudentUpdateRequest {

    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private Address address;
    private Date dob;
    private String profileImageUrl;
}
