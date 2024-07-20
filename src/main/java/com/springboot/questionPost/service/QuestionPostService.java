package com.springboot.questionPost.service;


import com.springboot.answer.entity.AnswerPost;
import com.springboot.exception.BusinessLogicException;
import com.springboot.exception.ExceptionCode;
import com.springboot.likes.LikePost;
import com.springboot.likes.LikesRepository;
import com.springboot.member.entity.Member;
import com.springboot.member.service.MemberService;
import com.springboot.questionPost.entity.QuestionPost;
import com.springboot.questionPost.repository.PostRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Optional;


@Service
public class QuestionPostService {
    private final PostRepository postRepository;
    private final MemberService memberService;
    private final LikesRepository likesRepository;

    public QuestionPostService(PostRepository postRepository, MemberService memberService, LikesRepository likesRepository) {
        this.postRepository = postRepository;
        this.memberService = memberService;
        this.likesRepository = likesRepository;
    }

    public QuestionPost createQuestionPost(QuestionPost questionPost){
        //회원만 질문가능 -> 현재는 memberId를 넣어야 생성가능.
        memberService.findVerifiedMember(questionPost.getMember().getMemberId());

        publicOrPrivate(questionPost); //postDto 에서 null 이면 public으로 구현, null이 아니면 설정값으로 들어오게 함.
        addDisplayNew(questionPost);

        return postRepository.save(questionPost);
    }

    public QuestionPost updateQuestionPost(QuestionPost questionPost){
        QuestionPost findPost = findVerifiedPost(questionPost.getPostId());

        Optional.ofNullable(questionPost.getTitle()).ifPresent(findPost::setTitle);
        Optional.ofNullable(questionPost.getQuestionStatus()).ifPresent(findPost::setQuestionStatus);
        Optional.ofNullable(questionPost.getContent()).ifPresent(findPost::setContent);

        //여기서 공개설정 수정되면 Answer 도 같이 변경해야 함. -> 완료
        Optional.ofNullable(questionPost.getOpenStatus()).ifPresent(findPost::setOpenStatus);
        if(findPost.getQuestionStatus().equals(QuestionPost.QuestionStatus.QUESTION_ANSWERED)
                && findPost.getAnswerPost()!=null){
            AnswerPost answerPost = findPost.getAnswerPost();
            answerPost.setOpenStatus(findPost.getOpenStatus());
        }


        findPost.setModifiedAt(LocalDateTime.now());
        return postRepository.save(findPost);

    }

    public QuestionPost findQuestionPost(long postId){
        QuestionPost questionPost =findVerifiedPost(postId);

        return postRepository.save(questionPost);
    }

    public Page<QuestionPost> findQuestionPosts(String sort ,int page, int size){
        Pageable pageable = sortingPost(sort,page,size);


        return postRepository
                .findByQuestionStatusNotAndOpenStatus
                        (QuestionPost.QuestionStatus.QUESTION_DELETED,
                                QuestionPost.OpenStatus.PUBLIC, pageable);


    }

    public void deleteQuestionPost(long postId){
        QuestionPost findQuestionPost = findVerifiedPost(postId);

        findQuestionPost.setQuestionStatus(QuestionPost.QuestionStatus.QUESTION_DELETED);
        findQuestionPost.setModifiedAt(LocalDateTime.now());
        postRepository.save(findQuestionPost);
       //삭제는 되면  안되고 상태만 변경할 수 있다.

    }

     public QuestionPost findVerifiedPost(long postId){
        Optional<QuestionPost>optionalQuestionPost =postRepository.findById(postId);
        return optionalQuestionPost.orElseThrow(()->new BusinessLogicException(ExceptionCode.POST_NOT_FOUND));
    }

    private void publicOrPrivate(QuestionPost questionPost){
        Optional.ofNullable(questionPost.getOpenStatus())
                .ifPresentOrElse(
                        value->{
                            questionPost.setOpenStatus(value);
                        }, () -> questionPost.setOpenStatus(QuestionPost.OpenStatus.PUBLIC)
                );

    }

    //좋아요 기능
    @Transactional
    public void addLike(long postId, long memberId){
        QuestionPost questionPost = findVerifiedPost(postId);
        Member member = memberService.findVerifiedMember(memberId);
        if(!likesRepository.existsByMemberAndQuestionPost(member,questionPost)){
            questionPost.setLikeCount(questionPost.getLikeCount()+1);
            likesRepository.save(new LikePost(member,questionPost));
        }else{
            questionPost.setLikeCount(questionPost.getLikeCount()-1);
            likesRepository.deleteByMemberAndQuestionPost(member,questionPost);
        }
    }

    //조회수 증가 , 마찬가지로 post가 삭제되면 볼 수 없어야 함..
    public int updateViews(long postId,long memberId){
        Member member = memberService.findVerifiedMember(memberId);
        findVerifiedPost(postId);
        return postRepository.updateView(postId);
    }

    // post upload시 new가 생성되는 기능 -> 근데 시간이 지나도 false로 변경이 안됨.
    public QuestionPost addDisplayNew(QuestionPost questionPost){
        LocalDateTime currentTime = LocalDateTime.now();
        LocalDateTime getTime = questionPost.getCreatedAt();

        if(getTime.plusMinutes(1).isBefore(currentTime)){
            questionPost.setDisplayNew(false);
        }
        return questionPost;
    }

    private Pageable sortingPost(String sorting, int page, int size){
        Pageable pageable ;
        switch (sorting) {
            case "like_desc" :
                pageable = PageRequest.of(page, size, Sort.by("likeCount").descending());
                break;
            case "like_asc" :
                pageable = PageRequest.of(page, size, Sort.by("likeCount").ascending());
                break;
            case "view_desc" :
                pageable = PageRequest.of(page, size, Sort.by("viewCount").descending());
                break;
            case "view_asc" :
                pageable = PageRequest.of(page, size, Sort.by("viewCount").ascending());
                break;
            case "time_asc" :
                pageable = PageRequest.of(page, size, Sort.by("createdAt").ascending());
                break;
            default:
                pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        }
        return pageable;
    }


}
