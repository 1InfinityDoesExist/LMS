package in.lms.sinchan.model.response;

import org.springframework.stereotype.Component;
import com.fasterxml.jackson.annotation.JsonInclude;

@Component
@lombok.Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LibrarianCreateResponse {

    private String id;
    private String msg;
}
