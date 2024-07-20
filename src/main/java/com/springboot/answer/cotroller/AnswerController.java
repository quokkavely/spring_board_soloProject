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
@RequestMapping("/v2/answers")
public class AnswerController {
    private final AnswerMapper answerMapper;
    private final AnswerService answerService;
    private final static String ANSWER_DEFAULT_URL="/v2/answers";

    public AnswerController(AnswerMapper answerMapper, AnswerService answerService) {
        this.answerMapper = answerMapper;
        this.answerService = answerService;
    }

    @PostMapping
    public ResponseEntity postAnswer(@RequestBody AnswerDto.Post answerPostDto){
      AnswerPost answerPost = answerService
              .createAnswer(answerMapper.postDtoToAnswerPost(answerPostDto));
        URI location = UriComponentsBuilder
                .newInstance()
                .path(ANSWER_DEFAULT_URL+"/{answerId}")
                .buildAndExpand(answerPost.getAnswerId())
                .toUri();

        return ResponseEntity.created(location).build();
    }

    @PatchMapping("/{answerId}")
    public ResponseEntity patchAnswer(@PathVariable("answerId") long answerId,
                                      @RequestBody AnswerDto.Patch answerPatchDto){

        answerPatchDto.setAnswerId(answerId);
        AnswerPost answerPost =answerService
                .updateAnswer( answerMapper.patchDtoToAnswerPost(answerPatchDto));
        return new ResponseEntity( answerMapper.answerPostToResponseDto(answerPost), HttpStatus.OK);
    }

    @GetMapping("/{answerId}")
    public ResponseEntity getAnswer( @PathVariable("answerId") @Positive long answerId){
        AnswerPost answerPost= answerService.findAnswer(answerId);
        return new ResponseEntity(answerMapper.answerPostToResponseDto(answerPost),HttpStatus.OK);
    }

//    @GetMapping
//    public ResponseEntity getAnswers(@Positive @RequestParam int page,
//                                     @Positive @RequestParam int size){
//
//        Page<AnswerPost>pageAnswers = answerService.findAnswers(page-1,size);
//        List<AnswerPost> answerPosts = pageAnswers.getContent();
//        return new ResponseEntity(
//                new MultiResponseDto<>(
//                        answerMapper.QuestionPostsToResponseDtos(answerPosts),pageAnswers),HttpStatus.OK);
//    }

    @DeleteMapping("/{answerId}")
    public ResponseEntity deleteAnswer( @PathVariable("answerId")long answerId ) {
        answerService.deleteAnswer( answerId ) ;
        return new ResponseEntity( HttpStatus.NO_CONTENT );
    }
}
