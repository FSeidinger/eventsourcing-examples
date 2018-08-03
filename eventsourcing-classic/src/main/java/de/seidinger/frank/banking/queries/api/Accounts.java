package de.seidinger.frank.banking.queries.api;

import java.math.BigDecimal;
import java.util.Collection;

public class Accounts {
    public static class Account {
        private final String id;
        private final BigDecimal balance;

        public Account(final String id, final BigDecimal balance) {
            this.id = id;
            this.balance = balance;
        }

        public String getId() {
            return id;
        }

        public BigDecimal getBalance() {
            return balance;
        }
    }

    private final Collection<Account> accounts;

    public Accounts(final Collection<Account> accounts) {
        this.accounts = accounts;
    }

    public Collection<Account> getAccounts() {
        return accounts;
    }
}
