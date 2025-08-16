package com.example.sentiment.controller;

import com.example.sentiment.model.CommentRequest;
import com.example.sentiment.model.SentimentResponse;
import com.example.sentiment.service.SentimentService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sentiment")
public class SentimentController {

    private final SentimentService service;

    public SentimentController(SentimentService service) {
        this.service = service;
    }

    @PostMapping
    public SentimentResponse analyze(@RequestBody CommentRequest request) {
        return service.analyze(request.getText());
    }
}
