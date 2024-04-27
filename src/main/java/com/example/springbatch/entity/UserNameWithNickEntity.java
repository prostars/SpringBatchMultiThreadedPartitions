package com.example.springbatch.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "user_name_with_nick")
public class UserNameWithNickEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", updatable = false)
  private Long id;
  
  @Column(name = "name", nullable = false)
  private String name;

  @Column(name = "nick", nullable = false)
  private String nick;

  public UserNameWithNickEntity() {
  }

  public UserNameWithNickEntity(String name, String nick) {
    this.name = name;
    this.nick = nick;
  }

  public Long getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getNick() {
    return nick;
  }
}
