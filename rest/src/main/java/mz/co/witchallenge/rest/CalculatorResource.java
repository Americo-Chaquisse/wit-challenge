package mz.co.witchallenge.rest;

import mz.co.witchallenge.service.RestQueueService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static org.awaitility.Awaitility.await;

@RestController
public class CalculatorResource {

    private static final Logger LOG = LoggerFactory.getLogger(CalculatorResource.class);

    private static final List<String> validOperations = Arrays.asList("sum", "subtract", "multiply", "divide");

    @Value("${calculator.response.wait.time}")
    private long waitTime;

    private final RestQueueService restQueueService;

    @Autowired
    public CalculatorResource(RestQueueService restQueueService) {
        this.restQueueService = restQueueService;
    }

    @GetMapping("/{operation}")
    public ResponseEntity<Map<String, Object>> sum(@PathVariable("operation") String operation,
                                                   @RequestParam BigDecimal a, @RequestParam BigDecimal b) {

        if (!validOperations.contains(operation)) {
            throw new IllegalArgumentException(String.format("Invalid operation: %s, Allowed operations are: %s",
                    operation, validOperations));
        }

        return new ResponseEntity<>(
                calculate(operation, a, b),
                HttpStatus.OK);
    }

    private Map<String, Object> calculate(String operation, BigDecimal a, BigDecimal b) {
        String uuid = sendRequest(operation, a, b);
        AtomicReference<Map<String, Object>> responseMap = new AtomicReference<>();

        await().pollInterval(5, TimeUnit.MILLISECONDS).atMost(waitTime, TimeUnit.SECONDS).until(() -> {
            responseMap.set(restQueueService.getByUuid(uuid));
            return responseMap.get() != null;
        });

        if (responseMap.get() != null) {
            return composeResult(responseMap.get());
        }
        return composeError();
    }

    private String sendRequest(String operation, BigDecimal a, BigDecimal b) {
        Map<String, Object> map = new HashMap<>();
        map.put("operation", operation);
        map.put("a", a);
        map.put("b", b);

        if (LOG.isInfoEnabled()) {
            LOG.info("Sending request for module calc: {}", map);
        }
        return restQueueService.send(map);
    }

    private Map<String, Object> composeResult(Map<String, Object> responseMap) {
        Map<String, Object> map = new HashMap<>();
        map.put("result", responseMap.get("result"));

        if (LOG.isInfoEnabled()) {
            LOG.info("Returning result: {}", map);
        }
        return map;
    }

    private Map<String, Object> composeError() {
        Map<String, Object> map = new HashMap<>();
        map.put("result", "timeout");
        LOG.error("Failed to delivery result due timeout of: {}", map);
        return map;
    }

}
