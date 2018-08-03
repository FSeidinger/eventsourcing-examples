package de.seidinger.frank.domain.banking;

import de.seidinger.frank.eventsourcing.core.Event;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AccountAggregateTest {
    private AccountAggregate account;

    @BeforeEach
    void setup() {
        account = new AccountAggregate();
    }

    @Test
    @DisplayName("A new account can be opened")
    void m57f72a43() {
        final AccountAggregate.Model state = new AccountAggregate.Model(
                "1",
                AccountAggregate.AccountState.Undefined,
                new BigDecimal("0.00")
        );

        final Account.Open command = new Account.Open(
                "1",
                new BigDecimal(100.0)
        );

        final List<Event> events = Collections.singletonList(
                new Account.Opened(
                        command.getId(),
                        command.getOpeningBalance()
                )
        );

        assertEquals(events, account.decide(command, state));
    }

    @Test
    @DisplayName("An account can only be opened once")
    void mdefad437() {
        final AccountAggregate.Model state = new AccountAggregate.Model(
                "1",
                AccountAggregate.AccountState.Opened,
                new BigDecimal("0.00")
        );

        final Account.Open command = new Account.Open(
                state.getId(),
                new BigDecimal("5.00")
        );

        assertThrows(
                IllegalStateException.class,
                () -> account.decide(command, state)
        );
    }

    @Test
    @DisplayName("An account cannot be overdrawn")
    void m6f41a0a1() {
        final AccountAggregate.Model state = new AccountAggregate.Model(
                "1",
                AccountAggregate.AccountState.Opened,
                new BigDecimal("0.00")
       );

        final Account.Debit command = new Account.Debit(
                state.getId(),
                new BigDecimal("0.01")
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> account.decide(command, state)
        );
    }

    @Test
    @DisplayName("An account can be debited to a zero balance")
    void m703b19a1() {
        final AccountAggregate.Model state = new AccountAggregate.Model(
                "1",
                AccountAggregate.AccountState.Opened,
                new BigDecimal("0.01")
        );

        final Account.Debit command = new Account.Debit(
                state.getId(),
                new BigDecimal("0.01")
        );

        final List<Event> events = Collections.singletonList(
                new Account.Debited(
                        command.getId(),
                        command.getAmount()
                )
        );

        assertEquals(events, account.decide(command, state));
    }

    @Test
    @DisplayName("An account can be debited to a positive balance")
    void m816c7430() {
        final AccountAggregate.Model state = new AccountAggregate.Model(
                "1",
                AccountAggregate.AccountState.Opened,
                new BigDecimal("0.02")
        );

        final Account.Debit command = new Account.Debit(
                state.getId(),
                new BigDecimal("0.01")
        );

        final List<Event> events = Collections.singletonList(
                new Account.Debited(
                        command.getId(),
                        command.getAmount()
                )
        );

        assertEquals(events, account.decide(command, state));
    }

    @Test
    @DisplayName("An account can be credited")
    void me9a09bc6() {
        final AccountAggregate.Model state = new AccountAggregate.Model(
                "1",
                AccountAggregate.AccountState.Opened,
                new BigDecimal("0.00")
        );

        final Account.Credit command = new Account.Credit(
                state.getId(),
                new BigDecimal("0.01")
        );

        final List<Event> events = Collections.singletonList(
                new Account.Credited(
                        command.getId(),
                        command.getAmount()
                )
        );

        assertEquals(events, account.decide(command, state));
    }

    @Test
    @DisplayName("The account state can be projected from events")
    void md0bcbe6c() {
        final AccountAggregate.Model initialState = new AccountAggregate.Model(
                "1",
                AccountAggregate.AccountState.Undefined,
                BigDecimal.ZERO
        );

        final Stream<Event> events = Stream.of(
                new Account.Opened(
                        initialState.getId(),
                        new BigDecimal("5.00")
                ),
                new Account.Credited(
                        initialState.getId(),
                        new BigDecimal("1.00")
                ),
                new Account.Debited(
                        initialState.getId(),
                        new BigDecimal("2.00")
                )
        );

        final AccountAggregate.Model expectedState = new AccountAggregate.Model(
                initialState.getId(),
                AccountAggregate.AccountState.Opened,
                new BigDecimal("4.00")
        );

        assertEquals(expectedState, account.replay(initialState, events));
    }
}