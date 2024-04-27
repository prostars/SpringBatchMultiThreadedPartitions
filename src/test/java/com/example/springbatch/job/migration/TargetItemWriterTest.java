package com.example.springbatch.job.migration;

import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.springbatch.entity.UserNameWithNickEntity;
import com.example.springbatch.repository.target.TargetNickNameRepository;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TargetItemWriterTest {

  private TargetItemWriter targetItemWriter;
  @Mock
  private TargetNickNameRepository targetNickNameRepository;

  @BeforeEach
  void setUp() {
    targetItemWriter = new TargetItemWriter(targetNickNameRepository);
  }

  @Test
  void testWrite() {
    // Given
    final List<CompletableFuture<UserNameWithNickEntity>> futures = IntStream.range(0, 5)
        .mapToObj(i -> CompletableFuture.completedFuture(
            new UserNameWithNickEntity("Name" + i, "Nick" + i)))
        .collect(Collectors.toList());
    when(targetNickNameRepository.saveAll(anyList()))
        .thenAnswer(invocation -> invocation.getArgument(0));

    // When
    targetItemWriter.write(futures);

    // Then
    verify(targetNickNameRepository).saveAll(anyList());
  }
}
