package com.springboot.questionPost.service;


import com.springboot.answer.entity.AnswerPost;
import com.springboot.event.PostRegisterEvent;
import com.springboot.exception.BusinessLogicException;
import com.springboot.exception.ExceptionCode;
import com.springboot.likes.LikePost;
import com.springboot.likes.LikesRepository;
import com.springboot.member.entity.Member;
import com.springboot.member.service.MemberService;
import com.springboot.questionPost.entity.QuestionPost;
import com.springboot.questionPost.repository.PostRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Optional;


@Service
public class QuestionPostService {
    private final PostRepository postRepository;
    private final MemberService memberService;
    private final LikesRepository likesRepository;
    private final ApplicationEventPublisher publisher;

    public QuestionPostService(PostRepository postRepository, MemberService memberService, LikesRepository likesRepository, ApplicationEventPublisher publisher) {
        this.postRepository = postRepository;
        this.memberService = memberService;
        this.likesRepository = likesRepository;
        this.publisher = publisher;
    }

    @Transactional
    public QuestionPost createQuestionPost(QuestionPost questionPost, Authentication authentication) {
        //회원만 질문가능 -> 현재는 memberId를 넣어야 생성가능 -> authentication으로 memberId 제거
        String user = (String) authentication.getPrincipal();
        Member member = memberService.findEmailVerifiedMember(user);

        publicOrPrivate(questionPost);
        //postDto 에서 null 이면 public으로 구현, null이 아니면 설정값으로 들어오게 함.

        questionPost.setMember(member);

        publisher.publishEvent(new PostRegisterEvent(this, questionPost));
        return postRepository.save(questionPost);
    }

    @Transactional
    public QuestionPost updateQuestionPost(QuestionPost questionPost, Authentication authentication) {

        String user = authentication.getPrincipal().toString();
        Member findMember = memberService.findEmailVerifiedMember(user);

        //게시글 접근제한 - public/private & Delete/Deactive
        QuestionPost findPost = isPostAccessible(questionPost.getPostId(), findMember);

        //본인이 아니라면 수정할 수 없다고 예외던지기
        if(findPost.getMember().getMemberId()!=findMember.getMemberId()) {
            throw new BusinessLogicException(ExceptionCode.ONLY_ACCESSIBLE_WHAT_YOU_WRITE);
        }

        Optional.ofNullable(questionPost.getTitle()).ifPresent(findPost::setTitle);
        Optional.ofNullable(questionPost.getQuestionStatus()).ifPresent(findPost::setQuestionStatus);
        Optional.ofNullable(questionPost.getContent()).ifPresent(findPost::setContent);

        //여기서 공개설정 수정되면 Answer 도 같이 변경해야 함. -> 완료
        Optional.ofNullable(questionPost.getOpenStatus()).ifPresent(findPost::setOpenStatus);
        if( findPost.getQuestionStatus().equals(QuestionPost.QuestionStatus.QUESTION_ANSWERED )
                && findPost.getAnswerPost()!=null ) {
            AnswerPost answerPost = findPost.getAnswerPost();
            answerPost.setOpenStatus(findPost.getOpenStatus());
        }

        findPost.setModifiedAt(LocalDateTime.now());
        return postRepository.save(findPost);
    }

    public QuestionPost findQuestionPost(long postId, Authentication authentication) {
        String user = authentication.getPrincipal().toString();
        Member findMember = memberService.findEmailVerifiedMember(user);

        //게시글 접근제한 - public/private & Delete/Deactive
        QuestionPost findPost = isPostAccessible(postId, findMember);

        //조회수 증가 (작성자가 아닌 회원이 조회할 경우 증가)
        increaseViewCount(postId,findMember.getMemberId());
        return postRepository.save(findPost);

    }

    public Page<QuestionPost> findQuestionPosts(String sort ,int page, int size) {

        Pageable pageable = sortingPost(sort,page,size);
        return postRepository
                .findByQuestionStatusNotAndOpenStatus
                        (QuestionPost.QuestionStatus.QUESTION_DELETED,
                                QuestionPost.OpenStatus.PUBLIC, pageable);
    }

