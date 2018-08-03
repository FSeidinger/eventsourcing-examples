package de.seidinger.frank.banking.domain.banking.api;

import org.axonframework.commandhandling.TargetAggregateIdentifier;

public class FinishCreditTransfer {
    @TargetAggregateIdentifier
    private final String id;

    public FinishCreditTransfer(final String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
