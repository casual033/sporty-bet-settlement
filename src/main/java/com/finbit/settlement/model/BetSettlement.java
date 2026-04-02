package com.finbit.settlement.model;

import java.time.Instant;

/**
 * Normalized representation of a bet settlement for the 1X2 market.
 */
public record BetSettlement(
        String eventId,
        MatchOutcome outcome,
        Instant receivedAt
) implements SportEventMessage {
}
