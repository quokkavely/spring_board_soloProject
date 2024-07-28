package com.springboot.likes;

import com.springboot.member.entity.Member;
import com.springboot.questionPost.entity.QuestionPost;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LikesRepository extends JpaRepository<LikePost, Long> {
    boolean existsByMemberAndQuestionPost(Member member, QuestionPost questionPost);

    void deleteByMemberAndQuestionPost(Member member, QuestionPost questionPost);
}