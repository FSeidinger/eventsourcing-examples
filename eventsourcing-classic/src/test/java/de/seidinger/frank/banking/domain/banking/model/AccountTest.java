package de.seidinger.frank.banking.domain.banking.model;

import de.seidinger.frank.banking.domain.banking.api.Credit;
import de.seidinger.frank.banking.domain.banking.api.Credited;
import de.seidinger.frank.banking.domain.banking.api.Debit;
import de.seidinger.frank.banking.domain.banking.api.Debited;
import de.seidinger.frank.banking.domain.banking.api.Open;
import de.seidinger.frank.banking.domain.banking.api.Opened;
import org.axonframework.test.aggregate.AggregateTestFixture;
import org.axonframework.test.aggregate.FixtureConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

class AccountTest {
    private FixtureConfiguration fixture;

    @BeforeEach
    void setup() {
        fixture = new AggregateTestFixture<>(Account.class);
    }

    @Test
    @DisplayName("A new account can be opened")
    void m1() {
        final String id = "1";
        final BigDecimal openingBalance = new BigDecimal("100.00");

        fixture
                .givenNoPriorActivity()
                .when(
                        new Open(
                                id,
                                openingBalance
                        )
                )
                .expectEvents(
                        new Opened(
                                id,
                                openingBalance
                        )
                );
    }

    @Test
    @DisplayName("An account cannot be overdrawn")
    void m2() {
        final String id = "1";
        final BigDecimal openingBalance = new BigDecimal("0.00");
        final BigDecimal debitAmount = new BigDecimal("0.01");

        fixture
                .given(
                        new Opened(
                                id,
                                openingBalance
                        )
                )
                .when(
                        new Debit(
                                id,
                                debitAmount
                        )
                )
                .expectException(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("An account can be debited to a zero balance")
    void m3() {
        final String id = "1";
        final BigDecimal openingBalance = new BigDecimal("0.01");
        final BigDecimal debitAmount = new BigDecimal("0.01");

        fixture
                .given(
                        new Opened(
                                id,
                                openingBalance
                        )
                )
                .when(
                        new Debit(
                                id,
                                debitAmount
                        )
                )
                .expectEvents(
                        new Debited(
                                id,
                                debitAmount
                        )
                );
    }

    @Test
    @DisplayName("An account can be debited to a positive balance")
    void m4() {
        final String id = "1";
        final BigDecimal openingBalance = new BigDecimal("0.02");
        final BigDecimal debitAmount = new BigDecimal("0.01");

        fixture
                .given(
                        new Opened(
                                id,
                                openingBalance
                        )
                )
                .when(
                        new Debit(
                                id,
                                debitAmount
                        )
                )
                .expectEvents(
                        new Debited(
                                id,
                                debitAmount
                        )
                );
    }

    @Test
    @DisplayName("An account can be credited")
    void m5() {
        final String id = "1";
        final BigDecimal openingBalance = new BigDecimal("0.00");
        final BigDecimal creditAmount = new BigDecimal("0.01");

        fixture
                .given(
                        new Opened(
                                id,
                                openingBalance
                        )
                )
                .when(
                        new Credit(
                                id,
                                creditAmount
                        )
                )
                .expectEvents(
                        new Credited(
                                id,
                                creditAmount
                        )
                );
    }
}