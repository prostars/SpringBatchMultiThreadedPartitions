package com.example.springbatch.job;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class RangePartitioner implements Partitioner {
  
  private static final Logger log = LoggerFactory.getLogger(RangePartitioner.class);
  @Value("${batch.range.begin}")
  private long begin;
  @Value("${batch.range.end}")
  private long end;

  @Override
  public Map<String, ExecutionContext> partition(int partitionSize) {
    if (0 >= partitionSize)
      throw new IllegalArgumentException(String.format("partitionSize must be greater than 0. partitionSize=%d", partitionSize));
    if (begin > end)
      throw new IllegalStateException(String.format("begin cannot be greater than end. begin=%d, end=%d", begin, end));
      
    final Map<String, ExecutionContext> partitions = new HashMap<>();
    final long range = end - begin + 1;
    final long subRange = (range + partitionSize - 1) / partitionSize;
    IntStream.range(0, partitionSize).forEach(idx -> {
      final long subBegin = begin + (idx * subRange);
      final long subEnd = Math.min(subBegin + subRange - 1, end);
      final ExecutionContext executionContext = new ExecutionContext();
      executionContext.putLong("subBegin", subBegin);
      executionContext.putLong("subEnd", subEnd);
      partitions.put(String.format("partition:%d", idx), executionContext);
      log.info("Prepare partition:{} [{}:{}]", idx, subBegin, subEnd);
    });
    
    return partitions;
  }
                                                        
  public void setBegin(long begin) {
    this.begin = begin;
  }

  public void setEnd(long end) {
    this.end = end;
  }

  public long getBegin() {
    return begin;
  }

  public long getEnd() {
    return end;
  }
}
