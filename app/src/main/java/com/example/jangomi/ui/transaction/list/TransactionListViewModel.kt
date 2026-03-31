package com.example.jangomi.ui.transaction.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jangomi.domain.model.Transaction
import com.example.jangomi.domain.repository.TransactionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TransactionListUiState(
    val transactions: List<Transaction> = emptyList(),
    val groupedTransactions: Map<String, List<Transaction>> = emptyMap(),
    val searchQuery: String = "",
    val isLoading: Boolean = true
)

sealed class TransactionListUiEvent {
    data class SearchQueryChanged(val query: String) : TransactionListUiEvent()
    data class DeleteTransaction(val id: Long) : TransactionListUiEvent()
}

@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
@HiltViewModel
class TransactionListViewModel @Inject constructor(
    private val repository: TransactionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TransactionListUiState())
    val uiState: StateFlow<TransactionListUiState> = _uiState.asStateFlow()

    private val searchQuery = MutableStateFlow("")

    init {
        searchQuery
            .debounce(300)
            .flatMapLatest { query ->
                if (query.isBlank()) repository.getAllTransactions()
                else repository.searchTransactions(query)
            }
            .onEach { transactions ->
                _uiState.update { state ->
                    state.copy(
                        transactions = transactions,
                        groupedTransactions = transactions.groupBy { it.date.toString() },
                        isLoading = false
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    fun onEvent(event: TransactionListUiEvent) {
        when (event) {
            is TransactionListUiEvent.SearchQueryChanged -> {
                searchQuery.value = event.query
                _uiState.update { it.copy(searchQuery = event.query) }
            }
            is TransactionListUiEvent.DeleteTransaction -> {
                viewModelScope.launch { repository.deleteTransaction(event.id) }
            }
        }
    }
}
