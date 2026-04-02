package com.finbit.settlement.mapper;

import com.finbit.settlement.model.SportEventMessage;

/**
 * Converts a provider-specific feed message into a normalized {@link SportEventMessage}.
 *
 * @param <T> the provider-specific message type
 */
public interface FeedMapper<T> {

    SportEventMessage toSportEventMessage(T message);
}
