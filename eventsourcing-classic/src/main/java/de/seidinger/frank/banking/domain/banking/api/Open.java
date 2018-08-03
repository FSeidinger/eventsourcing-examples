package de.seidinger.frank.banking.domain.banking.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.axonframework.commandhandling.TargetAggregateIdentifier;

import java.math.BigDecimal;

public class Open {
    @TargetAggregateIdentifier
    private final String accountNumber;
    private final BigDecimal openingBalance;

    @JsonCreator
    public Open(@JsonProperty("accountNumber") final String accountNumber, @JsonProperty("openingBalance") final BigDecimal openingBalance) {
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
