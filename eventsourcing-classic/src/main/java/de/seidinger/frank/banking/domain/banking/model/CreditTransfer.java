package de.seidinger.frank.banking.domain.banking.model;

import de.seidinger.frank.banking.domain.banking.api.CreditTransferFinished;
import de.seidinger.frank.banking.domain.banking.api.CreditTransferInitiated;
import de.seidinger.frank.banking.domain.banking.api.FinishCreditTransfer;
import de.seidinger.frank.banking.domain.banking.api.InitiateCreditTransfer;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.commandhandling.model.AggregateIdentifier;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.spring.stereotype.Aggregate;

import static org.axonframework.commandhandling.model.AggregateLifecycle.apply;

@Aggregate
public class CreditTransfer {
    @AggregateIdentifier
    private String id;

    private CreditTransferState state;

    private CreditTransfer() { }

    @CommandHandler
    private CreditTransfer(final InitiateCreditTransfer command) {
        apply(
                new CreditTransferInitiated(
                        command.getId(),
                        command.getCreditAccountId(),
                        command.getDebitAccountId(),
                        command.getAmount(),
                        command.getStatement()
                )
        );
    }

    @CommandHandler
    public void when(final FinishCreditTransfer command) {
        apply(
                new CreditTransferFinished(
                        command.getId()
                )
        );
    }

    @EventHandler
    private void on(final CreditTransferInitiated event) {
        this.id = event.getId();
        this.state = CreditTransferState.Initiated;
    }

    @EventHandler
    private void on(final FinishCreditTransfer event) {
        this.state = CreditTransferState.Finished;
    }

    private enum CreditTransferState {
        Initiated,
        Finished
    }
}
