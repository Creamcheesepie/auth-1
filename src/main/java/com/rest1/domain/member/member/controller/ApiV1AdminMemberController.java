package com.rest1.domain.member.member.controller;

import com.rest1.domain.member.member.dto.MemberWithUsernameDto;
import com.rest1.domain.member.member.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin")
public class ApiV1AdminMemberController {
    private final MemberService memberService;

    @GetMapping("/members")
    @Transactional(readOnly = true)
    @Operation(summary = "글 다건 조회")
    public List<MemberWithUsernameDto> getItems() {
        return memberService.findAll().stream()
                .map(MemberWithUsernameDto::new)
                .toList();
    }
}
