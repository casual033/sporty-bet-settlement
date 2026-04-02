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
class ProviderBetaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private InMemoryEventPublisher eventPublisher;

    @BeforeEach
    void setUp() {
        eventPublisher.clear();
    }

    @Test
    void oddsReturnsNormalizedOddsChange() throws Exception {
        String json = Files.readString(Path.of("src/test/resources/beta/odds.json"));

        mockMvc.perform(post("/provider-beta/feed")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.eventId").value("ev456"))
                .andExpect(jsonPath("$.homeOdds").value(1.95))
                .andExpect(jsonPath("$.drawOdds").value(3.2))
                .andExpect(jsonPath("$.awayOdds").value(4.0));

        List<SportEventMessage> published = eventPublisher.getPublishedMessages();
        assertThat(published).hasSize(1);
        assertThat(published.get(0)).isInstanceOf(OddsChange.class);
    }

    @Test
    void settlementReturnsNormalizedBetSettlement() throws Exception {
        String json = Files.readString(Path.of("src/test/resources/beta/settlement.json"));

        mockMvc.perform(post("/provider-beta/feed")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.eventId").value("ev456"))
                .andExpect(jsonPath("$.outcome").value("AWAY"));

        List<SportEventMessage> published = eventPublisher.getPublishedMessages();
        assertThat(published).hasSize(1);
        BetSettlement settlement = (BetSettlement) published.get(0);
        assertThat(settlement.outcome()).isEqualTo(MatchOutcome.AWAY);
    }

    @Test
    void unknownMessageTypeReturnsBadRequest() throws Exception {
        String json = """
                {
                  "type": "INVALID",
                  "event_id": "ev999"
                }
                """;

        mockMvc.perform(post("/provider-beta/feed")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("Unknown message type: INVALID"));

        assertThat(eventPublisher.getPublishedMessages()).isEmpty();
    }
}
