package in.lms.sinchan.lmsConsumer;

import java.util.List;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.ui.ModelMap;
import in.lms.sinchan.email.EmailService;
import in.lms.sinchan.entity.Student;
import in.lms.sinchan.repository.StudentRepository;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class LmsConsumer {

    public LmsConsumer() {
        log.info(":::::LmsConsumer Constructor::::");
    }

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private EmailService emailService;

    @KafkaListener(topics = "bookTopic",
                    containerFactory = "concurrentKafkaListenerContainerFactory")
    public void consumes(ConsumerRecord<String, String> consumerRecord) throws ParseException {
        log.info("-----Inside LmsConsumer Class, consume method----");
        JSONObject jsonObject =
                        (JSONObject) new JSONParser().parse(consumerRecord.value().toString());
        log.info(":::::consumerValue {}", jsonObject);
        String bookId = (String) jsonObject.get("id");
        log.info(":::::bookId :  {}", bookId);
        List<Student> listOfStudent = studentRepository.findStudentByBookId(bookId);
        log.info(":::::listOfStudent :{}", listOfStudent);
        listOfStudent.stream().forEach(student -> {
            emailService.sendMail(new ModelMap().addAttribute("to", student.getEmail())
                            .addAttribute("subject", "Your most awaited book available in LMS")
                            .addAttribute("body", "Book Name :" + jsonObject.get("bookName")
                                            + " version : " + jsonObject.get("version")
                                            + " is available now in LMS. Hurry there are only few available."));
        });

    }

}
