package com.finbit.settlement.controller;

import com.finbit.settlement.dto.beta.BetaFeedMessage;
import com.finbit.settlement.model.SportEventMessage;
import com.finbit.settlement.service.SportEventService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/provider-beta")
public class ProviderBetaController {

    private final SportEventService sportEventService;

    public ProviderBetaController(SportEventService sportEventService) {
        this.sportEventService = sportEventService;
    }

    @PostMapping("/feed")
    public ResponseEntity<SportEventMessage> handleFeed(@RequestBody BetaFeedMessage message) {
        SportEventMessage result = sportEventService.processBetaFeed(message);
        return ResponseEntity.ok(result);
    }
}
