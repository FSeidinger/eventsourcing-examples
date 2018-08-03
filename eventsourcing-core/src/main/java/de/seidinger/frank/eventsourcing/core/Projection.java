package de.seidinger.frank.eventsourcing.core;

import java.util.stream.Stream;

/**
 * A central concept of eventsourcing is a projection. A projection transforms a stream of events into and aggregated
 * state. In the context of CQRS projections are called read model.
 *
 * In the context of an aggregate, a projection is used to create the state necessary to enforce the invariants
 * of that aggregate. In the context of CQRS these kind of projections are called the write model.
 */
@FunctionalInterface
public interface Projection {
    State project(final Stream<Event> eventStream);
}
