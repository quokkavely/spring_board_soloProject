package com.springboot.answer.repository;

import com.springboot.answer.entity.AnswerPost;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnswerRepository extends JpaRepository<AnswerPost, Long> {
}
