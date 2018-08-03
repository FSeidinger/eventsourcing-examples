package de.seidinger.frank.banking.domain.banking.model;

import de.seidinger.frank.banking.domain.banking.api.Credit;
import de.seidinger.frank.banking.domain.banking.api.CreditTransferFinished;
import de.seidinger.frank.banking.domain.banking.api.CreditTransferInitiated;
import de.seidinger.frank.banking.domain.banking.api.Credited;
import de.seidinger.frank.banking.domain.banking.api.Debit;
import de.seidinger.frank.banking.domain.banking.api.Debited;
import de.seidinger.frank.banking.domain.banking.api.FinishCreditTransfer;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.eventhandling.saga.EndSaga;
import org.axonframework.eventhandling.saga.SagaEventHandler;
import org.axonframework.eventhandling.saga.SagaLifecycle;
import org.axonframework.eventhandling.saga.StartSaga;
import org.axonframework.spring.stereotype.Saga;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.concurrent.CompletableFuture;

@Saga
public class CreditTransferProcess {
    private final Logger log = LoggerFactory.getLogger(getClass());

    @Inject
    private transient CommandGateway gateway;

    private String id = null;
    private boolean credited = false;
    private boolean debited = false;

    @StartSaga
    @SagaEventHandler(associationProperty = "id")
    public void on(final CreditTransferInitiated event) {
        log.info("Credit transfer {} started", event.getId());

        SagaLifecycle.associateWith("creditAccountId", event.getCreditAccountId());
        SagaLifecycle.associateWith("debitAccountId", event.getDebitAccountId());

        this.id = event.getId();

        final CompletableFuture<Void> debitCommand = gateway.send(
                new Debit(
                        event.getDebitAccountId(),
                        event.getAmount()
                )
        );

        final CompletableFuture<Void> creditCommand = gateway.send(
                new Credit(
                        event.getCreditAccountId(),
                        event.getAmount()
                )
        );

        try {
            CompletableFuture.allOf(
                    debitCommand,
                    creditCommand
            ).get();
        } catch (Exception e) {
            log.error("Failed to transfer {} from {} to {}", event.getAmount(), event.getDebitAccountId(), event.getCreditAccountId(), e);
        }
    }

    @SagaEventHandler(associationProperty = "id", keyName = "creditAccountId")
    public void on(final Credited event) {
        this.credited = true;
        finishCreditTransferIfMoneyTransferCompleted(id);
    }

    @SagaEventHandler(associationProperty = "id", keyName = "debitAccountId")
    public void on(final Debited event) {
        this.debited = true;
        finishCreditTransferIfMoneyTransferCompleted(id);
    }

    @EndSaga
    @SagaEventHandler(associationProperty = "id")
    public void on(final CreditTransferFinished event) {
        log.info("Credit transfer {} finished", event.getId());
    }

    private void finishCreditTransferIfMoneyTransferCompleted(final String id) {
        if (isMoneyTransferCompleted()) {
            log.info("Money transfer for credit transfer {} finished", id);
            gateway.send(
                    new FinishCreditTransfer(
                            id
                    )
            );
        }
    }

    private boolean isMoneyTransferCompleted() {
        return credited && debited;
    }
}
