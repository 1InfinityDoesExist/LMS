package in.lms.sinchan.util;

import java.util.Random;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class OTPGeneration {

    @Value("${otp.start.range}")
    public int otpStartRange;

    @Value("${otp.end.range}")
    public int otpEndRange;

    /* Length of OTP is Always 6 */
    public int generateOTP() {
        return otpStartRange + new Random().nextInt(otpEndRange);
    }

}
