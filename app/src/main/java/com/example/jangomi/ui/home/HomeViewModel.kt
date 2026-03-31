package com.example.jangomi.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jangomi.domain.model.Transaction
import com.example.jangomi.domain.model.TransactionType
import com.example.jangomi.domain.repository.TransactionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import java.time.LocalDate
import javax.inject.Inject

data class HomeUiState(
    val year: Int = LocalDate.now().year,
    val month: Int = LocalDate.now().monthValue,
    val totalIncome: Long = 0L,
    val totalExpense: Long = 0L,
    val recentTransactions: List<Transaction> = emptyList(),
    val isLoading: Boolean = true
)

sealed class HomeUiEvent {
    data object PreviousMonth : HomeUiEvent()
    data object NextMonth : HomeUiEvent()
}

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: TransactionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val selectedYearMonth = MutableStateFlow(
        LocalDate.now().year to LocalDate.now().monthValue
    )

    init {
        selectedYearMonth
            .flatMapLatest { (year, month) ->
                repository.getTransactionsByMonth(year, month)
            }
            .onEach { transactions ->
                val income = transactions
                    .filter { it.type == TransactionType.INCOME }
                    .sumOf { it.amount }
                val expense = transactions
                    .filter { it.type == TransactionType.EXPENSE }
                    .sumOf { it.amount }
                _uiState.update { state ->
                    state.copy(
                        totalIncome = income,
                        totalExpense = expense,
                        recentTransactions = transactions.take(10),
                        isLoading = false
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    fun onEvent(event: HomeUiEvent) {
        when (event) {
            HomeUiEvent.PreviousMonth -> navigateMonth(-1)
            HomeUiEvent.NextMonth -> navigateMonth(1)
        }
    }

    private fun navigateMonth(delta: Int) {
        val current = LocalDate.of(_uiState.value.year, _uiState.value.month, 1)
        val next = current.plusMonths(delta.toLong())
        _uiState.update { it.copy(year = next.year, month = next.monthValue) }
        selectedYearMonth.value = next.year to next.monthValue
    }
}
