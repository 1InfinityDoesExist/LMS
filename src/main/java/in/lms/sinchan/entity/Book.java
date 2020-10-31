package in.lms.sinchan.entity;

import java.util.Date;
import java.util.List;
import org.springframework.data.mongodb.core.mapping.Document;
import com.fasterxml.jackson.annotation.JsonInclude;
import in.lms.sinchan.model.BIRD;


@Document(collection = "book")
@lombok.Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Book {
    private String bookId;
    private String bookName;
    private boolean isIssued;
    private boolean isAvailable;
    private Date availableOn;
    private Double finePerDay;
    private Double version;
    private String author;
    private String Section;
    private List<String> gener;
    private List<BIRD> bid;
}
