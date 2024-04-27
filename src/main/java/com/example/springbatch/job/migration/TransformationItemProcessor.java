package com.example.springbatch.job.migration;

import com.example.springbatch.dto.NicknameRequest;
import com.example.springbatch.dto.NicknameResponse;
import com.example.springbatch.entity.UserNameEntity;
import com.example.springbatch.entity.UserNameWithNickEntity;
import com.example.springbatch.service.NicknameClientService;
import java.security.InvalidParameterException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@StepScope
@Component
public class TransformationItemProcessor implements 
    ItemProcessor<UserNameEntity, CompletableFuture<UserNameWithNickEntity>>, DisposableBean {

  private final NicknameClientService nicknameClientService;
  private final ExecutorService executor; 

  public TransformationItemProcessor(NicknameClientService nicknameClientService,
      @Value("${batch.chunk-size}") int chunkCount) {
    this.nicknameClientService = nicknameClientService;
    this.executor = Executors.newFixedThreadPool(chunkCount);
  }

  @Override
  public CompletableFuture<UserNameWithNickEntity> process(UserNameEntity userNameEntity) {
    if (userNameEntity == null) {
      throw new InvalidParameterException();
    }
    return CompletableFuture.supplyAsync(() -> build(userNameEntity), executor);
  }
  
  private UserNameWithNickEntity build(UserNameEntity userNameEntity) {
    final NicknameResponse response = nicknameClientService.generateNickname(new NicknameRequest(userNameEntity.getName()));
    return new UserNameWithNickEntity(userNameEntity.getName(), response.getNickname());
  }

  @Override
  public void destroy() {
    executor.shutdown();
  }
}
