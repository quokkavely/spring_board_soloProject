package com.springboot.questionPost.cotroller;

import com.springboot.questionPost.dto.PostDto;
import com.springboot.questionPost.entity.QuestionPost;
import com.springboot.questionPost.mapper.PostMapper;
import com.springboot.questionPost.service.QuestionPostService;
import com.springboot.response.MultiResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.constraints.Positive;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/v3/posts")
@Validated
public class QuestionPostController {
    private final PostMapper mapper;
    private final QuestionPostService postService;
    private final static String QUESTION_DEFAULT_URL="/v3/posts";

    public QuestionPostController(PostMapper mapper, QuestionPostService postService) {
        this.mapper = mapper;
        this.postService = postService;
    }

    @PostMapping
    public ResponseEntity postQuestionPost(@RequestBody PostDto.Post postDto,
                                           Authentication authentication){
        QuestionPost questionPost = postService
                .createQuestionPost(mapper.postDtoToQuestionPost(postDto), authentication);
        URI location = UriComponentsBuilder
                .newInstance()
                .path(QUESTION_DEFAULT_URL+"/{postId}")
                .buildAndExpand(questionPost.getPostId())
                .toUri();

        return ResponseEntity.created(location).build();

    }
    @PatchMapping("/{postId}")
   public ResponseEntity patchQuestionPost(@PathVariable("postId") @Positive long postId,
                                           @RequestBody PostDto.Patch patchDto,
                                           Authentication authentication){
        patchDto.setPostId(postId);
       QuestionPost questionPost= postService.updateQuestionPost(mapper.patchDtoToQuestionPost(patchDto),authentication);
        return new ResponseEntity(mapper.QuestionToResponseDto(questionPost), HttpStatus.OK);
    }

    @GetMapping("/{postId}")
    public ResponseEntity getQuestionPost(@PathVariable("postId") @Positive long postId,
                                          Authentication authentication){

       return new ResponseEntity(mapper.QuestionToResponseDto
               ( postService.findQuestionPost(postId,authentication )),
               HttpStatus.OK );
    }

    @GetMapping
    public ResponseEntity getQuestionPosts(@RequestParam @Positive int page,
                                           @RequestParam @Positive int size,
                                           @RequestParam String sort){
        Page<QuestionPost> pagePosts =postService.findQuestionPosts(sort,page-1,size);
        List<QuestionPost> posts = pagePosts.getContent();
        return new ResponseEntity<>(
                new MultiResponseDto<>(
                        mapper.QuestionPostsToResponseDtos(posts),pagePosts),HttpStatus.OK);
    }
    @DeleteMapping("/{postId}")
    public ResponseEntity deleteQuestionPosts(@PathVariable("postId")long postId,
                                              Authentication authentication){
        postService.deleteQuestionPost(postId,authentication);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/{postId}/like")
    public ResponseEntity getLikeQuestionPostsWithConfirmMember(@PathVariable("postId") @Positive long postId,
                                                                Authentication authentication){
        postService.addLike(postId,authentication);

        return new ResponseEntity(HttpStatus.OK );
    }

}
