package mz.co.witchallenge.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "mz.co.witchallenge")
public class WitChallengeApplication {

    public static void main(String[] args) {
        SpringApplication.run(WitChallengeApplication.class, args);
    }

}
