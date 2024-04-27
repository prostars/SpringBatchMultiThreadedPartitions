package com.example.springbatch.service;

import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.Mockito.*;

import com.example.springbatch.dto.NicknameRequest;
import com.example.springbatch.dto.NicknameResponse;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

class NicknameClientServiceTest {

  @Test
  void testGenerateNicknameReturnsValidResponseWhenRequestIsValid() {
    // Given
    final RestTemplate restTemplate = mock(RestTemplate.class);
    final NicknameClientService service = new NicknameClientService(restTemplate);
    final NicknameRequest request = new NicknameRequest("User123");
    final NicknameResponse expectedResponse = new NicknameResponse("CoolUser123");
    when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(NicknameResponse.class)))
        .thenReturn(new ResponseEntity<>(expectedResponse, HttpStatus.OK));

    // When
    final NicknameResponse actualResponse = service.generateNickname(request);

    // Then
    assertNotNull(actualResponse);
    assertEquals(expectedResponse.getNickname(), actualResponse.getNickname());
  }

  @Test
  void testGenerateNicknameThrowsExceptionWhenServerIsDown() {
    // Given
    final RestTemplate restTemplate = mock(RestTemplate.class);
    final NicknameClientService service = new NicknameClientService(restTemplate);
    final NicknameRequest request = new NicknameRequest("User123");
    when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(NicknameResponse.class)))
        .thenThrow(new RuntimeException("Server is down"));

    // When & Then
    assertThrows(RuntimeException.class, () -> service.generateNickname(request));
  }

  @Test
  void testGenerateNicknameReturnsNullWhenResponseIsEmpty() {
    // Given
    final RestTemplate restTemplate = mock(RestTemplate.class);
    final NicknameClientService service = new NicknameClientService(restTemplate);
    final NicknameRequest request = new NicknameRequest("User123");
    when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(NicknameResponse.class)))
        .thenReturn(ResponseEntity.ok(null));

    // When
    final NicknameResponse response = service.generateNickname(request);

    // Then
    assertNull(response);
  }

  @Test
  void testGenerateNicknameUsesCorrectUrlAndMethod() {
    // Given
    final RestTemplate restTemplate = mock(RestTemplate.class);
    final NicknameClientService service = new NicknameClientService(restTemplate);
    final NicknameRequest request = new NicknameRequest("User123");
    when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(NicknameResponse.class)))
        .thenAnswer(invocation -> {
          HttpEntity<NicknameRequest> entity = invocation.getArgument(1);
          assertEquals("http://localhost:8080/generateNickname", invocation.getArgument(0));
          assertNotNull(entity);
          return new ResponseEntity<>(new NicknameResponse("CoolUser123"), HttpStatus.OK);
        });

    // When
    service.generateNickname(request);

    // Then
    verify(restTemplate).postForEntity(eq("http://localhost:8080/generateNickname"), any(HttpEntity.class), eq(NicknameResponse.class));
  }

  @Test
  void testGenerateNicknameHandlesTimeoutProperly() {
    // Given
    final RestTemplate restTemplate = mock(RestTemplate.class);
    final NicknameClientService service = new NicknameClientService(restTemplate);
    final NicknameRequest request = new NicknameRequest("User123");
    when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(NicknameResponse.class)))
        .thenThrow(new ResourceAccessException("Timeout occurred"));

    // When & Then
    assertThrows(ResourceAccessException.class, () -> service.generateNickname(request));
  }  
}
