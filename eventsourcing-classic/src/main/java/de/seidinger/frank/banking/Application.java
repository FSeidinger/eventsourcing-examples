package de.seidinger.frank.banking;

import de.seidinger.frank.banking.domain.banking.api.InitiateCreditTransfer;
import de.seidinger.frank.banking.domain.banking.api.Open;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.math.BigDecimal;

@SpringBootApplication
public class Application implements CommandLineRunner {
    private final CommandGateway commandGateway;

    @Autowired
    public Application(final CommandGateway commandGateway) {
        this.commandGateway = commandGateway;
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        commandGateway.sendAndWait(
                new Open(
                        "41ef81b7-064f-42f4-8f44-41eda197e7fd",
                        new BigDecimal("2678.95")
                )
        );

        commandGateway.sendAndWait(
                new Open(
                        "bc6c9849-76f6-4ff8-8bbe-711d992b9500",
                        new BigDecimal("3624.54")
                )
        );


        commandGateway.sendAndWait(
                new InitiateCreditTransfer(
                        "65bdbe93-f601-44d3-8fea-1947490cf986",
                        "bc6c9849-76f6-4ff8-8bbe-711d992b9500",
                        "41ef81b7-064f-42f4-8f44-41eda197e7fd",
                        new BigDecimal("1.23"),
                        "JUG Nürnberg Jahresbeitrag"
                )
        );
    }
}
