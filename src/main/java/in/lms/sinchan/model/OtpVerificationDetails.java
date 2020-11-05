package in.lms.sinchan.model;

import java.io.Serializable;
import org.springframework.stereotype.Component;

@Component
@lombok.Data
public class OtpVerificationDetails implements Serializable {

    private Integer emailOtp;
    private Integer mobileOtp;
    private String email;
    private String mobile;
}
