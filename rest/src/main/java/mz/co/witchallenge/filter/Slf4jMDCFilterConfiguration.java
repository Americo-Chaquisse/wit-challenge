package mz.co.witchallenge.filter;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "mz.co.witchallenge.slf4jfilter")
public class Slf4jMDCFilterConfiguration {

    public static final String RESPONSE_HEADER = "Request-Unique-id";
    public static final String MDC_UUID_KEY = "UUID";

    private final String requestHeader = null;

    @Bean
    public FilterRegistrationBean servletRegistrationBean() {
        final FilterRegistrationBean registrationBean = new FilterRegistrationBean();
        registrationBean.setFilter(new Slf4jMDCFilter(RESPONSE_HEADER, MDC_UUID_KEY, requestHeader));
        return registrationBean;
    }

}
