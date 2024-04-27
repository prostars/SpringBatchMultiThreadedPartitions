package com.example.springbatch.service;

import com.example.springbatch.dto.NicknameRequest;
import com.example.springbatch.dto.NicknameResponse;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class NicknameClientService {

  private final RestTemplate restTemplate;

  public NicknameClientService(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  public NicknameResponse generateNickname(NicknameRequest request) {
    final String baseUrl = "http://localhost:8080";
    final String url = baseUrl + "/generateNickname";
    final HttpEntity<NicknameRequest> requestEntity = new HttpEntity<>(request);
    final ResponseEntity<NicknameResponse> response = restTemplate.postForEntity(url, requestEntity, NicknameResponse.class);
    return response.getBody();
  }  
}
