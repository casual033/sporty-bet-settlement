package com.finbit.settlement.publisher;

import com.finbit.settlement.model.SportEventMessage;

/**
 * Abstraction for publishing normalized sport event messages to a message queue.
 */
public interface EventPublisher {

    void publish(SportEventMessage message);
}
