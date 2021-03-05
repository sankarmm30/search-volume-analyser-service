package com.sellics.analytics.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 *
 * Configuration for RestTemplate
 */
@Configuration
public class RestTemplateConfiguration {

    private static final Integer READ_TIMEOUT = 5000;
    private static final Integer CONNECTION_TIMEOUT = 5000;

    @Bean("restTemplate")
    public RestTemplate restTemplate() {

        return new RestTemplate(clientHttpRequestFactory());
    }

    private ClientHttpRequestFactory clientHttpRequestFactory() {

        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setReadTimeout(READ_TIMEOUT);
        factory.setConnectTimeout(CONNECTION_TIMEOUT);

        return factory;
    }
}
