package mz.co.witchallenge.calc;

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
        if (!dataMap.containsKey("operation")) return;
        if (!(dataMap.get("a") instanceof BigDecimal)) return;
        if (!(dataMap.get("b") instanceof BigDecimal)) return;

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
        rabbitTemplate.convertAndSend(outQueueName, dataMap);
    }

}