package com.rest1.domain.member.member.controller;

import com.rest1.domain.member.member.dto.MemberDto;
import com.rest1.domain.member.member.entity.Member;
import com.rest1.domain.member.member.service.MemberService;
import com.rest1.global.rsData.RsData;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/members")
@Tag(name = "ApiV1MemberController", description = "회원 API")
public class ApiV1MemberController {
    private final MemberService memberService;

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

    @PostMapping
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

}
