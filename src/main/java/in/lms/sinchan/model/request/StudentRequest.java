package in.lms.sinchan.model.request;

import java.util.Date;
import javax.validation.constraints.NotEmpty;
import com.fasterxml.jackson.annotation.JsonInclude;
import in.lms.sinchan.model.Address;

@lombok.Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StudentRequest {

    private String firstName;
    private String lastName;
    @NotEmpty
    private String email;
    @NotEmpty
    private String phone;
    private Address address;
    private String role;
    private Date dob;
    @NotEmpty
    private String parentTenant;
    private String profileImageUrl;
    private boolean isEligibleToIssueBook;
}
