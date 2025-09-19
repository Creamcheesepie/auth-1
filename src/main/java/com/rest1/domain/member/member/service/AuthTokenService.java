package com.rest1.domain.member.member.service;

import com.rest1.domain.member.member.entity.Member;
import com.rest1.standard.Ut;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class AuthTokenService {
    private  long expireMillis = 1000L * 60 * 60 * 24 * 365;
    private String secretPattern = "abcdefghijklmnopqrstuvwxyz1234567890abcdefghijklmnopqrstuvwxyz1234567890";

    public String genAccessToken(Member member) {
        String accessToken = Ut.jwt.toString(
                secretPattern,
                expireMillis,
                Map.of("id",member.getId(),"username",member.getUsername())
        );

        return accessToken;
    }
}
