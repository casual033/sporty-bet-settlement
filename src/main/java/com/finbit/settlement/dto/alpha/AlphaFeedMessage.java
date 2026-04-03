package com.finbit.settlement.dto.alpha;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Raw message format received from ProviderAlpha.
 * <p>
 * Covers both odds updates ({@code msg_type = "odds_update"}) and
 * settlements ({@code msg_type = "settlement"}) in a single DTO,
 * with nullable fields depending on the message type.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record AlphaFeedMessage(
        @JsonProperty("msg_type") String msgType,
        @JsonProperty("event_id") String eventId,
        Map<String, BigDecimal> values,
        String outcome
) {
}
