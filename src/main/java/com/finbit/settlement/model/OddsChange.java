package com.finbit.settlement.model;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Normalized representation of an odds update for a betting market.
 */
public record OddsChange(
        String eventId,
        MarketType marketType,
        BigDecimal homeOdds,
        BigDecimal drawOdds,
        BigDecimal awayOdds,
        Instant receivedAt
) implements SportEventMessage {
}
