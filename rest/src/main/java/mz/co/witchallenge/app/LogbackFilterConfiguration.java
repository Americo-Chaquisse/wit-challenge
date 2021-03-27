package mz.co.witchallenge.app;

import ch.qos.logback.access.servlet.TeeFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;

import java.util.Collections;

public class LogbackFilterConfiguration {

    @Bean
    public FilterRegistrationBean requestResponseFilter() {

        final FilterRegistrationBean filterRegBean = new FilterRegistrationBean();
        filterRegBean.setFilter(new TeeFilter());
        filterRegBean.setUrlPatterns(Collections.singleton("/"));
        filterRegBean.setAsyncSupported(Boolean.TRUE);
        return filterRegBean;
    }

}
