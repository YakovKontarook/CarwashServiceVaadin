package com.kontarook.carwashservicevaadin.config;

import CarWashSwaggerApi.api.invoker.ApiClient;
import CarWashSwaggerApi.api.model.AuthenticationRequest;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.web.client.RestTemplate;

@Configuration
@ComponentScan(
        basePackages = {
                "CarWashSwaggerApi.api"
        }, excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = ApiClient.class)
})
public class Config {
    @Bean("CarWashSwaggerApi.api.invoker.ApiClient")
    ApiClient apiClient(RestTemplate restTemplate) {
        ApiClient apiClient = new ApiClient(restTemplate);
        apiClient.setBasePath("http://localhost:8080");
        return apiClient;
    }

    @Bean
    public AuthenticationRequest authenticationRequest() {
        return new AuthenticationRequest();
    }

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }
}
