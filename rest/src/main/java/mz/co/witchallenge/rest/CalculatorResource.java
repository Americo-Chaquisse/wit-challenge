package mz.co.witchallenge.rest;

import mz.co.witchallenge.exception.ResponseWaitException;
import mz.co.witchallenge.service.RestQueueService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

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
    public DeferredResult<Map<String, Object>> sum(@PathVariable("operation") String operation,
                                                   @RequestParam BigDecimal a, @RequestParam BigDecimal b) {

        if (!validOperations.contains(operation)) {
            throw new IllegalArgumentException(String.format("Invalid operation: %s, Allowed operations are: %s",
                    operation, validOperations));
        }

        return asyncResponse(operation, a, b);
    }

    private DeferredResult<Map<String, Object>> asyncResponse(String operation, BigDecimal a, BigDecimal b) {
        DeferredResult<Map<String, Object>> deferredResult = new DeferredResult<>();
        String uuid = sendRequest(operation, a, b);

        ForkJoinPool.commonPool().submit(() -> {
            AtomicBoolean deferred = new AtomicBoolean(false);

            await().atMost(waitTime, TimeUnit.SECONDS).until(() -> {
                Map<String, Object> responseMap = restQueueService.getByUuid(uuid);
                if (responseMap == null) return false;

                deferredResult.setResult(composeResult(responseMap));
                deferred.set(true);
                return true;
            });

            if (!deferred.get()) {
                throw new ResponseWaitException(String.format("Timeout waiting for calc response. uuid: %s", uuid));
            }
        });

        deferredResult.onError((Throwable t) -> deferredResult.setErrorResult(composeError()));
        return deferredResult;
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
            LOG.info("Composing result to be delivery of: {}", map);
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
