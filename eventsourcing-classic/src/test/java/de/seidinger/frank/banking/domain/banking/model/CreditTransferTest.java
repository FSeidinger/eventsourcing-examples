package de.seidinger.frank.banking.domain.banking.model;

import de.seidinger.frank.banking.domain.banking.api.CreditTransferFinished;
import de.seidinger.frank.banking.domain.banking.api.CreditTransferInitiated;
import de.seidinger.frank.banking.domain.banking.api.FinishCreditTransfer;
import de.seidinger.frank.banking.domain.banking.api.InitiateCreditTransfer;
import org.axonframework.test.aggregate.AggregateTestFixture;
import org.axonframework.test.aggregate.FixtureConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

class CreditTransferTest {
    private FixtureConfiguration fixture;

    @BeforeEach
    void setup() {
        fixture = new AggregateTestFixture(CreditTransfer.class);
    }

    @Test
    @DisplayName("A credit transfer can be initiated")
    void m1() {
        final InitiateCreditTransfer command = new InitiateCreditTransfer(
                "CT1",
                "AC1",
                "AC2",
                new BigDecimal("24.99"),
                "Hotel California invoide I2312"
        );

        final CreditTransferInitiated event = new CreditTransferInitiated(
                command.getId(),
                command.getCreditAccountId(),
                command.getDebitAccountId(),
                command.getAmount(),
                command.getStatement()
        );

        fixture
                .givenNoPriorActivity()
                .when(
                        command
                )
                .expectEvents(
                        event
                );
    }

    @Test
    @DisplayName("A credit transfer can be finished")
    void m2() {
        final FinishCreditTransfer command = new FinishCreditTransfer(
                "CT1"
        );

        final CreditTransferFinished event = new CreditTransferFinished(
                command.getId()
        );

        fixture
                .given(
                        new CreditTransferInitiated(
                                command.getId(),
                                "ACC1",
                                "ACC2",
                                new BigDecimal("23.99"),
                                "Hotel California, Invoice I2345"
                        )
                )
                .when(
                        command
                )
                .expectEvents(
                        event
                );
    }
}