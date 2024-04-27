package com.example.springbatch.job;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class PartitionerTaskExecutor {

  private static final Logger log = LoggerFactory.getLogger(PartitionerTaskExecutor.class);

  @Bean
  @JobScope
  public TaskExecutor taskExecutor(@Value("${batch.partition.size}") int partitionSize) {
    log.info("create TaskExecutor");
    final ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(partitionSize);
    executor.setMaxPoolSize(partitionSize);
    executor.setWaitForTasksToCompleteOnShutdown(true);
    executor.setThreadNamePrefix("SubStep-");
    executor.initialize();
    return executor;
  }
}
