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
    @Mapping(source ="postId", target = "questionPost.postId")
    AnswerPost postDtoToAnswerPost (AnswerDto.Post postDto);
    AnswerPost patchDtoToAnswerPost(AnswerDto.Patch patchDto);

    @Mapping(source="questionPost.postId", target = "postId")
    AnswerDto.Response answerPostToResponseDto(AnswerPost answerPost);
    List<AnswerDto.Response> QuestionPostsToResponseDtos(List<AnswerPost>posts);
}
