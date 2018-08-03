package de.seidinger.frank.banking.domain.banking.model;

import de.seidinger.frank.banking.domain.banking.api.Credit;
import de.seidinger.frank.banking.domain.banking.api.CreditTransferFinished;
import de.seidinger.frank.banking.domain.banking.api.CreditTransferInitiated;
import de.seidinger.frank.banking.domain.banking.api.Credited;
import de.seidinger.frank.banking.domain.banking.api.Debit;
import de.seidinger.frank.banking.domain.banking.api.Debited;
import de.seidinger.frank.banking.domain.banking.api.FinishCreditTransfer;
import org.axonframework.test.saga.SagaTestFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

class CreditTransferProcessTest {
    private SagaTestFixture<CreditTransferProcess> fixture;

    @BeforeEach
    private void setup() {
        this.fixture = new SagaTestFixture<>(CreditTransferProcess.class);
    }

    @Test
    @DisplayName("Initiating a credit transfer starts a new credit transfer process")
    void m1() {
        fixture
                .whenAggregate("CT1")
                .publishes(
                        new CreditTransferInitiated(
                                "CT1",
                                "Creditor",
                                "Debtor",
                                new BigDecimal("19.99"),
                                "Hotel California overnight stay"
                        )
                )
                .expectActiveSagas(1)
                .expectAssociationWith("id", "CT1")
                .expectAssociationWith("creditAccountId", "Creditor")
                .expectAssociationWith("debitAccountId", "Debtor")
                .expectDispatchedCommands(
                        new Debit(
                                "Debtor",
                                new BigDecimal("19.99")
                        ),
                        new Credit(
                                "Creditor",
                                new BigDecimal("19.99")
                        )
                );
    }

    @Test
    @DisplayName("When receiving the Credit message, the credit account id is associated")
    void m2() {
        fixture
                .givenAggregate("CT1")
                .published(
                        new CreditTransferInitiated(
                                "CT1",
                                "Creditor",
                                "Debtor",
                                new BigDecimal("19.99"),
                                "Hotel California overnight stay"
                        )
                )
                .whenAggregate("Creditor")
                .publishes(
                        new Credited(
                                "Creditor",
                                new BigDecimal("7.98")
                        )
                )
                .expectAssociationWith("creditAccountId", "Creditor")
                .expectNoDispatchedCommands();
    }

    @Test
    @DisplayName("When receiving the Debig event, the debit account id is associated")
    void m3() {
        fixture
                .givenAggregate("CT1")
                .published(
                        new CreditTransferInitiated(
                                "CT1",
                                "Creditor",
                                "Debtor",
                                new BigDecimal("19.99"),
                                "Hotel California overnight stay"
                        )
                )
                .whenAggregate("Debtor")
                .publishes(
                        new Debited(
                                "Debtor",
                                new BigDecimal("7.98")
                        )
                )
                .expectAssociationWith("debitAccountId", "Debtor")
                .expectNoDispatchedCommands();
    }

    @Test
    @DisplayName("When the money transfer is done, the credit transfer is finished")
    void m4() {
        fixture
                .givenAggregate("CT1")
                .published(
                        new CreditTransferInitiated(
                                "CT1",
                                "Creditor",
                                "Debtor",
                                new BigDecimal("3.45"),
                                "Hotel California overnight stay"
                        )
                )
                .andThenAggregate("Debtor")
                .published(
                        new Debited(
                                "Debtor",
                                new BigDecimal("3.45")
                        )
                )
                .whenAggregate("Creditor")
                .publishes(
                        new Credited(
                                "Creditor",
                                new BigDecimal("3.45")
                        )
                )
                .expectDispatchedCommands(
                        new FinishCreditTransfer(
                                "CT1"
                        )
                );
    }

    @Test
    @DisplayName("When the credit transfer is finished, the credit transfer process is finished")
    void m5() {
        fixture
                .givenAggregate("CT1")
                .published(
                        new CreditTransferInitiated(
                                "CT1",
                                "Creditor",
                                "Debtor",
                                new BigDecimal("3.45"),
                                "Hotel California overnight stay"
                        )
                )
                .andThenAggregate("Debtor")
                .published(
                        new Debited(
                                "Debtor",
                                new BigDecimal("3.45")
                        )
                )
                .andThenAggregate("Creditor")
                .published(
                        new Credited(
                                "Creditor",
                                new BigDecimal("3.45")
                        )
                )
                .whenAggregate("CT1")
                .publishes(
                        new CreditTransferFinished(
                                "CT1"
                        )
                )
                .expectActiveSagas(0);
    }
}