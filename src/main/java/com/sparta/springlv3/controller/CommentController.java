package com.sparta.springlv3.controller;

import com.sparta.springlv3.dto.*;
import com.sparta.springlv3.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    //댓글 작성
    @PostMapping("/comment")
    public CommentResponseDto createComment(@RequestBody CommentRequestDto requestDto, HttpServletRequest request) {
        return commentService.createComment(requestDto, request);
    }
    //댓글 수정
    @PutMapping("/comment/{commentid}")
    public CommentResponseDto updateComment(@PathVariable Long commentid, @RequestBody CommentRequestDto commentRequestDto, HttpServletRequest request){
        return commentService.updateComment(commentid, commentRequestDto, request);
    }
    //댓글 삭제
    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/comment/{commentid}")
    public CommentDeleteResponseDto deleteComment(@PathVariable Long commentid, HttpServletRequest request){
       return commentService.deleteComment(commentid, request);

    }


}
