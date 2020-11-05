package in.lms.sinchan.entity;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import javax.persistence.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@lombok.Data
@Document(collection = "OTPDetails")
public class OtpDetails implements Serializable {

    @Id
    private String id;
    private Integer emailOtp;
    private Integer mobileOtp;
    private Date emailOtpExpiryDate;
    private Date mobileOtpExpiryDate;


    public Date getEmailOtpExpiryDate() {
        return emailOtpExpiryDate;
    }

    public void setEmailOtpExpiryDate(Date emailOtpExpiryDate) {
        this.emailOtpExpiryDate = emailOtpExpiryDate;
    }

    public void setEmailOtpExpiryDate(int minutes) {
        Calendar now = Calendar.getInstance();
        now.add(Calendar.MINUTE, minutes);
        this.emailOtpExpiryDate = now.getTime();
    }

    public boolean isEmailOtpExpired() {
        return new Date().after(emailOtpExpiryDate);
    }


    public Date getMobileOtpExpiryDate() {
        return mobileOtpExpiryDate;
    }

    public void setMobileOtpExpiryDate(Date mobileOtpExpiryDate) {
        this.mobileOtpExpiryDate = mobileOtpExpiryDate;
    }

    public void setMobileOtpExpiryDate(int minutes) {
        Calendar now = Calendar.getInstance();
        now.add(Calendar.MINUTE, minutes);
        this.mobileOtpExpiryDate = now.getTime();
    }

    public boolean isMobileOtpExpired() {
        return new Date().after(mobileOtpExpiryDate);
    }

}
