package de.seidinger.frank.banking.domain.banking.api;

import java.math.BigDecimal;

public class CreditTransferInitiated {
    private final String id;
    private final String creditAccountId;
    private final String debitAccountId;
    private final BigDecimal amount;
    private final String statement;

    public CreditTransferInitiated(final String id, final String creditAccountId, final String debitAccountId, final BigDecimal amount, final String statement) {
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
