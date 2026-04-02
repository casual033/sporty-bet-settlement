package com.finbit.settlement.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.finbit.settlement.dto.beta.BetaFeedMessage;
import com.finbit.settlement.dto.beta.BetaOdds;
import com.finbit.settlement.exception.UnknownMessageTypeException;
import com.finbit.settlement.model.BetSettlement;
import com.finbit.settlement.model.MatchOutcome;
import com.finbit.settlement.model.OddsChange;
import com.finbit.settlement.model.SportEventMessage;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BetaFeedMapperTest {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final BetaFeedMapper mapper = new BetaFeedMapper();

    private BetaFeedMessage readFixture(String path) throws IOException {
        return objectMapper.readValue(new ClassPathResource(path).getInputStream(), BetaFeedMessage.class);
    }

    @Test
    void deserializesAndMapsOdds() throws IOException {
        BetaFeedMessage message = readFixture("beta/odds.json");

        assertThat(message.type()).isEqualTo("ODDS");
        assertThat(message.eventId()).isEqualTo("ev456");

        SportEventMessage result = mapper.toSportEventMessage(message);

        assertThat(result).isInstanceOf(OddsChange.class);
        OddsChange odds = (OddsChange) result;
        assertThat(odds.eventId()).isEqualTo("ev456");
        assertThat(odds.homeOdds()).isEqualByComparingTo("1.95");
        assertThat(odds.drawOdds()).isEqualByComparingTo("3.2");
        assertThat(odds.awayOdds()).isEqualByComparingTo("4.0");
        assertThat(odds.receivedAt()).isNotNull();
    }

    @Test
    void deserializesAndMapsSettlement() throws IOException {
        BetaFeedMessage message = readFixture("beta/settlement.json");

        assertThat(message.type()).isEqualTo("SETTLEMENT");
        assertThat(message.eventId()).isEqualTo("ev456");

        SportEventMessage result = mapper.toSportEventMessage(message);

        assertThat(result).isInstanceOf(BetSettlement.class);
        BetSettlement settlement = (BetSettlement) result;
        assertThat(settlement.eventId()).isEqualTo("ev456");
        assertThat(settlement.outcome()).isEqualTo(MatchOutcome.AWAY);
    }

    @Test
    void mapsHomeOutcome() {
        var message = new BetaFeedMessage("SETTLEMENT", "ev100", null, "home");

        BetSettlement settlement = (BetSettlement) mapper.toSportEventMessage(message);

        assertThat(settlement.outcome()).isEqualTo(MatchOutcome.HOME);
    }

    @Test
    void mapsDrawOutcome() {
        var message = new BetaFeedMessage("SETTLEMENT", "ev200", null, "draw");

        BetSettlement settlement = (BetSettlement) mapper.toSportEventMessage(message);

        assertThat(settlement.outcome()).isEqualTo(MatchOutcome.DRAW);
    }

    @Test
    void throwsOnUnknownMessageType() {
        var message = new BetaFeedMessage("UNKNOWN", "ev456", null, null);

        assertThatThrownBy(() -> mapper.toSportEventMessage(message))
                .isInstanceOf(UnknownMessageTypeException.class)
                .hasMessageContaining("UNKNOWN");
    }

    @Test
    void throwsOnMissingOddsValues() {
        var message = new BetaFeedMessage("ODDS", "ev456", new BetaOdds(BigDecimal.ONE, null, null), null);

        assertThatThrownBy(() -> mapper.toSportEventMessage(message))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("home/draw/away");
    }

    @Test
    void throwsOnInvalidSettlementResult() {
        var message = new BetaFeedMessage("SETTLEMENT", "ev456", null, "invalid");

        assertThatThrownBy(() -> mapper.toSportEventMessage(message))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Unknown Beta outcome");
    }
}
