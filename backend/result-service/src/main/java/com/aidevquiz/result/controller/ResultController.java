package com.aidevquiz.result.controller;

import com.aidevquiz.result.dto.CreateResultRequest;
import com.aidevquiz.result.dto.ResultResponse;
import com.aidevquiz.result.service.ResultService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/results")
public class ResultController {

    private static final Logger log = LoggerFactory.getLogger(ResultController.class);

    private final ResultService resultService;

    public ResultController(ResultService resultService) {
        this.resultService = resultService;
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of("status", "OK", "service", "result-service"));
    }

    @PostMapping
    public ResultResponse create(
            @RequestHeader(value = "X-User-Id", required = false) Long authenticatedUserId,
            @Valid @RequestBody CreateResultRequest request
    ) {
        log.info("Create result called with userId: {}", authenticatedUserId);
        if (authenticatedUserId == null) {
            throw new IllegalArgumentException("X-User-Id header is missing");
        }
        return resultService.create(authenticatedUserId, request);
    }

    @GetMapping("/users/me")
    public List<ResultResponse> getMyResults(
            @RequestHeader(value = "X-User-Id", required = false) Long authenticatedUserId
    ) {
        log.info("Get my results called - X-User-Id header value: {}", authenticatedUserId);
        if (authenticatedUserId == null) {
            throw new IllegalArgumentException("X-User-Id header is missing. Please login again.");
        }
        return resultService.getByUser(authenticatedUserId);
    }

    @GetMapping("/users/{userId}")
    public List<ResultResponse> getResultsByUserId(
            @PathVariable("userId") Long userId,
            @RequestHeader(value = "X-User-Id", required = false) Long authenticatedUserId
    ) {
        log.info("Get results for userId: {}", userId);
        return resultService.getByUser(userId);
    }

    @GetMapping("/debug/headers")
    public ResponseEntity<Map<String, String>> debugHeaders(
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @RequestHeader(value = "Authorization", required = false) String auth
    ) {
        return ResponseEntity.ok(Map.of(
                "X-User-Id", userId != null ? userId : "NOT PRESENT",
                "Authorization", auth != null ? (auth.length() > 20 ? auth.substring(0, 20) + "..." : auth) : "NOT PRESENT"
        ));
    }
}
