package in.lms.sinchan.model.response;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.annotation.JsonInclude;

@Component
@JsonInclude(JsonInclude.Include.NON_NULL)
@lombok.Data
public class StudentUpdateReponse {
    private List<String> msg = new ArrayList<String>();
}
