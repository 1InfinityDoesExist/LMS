package in.lms.sinchan.lmsConsumer;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class LmsConsumer {

    public LmsConsumer() {
        log.info(":::::LmsConsumer Constructor::::");
    }

    @KafkaListener(topics = "books", containerFactory = "concurrentKafkaListenerContainerFactory")
    public void consumes(ConsumerRecord<String, Object> consumerRecord) {

    }

}
