package com.springboot.answer.cotroller;

import com.springboot.answer.dto.AnswerDto;
import com.springboot.answer.entity.AnswerPost;
import com.springboot.answer.mapper.AnswerMapper;
import com.springboot.answer.service.AnswerService;
import com.springboot.response.MultiResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.constraints.Positive;
import java.net.URI;
import java.util.List;

@Validated
@RestController
@RequestMapping("/v3/posts/{postId}/answers")
public class AnswerController {
    private final AnswerMapper answerMapper;
    private final AnswerService answerService;
    private final static String ANSWER_DEFAULT_URL="/v3/posts/{postId}/answers";

    public AnswerController(AnswerMapper answerMapper, AnswerService answerService) {
        this.answerMapper = answerMapper;
        this.answerService = answerService;
    }

    @PostMapping
    public ResponseEntity postAnswer(@PathVariable("postId") @Positive long postId,
                                     @RequestBody AnswerDto.Post answerPostDto,
                                     Authentication authentication) {

        answerPostDto.setPostId(postId);
        AnswerPost answerPost = answerService
              .createAnswer(answerMapper.postDtoToAnswerPost(answerPostDto));
        URI location = UriComponentsBuilder
                .newInstance()
                .path(ANSWER_DEFAULT_URL+"/{answerId}")
                .buildAndExpand(postId,answerPost.getAnswerId())
                .toUri();

        return ResponseEntity.created(location).build();
    }

    @PatchMapping("/{answerId}")
    public ResponseEntity patchAnswer(@PathVariable("postId") long postId,
                                      @PathVariable("answerId") long answerId,
                                      @RequestBody AnswerDto.Patch answerPatchDto,
                                      Authentication authentication){
        answerPatchDto.setPostId(postId);
        answerService.findVerifiedAnswerPost(answerId); //꼭 해야되는지 체크해보기.
        answerPatchDto.setAnswerId(answerId);


        AnswerPost answerPost =answerService
                .updateAnswer( answerMapper.patchDtoToAnswerPost(answerPatchDto) );
        return new ResponseEntity( answerMapper.answerPostToResponseDto(answerPost), HttpStatus.OK);
    }

    @GetMapping("/{answerId}")
    public ResponseEntity getAnswer( @PathVariable("answerId") @Positive long answerId,
                                     Authentication authentication) {
        AnswerPost answerPost= answerService.findAnswer(answerId);
        return new ResponseEntity(answerMapper.answerPostToResponseDto(answerPost),HttpStatus.OK);
    }


    @DeleteMapping("/{answerId}")
    public ResponseEntity deleteAnswer( @PathVariable("answerId")long answerId,
                                        Authentication authentication) {
        answerService.deleteAnswer( answerId ) ;
        return new ResponseEntity( HttpStatus.NO_CONTENT );
    }
}
