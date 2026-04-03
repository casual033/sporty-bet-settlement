package com.finbit.settlement.model;

import java.time.Instant;

/**
 * Normalized representation of a bet settlement for a betting market.
 */
public record BetSettlement(
        String eventId,
        MarketType marketType,
        MatchOutcome outcome,
        Instant receivedAt
) implements SportEventMessage {
}
