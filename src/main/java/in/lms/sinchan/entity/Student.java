package in.lms.sinchan.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import javax.persistence.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import com.fasterxml.jackson.annotation.JsonInclude;
import in.lms.sinchan.model.Address;
import lombok.ToString;

@Document(collection = "student")
@lombok.Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@ToString
public class Student {

    @Id
    private String id;
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
    private List<BIRD> libraryDetails = new LinkedList<>();
    private String parentTenant;
    private String profileImageUrl;
    private boolean isEligibleToIssueBook;
    private List<String> mostAwatedBooks = new ArrayList<>();

}
