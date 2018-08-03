package de.seidinger.frank.banking.domain.banking.model;

import de.seidinger.frank.banking.domain.banking.api.Credit;
import de.seidinger.frank.banking.domain.banking.api.Credited;
import de.seidinger.frank.banking.domain.banking.api.Debit;
import de.seidinger.frank.banking.domain.banking.api.Debited;
import de.seidinger.frank.banking.domain.banking.api.Open;
import de.seidinger.frank.banking.domain.banking.api.Opened;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.commandhandling.model.AggregateIdentifier;
import org.axonframework.commandhandling.model.AggregateLifecycle;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.spring.stereotype.Aggregate;

import java.math.BigDecimal;
import java.text.MessageFormat;

@Aggregate
public class Account {
    @AggregateIdentifier
    private String accountNumber;
    private BigDecimal balance;

    private Account() {
    }

    @CommandHandler
    Account(final Open command) {
        AggregateLifecycle.apply(
                new Opened(
                        command.getAccountNumber(),
                        command.getOpeningBalance()
                )
        );
    }

    @CommandHandler
    void when(final Credit command) {
        AggregateLifecycle.apply(
                new Credited(
                        command.getAccountNumber(),
                        command.getAmount()
                )
        );
    }

    @CommandHandler
    void when(final Debit command) {
        final BigDecimal debitAmount = command.getAmount();

        if (wouldAccountBeOverdrawn(balance, debitAmount)) {
            final String message = MessageFormat.format("Accounts balance is not sufficient to debit {0} EUR", debitAmount);
            throw new IllegalArgumentException(message);
        }

        AggregateLifecycle.apply(
                new Debited(
                        command.getAccountNumber(),
                        command.getAmount()
                )
        );
    }

    @EventSourcingHandler
    void on(final Opened event) {
        this.accountNumber = event.getAccountNumber();
        this.balance = event.getOpeningBalance();
    }

    @EventSourcingHandler
    void on(final Credited event) {
        this.balance = balance.add(event.getAmount());
    }

    @EventSourcingHandler
    void on(final Debited event) {
        this.balance = balance.subtract(event.getAmount());
    }

    private boolean wouldAccountBeOverdrawn(final BigDecimal balance, final BigDecimal debit) {
        return this.balance.subtract(debit).compareTo(BigDecimal.ZERO) < 0;
    }
}
