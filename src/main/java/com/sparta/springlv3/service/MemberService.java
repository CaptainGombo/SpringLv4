package com.sparta.springlv3.service;


import com.sparta.springlv3.dto.MemberRequestDto;
import com.sparta.springlv3.dto.SignupRequestDto;
import com.sparta.springlv3.entity.Member;
import com.sparta.springlv3.entity.UserRoleEnum;
import com.sparta.springlv3.exception.DuplicateUsernameException;
import com.sparta.springlv3.exception.MemberNotFoundException;
import com.sparta.springlv3.jwt.JwtUtil;
import com.sparta.springlv3.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final JwtUtil jwtUtil;
    // ADMIN_TOKEN
    private static final String ADMIN_TOKEN = "AAABnvxRVklrnYxKZ0aHgTBcXukeZygoC";
    @Transactional(readOnly = true)
    public void login(MemberRequestDto memberRequestDto, HttpServletResponse response){
        String userName = memberRequestDto.getUsername();
        String password = memberRequestDto.getPassword();


        Member member = memberRepository.findByUsername(userName).orElseThrow(
                () -> new IllegalArgumentException("등록된 아이디가 없습니다.")
        );

        if(!member.getPassword().equals(password)){
            throw new MemberNotFoundException("아이디 찾을 수 없습니다.");
        }
        response.addHeader(JwtUtil.AUTHORIZATION_HEADER, jwtUtil.createToken(member.getUsername(), member.getRole()));
    }
    @Transactional
    public void signup(SignupRequestDto signupRequestDto)  {
        //회원가입 유저 아이디 중복확인
        Optional<Member> overlapUser = memberRepository.findByUsername(signupRequestDto.getUsername());
        if(overlapUser.isPresent()) {
            throw new DuplicateUsernameException("중복된 아이디가 있습니다.");
        }

//        사용자 Role 확인
        UserRoleEnum role = UserRoleEnum.USER;
        if (signupRequestDto.isAdmin()) {
            if (!signupRequestDto.getAdminToken().equals(ADMIN_TOKEN)) {
                throw new IllegalArgumentException("관리자 암호가 틀려 등록이 불가능합니다.");
            }

            role = UserRoleEnum.ADMIN;

            System.out.println("관리자권한이 부여되었습니다.");
        }
        Member member = new Member(signupRequestDto, role);
        memberRepository.save(member);

    }



}