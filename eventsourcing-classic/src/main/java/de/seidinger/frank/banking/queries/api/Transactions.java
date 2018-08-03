package de.seidinger.frank.banking.queries.api;

import java.math.BigDecimal;
import java.util.Collection;

public class Transactions {
    public static class Transaction {
        private final String id;
        private final String creditAccountId;
        private final String debitAccountId;
        private final BigDecimal amount;
        private final String statement;

        public Transaction(final String id, final String creditAccountId, final String debitAccountId, final BigDecimal amount, final String statement) {
            this.id = id;
            this.creditAccountId = creditAccountId;
            this.debitAccountId = debitAccountId;
            this.amount = amount;
            this.statement = statement;
        }

        public String getId() {
            return id;
        }

        public String getCreditAccountId() {
            return creditAccountId;
        }

        public String getDebitAccountId() {
            return debitAccountId;
        }

        public BigDecimal getAmount() {
            return amount;
        }

        public String getStatement() {
            return statement;
        }
    }

    private final Collection<Transaction> transactions;

    public Transactions(final Collection<Transaction> transactions) {
        this.transactions = transactions;
    }

    public Collection<Transaction> getTransactions() {
        return transactions;
    }
}
