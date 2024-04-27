package com.example.springbatch.job.initialization;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class CleanTablesTasklet implements Tasklet {

  private final JdbcTemplate sourceJdbcTemplate;
  private final JdbcTemplate targetJdbcTemplate;

  public CleanTablesTasklet(
      @Qualifier("sourceJdbcTemplate") JdbcTemplate sourceJdbcTemplate,
      @Qualifier("targetJdbcTemplate") JdbcTemplate targetJdbcTemplate) {
    this.sourceJdbcTemplate = sourceJdbcTemplate;
    this.targetJdbcTemplate = targetJdbcTemplate;
  }

  @Override
  public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {
    sourceJdbcTemplate.execute("TRUNCATE TABLE user_name");
    targetJdbcTemplate.execute("TRUNCATE TABLE user_name_with_nick");
    return RepeatStatus.FINISHED;
  }
}
