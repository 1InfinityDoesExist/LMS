package in.lms.sinchan.model.request;

import java.io.Serializable;
import java.util.Date;
import org.springframework.stereotype.Component;

@Component
@lombok.Data
public class BIRDRequest implements Serializable {
    private String bookId;
    /*
     * Student Id
     */
    private String issuedBy;
    private Date issuedDate;
    /*
     * Librarian who issues the book
     */
    private String issuerId;
}
