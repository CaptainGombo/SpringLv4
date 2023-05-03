package com.sparta.springlv3.dto;

import com.sparta.springlv3.entity.Member;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BoardRequestDto {
    private Member member;
    private String password;
    private String title;
    private String contents;

}