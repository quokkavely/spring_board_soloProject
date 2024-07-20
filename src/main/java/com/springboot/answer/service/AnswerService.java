package com.springboot.answer.service;

import com.springboot.answer.entity.AnswerPost;
import com.springboot.answer.repository.AnswerRepository;
import com.springboot.exception.BusinessLogicException;
import com.springboot.exception.ExceptionCode;
import com.springboot.member.entity.Member;
import com.springboot.member.service.MemberService;
import com.springboot.questionPost.entity.QuestionPost;
import com.springboot.questionPost.service.QuestionPostService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class AnswerService {
    private final AnswerRepository answerRepository;
    private final QuestionPostService postService;
    private final MemberService memberService;


    public AnswerService(AnswerRepository answerRepository, QuestionPostService postService, MemberService memberService) {
        this.answerRepository = answerRepository;
        this.postService = postService;
        this.memberService = memberService;
    }


    public AnswerPost createAnswer(AnswerPost answerPost){
        AnswerPost uploadAnswer = possibleAnswerPost(answerPost);
        QuestionPost questionPost = uploadAnswer.getQuestionPost();

        //상태가 answered 이면 이미 답변이 등록된 상태이므로 post를 등록할 수 없으니 예외던지기.
        if (questionPost.getQuestionStatus().equals(QuestionPost.QuestionStatus.QUESTION_ANSWERED)) {
            throw new BusinessLogicException(ExceptionCode.POST_ALREADY_EXIST);
        }

        //질문글과 공개상태를 동일하게 설정
        uploadAnswer.setOpenStatus(questionPost.getOpenStatus());
       //답변이 등록 될때 질문글의 상태는 answered 로 변경한다.
        questionPost.setQuestionStatus(QuestionPost.QuestionStatus.QUESTION_ANSWERED);
      //여기 수정해야됨  postService.updateQuestionPost(questionPost);
        //admin은 모든 권한을 수정할 수 있게 설정하기
        return answerRepository.save(uploadAnswer);
    }

    public AnswerPost updateAnswer(AnswerPost answerPost){
        //어짜피 관리자만 수정할 수 있기 때문에 접근권한 설정 필요 없음.
       AnswerPost findAnswer = findVerifiedAnswerPost(answerPost.getAnswerId());
       Optional.ofNullable(answerPost.getTitle()).ifPresent(findAnswer::setTitle);
       Optional.ofNullable(answerPost.getReplyContent()).ifPresent(findAnswer::setReplyContent);

       //답변 오픈 상태는 질문 상태에 따라 설정된다.

       //수정한 시간 저장
        findAnswer.setModifiedAt(LocalDateTime.now());
        return answerRepository.save(findAnswer);
    }

    public AnswerPost findAnswer(long answerId){

        // Private, public 둘다 볼 수 있기 때문에 조회에서 공개나 비공개를 설정할 필요가 없다.
        //답변이 비공개라면 등록한 회원만 보낼 수 있다.
        return findVerifiedAnswerPost(answerId);
    }

    //답변만 조회할 필요가 없을 것 같아서 주석처리 했다.
//    public Page<AnswerPost> findAnswers(int page, int size){
//        Pageable pageable = PageRequest.of(page,size, Sort.by("createdAt").ascending());
//        return answerRepository.findAll(pageable);
//    }

    public void deleteAnswer(long answerId){
        //answer 는 관리자만 접근가능 하기 때문에 확인할 필요 없다.

        AnswerPost findAnswerPost = findVerifiedAnswerPost(answerId);
        answerRepository.delete(findAnswerPost);
    }

    private AnswerPost findVerifiedAnswerPost(long answerId){
        Optional<AnswerPost> optionalAnswerPost = answerRepository.findById(answerId);

        return optionalAnswerPost.orElseThrow(()-> new BusinessLogicException(ExceptionCode.POST_NOT_FOUND));
    }

    //답변 가능한 게시글인지 확인하는 메서드
    private AnswerPost possibleAnswerPost(AnswerPost answerPost) {
        //질문이 존재하는지 검증하고 연결.
        long questionId = answerPost.getQuestionPost().getPostId();
        QuestionPost findQuestionPost = postService.findVerifiedPost(questionId);
        answerPost.setQuestionPost(findQuestionPost);
        return answerPost;
    }


}
