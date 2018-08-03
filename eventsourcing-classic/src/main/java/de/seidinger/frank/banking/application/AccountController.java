package de.seidinger.frank.banking.application;

import de.seidinger.frank.banking.application.api.CouldNotReadError;
import de.seidinger.frank.banking.domain.banking.api.Open;
import de.seidinger.frank.banking.queries.api.Accounts;
import de.seidinger.frank.banking.queries.api.ReadAccounts;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.queryhandling.GenericQueryMessage;
import org.axonframework.queryhandling.QueryBus;
import org.axonframework.queryhandling.responsetypes.ResponseTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping(value = "api/accounts", produces = "application/json")
public class AccountController {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final CommandGateway gateway;
    private final QueryBus queryBus;

    @Autowired
    public AccountController(final CommandGateway gateway, final QueryBus queryBus) {
        this.gateway = gateway;
        this.queryBus = queryBus;
    }

    @PostMapping(consumes = "application/json")
    public HttpHeaders createAccount(@RequestBody final Open command) {
        gateway.sendAndWait(
                command
        );

        final HttpHeaders headers = new HttpHeaders();
        headers.setLocation(UriComponentsBuilder.fromPath("api/accounts").build().toUri());

        return headers;
    }

    @GetMapping
    public Accounts readAccountList() {
        try {
            return queryBus
                    .query(
                            new GenericQueryMessage<>(
                                    new ReadAccounts(),
                                    ResponseTypes.instanceOf(Accounts.class)
                            )
                    )
                    .get()
                    .getPayload();
        } catch (final Exception e) {
            logger.error("Failed to read accounts", e);
            throw new CouldNotReadError("Failed to read accounts", e);
        }
    }
}
