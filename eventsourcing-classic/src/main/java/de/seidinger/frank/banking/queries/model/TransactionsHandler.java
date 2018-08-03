package de.seidinger.frank.banking.queries.model;

import de.seidinger.frank.banking.domain.banking.api.CreditTransferFinished;
import de.seidinger.frank.banking.domain.banking.api.CreditTransferInitiated;
import de.seidinger.frank.banking.queries.api.ReadAccounts;
import de.seidinger.frank.banking.queries.api.ReadTransactions;
import de.seidinger.frank.banking.queries.api.Transactions;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class TransactionsHandler {
    private final Map<String, Transactions.Transaction> startedTransactionRepository;
    private final Map<String, Transactions.Transaction> finishedTransactionRepository;

    public TransactionsHandler() {
        this.startedTransactionRepository = new HashMap<>();
        this.finishedTransactionRepository = new HashMap<>();
    }

    @EventHandler
    public void on(final CreditTransferInitiated event) {
        startedTransactionRepository.put(
                event.getId(),
                new Transactions.Transaction(
                        event.getId(),
                        event.getCreditAccountId(),
                        event.getDebitAccountId(),
                        event.getAmount(),
                        event.getStatement()
                )
        );
    }

    @EventHandler
    public void on(final CreditTransferFinished event) {
        final Transactions.Transaction transaction = startedTransactionRepository.get(event.getId());

        if (transaction != null) {
            finishedTransactionRepository.put(event.getId(), transaction);
            startedTransactionRepository.remove(event.getId());
        }
    }

    @QueryHandler
    public Transactions handle(final ReadTransactions query) {
        return new Transactions(
                finishedTransactionRepository.values()
        );
    }
}
