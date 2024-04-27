package com.example.springbatch.job.migration;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

import com.example.springbatch.dto.NicknameRequest;
import com.example.springbatch.dto.NicknameResponse;
import com.example.springbatch.entity.UserNameEntity;
import com.example.springbatch.entity.UserNameWithNickEntity;
import com.example.springbatch.service.NicknameClientService;
import java.security.InvalidParameterException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class TransformationItemProcessorTest {

  private TransformationItemProcessor processor;
  @Mock
  private NicknameClientService nicknameClientService;

  @BeforeEach
  void setUp() {
    processor = new TransformationItemProcessor(nicknameClientService, 10);
  }

  @Test
  void testProcessWithNickname() {
    // given
    final UserNameEntity userNameEntity = new UserNameEntity("user123");
    final NicknameResponse response = new NicknameResponse("CoolUser123");
    when(nicknameClientService.generateNickname(any(NicknameRequest.class))).thenReturn(response);

    // when
    final CompletableFuture<UserNameWithNickEntity> result = processor.process(userNameEntity);

    // then
    assertNotNull(result);
    assertEquals("CoolUser123", result.join().getNick(), "The nickname should be 'CoolUser123'");
  }

  @Test
  void testProcessWithNullInput() {
    // given
    final UserNameEntity userNameEntity = null;

    // when & then
    assertThrows(InvalidParameterException.class, () -> processor.process(userNameEntity));
  }

  @Test
  void testProcessWhenExternalServiceThrowsException() {
    // given
    final UserNameEntity userNameEntity = new UserNameEntity("user123");
    when(nicknameClientService.generateNickname(any(NicknameRequest.class)))
        .thenThrow(new RuntimeException("Service unavailable"));

    // when
    final CompletableFuture<UserNameWithNickEntity> result = processor.process(userNameEntity);

    // then
    assertThrows(CompletionException.class, () -> result.join());
  }
}
