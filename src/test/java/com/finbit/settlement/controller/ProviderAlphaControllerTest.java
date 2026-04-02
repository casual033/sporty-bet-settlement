package com.finbit.settlement.controller;

import com.finbit.settlement.model.BetSettlement;
import com.finbit.settlement.model.MatchOutcome;
import com.finbit.settlement.model.OddsChange;
import com.finbit.settlement.model.SportEventMessage;
import com.finbit.settlement.publisher.InMemoryEventPublisher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ProviderAlphaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private InMemoryEventPublisher eventPublisher;

    @BeforeEach
    void setUp() {
        eventPublisher.clear();
    }

    @Test
    void oddsUpdateReturnsNormalizedOddsChange() throws Exception {
        String json = Files.readString(Path.of("src/test/resources/alpha/odds_update.json"));

        mockMvc.perform(post("/provider-alpha/feed")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.eventId").value("ev123"))
                .andExpect(jsonPath("$.homeOdds").value(2.0))
                .andExpect(jsonPath("$.drawOdds").value(3.1))
                .andExpect(jsonPath("$.awayOdds").value(3.8));

        List<SportEventMessage> published = eventPublisher.getPublishedMessages();
        assertThat(published).hasSize(1);
        assertThat(published.get(0)).isInstanceOf(OddsChange.class);
    }

    @Test
    void settlementReturnsNormalizedBetSettlement() throws Exception {
        String json = Files.readString(Path.of("src/test/resources/alpha/settlement.json"));

        mockMvc.perform(post("/provider-alpha/feed")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.eventId").value("ev123"))
                .andExpect(jsonPath("$.outcome").value("HOME"));

        List<SportEventMessage> published = eventPublisher.getPublishedMessages();
        assertThat(published).hasSize(1);
        BetSettlement settlement = (BetSettlement) published.get(0);
        assertThat(settlement.outcome()).isEqualTo(MatchOutcome.HOME);
    }

    @Test
    void unknownMessageTypeReturnsBadRequest() throws Exception {
        String json = """
                {
                  "msg_type": "unknown",
                  "event_id": "ev999"
                }
                """;

        mockMvc.perform(post("/provider-alpha/feed")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("Unknown message type: unknown"));

        assertThat(eventPublisher.getPublishedMessages()).isEmpty();
    }
}
