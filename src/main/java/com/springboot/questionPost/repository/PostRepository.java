package com.springboot.questionPost.repository;

import com.springboot.questionPost.entity.QuestionPost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;

public interface PostRepository extends JpaRepository<QuestionPost,Long> {
    @Modifying
    @Transactional
    @Query("update QuestionPost p set p.viewCount = p.viewCount + 1 where p.postId = :postId")
    int updateView(Long postId);

    Page<QuestionPost> findByQuestionStatusNotAndOpenStatus(QuestionPost.QuestionStatus status, QuestionPost.OpenStatus openStatus, Pageable pageable);


}
