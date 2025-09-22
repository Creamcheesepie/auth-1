package com.rest1.domain.member.member.service;

import com.rest1.domain.member.member.entity.Member;
import com.rest1.domain.member.member.repository.MemberRepository;
import com.rest1.global.exception.ServiceException;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final  AuthTokenService authTokenService;
    private final MemberRepository memberRepository;

    public List<Member> findAll() {
        return memberRepository.findAll();
    }


    public Optional<Member> findByUsername(String username) {
        return  this.memberRepository.findByUsername(username);
    }

    public long memberCount(){
        return memberRepository.count();
    }


    public Member join(String username , String password, String nickname) {
        memberRepository.findByUsername(username).ifPresent( m ->{
            throw new ServiceException("409-1","이미 사용중인 아이디입니다.");
        });

        Member member = new Member(username, password, nickname);
        return memberRepository.save(member);
    }

    public Optional<Member> findByAPiKey(@NotBlank @Size(min = 30, max = 40) String apiKey) {
        return memberRepository.findByApiKey(apiKey);
    }

    public Member login(@NotBlank @Size(min = 2, max = 30) String username, @NotBlank @Size(min = 2, max = 30) String password) {
        Member member = memberRepository.findByUsername(username).orElseThrow(
                () -> new ServiceException("401-1", "존재하지 않는 아이디입니다.")
        );

        if(!member.isCorrectPassword(password)) {
            throw  new ServiceException("401-1", "틀린 비밀번호입니다. 다시 확인해주세요.");
        }

        return member;

    }

    public String genAccessToken(Member member) {
        return  authTokenService.genAccessToken(member);
    }

    public Map<String, Object> payloadOrNull(String accessToken) {
        return  authTokenService.payloadOrNull(accessToken);
    }

    public Optional<Member> findById(long id) {
        return memberRepository.findById((long)id);
    }

    public Optional<Member> findByApiKey(String apiKey) {
        return memberRepository.findByApiKey(apiKey);
    }
}
