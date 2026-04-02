package com.finbit.settlement.controller;

import com.finbit.settlement.dto.alpha.AlphaFeedMessage;
import com.finbit.settlement.model.SportEventMessage;
import com.finbit.settlement.service.SportEventService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/provider-alpha")
public class ProviderAlphaController {

    private final SportEventService sportEventService;

    public ProviderAlphaController(SportEventService sportEventService) {
        this.sportEventService = sportEventService;
    }

    @PostMapping("/feed")
    public ResponseEntity<SportEventMessage> handleFeed(@RequestBody AlphaFeedMessage message) {
        SportEventMessage result = sportEventService.processAlphaFeed(message);
        return ResponseEntity.ok(result);
    }
}
