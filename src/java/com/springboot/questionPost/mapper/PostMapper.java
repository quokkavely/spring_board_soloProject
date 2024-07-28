package com.springboot.questionPost.mapper;

import com.springboot.answer.dto.AnswerDto;
import com.springboot.answer.entity.AnswerPost;
import com.springboot.questionPost.dto.PostDto;
import com.springboot.questionPost.entity.QuestionPost;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.time.LocalDateTime;
import java.util.List;

@Mapper(componentModel = "spring")
public interface PostMapper {
    QuestionPost postDtoToQuestionPost (PostDto.Post postDto);

    QuestionPost patchDtoToQuestionPost(PostDto.Patch patchDto);

    default PostDto.Response QuestionToResponseDto(QuestionPost questionPost){

        if (questionPost.getAnswerPost()==null){
            questionPost.setAnswerPost
                    (new AnswerPost("답변 대기",
                            "질문을 확인했습니다. 답변이 대기될때까지 조금만 기다려주세요")
            );
        }

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
                questionPost.getModifiedAt(),
                questionPost.getAnswerPost().getTitle(),
                questionPost.getAnswerPost().getReplyContent()
        );

    }
    List<PostDto.Response>QuestionPostsToResponseDtos(List<QuestionPost>posts);

}
