package com.example.springbatch.job.migration;

import com.example.springbatch.entity.UserNameWithNickEntity;
import com.example.springbatch.repository.target.TargetNickNameRepository;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

@StepScope
@Component
public class TargetItemWriter implements ItemWriter<CompletableFuture<UserNameWithNickEntity>> {
  
  private static final Logger log = LoggerFactory.getLogger(TargetItemWriter.class);
  private final TargetNickNameRepository targetNickNameRepository;

  public TargetItemWriter(TargetNickNameRepository targetNickNameRepository) {
    this.targetNickNameRepository = targetNickNameRepository;
  }

  @Override
  public void write(List<? extends CompletableFuture<UserNameWithNickEntity>> items) {
    CompletableFuture.allOf(items.toArray(new CompletableFuture[0])).join();

    final List<UserNameWithNickEntity> userNameWithNickEntities = items.stream()
        .map(future -> future.getNow(null))
        .collect(Collectors.toList());

    final List<UserNameWithNickEntity> savedItems = targetNickNameRepository.saveAll(userNameWithNickEntities);
    log.info("Chunk Finished - saved rows: {}", savedItems.size());
  }
}
