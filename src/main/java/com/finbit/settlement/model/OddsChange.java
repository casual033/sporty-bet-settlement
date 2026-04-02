package com.finbit.settlement.model;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Normalized representation of an odds update for the 1X2 market.
 */
public record OddsChange(
        String eventId,
        BigDecimal homeOdds,
        BigDecimal drawOdds,
        BigDecimal awayOdds,
        Instant receivedAt
) implements SportEventMessage {
}
