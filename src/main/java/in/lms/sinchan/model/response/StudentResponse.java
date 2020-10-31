package in.lms.sinchan.model.response;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;

@lombok.Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StudentResponse {

    private String studentId;
    private List<String> msg;
}
