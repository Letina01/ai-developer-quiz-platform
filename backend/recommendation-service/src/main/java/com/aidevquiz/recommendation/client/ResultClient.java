package com.aidevquiz.recommendation.client;

import com.aidevquiz.recommendation.dto.ResultSummary;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "result-service")
public interface ResultClient {

    @GetMapping("/api/results/users/{userId}")
    List<ResultSummary> getResults(@PathVariable("userId") Long userId);
}
