package com.sparta.springlv3.dto;


import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;


@Data
public class SignupRequestDto {

    @Size(min = 4, max = 10,message = "아이디는 4글자~10자 사이로 입력해주세요.")
    @Pattern(regexp = "^[a-z0-9]*$",message = "알파벳 소문자와 숫자의 형태로 입력해주세요.")
    private String username;

    @Size(min = 8, max = 15)
    @NotBlank
    @Pattern(regexp = "(?=.*[0-9])(?=.*[a-zA-Z])(?=.*\\W)(?=\\S+$).{8,15}", message = "비밀번호는 8~15자 영문 대 소문자, 숫자, 특수문자를 사용하세요.")
    private String password;
    private boolean admin = false;
    private String adminToken = "";


}