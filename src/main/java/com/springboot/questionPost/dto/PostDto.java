package com.springboot.questionPost.dto;

import com.springboot.member.entity.Member;
import com.springboot.questionPost.entity.QuestionPost;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;


public class PostDto {
    @Getter
    @AllArgsConstructor
    public static class Post{
        private long memberId;
        private String title;
        private String content;
        private QuestionPost.OpenStatus openStatus;


        public Member getMember() {
            Member member = new Member();
            member.setMemberId(memberId);
            return member;
        }

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
        private boolean displayNew;
        private int likeCount;
        private int viewCount;

    }
}
