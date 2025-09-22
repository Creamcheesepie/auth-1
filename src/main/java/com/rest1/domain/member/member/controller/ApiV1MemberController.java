package com.rest1.domain.member.member.controller;

import com.rest1.domain.member.member.dto.MemberDto;
import com.rest1.domain.member.member.entity.Member;
import com.rest1.domain.member.member.service.MemberService;
import com.rest1.global.rq.Rq;
import com.rest1.global.rsData.RsData;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/members")
@Tag(name = "ApiV1MemberController", description = "회원 API")
public class ApiV1MemberController {
    private final MemberService memberService;
    private final Rq rq;

    record JoinReqBody(
            @NotBlank
            @Size(min = 2, max = 30)
            String username,
            @NotBlank
            @Size(min = 2, max = 30)
            String password,
            @NotBlank
            @Size(min = 2, max = 30)
            String nickname
    ) {
    }

    record JoinResBody(
            MemberDto memberDto
    ) {
    }

    @PostMapping("/join")
    public RsData<MemberDto> join(
            @RequestBody @Valid JoinReqBody reqBody
    ){
        Member newMember = memberService.join(reqBody.username, reqBody.password, reqBody.nickname);
        return new RsData<MemberDto>(
                "201-1",
                "회원가입이 완료되었습니다. %s님 환영합니다.".formatted(newMember.getNickname()),
                new MemberDto(newMember)
        );
    }

    record LogInReqBody(
            @NotBlank
            @Size(min = 2, max = 30)
            String username,
            @NotBlank
            @Size(min = 2, max = 30)
            String password
    ) {
    }

    record LoginResBody(
            MemberDto memberDto,
            String apiKey,
            String accessToken
    ) {
    }

    @PostMapping("/login")
    public RsData<LoginResBody> login(
            @RequestBody @Valid LogInReqBody reqBody,
            HttpServletResponse response
    ){
        Member member = memberService.login(reqBody.username,reqBody.password);

        String accessToken = memberService.genAccessToken(member);

        rq.setCookie("apiKey",member.getApiKey());
        rq.setCookie("accessToken",accessToken);

        return new RsData<LoginResBody>(
            "200-1",
                "%s님 환영합니다.".formatted(member.getUsername()),
                new LoginResBody(
                        new MemberDto(member),
                        member.getApiKey(),
                        accessToken
                )
        );
    }
    record ResBody(
            MemberDto memberDto
    ) {
    }

    @GetMapping("/me")
    public RsData<ResBody> me(){
        Member actor = rq.getActor();

        return new RsData<ResBody>(
                "200-1",
                "%s님의 회원정보입니다.".formatted(actor.getUsername()),
                new ResBody(
                        new MemberDto(actor)
                )
        );
    }

    @DeleteMapping("/logout")
    public RsData<MemberDto> logout(

    ){
        rq.deleteCookie("apiKey");
        return new RsData<>(
                "200-1",
                "로그아웃 되었습니다."
        );
    }

}
