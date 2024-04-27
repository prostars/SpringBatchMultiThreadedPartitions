package com.example.springbatch.job.migration;

import com.example.springbatch.entity.UserNameEntity;
import com.example.springbatch.entity.UserNameWithNickEntity;
import java.util.concurrent.CompletableFuture;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class MigrationBatch {

  @Value("${batch.chunk-size}")
  private int chunkSize;
  private final PlatformTransactionManager platformTransactionManager;
  private final JobBuilderFactory jobBuilderFactory;
  private final StepBuilderFactory stepBuilderFactory;
  private final Partitioner partitioner;
  private final TaskExecutor taskExecutor;
  private final int partitionSize;
  private final SourceItemReader sourceItemReader;
  private final TransformationItemProcessor transformationItemProcessor;
  private final TargetItemWriter targetItemWriter;

  public MigrationBatch(
      PlatformTransactionManager platformTransactionManager,
      JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory,
      Partitioner partitioner, TaskExecutor taskExecutor,
      @Value("${batch.partition.size}") int partitionSize,
      SourceItemReader sourceItemReader, TransformationItemProcessor transformationItemProcessor,
      TargetItemWriter targetItemWriter) {
    this.platformTransactionManager = platformTransactionManager;
    this.jobBuilderFactory = jobBuilderFactory;
    this.stepBuilderFactory = stepBuilderFactory;
    this.partitioner = partitioner;
    this.partitionSize = partitionSize;
    this.taskExecutor = taskExecutor;
    this.sourceItemReader = sourceItemReader;
    this.transformationItemProcessor = transformationItemProcessor;
    this.targetItemWriter = targetItemWriter;
  }

  @Bean
  public Job migrationJob() {
    return this.jobBuilderFactory.get("MigrationJob")
        .start(migrationMainStep())
        .build();
  }

  private Step migrationMainStep() {
    return stepBuilderFactory.get("MigrationMainStep")
        .allowStartIfComplete(true)
        .partitioner("MigrationSubStep", partitioner)
        .gridSize(partitionSize)
        .taskExecutor(taskExecutor)
        .step(migrationSubStep())
        .build();
  }

  private Step migrationSubStep() {
    return this.stepBuilderFactory.get("MigrationSubStep")
        .allowStartIfComplete(true)
        .transactionManager(platformTransactionManager)
        .<UserNameEntity, CompletableFuture<UserNameWithNickEntity>>chunk(chunkSize)
        .reader(sourceItemReader)
        .processor(transformationItemProcessor)
        .writer(targetItemWriter)
        .build();
  }
}
