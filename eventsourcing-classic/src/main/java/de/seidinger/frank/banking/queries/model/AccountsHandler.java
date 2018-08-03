package de.seidinger.frank.banking.queries.model;

import de.seidinger.frank.banking.domain.banking.api.Credited;
import de.seidinger.frank.banking.domain.banking.api.Debited;
import de.seidinger.frank.banking.domain.banking.api.Opened;
import de.seidinger.frank.banking.queries.api.Accounts;
import de.seidinger.frank.banking.queries.api.ReadAccounts;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class AccountsHandler {
    private final Map<String, Accounts.Account> accountRepository;

    public AccountsHandler() {
        accountRepository = new HashMap<>();
    }

    @EventHandler
    public void on(final Opened event) {
        accountRepository.put(
                event.getAccountNumber(),
                new Accounts.Account(
                        event.getAccountNumber(),
                        event.getOpeningBalance()
                )
        );
    }

    @EventHandler
    public void on(final Credited event) {
        final Accounts.Account account = accountRepository.get(event.getAccountNumber());

        if (account != null) {
            accountRepository.put(
                    event.getAccountNumber(),
                    new Accounts.Account(
                            event.getAccountNumber(),
                            account.getBalance().add(event.getAmount())
                    )
            );
        }
    }

    @EventHandler
    public void on(final Debited event) {
        final Accounts.Account account = accountRepository.get(event.getAccountNumber());

        if (account != null) {
            accountRepository.put(
                    event.getAccountNumber(),
                    new Accounts.Account(
                            event.getAccountNumber(),
                            account.getBalance().subtract(event.getAmount())
                    )
            );
        }
    }

    @QueryHandler
    public Accounts handle(final ReadAccounts query) {
        return new Accounts(
                accountRepository.values()
        );
    }
}
