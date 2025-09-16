package com.rest1.domain.member.member.service;

import com.rest1.domain.member.member.entity.Member;
import com.rest1.domain.member.member.repository.MemberRepository;
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
        Member member = new Member(username, password, nickname);
        return memberRepository.save(member);
    }
}
