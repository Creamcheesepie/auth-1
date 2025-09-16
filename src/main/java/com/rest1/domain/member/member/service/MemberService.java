package com.rest1.domain.member.member.service;

import com.rest1.domain.member.member.entity.Member;
import com.rest1.domain.member.member.repository.MemberRepository;
import com.rest1.global.exception.ServiceException;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

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
}
