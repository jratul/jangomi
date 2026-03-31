package com.example.jangomi.ui.transaction.edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jangomi.domain.model.Category
import com.example.jangomi.domain.model.Transaction
import com.example.jangomi.domain.model.TransactionType
import com.example.jangomi.domain.repository.TransactionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

data class TransactionEditUiState(
    val id: Long = 0L,
    val amount: String = "",
    val type: TransactionType = TransactionType.EXPENSE,
    val category: Category = Category.FOOD,
    val merchantName: String = "",
    val memo: String = "",
    val date: LocalDate = LocalDate.now(),
    val isLoading: Boolean = false,
    val isSaved: Boolean = false,
    val isDeleted: Boolean = false
)

sealed class TransactionEditUiEvent {
    data class AmountChanged(val amount: String) : TransactionEditUiEvent()
    data class TypeChanged(val type: TransactionType) : TransactionEditUiEvent()
    data class CategoryChanged(val category: Category) : TransactionEditUiEvent()
    data class MerchantNameChanged(val name: String) : TransactionEditUiEvent()
    data class MemoChanged(val memo: String) : TransactionEditUiEvent()
    data class DateChanged(val date: LocalDate) : TransactionEditUiEvent()
    data object Save : TransactionEditUiEvent()
    data object Delete : TransactionEditUiEvent()
}

@HiltViewModel
class TransactionEditViewModel @Inject constructor(
    private val repository: TransactionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TransactionEditUiState())
    val uiState: StateFlow<TransactionEditUiState> = _uiState.asStateFlow()

    fun loadTransaction(id: Long) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val transaction = repository.getTransactionById(id)
            if (transaction != null) {
                _uiState.update {
                    TransactionEditUiState(
                        id = transaction.id,
                        amount = transaction.amount.toString(),
                        type = transaction.type,
                        category = transaction.category,
                        merchantName = transaction.merchantName,
                        memo = transaction.memo,
                        date = transaction.date,
                        isLoading = false
                    )
                }
            } else {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun onEvent(event: TransactionEditUiEvent) {
        when (event) {
            is TransactionEditUiEvent.AmountChanged ->
                _uiState.update { it.copy(amount = event.amount.filter { c -> c.isDigit() }) }

            is TransactionEditUiEvent.TypeChanged -> {
                val defaultCategory = Category.defaultFor(event.type)
                _uiState.update { it.copy(type = event.type, category = defaultCategory) }
            }

            is TransactionEditUiEvent.CategoryChanged ->
                _uiState.update { it.copy(category = event.category) }

            is TransactionEditUiEvent.MerchantNameChanged ->
                _uiState.update { it.copy(merchantName = event.name) }

            is TransactionEditUiEvent.MemoChanged ->
                _uiState.update { it.copy(memo = event.memo) }

            is TransactionEditUiEvent.DateChanged ->
                _uiState.update { it.copy(date = event.date) }

            TransactionEditUiEvent.Save -> save()
            TransactionEditUiEvent.Delete -> delete()
        }
    }

    private fun save() {
        val state = _uiState.value
        val amount = state.amount.toLongOrNull() ?: return
        if (amount <= 0) return

        viewModelScope.launch {
            val transaction = Transaction(
                id = state.id,
                amount = amount,
                type = state.type,
                category = state.category,
                merchantName = state.merchantName,
                memo = state.memo,
                date = state.date
            )
            if (state.id == 0L) {
                repository.insertTransaction(transaction)
            } else {
                repository.updateTransaction(transaction)
            }
            _uiState.update { it.copy(isSaved = true) }
        }
    }

    private fun delete() {
        val id = _uiState.value.id
        if (id == 0L) return
        viewModelScope.launch {
            repository.deleteTransaction(id)
            _uiState.update { it.copy(isDeleted = true) }
        }
    }
}
