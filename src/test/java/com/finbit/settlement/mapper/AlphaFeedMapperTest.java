package com.finbit.settlement.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.finbit.settlement.dto.alpha.AlphaFeedMessage;
import com.finbit.settlement.exception.UnknownMessageTypeException;
import com.finbit.settlement.model.BetSettlement;
import com.finbit.settlement.model.MatchOutcome;
import com.finbit.settlement.model.OddsChange;
import com.finbit.settlement.model.SportEventMessage;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AlphaFeedMapperTest {

    private static final Instant FIXED_TIME = Instant.parse("2025-06-01T12:00:00Z");
    private static final Clock FIXED_CLOCK = Clock.fixed(FIXED_TIME, ZoneOffset.UTC);

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final AlphaFeedMapper mapper = new AlphaFeedMapper(FIXED_CLOCK);

    private AlphaFeedMessage readFixture(String path) throws IOException {
        return objectMapper.readValue(new ClassPathResource(path).getInputStream(), AlphaFeedMessage.class);
    }

    @Test
    void deserializesAndMapsOddsUpdate() throws IOException {
        AlphaFeedMessage message = readFixture("alpha/odds_update.json");

        assertThat(message.msgType()).isEqualTo("odds_update");
        assertThat(message.eventId()).isEqualTo("ev123");

        SportEventMessage result = mapper.toSportEventMessage(message);

        assertThat(result).isInstanceOf(OddsChange.class);
        OddsChange odds = (OddsChange) result;
        assertThat(odds.eventId()).isEqualTo("ev123");
        assertThat(odds.homeOdds()).isEqualByComparingTo("2.0");
        assertThat(odds.drawOdds()).isEqualByComparingTo("3.1");
        assertThat(odds.awayOdds()).isEqualByComparingTo("3.8");
        assertThat(odds.receivedAt()).isEqualTo(FIXED_TIME);
    }

    @Test
    void deserializesAndMapsSettlement() throws IOException {
        AlphaFeedMessage message = readFixture("alpha/settlement.json");

        assertThat(message.msgType()).isEqualTo("settlement");
        assertThat(message.eventId()).isEqualTo("ev123");

        SportEventMessage result = mapper.toSportEventMessage(message);

        assertThat(result).isInstanceOf(BetSettlement.class);
        BetSettlement settlement = (BetSettlement) result;
        assertThat(settlement.eventId()).isEqualTo("ev123");
        assertThat(settlement.outcome()).isEqualTo(MatchOutcome.HOME);
        assertThat(settlement.receivedAt()).isEqualTo(FIXED_TIME);
    }

    @Test
    void mapsDrawOutcome() {
        var message = new AlphaFeedMessage("settlement", "ev200", null, "X");

        BetSettlement settlement = (BetSettlement) mapper.toSportEventMessage(message);

        assertThat(settlement.outcome()).isEqualTo(MatchOutcome.DRAW);
    }

    @Test
    void mapsAwayOutcome() {
        var message = new AlphaFeedMessage("settlement", "ev300", null, "2");

        BetSettlement settlement = (BetSettlement) mapper.toSportEventMessage(message);

        assertThat(settlement.outcome()).isEqualTo(MatchOutcome.AWAY);
    }

    @Test
    void throwsOnUnknownMessageType() {
        var message = new AlphaFeedMessage("unknown_type", "ev123", null, null);

        assertThatThrownBy(() -> mapper.toSportEventMessage(message))
                .isInstanceOf(UnknownMessageTypeException.class)
                .hasMessageContaining("unknown_type");
    }

    @Test
    void throwsOnMissingOddsValues() {
        var message = new AlphaFeedMessage("odds_update", "ev123", Map.of("1", BigDecimal.ONE), null);

        assertThatThrownBy(() -> mapper.toSportEventMessage(message))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("1X2 values");
    }

    @Test
    void throwsOnInvalidSettlementOutcome() {
        var message = new AlphaFeedMessage("settlement", "ev123", null, "Z");

        assertThatThrownBy(() -> mapper.toSportEventMessage(message))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Unknown Alpha outcome");
    }
}
