package com.rest1.domain.member.member.service;

import com.rest1.domain.member.member.entity.Member;
import com.rest1.domain.member.member.repository.MemberRepository;
import com.rest1.standard.Ut;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class AuthTokenServiceTest {
    // 토큰 만료기간: 1년
    // 만료기간 : 필수
    private  long expireMillis = 1000L * 60 * 60 * 24 * 365;
    private String secretPattern = "abcdefghijklmnopqrstuvwxyz1234567890abcdefghijklmnopqrstuvwxyz1234567890";

    @Autowired
    private AuthTokenService authTokenService;
    @Autowired
    private MemberRepository memberRepository;

    @Test
    @DisplayName("authTokenService가 존재하는지?")
    void t1() {
        assertThat(authTokenService).isNotNull();
    }

    @Test
    @DisplayName("jjwt 최신 방식으로 JWT 생성, {name=/Pual/,age=20}")
    void t2() {

        // SecretKey : 위변조 방지 토큰, 이 위변조 방지 토큰의 키바이트는 유출되면 안된다.
        SecretKey secretKey = Keys.hmacShaKeyFor(secretPattern.getBytes(StandardCharsets.UTF_8));

        // 발행 시간과 만료 시간 설정
        Date issuedAt = new Date();
        Date expiration = new Date(issuedAt.getTime() + expireMillis);

        Map<String,Object> payload = Map.of("name", "Paul", "age", 23);

        String jwt = Jwts.builder()
                .claims(payload) // 내용
                .issuedAt(issuedAt) // 생성날짜
                .expiration(expiration) // 만료날짜
                .signWith(secretKey) // 키 서명
                .compact();


        Map<String, Object> parsedPayload = (Map<String, Object>) Jwts
                .parser()
                .verifyWith(secretKey)
                .build()
                .parse(jwt)
                .getPayload();

        assertThat(parsedPayload)
                .containsAllEntriesOf(payload);


        System.out.println("jwt = " + jwt);
    }

    @Test
    @DisplayName("Ut.jwt.toString을 통해 JWT 생성, {name=\"Paul\",age=23}")
    void t3() {
        String jwt = Ut.jwt.toString(
                secretPattern,
                expireMillis,
                Map.of("name", "Paul", "age", 23)
        );

        assertThat(jwt).isNotBlank();

        boolean validResult = Ut.jwt.isValid(jwt,secretPattern);

        assertThat(validResult).isTrue();
        System.out.println("jwt = " + jwt);
    }

    @Test
    @DisplayName("member의 인증 데이터를 기반으로 JWT 생성")
    void t4() {
        Member member1 = memberRepository.findByUsername("user1").get();

        String accessToken = authTokenService.genAccessToken(member1);
        assertThat(accessToken).isNotBlank();

        System.out.println("accessToken = " + accessToken);

    }

}
