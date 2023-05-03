package com.sparta.springlv3.dto;


import lombok.Getter;

@Getter
public class CommentDeleteResponseDto {


    private String alert;
    private int statusCode;

    public CommentDeleteResponseDto() {
        this.alert = "댓글이 삭제되었습니다.";


        this.statusCode = 200;

    }
}
