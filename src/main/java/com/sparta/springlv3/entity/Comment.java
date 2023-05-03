package com.sparta.springlv3.entity;

import com.sparta.springlv3.dto.CommentRequestDto;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
public class Comment extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne
    @JoinColumn(name = "board_id")
    private Board board;

    @Column(nullable = false)
    private String content;

    public Comment(CommentRequestDto requestDto, Member member, Board board) { // 생성자에 Member와 Board 객체를 전달받음
        this.member = member;
        this.content = requestDto.getContent();
        this.board = board;
    }

    public void update(CommentRequestDto commentRequestDto){
        this.content = commentRequestDto.getContent();
    }
}
