package in.lms.sinchan.util;


import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CustomDateAndTimeSerialize extends JsonSerializer<Date> {
    private SimpleDateFormat dateFormat = new SimpleDateFormat(
                    "yyyy-MM-dd HH:mm:ss");


    @Override
    public void serialize(Date value, JsonGenerator gen, SerializerProvider serializers)
                    throws IOException {
        gen.writeString(dateFormat.format(value));

    }
}
