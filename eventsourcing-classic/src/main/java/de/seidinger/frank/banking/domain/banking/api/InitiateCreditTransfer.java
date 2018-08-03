package de.seidinger.frank.banking.domain.banking.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.axonframework.commandhandling.TargetAggregateIdentifier;

import java.math.BigDecimal;

public class InitiateCreditTransfer {
    @TargetAggregateIdentifier
    private final String id;

    private final String creditAccountId;
    private final String debitAccountId;
    private final BigDecimal amount;
    private final String statement;

    @JsonCreator
    public InitiateCreditTransfer(@JsonProperty("id") final String id, @JsonProperty("creditAccountId") final String creditAccountId, @JsonProperty("debitAccountId") final String debitAccountId, @JsonProperty("amount") final BigDecimal amount, @JsonProperty("statement") final String statement) {
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
