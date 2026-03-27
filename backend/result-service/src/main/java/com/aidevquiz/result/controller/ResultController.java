package com.aidevquiz.result.controller;

import com.aidevquiz.result.dto.CreateResultRequest;
import com.aidevquiz.result.dto.ResultResponse;
import com.aidevquiz.result.service.ResultService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/results")
public class ResultController {

    private final ResultService resultService;

    public ResultController(ResultService resultService) {
        this.resultService = resultService;
    }

    @PostMapping
    public ResultResponse create(@Valid @RequestBody CreateResultRequest request) {
        return resultService.create(request);
    }

    @GetMapping("/users/{userId}")
    public List<ResultResponse> getByUser(@PathVariable("userId") Long userId) {
        return resultService.getByUser(userId);
    }
}
