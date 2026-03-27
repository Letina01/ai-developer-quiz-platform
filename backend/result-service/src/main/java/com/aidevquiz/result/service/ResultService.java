package com.aidevquiz.result.service;

import com.aidevquiz.result.dto.CreateResultRequest;
import com.aidevquiz.result.dto.ResultResponse;
import java.util.List;

public interface ResultService {
    ResultResponse create(CreateResultRequest request);
    List<ResultResponse> getByUser(Long userId);
}
