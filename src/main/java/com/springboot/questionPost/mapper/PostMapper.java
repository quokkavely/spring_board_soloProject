package com.springboot.questionPost.mapper;

import com.springboot.questionPost.dto.PostDto;
import com.springboot.questionPost.entity.QuestionPost;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PostMapper {
    QuestionPost postDtoToQuestionPost (PostDto.Post postDto);
    QuestionPost patchDtoToQuestionPost(PostDto.Patch patchDto);
    PostDto.Response QuestionToResponseDto(QuestionPost questionPost);
    List<PostDto.Response>QuestionPostsToResponseDtos(List<QuestionPost>posts);

}
