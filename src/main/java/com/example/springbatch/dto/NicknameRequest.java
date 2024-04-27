package com.example.springbatch.dto;

public class NicknameRequest {
  
  private final String name;

  public NicknameRequest(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }
}
