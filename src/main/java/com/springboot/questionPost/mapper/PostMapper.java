package com.springboot.questionPost.mapper;

import com.springboot.questionPost.dto.PostDto;
import com.springboot.questionPost.entity.QuestionPost;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.time.LocalDateTime;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PostMapper {
    QuestionPost postDtoToQuestionPost (PostDto.Post postDto);
    QuestionPost patchDtoToQuestionPost(PostDto.Patch patchDto);
    default PostDto.Response QuestionToResponseDto(QuestionPost questionPost){
        // post upload시 1분동안 new가 생성되는 기능 -> 근데 시간이 지나도 false로 변경이 안됨.

        return new PostDto.Response(
                questionPost.getPostId(),
                questionPost.getTitle(),
                questionPost.getContent(),
                questionPost.getQuestionStatus(),
                questionPost.getOpenStatus(),
                questionPost.getCreatedAt().plusDays(2).isAfter(LocalDateTime.now()),
                questionPost.getLikeCount(),
                questionPost.getViewCount(),
                questionPost.getCreatedAt(),
                questionPost.getModifiedAt());

    }
    List<PostDto.Response>QuestionPostsToResponseDtos(List<QuestionPost>posts);

}
