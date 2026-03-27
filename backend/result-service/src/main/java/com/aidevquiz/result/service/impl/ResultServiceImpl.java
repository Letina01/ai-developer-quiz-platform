package com.aidevquiz.result.service.impl;

import com.aidevquiz.result.dto.CreateResultRequest;
import com.aidevquiz.result.dto.ResultResponse;
import com.aidevquiz.result.entity.Result;
import com.aidevquiz.result.repository.ResultRepository;
import com.aidevquiz.result.service.ResultService;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ResultServiceImpl implements ResultService {

    private final ResultRepository resultRepository;

    public ResultServiceImpl(ResultRepository resultRepository) {
        this.resultRepository = resultRepository;
    }

    @Override
    public ResultResponse create(CreateResultRequest request) {
        Result result = new Result();
        result.setUserId(request.userId());
        result.setQuizId(request.quizId());
        result.setDomain(request.domain());
        result.setTopic(request.topic());
        result.setDifficulty(request.difficulty());
        result.setScore(request.score());
        result.setTotalQuestions(request.totalQuestions());
        return map(resultRepository.save(result));
    }

    @Override
    public List<ResultResponse> getByUser(Long userId) {
        return resultRepository.findByUserIdOrderByCreatedAtDesc(userId).stream().map(this::map).toList();
    }

    private ResultResponse map(Result result) {
        return new ResultResponse(
                result.getId(),
                result.getUserId(),
                result.getQuizId(),
                result.getDomain(),
                result.getTopic(),
                result.getDifficulty(),
                result.getScore(),
                result.getTotalQuestions(),
                result.getCreatedAt()
        );
    }
}
