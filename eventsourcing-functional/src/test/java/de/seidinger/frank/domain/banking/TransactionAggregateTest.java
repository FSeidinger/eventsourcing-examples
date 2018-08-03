package de.seidinger.frank.domain.banking;

import de.seidinger.frank.eventsourcing.core.Event;
import de.seidinger.frank.eventsourcing.core.State;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

public class TransactionAggregateTest {
    private TransactionAggregate transaction;

    @BeforeEach
    private void setup() {
        transaction = new TransactionAggregate();
    }

    @Test
    @DisplayName("A credit transfer can be initiated")
    void m1227() {
        final State givenState = new TransactionAggregate.Model(
                "",
                "",
                "",
                BigDecimal.ZERO,
                "",
                TransactionAggregate.TransactionState.Undefined
        );

        final Event when = new Transaction.Initiated(
                "1",
                "Credit Account",
                "Debit Account",
                new BigDecimal("49.00"),
                "Hotel California overnight stay"
        );

        final State then = new TransactionAggregate.Model(
                "1",
                "Credit Account",
                "Debit Account",
                new BigDecimal("49.00"),
                "Hotel California overnight stay",
                TransactionAggregate.TransactionState.Initiated
        );

        final State expected = transaction.apply(
                givenState,
                when
        );

        Assertions.assertEquals(then, expected);
    }

    @Test
    @DisplayName("A credit transfer can be finished")
    void m8826() {
        final State givenState = new TransactionAggregate.Model(
                "1",
                "Credit Account",
                "Debit Account",
                new BigDecimal("49.00"),
                "Hotel California overnight stay",
                TransactionAggregate.TransactionState.Initiated
        );

        final Event when = new Transaction.Finished(
                "1"
        );

        final State then = new TransactionAggregate.Model(
                "1",
                "Credit Account",
                "Debit Account",
                new BigDecimal("49.00"),
                "Hotel California overnight stay",
                TransactionAggregate.TransactionState.Finished
        );

        final State expected = transaction.apply(
                givenState,
                when
        );

        Assertions.assertEquals(then, expected);
    }
}
