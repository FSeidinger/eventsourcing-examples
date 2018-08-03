package de.seidinger.frank.banking.domain.banking.api;

import java.math.BigDecimal;

public class Opened {
    private final String accountNumber;
    private final BigDecimal openingBalance;

    public Opened(final String accountNumber, final BigDecimal openingBalance) {
        this.accountNumber = accountNumber;
        this.openingBalance = openingBalance;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public BigDecimal getOpeningBalance() {
        return openingBalance;
    }
}
