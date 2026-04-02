package com.finbit.settlement.dto.beta;

import java.math.BigDecimal;

/**
 * Nested odds structure used by ProviderBeta.
 */
public record BetaOdds(
        BigDecimal home,
        BigDecimal draw,
        BigDecimal away
) {
}
