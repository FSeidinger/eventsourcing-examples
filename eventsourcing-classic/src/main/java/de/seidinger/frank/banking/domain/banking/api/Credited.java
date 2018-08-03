package de.seidinger.frank.banking.domain.banking.api;

import java.math.BigDecimal;

public class Credited {
    private final String accountNumber;
    private final BigDecimal amount;

    public Credited(final String accountNumber, final BigDecimal amount) {
        this.accountNumber = accountNumber;
        this.amount = amount;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public BigDecimal getAmount() {
        return amount;
    }
}
