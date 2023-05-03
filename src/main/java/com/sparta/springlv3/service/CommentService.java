package com.sparta.springlv3.service;

import com.sparta.springlv3.dto.*;
import com.sparta.springlv3.entity.Board;
import com.sparta.springlv3.entity.Comment;
import com.sparta.springlv3.entity.Member;
import com.sparta.springlv3.entity.UserRoleEnum;
import com.sparta.springlv3.exception.BoardNotFoundException;
import com.sparta.springlv3.exception.InvalidTokenException;
import com.sparta.springlv3.exception.UnauthorizedUserException;
import com.sparta.springlv3.jwt.JwtUtil;
import com.sparta.springlv3.repository.BoardRepository;
import com.sparta.springlv3.repository.CommentRepository;
import com.sparta.springlv3.repository.MemberRepository;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;


@Service
@RequiredArgsConstructor
public class CommentService {

    public final BoardRepository boardRepository ;
    private final MemberRepository memberRepository;
    private final CommentRepository commentRepository;
    private final JwtUtil jwtUtil;


    @Transactional
    public CommentResponseDto createComment(CommentRequestDto requestDto, HttpServletRequest request) {
        Member member = checkJwtToken(request);
        if (member == null) {
            throw new IllegalArgumentException("로그인이 필요합니다.");
        }

        // 게시글 찾기
        Board board = boardRepository.findById(requestDto.getId()).orElseThrow(() -> new BoardNotFoundException("게시글을 찾을 수 없습니다."));

        // 댓글 생성 및 저장
        Comment comment = new Comment(requestDto, member, board);
        commentRepository.save(comment);

        return new CommentResponseDto(comment);
    }

    //댓글 수정
    @Transactional
    public CommentResponseDto updateComment(Long commentid, CommentRequestDto commentRequestDto, HttpServletRequest request) {
        Member member = checkJwtToken(request);

        if(member == null){
            throw new InvalidTokenException("로그인 하세요.");
        }

        Comment comment = commentRepository.findById(commentid).orElseThrow(
                () -> new IllegalArgumentException("댓글이 존재하지 않습니다.")
        );
        //댓글 작성자 또는 관리자만 수정할 수 있다.
        if(member.getUsername().equals(comment.getMember().getUsername()) || member.getRole() == UserRoleEnum.ADMIN){
            comment.update(commentRequestDto);
        }else{
            throw new UnauthorizedUserException("작성자만 댓글을 수정할 수 있습니다.");
        }
        return new CommentResponseDto(comment);
    }

    //댓글 삭제
    @Transactional
    public CommentDeleteResponseDto deleteComment(Long commentid, HttpServletRequest request) {
        Member member = checkJwtToken(request);
        if(member == null){
            throw new InvalidTokenException("로그인 하세요.");
        }

        Comment comment = commentRepository.findById(commentid).orElseThrow(
                () -> new IllegalArgumentException("댓글이 존재하지 않습니다.")
        );
        if(member.getUsername().equals(comment.getMember().getUsername()) || member.getRole() == UserRoleEnum.ADMIN){
            commentRepository.deleteById(commentid);
        }else{
            throw new UnauthorizedUserException("작성자만 댓글을 삭제할 수 있습니다.");
        }
       return new CommentDeleteResponseDto();

    }

    //토큰 유효 확인
    public Member checkJwtToken(HttpServletRequest request){
        String token = jwtUtil.resolveToken(request);
        Claims claims;
        if(token != null){
            if(jwtUtil.validateToken(token)){
                claims = jwtUtil.getUserInfoFromToken(token);
            }else{
                throw new IllegalArgumentException("Token Error");
            }

            Member member = memberRepository.findByUsername(claims.getSubject()).orElseThrow(
                    () -> new IllegalArgumentException("사용자가 존재하지 않습니다")
            );
            return member;
        }else {
            return null;
        }
    }
}
