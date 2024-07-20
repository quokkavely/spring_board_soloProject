package com.springboot.answer.mapper;

import com.springboot.answer.dto.AnswerDto;
import com.springboot.answer.entity.AnswerPost;
import com.springboot.questionPost.entity.QuestionPost;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AnswerMapper {

    default AnswerPost postDtoToAnswerPost (AnswerDto.Post postDto){
        QuestionPost questionPost = new QuestionPost();
        questionPost.setPostId(postDto.getQuestionPostId());
        AnswerPost answerPost = new AnswerPost(postDto.getTitle(), postDto.getReplyContent());
        answerPost.setQuestionPost(questionPost);
        return answerPost;
    }
    AnswerPost patchDtoToAnswerPost(AnswerDto.Patch patchDto);
    AnswerDto.Response answerPostToResponseDto(AnswerPost answerPost);
    List<AnswerDto.Response> QuestionPostsToResponseDtos(List<AnswerPost>posts);
}
