package com.example.springbatch.config;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class WebClientConfig {

  @Bean
  public RestTemplate restTemplate(RestTemplateBuilder builder) {
    PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
    connectionManager.setMaxTotal(100);
    connectionManager.setDefaultMaxPerRoute(100);

    CloseableHttpClient httpClient = HttpClients.custom()
        .setConnectionManager(connectionManager)
        .build();

    ClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(
        httpClient);

    return builder.requestFactory(() -> requestFactory).build();
  }
}
