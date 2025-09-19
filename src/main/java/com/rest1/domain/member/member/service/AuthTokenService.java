package com.rest1.domain.member.member.service;

import com.rest1.domain.member.member.entity.Member;
import com.rest1.standard.Ut;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class AuthTokenService {

    @Value("${custom.jwt.secretPattern}")
    private String secretPattern;

    @Value("${custom.jwt.expireMillis}")
    private Long expireMillis;

    String genAccessToken(Member member) {
        String accessToken = Ut.jwt.toString(
                secretPattern,
                expireMillis,
                Map.of("id",member.getId(),"username",member.getUsername())
        );

        return accessToken;
    }

    Map<String, Object> payloadOrNull(String jwt) {
        Map<String, Object> payload = Ut.jwt.payloadOrNull(jwt,secretPattern);
        if(payload == null){
            return null;
        }

        int id = (int)payload.get("id");
        String name = (String)payload.get("username");
        return Map.of("id",(long)id,"username",name);
    }
}
