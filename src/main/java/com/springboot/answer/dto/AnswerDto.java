package com.springboot.answer.dto;

import com.springboot.answer.entity.AnswerPost;
import com.springboot.questionPost.entity.QuestionPost;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

public class AnswerDto {
    @Getter
    @AllArgsConstructor
    public static class Post{
        private long questionPostId;
        private String title;
        private String replyContent;


    }
    @Getter
    @AllArgsConstructor
    public static class Patch{
        @Setter
        private long answerId;
        private String title;
        private String replyContent;
        private QuestionPost.OpenStatus openStatus;


    }
    @Getter
    @AllArgsConstructor
    public static class Response{
        private long answerId;
        private String title;
        private String replyContent;
        private QuestionPost.OpenStatus openStatus;
        private LocalDateTime createdAt;
        private LocalDateTime modifiedAt;


    }
}