package mz.co.witchallenge.calc;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class CalculatorService {

    public BigDecimal sum(BigDecimal a, BigDecimal b) {
        validate(a, b);
        return a.add(b);
    }

    public BigDecimal subtract(BigDecimal a, BigDecimal b) {
        validate(a, b);
        return a.subtract(b);
    }

    public BigDecimal multiply(BigDecimal a, BigDecimal b) {
        validate(a, b);
        return a.multiply(b);
    }

    public BigDecimal divide(BigDecimal a, BigDecimal b) {
        validate(a, b);
        if (b.compareTo(BigDecimal.ZERO) == 0) throw new IllegalArgumentException("'b' cannot be zero");
        return a.divide(b, RoundingMode.DOWN);
    }

    private void validate(BigDecimal a, BigDecimal b) {
        if (a == null) throw new IllegalArgumentException("'a' is required");
        if (b == null) throw new IllegalArgumentException("'b' is required");
    }

}
