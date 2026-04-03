package com.finbit.settlement.dto.beta;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Raw message format received from ProviderBeta.
 * <p>
 * Covers both odds updates ({@code type = "ODDS"}) and
 * settlements ({@code type = "SETTLEMENT"}) in a single DTO,
 * with nullable fields depending on the message type.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record BetaFeedMessage(
        String type,
        @JsonProperty("event_id") String eventId,
        BetaOdds odds,
        String result
) {
}
