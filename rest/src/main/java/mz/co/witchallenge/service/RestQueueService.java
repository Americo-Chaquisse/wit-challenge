package mz.co.witchallenge.service;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.UUID.randomUUID;

@Service
public class RestQueueService {

    @Value("${rabbitmq.in.queue.name}")
    private String inQueueName;

    private final RabbitTemplate rabbitTemplate;
    private final Map<String, Map<String, Object>> controlMap = new ConcurrentHashMap<>();

    @Autowired
    public RestQueueService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @RabbitListener(queues = {"${rabbitmq.out.queue.name}"})
    public void receive(@Payload Map<String, Object> dataMap) {
        if (dataMap.containsKey("uuid")) {
            controlMap.put((String) dataMap.get("uuid"), dataMap);
        }
    }

    public String send(Map<String, Object> dataMap) {
        String uuid = randomUUID().toString();
        dataMap.put("uuid", uuid);
        rabbitTemplate.convertAndSend(inQueueName, dataMap);
        return uuid;
    }

    public Map<String, Object> getByUuid(String uuid) {
        return controlMap.get(uuid);
    }

}
