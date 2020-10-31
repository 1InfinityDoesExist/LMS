package in.lms.sinchan.model;

import java.util.Date;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.annotation.JsonInclude;

@Component
@lombok.Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LibraryDetails {
    private String bookId;
    private String bookName;
    private Double totalFinePaid;
    private Integer noOfDayForLateReturn;
    private Date issuedDate;
    private Date returnDate;
    private Double findDue;


}
