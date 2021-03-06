package de.seidinger.frank.banking.domain.banking.api;

import org.axonframework.commandhandling.TargetAggregateIdentifier;

import java.math.BigDecimal;

public class Credit {
    @TargetAggregateIdentifier
    private final String accountNumber;
    private final BigDecimal amount;

    public Credit(final String accountNumber, final BigDecimal amount) {
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
