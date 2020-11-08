package in.lms.sinchan.lmsProducer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class LmsProducer {

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    public LmsProducer() {
        log.info("::::::LmsProducer Constructor :::::");
    }

    public ListenableFuture<SendResult<String, Object>> produce(String topic, String msg) {
        log.info(":::::Sending file from kafka producer::::");
        ListenableFuture<SendResult<String, Object>> listenableFuture =
                        kafkaTemplate.send(topic, msg);
        log.info("::::Producer send the message to topic :  {}", topic);
        return listenableFuture;
    }
}
