package com.springboot.questionPost.dto;

import com.springboot.member.entity.Member;
import com.springboot.questionPost.entity.QuestionPost;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;


public class PostDto {
    @Getter
    @AllArgsConstructor
    public static class Post{
        private String title;
        private String content;
        private QuestionPost.OpenStatus openStatus;


//        public Member getMember() {
//            Member member = new Member();
//            member.setMemberId(memberId);
//            return member;
//        }

    }
    @Getter
    @AllArgsConstructor
    public static class Patch{
        @Setter
        private long postId;
        private String title;
        private String content;
        private QuestionPost.QuestionStatus questionStatus;
        private QuestionPost.OpenStatus openStatus;

    }
    @Getter
    @AllArgsConstructor
    public static class Response{
        private long postId;
        private String title;
        private String content;
        private QuestionPost.QuestionStatus questionStatus;
        private QuestionPost.OpenStatus openStatus;
        private Boolean displayNew;
        private int likeCount;
        private int viewCount;
        private LocalDateTime createdAt;
        private LocalDateTime modifiedAt;

    }
}
