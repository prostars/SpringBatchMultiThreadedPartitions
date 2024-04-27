package com.example.springbatch.job.migration;

import com.example.springbatch.entity.UserNameEntity;
import com.example.springbatch.repository.source.SourceNameRepository;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@StepScope
@Component
public class SourceItemReader implements ItemReader<UserNameEntity> {
  
  private static final Logger log = LoggerFactory.getLogger(SourceItemReader.class);
  private final SourceNameRepository sourceNameRepository;
  private final int fetchCount;
  private long end;
  private long slideBegin;
  private long slideEnd;
  private int nextIdx = 0;
  private List<UserNameEntity> userNameEntities = List.of();

  public SourceItemReader(SourceNameRepository sourceNameRepository,
      @Value("${batch.fetch-size}") int fetchCount) {
    this.sourceNameRepository = sourceNameRepository;
    this.fetchCount = fetchCount;
  }

  @BeforeStep
  public void beforeStep(StepExecution stepExecution) {
    final ExecutionContext executionContext = stepExecution.getExecutionContext();
    final long begin = executionContext.getLong("subBegin");
    this.end = executionContext.getLong("subEnd");
    log.info("execute partition range: [{}:{}]", begin, end);
    
    this.slideBegin = begin;
    this.slideEnd = Math.min(begin + fetchCount - 1, end);
    log.info("begin={} and end={}", begin, end);
  }

  @Override
  public UserNameEntity read() {
    if (nextIdx >= userNameEntities.size()) {
      nextIdx = 0;
      if (!fetch()) {
        log.info("Finished");
        return null;
      }
    }
    
    final UserNameEntity item = userNameEntities.get(nextIdx);
    nextIdx++;
    return item;
  }

  private boolean fetch() {
    if (slideBegin > end || slideBegin < 0) {
      return false;
    }

    while (slideBegin <= slideEnd) {
      userNameEntities = sourceNameRepository.findAllByIdBetween(slideBegin, slideEnd);
      slideBegin = slideEnd + 1;
      slideEnd = Math.min(slideBegin + fetchCount - 1, end);
      if (!userNameEntities.isEmpty()) {
        return true;
      }
    }
    return false;
  }
}
