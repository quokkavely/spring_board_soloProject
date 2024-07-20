package com.springboot.answer.service;

import com.springboot.answer.entity.AnswerPost;
import com.springboot.answer.repository.AnswerRepository;
import com.springboot.exception.BusinessLogicException;
import com.springboot.exception.ExceptionCode;
import com.springboot.member.service.MemberService;
import com.springboot.questionPost.entity.QuestionPost;
import com.springboot.questionPost.service.QuestionPostService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

    public AnswerPost createAnswer(long memberId, AnswerPost answerPost){
        //requestParam 으로 memberId로 관리자인지 검증
        memberService.isAdmin(memberId);
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
        postService.updateQuestionPost(questionPost);

        return answerRepository.save(uploadAnswer);
    }

    public AnswerPost updateAnswer(AnswerPost answerPost){
       AnswerPost findAnswer = findVerifiedAnswerPost(answerPost.getAnswerId());

       Optional.ofNullable(findAnswer.getTitle()).ifPresent(findAnswer::setTitle);
       Optional.ofNullable(findAnswer.getReplyContent()).ifPresent(findAnswer::setReplyContent);
       //시간수정
        findAnswer.setModifiedAt(LocalDateTime.now());
        return answerRepository.save(findAnswer);
    }

    public AnswerPost findAnswer(long answerId){
        return findVerifiedAnswerPost(answerId);
    }

    public Page<AnswerPost> findAnswers(int page, int size){
        Pageable pageable = PageRequest.of(page,size, Sort.by("createdAt").ascending());

        return answerRepository.findAll(pageable);
    }

    public void deleteAnswer(long memberId, long answerId){
        //관리자만 삭제할 수 있게 설정, requestParam 으로 memberId로 관리자인지 검증
        memberService.isAdmin(memberId);
        AnswerPost findAnswerPost = findVerifiedAnswerPost(answerId);
        answerRepository.delete(findAnswerPost);
    }

    private AnswerPost findVerifiedAnswerPost(long answerId){

        Optional<AnswerPost>optionalAnswerPost = answerRepository.findById(answerId);
        return optionalAnswerPost.orElseThrow(()->new BusinessLogicException(ExceptionCode.POST_NOT_FOUND));
    }

    private AnswerPost possibleAnswerPost(AnswerPost answerPost){
        //질문이 존재하는지 검증하고 연결.
        long questionId = answerPost.getQuestionPost().getPostId();
       QuestionPost findQuestionPost= postService.findVerifiedPost(questionId);
       answerPost.setQuestionPost(findQuestionPost);
        return answerPost;
    }

}
