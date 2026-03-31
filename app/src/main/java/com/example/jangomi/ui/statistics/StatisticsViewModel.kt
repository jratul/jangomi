package com.example.jangomi.ui.statistics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jangomi.domain.model.Category
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

data class CategoryAmount(val category: Category, val amount: Long)
data class MonthlyAmount(val month: Int, val income: Long, val expense: Long)

enum class StatisticsViewMode { MONTHLY, YEARLY }

data class StatisticsUiState(
    val year: Int = LocalDate.now().year,
    val month: Int = LocalDate.now().monthValue,
    val viewMode: StatisticsViewMode = StatisticsViewMode.MONTHLY,
    val expenseBreakdown: List<CategoryAmount> = emptyList(),
    val incomeBreakdown: List<CategoryAmount> = emptyList(),
    val monthlyTrend: List<MonthlyAmount> = emptyList(),
    val totalExpense: Long = 0L,
    val totalIncome: Long = 0L,
    val isLoading: Boolean = true
)

sealed class StatisticsUiEvent {
    data object PreviousMonth : StatisticsUiEvent()
    data object NextMonth : StatisticsUiEvent()
    data object PreviousYear : StatisticsUiEvent()
    data object NextYear : StatisticsUiEvent()
    data class ViewModeChanged(val mode: StatisticsViewMode) : StatisticsUiEvent()
}

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class StatisticsViewModel @Inject constructor(
    private val repository: TransactionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(StatisticsUiState())
    val uiState: StateFlow<StatisticsUiState> = _uiState.asStateFlow()

    private val selectedYearMonth = MutableStateFlow(
        LocalDate.now().year to LocalDate.now().monthValue
    )

    init {
        selectedYearMonth
            .flatMapLatest { (year, month) ->
                repository.getTransactionsByMonth(year, month)
            }
            .onEach { transactions ->
                val expenseByCategory = transactions
                    .filter { it.type == TransactionType.EXPENSE }
                    .groupBy { it.category }
                    .map { (cat, list) -> CategoryAmount(cat, list.sumOf { it.amount }) }
                    .sortedByDescending { it.amount }

                val incomeByCategory = transactions
                    .filter { it.type == TransactionType.INCOME }
                    .groupBy { it.category }
                    .map { (cat, list) -> CategoryAmount(cat, list.sumOf { it.amount }) }
                    .sortedByDescending { it.amount }

                _uiState.update { state ->
                    state.copy(
                        expenseBreakdown = expenseByCategory,
                        incomeBreakdown = incomeByCategory,
                        totalExpense = expenseByCategory.sumOf { it.amount },
                        totalIncome = incomeByCategory.sumOf { it.amount },
                        isLoading = false
                    )
                }
            }
            .launchIn(viewModelScope)

        // 연간 트렌드 (전체 거래 기반)
        repository.getAllTransactions()
            .onEach { all ->
                val year = _uiState.value.year
                val monthly = (1..12).map { month ->
                    val filtered = all.filter {
                        it.date.year == year && it.date.monthValue == month
                    }
                    MonthlyAmount(
                        month = month,
                        income = filtered.filter { it.type == TransactionType.INCOME }.sumOf { it.amount },
                        expense = filtered.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount }
                    )
                }
                _uiState.update { it.copy(monthlyTrend = monthly) }
            }
            .launchIn(viewModelScope)
    }

    fun onEvent(event: StatisticsUiEvent) {
        when (event) {
            StatisticsUiEvent.PreviousMonth -> navigateMonth(-1)
            StatisticsUiEvent.NextMonth -> navigateMonth(1)
            StatisticsUiEvent.PreviousYear -> navigateYear(-1)
            StatisticsUiEvent.NextYear -> navigateYear(1)
            is StatisticsUiEvent.ViewModeChanged ->
                _uiState.update { it.copy(viewMode = event.mode) }
        }
    }

    private fun navigateMonth(delta: Int) {
        val current = LocalDate.of(_uiState.value.year, _uiState.value.month, 1)
        val next = current.plusMonths(delta.toLong())
        _uiState.update { it.copy(year = next.year, month = next.monthValue) }
        selectedYearMonth.value = next.year to next.monthValue
    }

    private fun navigateYear(delta: Int) {
        val newYear = _uiState.value.year + delta
        _uiState.update { it.copy(year = newYear) }
        selectedYearMonth.value = newYear to _uiState.value.month
    }
}
