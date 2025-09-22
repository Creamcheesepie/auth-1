package com.rest1.domain.member.member.controller;

import com.rest1.domain.member.member.entity.Member;
import com.rest1.domain.member.member.repository.MemberRepository;
import jakarta.servlet.http.Cookie;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
public class ApiV1MemberControllerTest {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private MemberRepository memberRepository;

    @Test
    @DisplayName("회원 가입 테스트")
    public void t1() throws Exception {
        String username = "newUser";
        String password = "1234";
        String nickname = "새유저";

        ResultActions resultActions = mvc
                .perform(
                        post("/api/v1/members/join")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "username": "%s",
                                            "password": "%s",
                                            "nickname": "%s"
                                        }
                                        """.formatted(username, password, nickname))
                )
                .andDo(print());

        resultActions
                .andExpect(handler().handlerType(ApiV1MemberController.class))
                .andExpect(handler().methodName("join"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.resultCode").value("201-1"))
                .andExpect(jsonPath("$.msg").value("회원가입이 완료되었습니다. %s님 환영합니다.".formatted(nickname)))
                .andExpect(jsonPath("$.data.id").value(6))
                .andExpect(jsonPath("$.data.createDate").exists())
                .andExpect(jsonPath("$.data.modifyDate").exists())
                .andExpect(jsonPath("$.data.name").value(nickname));
    }

    @Test
    @DisplayName("중복 아이디 가입 테스트")
    public void t2() throws Exception {
        String username = "user1";
        String password = "1234";
        String nickname = "새유저";

        ResultActions resultActions = mvc
                .perform(
                        post("/api/v1/members/join")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "username": "%s",
                                            "password": "%s",
                                            "nickname": "%s"
                                        }
                                        """.formatted(username, password, nickname))
                )
                .andDo(print());

        resultActions
                .andExpect(handler().handlerType(ApiV1MemberController.class))
                .andExpect(handler().methodName("join"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.resultCode").value("409-1"))
                .andExpect(jsonPath("$.msg").value("이미 사용중인 아이디입니다."));
    }

    @Test
    @DisplayName("로그인 테스트")
    public void t3() throws Exception {
        String username = "user1";
        String password = "1234";

        ResultActions resultActions = mvc
                .perform(
                        post("/api/v1/members/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "username": "%s",
                                            "password": "%s"
                                        }
                                        """.formatted(username, password))
                )
                .andDo(print());

        Member member = memberRepository.findByUsername(username).get();

        resultActions
                .andExpect(handler().handlerType(ApiV1MemberController.class))
                .andExpect(handler().methodName("login"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("200-1"))
                .andExpect(jsonPath("$.msg").value("%s님 환영합니다.".formatted(username)))
                .andExpect(jsonPath("$.data.apiKey").exists())
                .andExpect(jsonPath("$.data.memberDto").exists())
                .andExpect(jsonPath("$.data.memberDto.id").value(member.getId()))
                .andExpect(jsonPath("$.data.memberDto.createDate").value(member.getCreateDate().toString()))
                .andExpect(jsonPath("$.data.memberDto.modifyDate").value(member.getModifyDate().toString()))
                .andExpect(jsonPath("$.data.memberDto.name").value(member.getNickname()));
    }

    @Test
    @DisplayName("로그인 쿠키 테스트")
    public void t4() throws Exception {
        String username = "user1";
        String password = "1234";

        ResultActions resultActions = mvc
                .perform(
                        post("/api/v1/members/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "username": "%s",
                                            "password": "%s"
                                        }
                                        """.formatted(username, password))
                )
                .andDo(print());

        Member member = memberRepository.findByUsername(username).get();

        resultActions
                .andExpect(handler().handlerType(ApiV1MemberController.class))
                .andExpect(handler().methodName("login"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("200-1"))
                .andExpect(jsonPath("$.msg").value("%s님 환영합니다.".formatted(username)))
                .andExpect(jsonPath("$.data.apiKey").exists())
                .andExpect(jsonPath("$.data.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.data.memberDto").exists())
                .andExpect(jsonPath("$.data.memberDto.id").value(member.getId()))
                .andExpect(jsonPath("$.data.memberDto.createDate").value(Matchers.startsWith(member.getCreateDate().toString().substring(0, 20))))
                .andExpect(jsonPath("$.data.memberDto.modifyDate").value(Matchers.startsWith(member.getModifyDate().toString().substring(0, 20))))
                .andExpect(jsonPath("$.data.memberDto.name").value(member.getNickname()));

        resultActions
                .andExpect(result -> {
                    Cookie apiKeyCookie = result.getResponse().getCookie("apiKey");
                    assertThat(apiKeyCookie).isNotNull();

                    if (apiKeyCookie != null) {
                        assertThat(apiKeyCookie.getValue()).isEqualTo(member.getApiKey());
                        assertThat(apiKeyCookie.getDomain()).isEqualTo("localhost");
                        assertThat(apiKeyCookie.getPath()).isEqualTo("/");
                        assertThat(apiKeyCookie.isHttpOnly()).isTrue();
                    }

                    Cookie accessTokenCookue = result.getResponse().getCookie("accessToken");
                    assertThat(apiKeyCookie).isNotNull();

                    if (apiKeyCookie != null) {
                        assertThat(apiKeyCookie.getValue()).isEqualTo(member.getApiKey());
                        assertThat(apiKeyCookie.getDomain()).isEqualTo("localhost");
                        assertThat(apiKeyCookie.getPath()).isEqualTo("/");
                        assertThat(apiKeyCookie.isHttpOnly()).isTrue();
                    }
                });

    }

    @Test
    @DisplayName("로그아웃")
    public void t5() throws Exception {
        String username = "user1";
        String password = "1234";

        ResultActions resultActions = mvc
                .perform(
                        delete("/api/v1/members/logout")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print());


        String apiKey = "";
        resultActions
                .andExpect(handler().handlerType(ApiV1MemberController.class))
                .andExpect(handler().methodName("logout"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("200-1"))
                .andExpect(jsonPath("$.msg").value("로그아웃 되었습니다.".formatted(username)))
                .andExpect(result -> {
                    Cookie apiKeyCookie = result.getResponse().getCookie("apiKey");
                    if (apiKeyCookie != null) {
                        assertThat(apiKeyCookie.getDomain()).isEqualTo("localhost");
                        assertThat(apiKeyCookie.getPath()).isEqualTo("/");
                        assertThat(apiKeyCookie.getMaxAge()).isEqualTo(0);
                        assertThat(apiKeyCookie.isHttpOnly()).isTrue();
                    }

                });
    }

    @Test
    @DisplayName("내 정보")
    public void t6() throws Exception {
        Member actor = memberRepository.findByUsername("user1").get();
        String apiKey = actor.getApiKey();

        ResultActions resultActions = mvc
                .perform(
                        get("/api/v1/members/me")
                                .header("Authorization", "Bearer %s".formatted(apiKey))
                )
                .andDo(print());

        resultActions
                .andExpect(handler().handlerType(ApiV1MemberController.class))
                .andExpect(handler().methodName("me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("200-1"))
                .andExpect(jsonPath("$.msg").value("%s님의 회원정보입니다.".formatted(actor.getUsername())))
                .andExpect(jsonPath("$.data.memberDto").exists())
                .andExpect(jsonPath("$.data.memberDto.id").value(actor.getId()))
                .andExpect(jsonPath("$.data.memberDto.createDate").value(actor.getCreateDate().toString()))
                .andExpect(jsonPath("$.data.memberDto.modifyDate").value(actor.getModifyDate().toString()))
                .andExpect(jsonPath("$.data.memberDto.name").value(actor.getNickname()));
    }

    @Test
    @DisplayName("내 정보, 올바른 API키, 유효하지 않은 엑세스 토큰")
    public void t7() throws Exception {
        Member actor = memberRepository.findByUsername("user1").get();
        String apiKey = actor.getApiKey();

        ResultActions resultActions = mvc
                .perform(
                        get("/api/v1/members/me")
                                .cookie(new Cookie("apiKey", apiKey),new Cookie("accessToken","엑세스토큰~"))
//                                .header("Authorization", "Bearer %s".formatted(apiKey))
                )
                .andDo(print());

        resultActions
                .andExpect(handler().handlerType(ApiV1MemberController.class))
                .andExpect(handler().methodName("me"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("내 정보, 올바른 apiKey, 유효하지 않은 accessToken")
    public void t8() throws Exception {
        Member actor = memberRepository.findByUsername("user1").get();
        String apiKey = actor.getApiKey();
        String wrongAccessToken = "틀린 엑세스 토큰이렁 뭬렁뭬렁";

        ResultActions resultActions = mvc
                .perform(
                        get("/api/v1/members/me")
                                .header("Authorization", "Bearer %s %s".formatted(apiKey, wrongAccessToken))
                )
                .andDo(print());

        resultActions
                .andExpect(handler().handlerType(ApiV1MemberController.class))
                .andExpect(handler().methodName("me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("200-1"))
                .andExpect(jsonPath("$.msg").value("%s님의 회원정보입니다.".formatted(actor.getUsername())))
                .andExpect(jsonPath("$.data.memberDto").exists())
                .andExpect(jsonPath("$.data.memberDto.id").value(actor.getId()))
                .andExpect(jsonPath("$.data.memberDto.createDate").value(actor.getCreateDate().toString()))
                .andExpect(jsonPath("$.data.memberDto.modifyDate").value(actor.getModifyDate().toString()))
                .andExpect(jsonPath("$.data.memberDto.name").value(actor.getNickname()));
    }


}
