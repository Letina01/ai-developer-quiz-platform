package com.aidevquiz.quiz.repository;

import com.aidevquiz.quiz.entity.Quiz;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuizRepository extends JpaRepository<Quiz, Long> {
    List<Quiz> findByCreatedByOrderByCreatedAtDesc(Long createdBy);
}
