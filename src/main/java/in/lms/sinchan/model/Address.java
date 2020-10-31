package in.lms.sinchan.model;

import org.springframework.stereotype.Component;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Component
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Address {
    private String country;
    private String state;
    private String city;
    private String zipCode;
    private String addressLine1;
    private String addressLine2;
    private Double longitude;
    private Double latitude;
}
