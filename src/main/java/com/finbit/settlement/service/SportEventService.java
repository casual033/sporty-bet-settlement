package com.finbit.settlement.service;

import com.finbit.settlement.dto.alpha.AlphaFeedMessage;
import com.finbit.settlement.dto.beta.BetaFeedMessage;
import com.finbit.settlement.mapper.AlphaFeedMapper;
import com.finbit.settlement.mapper.BetaFeedMapper;
import com.finbit.settlement.mapper.FeedMapper;
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
    private final BetaFeedMapper betaFeedMapper;
    private final EventPublisher eventPublisher;

    public SportEventService(AlphaFeedMapper alphaFeedMapper,
                             BetaFeedMapper betaFeedMapper,
                             EventPublisher eventPublisher) {
        this.alphaFeedMapper = alphaFeedMapper;
        this.betaFeedMapper = betaFeedMapper;
        this.eventPublisher = eventPublisher;
    }

    public SportEventMessage processAlphaFeed(AlphaFeedMessage message) {
        log.debug("Processing Alpha feed: type={}, eventId={}", message.msgType(), message.eventId());
        return normalizeAndPublish(alphaFeedMapper, message);
    }

    public SportEventMessage processBetaFeed(BetaFeedMessage message) {
        log.debug("Processing Beta feed: type={}, eventId={}", message.type(), message.eventId());
        return normalizeAndPublish(betaFeedMapper, message);
    }

    private <T> SportEventMessage normalizeAndPublish(FeedMapper<T> mapper, T message) {
        SportEventMessage normalized = mapper.toSportEventMessage(message);
        eventPublisher.publish(normalized);
        return normalized;
    }
}
