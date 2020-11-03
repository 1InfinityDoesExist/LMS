package in.lms.sinchan.model.request;

import java.util.List;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.annotation.JsonInclude;
import in.lms.sinchan.model.BIRD;

@Component
@lombok.Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BookUpdateRequest {

    private String bookName;
    private Double finePerDay;
    private Double version;
    private String author;
    private String Section;
    private List<String> gener;
    private List<BIRD> bid;
}
