package com.aidevquiz.result.repository;

import com.aidevquiz.result.entity.Result;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResultRepository extends JpaRepository<Result, Long> {
    List<Result> findByUserIdOrderByCreatedAtDesc(Long userId);
}
