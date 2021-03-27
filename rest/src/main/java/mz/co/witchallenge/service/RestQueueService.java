package mz.co.witchallenge.service;

import mz.co.witchallenge.filter.Slf4jMDCFilterConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RestQueueService {

    private static final Logger LOG = LoggerFactory.getLogger(RestQueueService.class);

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
        MDC.put(Slf4jMDCFilterConfiguration.MDC_UUID_KEY, (String) dataMap.get(Slf4jMDCFilterConfiguration.MDC_UUID_KEY));
        if (LOG.isInfoEnabled()) {
            LOG.info("Received calc response of: {}", dataMap);
        }

        if (dataMap.containsKey(Slf4jMDCFilterConfiguration.MDC_UUID_KEY)) {
            controlMap.put((String) dataMap.get(Slf4jMDCFilterConfiguration.MDC_UUID_KEY), dataMap);
        }
    }

    public String send(Map<String, Object> dataMap) {
        dataMap.put(Slf4jMDCFilterConfiguration.MDC_UUID_KEY, MDC.get(Slf4jMDCFilterConfiguration.MDC_UUID_KEY));
        rabbitTemplate.convertAndSend(inQueueName, dataMap);
        return (String) dataMap.get(Slf4jMDCFilterConfiguration.MDC_UUID_KEY);
    }

    public Map<String, Object> getByUuid(String uuid) {
        return controlMap.get(uuid);
    }

}
