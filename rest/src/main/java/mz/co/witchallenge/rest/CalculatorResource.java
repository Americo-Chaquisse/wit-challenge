package mz.co.witchallenge.rest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

@RestController
public class CalculatorResource {

    @GetMapping
    public BigDecimal sum(BigDecimal a, BigDecimal b) {
        validate(a, b);
        return a.add(b);
    }

    @GetMapping
    public BigDecimal subtract(BigDecimal a, BigDecimal b) {
        validate(a, b);
        return a.subtract(b);
    }

    @GetMapping
    public BigDecimal multiply(BigDecimal a, BigDecimal b) {
        validate(a, b);
        return a.multiply(b);
    }

    @GetMapping
    public BigDecimal divide(BigDecimal a, BigDecimal b) {
        validate(a, b);
        if (b.compareTo(BigDecimal.ZERO) == 0) throw new IllegalArgumentException("'b' cannot be zero");
        return a.divide(b, RoundingMode.DOWN);
    }

    private Map<R>

    private void validate(BigDecimal a, BigDecimal b) {
        if (a == null) throw new IllegalArgumentException("'a' is required");
        if (b == null) throw new IllegalArgumentException("'b' is required");
    }

}
