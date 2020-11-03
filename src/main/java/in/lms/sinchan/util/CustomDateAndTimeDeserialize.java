package in.lms.sinchan.util;


import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Slf4j
public class CustomDateAndTimeDeserialize extends JsonDeserializer<Date> {
    private SimpleDateFormat dateFormat = new SimpleDateFormat(
                    "yyyy-MM-dd HH:mm:ss");

    @Override
    public Date deserialize(JsonParser paramJsonParser,
                    DeserializationContext paramDeserializationContext)
                    throws IOException, JsonProcessingException {
        String str = paramJsonParser.getText().trim();
        try {
            Date responseDate = dateFormat.parse(str);
            log.info(":::::responseDate : {}", responseDate);
            return responseDate;
        } catch (ParseException e) {
            log.error(e.getMessage());
            e.printStackTrace();
        }
        return paramDeserializationContext.parseDate(str);
    }
}
