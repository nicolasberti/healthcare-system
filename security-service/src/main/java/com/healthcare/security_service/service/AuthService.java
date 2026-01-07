package com.healthcare.security_service.service;

import com.healthcare.security_service.model.Member;
import com.healthcare.security_service.repository.MemberRepository;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final MemberRepository memberRepository;
    private final JwtService jwtService;

    public AuthService(MemberRepository memberRepository, JwtService jwtService) {
        this.memberRepository = memberRepository;
        this.jwtService = jwtService;
    }

    public String login(String email, String password) {

        /*
        Member member = memberRepository.findByEmailAndPassword(email, password)
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

         */
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Member not found"));

        if (!member.getPassword().equals(password)) {
            throw new RuntimeException("Invalid credentials");
        }

        return jwtService.generateToken(member);
    }
}

