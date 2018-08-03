package de.seidinger.frank.banking.infrastructure;

import org.axonframework.eventhandling.EventBus;
import org.axonframework.eventsourcing.eventstore.EmbeddedEventStore;
import org.axonframework.eventsourcing.eventstore.EventStorageEngine;
import org.axonframework.eventsourcing.eventstore.inmemory.InMemoryEventStorageEngine;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AxonConfig {
    @Bean
    public EventStorageEngine eventStorageEngineFactroy() {
        return new InMemoryEventStorageEngine();
    }

    @Bean
    public EventBus eventBusFactory(final EventStorageEngine engine) {
        return new EmbeddedEventStore(engine);
    }
}
