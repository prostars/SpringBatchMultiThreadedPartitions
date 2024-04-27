package com.example.springbatch.job;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.batch.item.ExecutionContext;

class RangePartitionerTest {

  private final RangePartitioner partitioner = new RangePartitioner();

  @BeforeEach
  void setUp() {
    partitioner.setBegin(0);
    partitioner.setEnd(99);
  }

  @Test
  void shouldThrowExceptionWhenPartitionSizeIsZeroOrNegative() {
    // given
    final int zeroPartitionSize = 0;
    final int negativePartitionSize = 0;

    // when, then                      
    assertThrows(IllegalArgumentException.class, () -> partitioner.partition(zeroPartitionSize));
    assertThrows(IllegalArgumentException.class, () -> partitioner.partition(negativePartitionSize));
  }

  @Test
  void shouldHaveEqualRangesForAllPartitions() {
    // given 
    final int partitionSize = 3;

    // when
    final Map<String, ExecutionContext> partitions = partitioner.partition(partitionSize);

    // then
    final List<ExecutionContext> executionContexts = new ArrayList<>(partitions.values());
    long sum = IntStream.range(0, partitionSize)
        .mapToObj(executionContexts::get)
        .mapToLong(context -> context.getLong("subEnd") - context.getLong("subBegin") + 1)
        .sum();
    assertEquals(sum, partitioner.getEnd() - partitioner.getBegin() + 1);
  }

  @Test
  void shouldHaveOnePartitionWithRangeOneSmaller() {
    // given
    final int partitionSize = 3;

    // when
    final Map<String, ExecutionContext> partitions = partitioner.partition(partitionSize);

    // then
    final List<ExecutionContext> executionContexts = new ArrayList<>(partitions.values());
    List<Long> subRange = executionContexts.stream()
        .map(context -> context.getLong("subEnd") - context.getLong("subBegin") + 1)
        .sorted()
        .collect(Collectors.toList());
    final Long smallestSubRange = subRange.remove(0);
    IntStream.range(0, subRange.size()).forEach(idx -> {
      assertTrue(smallestSubRange < subRange.get(idx));
      assertEquals(subRange.get(0), subRange.get(idx));
    });
  }
}                                              
