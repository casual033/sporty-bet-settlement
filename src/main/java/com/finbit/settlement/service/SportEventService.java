package com.finbit.settlement.service;

import com.finbit.settlement.dto.alpha.AlphaFeedMessage;
import com.finbit.settlement.mapper.AlphaFeedMapper;
import com.finbit.settlement.model.SportEventMessage;
import com.finbit.settlement.publisher.EventPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Orchestrates the normalization and publication of sport event feed messages.
 */
@Service
public class SportEventService {

    private static final Logger log = LoggerFactory.getLogger(SportEventService.class);

    private final AlphaFeedMapper alphaFeedMapper;
    private final EventPublisher eventPublisher;

    public SportEventService(AlphaFeedMapper alphaFeedMapper, EventPublisher eventPublisher) {
        this.alphaFeedMapper = alphaFeedMapper;
        this.eventPublisher = eventPublisher;
    }

    public SportEventMessage processAlphaFeed(AlphaFeedMessage message) {
        log.debug("Processing Alpha feed message: type={}, eventId={}", message.msgType(), message.eventId());
        SportEventMessage normalized = alphaFeedMapper.toSportEventMessage(message);
        eventPublisher.publish(normalized);
        return normalized;
    }
}
