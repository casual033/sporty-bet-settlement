package com.finbit.settlement.mapper;

import com.finbit.settlement.dto.beta.BetaFeedMessage;
import com.finbit.settlement.dto.beta.BetaOdds;
import com.finbit.settlement.exception.UnknownMessageTypeException;
import com.finbit.settlement.model.BetSettlement;
import com.finbit.settlement.model.MatchOutcome;
import com.finbit.settlement.model.OddsChange;
import com.finbit.settlement.model.SportEventMessage;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;

/**
 * Maps ProviderBeta feed messages into the normalized internal model.
 * <p>
 * Beta uses {@code "home"}, {@code "draw"}, {@code "away"} for odds keys and outcomes.
 */
@Component
public class BetaFeedMapper implements FeedMapper<BetaFeedMessage> {

    private static final String ODDS = "ODDS";
    private static final String SETTLEMENT = "SETTLEMENT";

    private static final Map<String, MatchOutcome> OUTCOME_MAPPING = Map.of(
            "home", MatchOutcome.HOME,
            "draw", MatchOutcome.DRAW,
            "away", MatchOutcome.AWAY
    );

    @Override
    public SportEventMessage toSportEventMessage(BetaFeedMessage message) {
        return switch (message.type()) {
            case ODDS -> mapOddsChange(message);
            case SETTLEMENT -> mapBetSettlement(message);
            default -> throw new UnknownMessageTypeException(message.type());
        };
    }

    private OddsChange mapOddsChange(BetaFeedMessage message) {
        BetaOdds odds = message.odds();
        if (odds == null || odds.home() == null || odds.draw() == null || odds.away() == null) {
            throw new IllegalArgumentException("Odds message missing required home/draw/away values");
        }
        return new OddsChange(
                message.eventId(),
                odds.home(),
                odds.draw(),
                odds.away(),
                Instant.now()
        );
    }

    private BetSettlement mapBetSettlement(BetaFeedMessage message) {
        MatchOutcome outcome = OUTCOME_MAPPING.get(message.result());
        if (outcome == null) {
            throw new IllegalArgumentException("Unknown Beta outcome: " + message.result());
        }
        return new BetSettlement(message.eventId(), outcome, Instant.now());
    }
}
