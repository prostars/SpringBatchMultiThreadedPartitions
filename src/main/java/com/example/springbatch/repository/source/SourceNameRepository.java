package com.example.springbatch.repository.source;

import com.example.springbatch.entity.UserNameEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SourceNameRepository extends JpaRepository<UserNameEntity, Long> {

  List<UserNameEntity> findAllByIdBetween(long begin, long end);
}
