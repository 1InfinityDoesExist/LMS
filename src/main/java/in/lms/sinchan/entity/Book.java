package in.lms.sinchan.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import in.lms.sinchan.util.CustomDateAndTimeDeserialize;
import in.lms.sinchan.util.CustomDateAndTimeSerialize;
import lombok.ToString;


@SuppressWarnings("serial")
@Document(collection = "book")
@lombok.Data
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
// JdkSerializationRedisSerializer
public class Book implements Serializable {

    @Id
    private String id;
    private String bookName;
    private boolean isIssued;
    private boolean isAvailable;
    @JsonDeserialize(using = CustomDateAndTimeDeserialize.class)
    @JsonSerialize(using = CustomDateAndTimeSerialize.class)
    private Date availableOn;
    private Double finePerDay;
    private Double version;
    private String author;
    private String Section;
    private List<String> gener;
    private boolean isActive;
}
