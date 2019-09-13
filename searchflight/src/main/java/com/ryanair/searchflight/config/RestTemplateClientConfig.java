package com.ryanair.searchflight.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import com.ryanair.searchflight.exception.RestTemplateErrorHandler;

@Configuration
public class RestTemplateClientConfig {
 
	@Autowired
	private static RestTemplate restTemplate;
	
    @Bean
    public static RestTemplate restTemplate() {
    	restTemplate = new RestTemplate(clientHttpRequestFactory());
        restTemplate.setErrorHandler(new RestTemplateErrorHandler());
        return restTemplate;
    }
	
    @Bean
    public static HttpComponentsClientHttpRequestFactory clientHttpRequestFactory() {
    	HttpComponentsClientHttpRequestFactory clientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory();
    	clientHttpRequestFactory.setConnectTimeout(20000);
    	clientHttpRequestFactory.setReadTimeout(20000);
        return clientHttpRequestFactory;
    }
}
