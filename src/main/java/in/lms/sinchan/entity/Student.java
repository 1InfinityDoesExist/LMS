package in.lms.sinchan.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.springframework.data.mongodb.core.mapping.Document;
import com.fasterxml.jackson.annotation.JsonInclude;
import in.lms.sinchan.model.Address;
import in.lms.sinchan.model.LibraryDetails;

@Document(collection = "student")
@lombok.Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Student {

    private String studentId;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private Address address;
    private boolean isEmailVerified;
    private boolean isMobileVerified;
    private String role;
    private boolean isActive;
    private Date dob;
    private List<LibraryDetails> libraryDetails = new ArrayList<>();
    private String parentTenant;
    private String profileImageUrl;
    private boolean isEligibleToIssueBook;

}
