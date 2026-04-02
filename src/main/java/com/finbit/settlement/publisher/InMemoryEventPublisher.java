package com.finbit.settlement.publisher;

import com.finbit.settlement.model.SportEventMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * In-memory implementation of {@link EventPublisher} that logs published messages
 * and retains them for inspection. Intended as a stand-in for a real message queue.
 */
@Component
public class InMemoryEventPublisher implements EventPublisher {

    private static final Logger log = LoggerFactory.getLogger(InMemoryEventPublisher.class);

    private final List<SportEventMessage> publishedMessages = new CopyOnWriteArrayList<>();

    @Override
    public void publish(SportEventMessage message) {
        log.info("Publishing message: {}", message);
        publishedMessages.add(message);
    }

    public List<SportEventMessage> getPublishedMessages() {
        return Collections.unmodifiableList(publishedMessages);
    }

    public void clear() {
        publishedMessages.clear();
    }
}
