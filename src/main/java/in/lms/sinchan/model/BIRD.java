package in.lms.sinchan.model;

import java.util.Date;
import org.springframework.data.mongodb.core.mapping.Document;
import com.fasterxml.jackson.annotation.JsonInclude;

@Document
@lombok.Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BIRD {
    private String issuedBy;
    private Date issuedDate;
    private Date issuedExpiryDate;
    private Date returnedOn;
    private Double fineAmount;
    private Integer lateReturnDays;

}
