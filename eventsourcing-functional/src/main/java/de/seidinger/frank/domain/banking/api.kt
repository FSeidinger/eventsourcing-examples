package de.seidinger.frank.domain.banking

import de.seidinger.frank.domain.banking.Account.*
import de.seidinger.frank.eventsourcing.core.Command
import de.seidinger.frank.eventsourcing.core.Event
import de.seidinger.frank.eventsourcing.core.State
import java.math.BigDecimal
import java.util.stream.Stream
import kotlin.streams.asSequence

interface Aggregate {
    fun apply(state: State, event: Event): State

    fun replay(initalState: State, events: Sequence<Event>): State
    fun replay(initialState: State, events: Stream<Event>): State {
        return replay(initialState, events.asSequence())
    }

    fun decide(command: Command, state: State): List<Event>
}

interface Account : Aggregate {
    data class Open (
            val id: String,
            val openingBalance: BigDecimal
    ) : Command

    data class Credit (
            val id: String,
            val amount: BigDecimal
    ) : Command

    data class Debit (
            val id: String,
            val amount: BigDecimal
    ) : Command

    data class Opened (
            val id: String,
            val openingBalance: BigDecimal
    ) : Event

    data class Credited(
            val id: String,
            val amount: BigDecimal
    ) : Event

    data class Debited(
            val id: String,
            val amount: BigDecimal
    ) : Event
}


interface Transaction : Aggregate {
    data class Initiate (
            val id: String,
            val creditAccountId: String,
            val debitAccountId: String,
            val amount: BigDecimal,
            val statement: String
    ) : Command

    data class Finish (
            val id: String
    ) : Command

    data class Initiated (
            val id: String,
            val creditAccountId: String,
            val debitAccountId: String,
            val amount: BigDecimal,
            val statement: String
    ) : Event

    data class Finished(
            val id: String
    ) : Event
}

class TransactionProcess {
    data class Model (
            val id: String,
            val creditAccountId: String,
            val debitAccountId: String,
            val creditDone: Boolean,
            val debitDone: Boolean
    ) : State

    fun apply(state: State, event: Event): State {
        with(state as Model) {
            return when (event) {
                is Transaction.Initiated -> Model(event.id, event.creditAccountId, event.debitAccountId, false, false)
                is Debited -> Model(state.id, state.creditAccountId, state.debitAccountId, state.creditDone, true)
                is Credited -> Model(state.id, state.creditAccountId, state.creditAccountId, true, state.debitDone)
                else -> throw IllegalArgumentException("Event type ${event.javaClass.name} cannot be projected")
            }
        }
    }

    private fun replay(initialState: State, events: Sequence<Event>): State {
        return events.fold(
                initialState,
                this::apply
        )
    }

    // For java streams api compatibility
    fun replay(initialState: TransactionProcess.Model, events: Stream<Event>): State {
        return replay(initialState, events.asSequence())
    }

    fun decide(event: Event, state: State): List<Command> {
        with(state as TransactionProcess.Model) {
            return when(event) {
                is Transaction.Initiated -> initiated(event, state)
                is Credited -> credited(event, state)
                is Debited -> debited(event, state)
                is Transaction.Finished -> finished(event, state)
                else -> throw IllegalArgumentException("Event type ${event.javaClass.name} cannot be handled")
            }
        }
    }

    private fun initiated(event: Transaction.Initiated, state: TransactionProcess.Model): List<Command> {
        return listOf(
                Credit(
                        event.creditAccountId,
                        event.amount
                ),
                Debit(
                        event.debitAccountId,
                        event.amount
                )
        )
    }

    private fun credited(event: Credited, state: TransactionProcess.Model): List<Command> {
        return if (isMoneyTransferDone(true, state.debitDone)) {
            listOf(
                    Transaction.Finish(
                            state.id
                    )
            )
        } else {
            listOf()
        }
    }

    private fun debited(event: Debited, state: TransactionProcess.Model): List<Command> {
        return if (isMoneyTransferDone(state.debitDone, true)) {
            listOf(
                    Transaction.Finish(
                            state.id
                    )
            )
        } else {
            listOf()
        }
    }

    private fun finished(event: Transaction.Finished, state: TransactionProcess.Model): List<Command> {
        return listOf()
    }

    private fun isMoneyTransferDone(creditDone: Boolean, debitDone: Boolean): Boolean {
        return creditDone && debitDone
    }
}