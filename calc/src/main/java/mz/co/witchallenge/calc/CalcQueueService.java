package mz.co.witchallenge.calc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;

@Service
public class CalcQueueService {

    private static final Logger LOG = LoggerFactory.getLogger(CalcQueueService.class);
    private static final String MDC_UUID_KEY = "UUID";

    @Value("${rabbitmq.out.queue.name}")
    private String outQueueName;

    private RabbitTemplate rabbitTemplate;

    private CalculatorService calculatorService;

    @Autowired
    public CalcQueueService(RabbitTemplate rabbitTemplate, CalculatorService calculatorService) {
        this.rabbitTemplate = rabbitTemplate;
        this.calculatorService = calculatorService;
    }

    @RabbitListener(queues = {"${rabbitmq.in.queue.name}"})
    public void receive(@Payload Map<String, Object> dataMap) {
        if (!(dataMap.get(MDC_UUID_KEY) instanceof String))
            throw new IllegalArgumentException(String.format("%s is required", MDC_UUID_KEY));

        MDC.put(MDC_UUID_KEY, (String) dataMap.get(MDC_UUID_KEY));
        if (LOG.isInfoEnabled()) {
            LOG.info("Received calc request of: {}", dataMap);
        }

        if (!dataMap.containsKey("operation")) throw new IllegalArgumentException("operation is required");
        if (!(dataMap.get("a") instanceof BigDecimal)) throw new IllegalArgumentException("'a' is required");
        if (!(dataMap.get("b") instanceof BigDecimal)) throw new IllegalArgumentException("'b' is required");

        BigDecimal a = (BigDecimal) dataMap.get("a");
        BigDecimal b = (BigDecimal) dataMap.get("b");
        BigDecimal result = null;

        switch (String.valueOf(dataMap.get("operation"))) {
            case "sum":
                result = calculatorService.sum(a, b);
                break;
            case "subtract":
                result = calculatorService.subtract(a, b);
                break;
            case "multiply":
                result = calculatorService.multiply(a, b);
                break;
            case "divide":
                result = calculatorService.divide(a, b);
                break;
        }
        if (result != null) {
            dataMap.put("result", result);
            send(dataMap);
        }
    }

    public void send(Map<String, Object> dataMap) {
        if (LOG.isInfoEnabled()) {
            LOG.info("Sending response of: {}", dataMap);
        }
        rabbitTemplate.convertAndSend(outQueueName, dataMap);
    }

}