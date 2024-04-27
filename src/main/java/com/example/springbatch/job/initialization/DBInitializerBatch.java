package com.example.springbatch.job.initialization;

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
public class DBInitializerBatch {
  
  private final PlatformTransactionManager platformTransactionManager;
  private final JobBuilderFactory jobBuilderFactory;
  private final StepBuilderFactory stepBuilderFactory;
  private final Partitioner partitioner;
  private final TaskExecutor taskExecutor;
  private final CleanTablesTasklet cleanTablesTasklet;
  private final PrepareTasklet prepareTasklet;
  private final int partitionSize;

  public DBInitializerBatch(
      PlatformTransactionManager platformTransactionManager,
      JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory,
      Partitioner partitioner, TaskExecutor taskExecutor,
      @Value("${batch.partition.size}") int partitionSize,
      CleanTablesTasklet cleanTablesTasklet, PrepareTasklet prepareTasklet) {
    this.platformTransactionManager = platformTransactionManager;
    this.jobBuilderFactory = jobBuilderFactory;
    this.stepBuilderFactory = stepBuilderFactory;
    this.partitioner = partitioner;
    this.partitionSize = partitionSize;
    this.taskExecutor = taskExecutor;
    this.cleanTablesTasklet = cleanTablesTasklet;
    this.prepareTasklet = prepareTasklet;
  }

  @Bean
  public Job dBInitializerJob() {
    return jobBuilderFactory.get("DBInitializerJob")
        .start(cleanTablesStep())
        .next(prepareDataMainStep())
        .build();
  }

  private Step cleanTablesStep() {
    return stepBuilderFactory.get("CleanTablesStep")
        .allowStartIfComplete(true)
        .tasklet(cleanTablesTasklet)
        .build();
  }

  private Step prepareDataMainStep() {
    return stepBuilderFactory.get("PrepareDataMainStep")
        .allowStartIfComplete(true)
        .partitioner("PrepareDataSubStep", partitioner)
        .gridSize(partitionSize)
        .taskExecutor(taskExecutor)
        .step(prepareDataSubStep())
        .build();
  }

  private Step prepareDataSubStep() {
    return stepBuilderFactory.get("PrepareDataSubStep")
        .allowStartIfComplete(true)
        .transactionManager(platformTransactionManager)
        .tasklet(prepareTasklet)
        .build();
  }
}