    @Transactional
    public void deleteQuestionPost(long postId, Authentication authentication ) {
        //삭제는 되면  안되고 상태만 변경할 수 있다. (관리자와 본인만 가능)

        String user = authentication.getPrincipal().toString();
        Member findMember = memberService.findEmailVerifiedMember(user);

        //게시글 접근제한 - public/private & Delete/Deactive
        QuestionPost findPost = isPostAccessible(postId, findMember);

        //본인이나 관리자가 아니라면 지울 수 없다고 예외던지기
        if(findPost.getMember().getMemberId()==findMember.getMemberId()
                || findMember.getRoles().contains("ADMIN")) {

            findPost.setQuestionStatus(QuestionPost.QuestionStatus.QUESTION_DELETED);
            findPost.setModifiedAt(LocalDateTime.now());
            postRepository.save(findPost);

        }else{
            throw new BusinessLogicException(ExceptionCode.ONLY_ACCESSIBLE_WHAT_YOU_WRITE);
        }

    }

    //존재하는 게시글인지 확인.
     public QuestionPost findVerifiedPost(long postId) {

        Optional<QuestionPost>optionalQuestionPost =postRepository.findById(postId);

        QuestionPost questionPost = optionalQuestionPost.orElseThrow(
                () -> new BusinessLogicException(ExceptionCode.POST_NOT_FOUND ));

        return questionPost;
    }


    //게시글 작성시 클라이언트에서 게시물의 상태를 설정했을 경우 설정값으로,
    // 설정하지 않았을 경우 public (공개상태)로 설정하는 메서드
    private void publicOrPrivate(QuestionPost questionPost) {

        Optional.ofNullable(questionPost.getOpenStatus())
                .ifPresentOrElse(
                        value->{
                            questionPost.setOpenStatus(value);
                        }, () -> questionPost.setOpenStatus(QuestionPost.OpenStatus.PUBLIC)
                );
    }


    //좋아요 기능
    @Transactional
    public void addLike(long postId, Authentication authentication) {

        String user = authentication.getPrincipal().toString();
        Member findMember = memberService.findEmailVerifiedMember(user);
        QuestionPost findPost = isPostAccessible(postId, findMember);


        if(!likesRepository.existsByMemberAndQuestionPost(findMember,findPost)) {

            findPost.setLikeCount(findPost.getLikeCount()+1);
            likesRepository.save(new LikePost(findMember, findPost));

        } else{

            findPost.setLikeCount(findPost.getLikeCount()-1);
            likesRepository.deleteByMemberAndQuestionPost(findMember, findPost);

        }
    }


    //Pagination 정렬 설정
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

    public QuestionPost isPostAccessible(long postId, Member findMember) {
        QuestionPost findPost = findVerifiedPost(postId);

        // Admin 은 모든 게시글에 접근 가능
        if(findMember.getRoles().contains("ADMIN")){
            return findPost;
        }

        // Admin 아닐 경우 삭제된 글에는 접근할 수 없다
        if( !findMember.getRoles().contains("ADMIN")
                && findPost.getQuestionStatus().equals(QuestionPost.QuestionStatus.QUESTION_DELETED )) {
            throw new BusinessLogicException(ExceptionCode.POST_NOT_FOUND);
        }

        // 작성자가 아니라면 Public 게시글에만 접근 가능, 삭제 - 비활성 게시글에는 접근 할 수 없다.
        if( findPost.getMember().getMemberId() != findMember.getMemberId() ) {
            if( !findPost.getOpenStatus().equals(QuestionPost.OpenStatus.PUBLIC) ||
                findPost.getQuestionStatus().equals(QuestionPost.QuestionStatus.QUESTION_DEACTIVED ) ||
                findPost.getQuestionStatus().equals(QuestionPost.QuestionStatus.QUESTION_DELETED )) {

                throw new BusinessLogicException(ExceptionCode.ONLY_ACCESSIBLE_WHAT_YOU_WRITE);
            }
        }
        return findPost;
    }

    //조회수 증가
    @Transactional
    private void increaseViewCount(long postId, long memberId) {
        postRepository.increaseViewCount(postId, memberId);
    }
}
