package de.seidinger.frank.banking.domain.banking.api;

public class CreditTransferFinished {
    private final String id;

    public CreditTransferFinished(final String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
