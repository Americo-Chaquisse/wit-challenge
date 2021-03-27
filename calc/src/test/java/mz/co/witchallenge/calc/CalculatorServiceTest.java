package mz.co.witchallenge.calc;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class CalculatorServiceTest {

    @Autowired
    private CalculatorService calculatorService;

    @Test
    void testSum() {
        assertEquals(BigDecimal.valueOf(4), calculatorService.sum(BigDecimal.valueOf(2), BigDecimal.valueOf(2)));
    }

    @Test
    void testSubtract() {
        assertEquals(BigDecimal.valueOf(3), calculatorService.subtract(BigDecimal.valueOf(5), BigDecimal.valueOf(2)));
    }

    @Test
    void testMultiply() {
        assertEquals(BigDecimal.valueOf(6), calculatorService.multiply(BigDecimal.valueOf(2), BigDecimal.valueOf(3)));
    }

    @Test
    void testDivide() {
        assertEquals(BigDecimal.valueOf(2.5), calculatorService.divide(BigDecimal.valueOf(5), BigDecimal.valueOf(2)));
    }
}