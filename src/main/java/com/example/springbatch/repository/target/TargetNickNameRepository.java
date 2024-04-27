package com.example.springbatch.repository.target;

import com.example.springbatch.entity.UserNameWithNickEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TargetNickNameRepository extends JpaRepository<UserNameWithNickEntity, Long> {
}
