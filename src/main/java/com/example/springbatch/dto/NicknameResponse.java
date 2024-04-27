package com.example.springbatch.dto;

public class NicknameResponse {
  
  private String nickname;

  public NicknameResponse() {
  }

  public NicknameResponse(String nickname) {
    this.nickname = nickname;
  }

  public String getNickname() {
    return nickname;
  }

  public void setNickname(String nickname) {
    this.nickname = nickname;
  }
}
