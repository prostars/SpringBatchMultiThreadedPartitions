package com.example.springbatch.job.migration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.when;

import com.example.springbatch.entity.UserNameEntity;
import com.example.springbatch.repository.source.SourceNameRepository;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.item.ExecutionContext;

@ExtendWith(MockitoExtension.class)
public class SourceItemReaderTest {

  private SourceItemReader sourceItemReader;
  private List<UserNameEntity> mockUserNameEntities;
  @Mock
  private SourceNameRepository sourceNameRepository;
  @Mock
  private StepExecution stepExecution;
  @Mock
  private ExecutionContext executionContext;

  @BeforeEach
  void setUp() {
    mockUserNameEntities = IntStream.rangeClosed(0, 299)
        .mapToObj(i -> new UserNameEntity("Nick" + i)).collect(Collectors.toList());
    when(stepExecution.getExecutionContext()).thenReturn(executionContext);
  }

  @Test
  public void testReadWithEmptyResult() {
    // Given
    sourceItemReader = new SourceItemReader(sourceNameRepository, 10);
    when(executionContext.getLong("subBegin")).thenReturn(0L);
    when(executionContext.getLong("subEnd")).thenReturn(99L);
    when(sourceNameRepository.findAllByIdBetween(anyLong(), anyLong())).thenReturn(
        Collections.emptyList());
    sourceItemReader.beforeStep(stepExecution);

    // When
    final UserNameEntity userNameEntity = sourceItemReader.read();

    // Then
    assertNull(userNameEntity, "Expected read() to return null when no data is available");
  }

  @Test
  public void testReadWithNonEmptyResult() {
    // Given
    sourceItemReader = new SourceItemReader(sourceNameRepository, 10);
    when(executionContext.getLong("subBegin")).thenReturn(0L);
    when(executionContext.getLong("subEnd")).thenReturn(99L);
    when(sourceNameRepository.findAllByIdBetween(anyLong(), anyLong())).thenAnswer(
        invocation -> {
          int startPk = Math.toIntExact(invocation.getArgument(0, Long.class));
          int endPk = Math.toIntExact(invocation.getArgument(1, Long.class));
          return mockUserNameEntities.subList(startPk, endPk + 1);
        });
    sourceItemReader.beforeStep(stepExecution);

    // When
    final List<String> userNames = IntStream.rangeClosed(0, 99)
        .mapToObj(idx -> Objects.requireNonNull(sourceItemReader.read()).getName())
        .collect(Collectors.toList());

    // Then
    IntStream.rangeClosed(0, 99).forEach(idx -> assertEquals("Nick" + idx, userNames.get(idx),
        "Expected read to return the user name entity"));
  }

  @Test
  public void testReadReturnsNullAfterAllDataIsRead() {
    // Given
    sourceItemReader = new SourceItemReader(sourceNameRepository, 10);
    when(executionContext.getLong("subBegin")).thenReturn(0L);
    when(executionContext.getLong("subEnd")).thenReturn(99L);
    when(sourceNameRepository.findAllByIdBetween(anyLong(), anyLong())).thenAnswer(
        invocation -> {
          final int startPk = Math.toIntExact(invocation.getArgument(0, Long.class));
          final int endPk = Math.toIntExact(invocation.getArgument(1, Long.class));
          return mockUserNameEntities.subList(startPk, endPk + 1);
        });
    sourceItemReader.beforeStep(stepExecution);

    // When
    IntStream.rangeClosed(0, 99).forEach(unused -> {
      final String ignore = Objects.requireNonNull(sourceItemReader.read()).getName();
    });

    // Then
    assertNull(sourceItemReader.read(), "Expected read() to return null after all data is read");
  }
}
