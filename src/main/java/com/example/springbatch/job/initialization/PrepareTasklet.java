package com.example.springbatch.job.initialization;

import com.example.springbatch.entity.UserNameEntity;
import com.example.springbatch.repository.source.SourceNameRepository;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.LongStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

@Component
public class PrepareTasklet implements Tasklet {
  
  private static final Logger log = LoggerFactory.getLogger(PrepareTasklet.class);
  private final SourceNameRepository sourceNameRepository;

  public PrepareTasklet(SourceNameRepository sourceNameRepository) {
    this.sourceNameRepository = sourceNameRepository;
  }

  @Override
  public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {
      ExecutionContext executionContext =
          chunkContext.getStepContext().getStepExecution().getExecutionContext();
      final long subBegin = executionContext.getLong("subBegin");
      final long subEnd = executionContext.getLong("subEnd");
      log.info("execute partition range: [{}:{}]", subBegin, subEnd);

      final List<UserNameEntity> userNameEntities = LongStream.rangeClosed(subBegin, subEnd)
          .mapToObj(value -> new UserNameEntity(String.valueOf(value))).collect(Collectors.toList());
      sourceNameRepository.saveAll(userNameEntities);

      return RepeatStatus.FINISHED;
  }
}
