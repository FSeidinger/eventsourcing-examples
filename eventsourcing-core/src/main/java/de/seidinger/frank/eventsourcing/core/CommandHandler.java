package de.seidinger.frank.eventsourcing.core;

import java.util.List;

@FunctionalInterface
public interface CommandHandler {
    List<Event> handle(final Command command, final State state);
}
