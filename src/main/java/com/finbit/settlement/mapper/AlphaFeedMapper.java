package com.finbit.settlement.mapper;

import com.finbit.settlement.dto.alpha.AlphaFeedMessage;
import com.finbit.settlement.exception.UnknownMessageTypeException;
import com.finbit.settlement.model.BetSettlement;
import com.finbit.settlement.model.MatchOutcome;
import com.finbit.settlement.model.OddsChange;
import com.finbit.settlement.model.SportEventMessage;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;

/**
 * Maps ProviderAlpha feed messages into the normalized internal model.
 * <p>
 * Alpha uses {@code "1"}, {@code "X"}, {@code "2"} for 1X2 market keys and outcomes.
 */
@Component
public class AlphaFeedMapper {

    private static final String ODDS_UPDATE = "odds_update";
    private static final String SETTLEMENT = "settlement";

    private static final Map<String, MatchOutcome> OUTCOME_MAPPING = Map.of(
            "1", MatchOutcome.HOME,
            "X", MatchOutcome.DRAW,
            "2", MatchOutcome.AWAY
    );

    public SportEventMessage toSportEventMessage(AlphaFeedMessage message) {
        return switch (message.msgType()) {
            case ODDS_UPDATE -> mapOddsChange(message);
            case SETTLEMENT -> mapBetSettlement(message);
            default -> throw new UnknownMessageTypeException(message.msgType());
        };
    }

    private OddsChange mapOddsChange(AlphaFeedMessage message) {
        var values = message.values();
        if (values == null || !values.containsKey("1") || !values.containsKey("X") || !values.containsKey("2")) {
            throw new IllegalArgumentException("Odds update missing required 1X2 values");
        }
        return new OddsChange(
                message.eventId(),
                values.get("1"),
                values.get("X"),
                values.get("2"),
                Instant.now()
        );
    }

    private BetSettlement mapBetSettlement(AlphaFeedMessage message) {
        MatchOutcome outcome = OUTCOME_MAPPING.get(message.outcome());
        if (outcome == null) {
            throw new IllegalArgumentException("Unknown Alpha outcome: " + message.outcome());
        }
        return new BetSettlement(message.eventId(), outcome, Instant.now());
    }
}
