package com.finbit.settlement.model;

import java.time.Instant;

/**
 * Base type for all normalized sport event messages flowing through the system.
 */
public sealed interface SportEventMessage permits OddsChange, BetSettlement {

    String eventId();

    Instant receivedAt();
}
