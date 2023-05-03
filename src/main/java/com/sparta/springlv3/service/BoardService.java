package com.sparta.springlv3.service;


import com.sparta.springlv3.dto.BoardRequestDto;
import com.sparta.springlv3.dto.BoardResponseDto;
import com.sparta.springlv3.entity.Board;
import com.sparta.springlv3.entity.Member;
import com.sparta.springlv3.exception.InvalidTokenException;
import com.sparta.springlv3.exception.UnauthorizedUserException;
import com.sparta.springlv3.jwt.JwtUtil;
import com.sparta.springlv3.repository.BoardRepository;
import com.sparta.springlv3.repository.MemberRepository;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BoardService {
    public final BoardRepository boardRepository ;
    private final MemberRepository memberRepository;
    private final JwtUtil jwtUtil;


    //게시글 작성
    @Transactional
    public BoardResponseDto createBoard(BoardRequestDto requestDto, HttpServletRequest request) {
        Member member = checkJwtToken(request);
        if (member == null) {
            throw new InvalidTokenException("로그인이 필요합니다.");
        }

        Board board = new Board(requestDto, member);

        return new BoardResponseDto(boardRepository.save(board));


    }


    //게시글 목록 조회
    @Transactional(readOnly = true)
    public List<BoardResponseDto> getList() {
        return boardRepository.findAllByOrderByModifiedAtDesc().stream()
                .map(BoardResponseDto::new).collect(Collectors.toList());
    }

    //선택한 게시글 조회
    @Transactional(readOnly = true)
    public BoardResponseDto getBoard(Long id){
        Board board = boardRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("아이디가 틀립니다.")
        );
        return new BoardResponseDto(board);
    }

    //게시글 수정
    @Transactional
    public BoardResponseDto update(Long id, BoardRequestDto requestDto, HttpServletRequest request) {
        Member member = checkJwtToken(request);

        Board board = boardRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("게시글이 존재하지 않습니다.")
        );

        if (!board.getMember().getId().equals(member.getId())) {
            throw new UnauthorizedUserException("게시글 작성자만 수정할 수 있습니다.");
        }

        board.update(requestDto);
        return new BoardResponseDto(board);
    }

    //게시글 삭제
    @Transactional
    public  String deleteBoard(Long id, HttpServletRequest request) {
        Member member = checkJwtToken(request);

        Board board = boardRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("게시글이 존재하지 않습니다.")
        );
        boardRepository.deleteById(id);

        return "게시글 삭제 성공.";
    }

    public Member checkJwtToken(HttpServletRequest request){
        String token = jwtUtil.resolveToken(request);
        Claims claims;
        if(token != null){
            if(jwtUtil.validateToken(token)){
                claims = jwtUtil.getUserInfoFromToken(token);
            }else{
                throw new InvalidTokenException("Token Error");
            }

            Member member = memberRepository.findByUsername(claims.getSubject()).orElseThrow(
                    () -> new UnauthorizedUserException("사용자가 존재하지 않습니다")
            );
            return member;
        }else {
            return null;
        }
    }
}