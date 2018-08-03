package de.seidinger.frank.domain.banking

import de.seidinger.frank.domain.banking.Account.*
import de.seidinger.frank.domain.banking.Transaction.*
import de.seidinger.frank.eventsourcing.core.Command
import de.seidinger.frank.eventsourcing.core.Event
import de.seidinger.frank.eventsourcing.core.State
import java.math.BigDecimal

class AccountAggregate : Aggregate {
    enum class AccountState {
        Undefined,
        Opened
    }

    data class Model (
            val id: String,
            val state: AccountState,
            val balance: BigDecimal
    ) : State

    override fun apply(state: State, event: Event): State {
        with(state as Model) {
            return when (event) {
                is Opened -> Model(state.id, AccountState.Opened, event.openingBalance)
                is Credited -> Model(state.id, state.state, state.balance.add(event.amount))
                is Debited -> Model(state.id, state.state, state.balance.subtract(event.amount))
                else -> throw IllegalArgumentException("Event type ${event.javaClass.name} cannot be projected")
            }
        }
    }

    override fun replay(initalState: State, events: Sequence<Event>): State {
        return events.fold(
                initalState,
                this::apply
        )
    }

    override fun decide(command: Command, state: State): List<Event> {
        with (state as Model) {
            return when(command) {
                is Open -> open(command, state)
                is Credit -> credit(command, state)
                is Debit -> debit(command, state)
                else -> throw IllegalArgumentException("Command type ${command.javaClass.name} cannot be decided")
            }
        }
    }

    private fun open(command: Open, state: Model): List<Event> {
        if (state.state != AccountState.Undefined) {
            throw IllegalStateException("An account can only be opened once")
        }

        return listOf(
                Opened(
                        command.id,
                        command.openingBalance
                )
        )
    }

    private fun credit(command: Credit, state: Model): List<Event> {
        if (state.state != AccountState.Opened) {
            throw IllegalStateException("An account must be opened before it can be credited")
        }

        return listOf(
                Credited(
                        command.id,
                        command.amount
                )
        )
    }

    private fun debit(command: Debit, state: Model): List<Event> {
        if (state.state != AccountState.Opened) {
            throw IllegalStateException("An account must be opened before it can be debited")
        }

        if (state.balance.subtract(command.amount) < BigDecimal.ZERO) {
            throw IllegalArgumentException("Account balance is not sufficient to debit ${command.amount} EUR")
        }

        return listOf(
                Debited(
                        command.id,
                        command.amount
                )
        )
    }
}

class TransactionAggregate : Transaction {
    enum class TransactionState {
        Undefined,
        Initiated,
        Finished
    }

    data class Model (
            val id: String,
            val creditAccountId: String,
            val debitAccountId: String,
            val amount: BigDecimal,
            val statement: String,
            val state: TransactionState
    ) : State

    override fun apply(state: State, event: Event): State {
        with(state as Model) {
            return when (event) {
                is Initiated -> Model(event.id, event.creditAccountId, event.debitAccountId, event.amount, event.statement, TransactionState.Initiated)
                is Finished -> Model(state.id, state.creditAccountId, state.debitAccountId, state.amount, state.statement, TransactionState.Finished)
                else -> throw IllegalArgumentException("Event type ${event.javaClass.name} cannot be projected")
            }
        }
    }

    override fun replay(initialState: State, events: Sequence<Event>): State {
        return events.fold(
                initialState,
                this::apply
        )
    }

    override fun decide(command: Command, state: State): List<Event> {
        with (state as Model) {
            return when(command) {
                is Initiate -> initiate(command, state)
                is Finish -> finish(command, state)
                else -> throw IllegalArgumentException("Command type ${command.javaClass.name} cannot be decided")
            }
        }
    }

    private fun initiate(command: Initiate, state: Model): List<Event> {
        return listOf(
                Initiated(
                        command.id,
                        command.creditAccountId,
                        command.debitAccountId,
                        command.amount,
                        command.statement
                )
        )
    }

    private fun finish(command: Finish, state: Model): List<Event> {
        return listOf(
                Finished(
                        command.id
                )
        )
    }

}
