package com.rest1.domain.member.member.entity;

import com.rest1.global.jpa.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@NoArgsConstructor
@Getter
public class Member extends BaseEntity {
    @Column(unique = true)
    private String username;
    @Getter(AccessLevel.PRIVATE)
    private String password;
    private String nickname;
    @Column(unique = true)
    private String apiKey;

    public Member(String username, String password, String nickname) {
        this.username =username;
        this.password = password;
        this.nickname = nickname;
        this.apiKey = UUID.randomUUID().toString();
    }

    public Member(long id, String username,String nickname) {
        this.username = username;
        this.nickname = nickname;
        setId(id);
    }

    public boolean isCorrectPassword(String password) {
        return this.password.equals(password);
    }

    public String updateApiKey(String apiKey) {
        this.apiKey = apiKey;
        return apiKey;
    }

    public boolean isAdmin(){
        return "admin".equals(this.username);
    }
}
