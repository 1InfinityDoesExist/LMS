package in.lms.sinchan.entity;

import java.util.Calendar;
import java.util.Date;
import javax.persistence.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.ToString;

@Document
@lombok.Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@ToString
public class BIRD {
    @Id
    private String id;
    private String bookId;
    private String issuedBy;
    private Date issuedDate;
    private Date returnedOn;
    private Double fineAmount;
    private long lateReturnDays;
    private String issuerId;
    private boolean isActive;


    private Date issuedExpiryDate;

    public Date getIssuedExpiryDate() {
        return issuedExpiryDate;
    }

    public void setIssuedExpiryDate(Date issuedExpiryDate) {
        this.issuedExpiryDate = issuedExpiryDate;
    }

    public void setIssuedExpiryDate(int min) {
        Calendar now = Calendar.getInstance();
        now.add(Calendar.MINUTE, min);
        this.issuedExpiryDate = now.getTime();
    }

    public boolean isIssuedDateExpired() {
        return new Date().after(issuedExpiryDate);
    }


}
