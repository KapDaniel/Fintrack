package com.fintrack.app.ui.screens.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fintrack.app.core.format.MoneyFormatter
import com.fintrack.app.domain.model.Category
import com.fintrack.app.domain.model.TransactionType
import com.fintrack.app.domain.repository.TransactionRepository
import com.fintrack.app.domain.usecase.ValidateTransactionInput
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId

data class AddTransactionUiState(
    val amountRaw: String = "",
    val categoryId: Long? = null,
    val dateMillis: Long = System.currentTimeMillis(),
    val type: TransactionType = TransactionType.EXPENSE,
    val comment: String = "",
    val categories: List<Category> = emptyList(),
    val balanceCents: Long = 0L,
    /** Ошибки показываются только после попытки сохранить или ухода из поля. */
    val showErrors: Boolean = false,
    val isSaving: Boolean = false,
    val savedEvent: Boolean = false,
) {
    val validation: ValidateTransactionInput.Result
        get() = ValidateTransactionInput(
            ValidateTransactionInput.Input(
                amountRaw = amountRaw,
                categoryId = categoryId,
                dateMillis = dateMillis,
                comment = comment,
            )
        )

    val canSubmit: Boolean get() = validation.isValid && !isSaving

    val amountError: MoneyFormatter.AmountError?
        get() = if (showErrors) validation.amountError else null

    val categoryError: Boolean get() = showErrors && validation.categoryMissing

    val selectedCategory: Category? get() = categories.firstOrNull { it.id == categoryId }
}

class AddTransactionViewModel(
    private val repository: TransactionRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddTransactionUiState())
    val uiState: StateFlow<AddTransactionUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.observeCategories().collect { categories ->
                _uiState.update { it.copy(categories = categories) }
            }
        }
        viewModelScope.launch {
            repository.observeBalanceCents().collect { balance ->
                _uiState.update { it.copy(balanceCents = balance) }
            }
        }
    }

    fun onAmountChange(value: String) {
        // Не даём вводить буквы и второй разделитель — ошибку проще предотвратить.
        val filtered = value.filter { it.isDigit() || it == '.' || it == ',' }
        _uiState.update { it.copy(amountRaw = filtered) }
    }

    fun onCategorySelected(category: Category) {
        _uiState.update { state ->
            state.copy(
                categoryId = category.id,
                // Категория с явным типом подставляет его — экономит одно касание.
                type = category.defaultType ?: state.type,
            )
        }
    }

    fun onTypeSelected(type: TransactionType) {
        _uiState.update { it.copy(type = type) }
    }

    /** Пикер возвращает дату; время берём текущее, чтобы порядок в списке был осмысленным. */
    fun onDateSelected(dateMillis: Long) {
        val zone = ZoneId.systemDefault()
        val date = LocalDate.ofEpochDay(dateMillis / MILLIS_IN_DAY)
        val time = LocalTime.now(zone)
        val millis = LocalDateTime.of(date, time).atZone(zone).toInstant().toEpochMilli()
        _uiState.update { it.copy(dateMillis = millis) }
    }

    fun onCommentChange(value: String) {
        if (value.length <= ValidateTransactionInput.MAX_COMMENT_LENGTH) {
            _uiState.update { it.copy(comment = value) }
        }
    }

    fun onFieldBlurred() {
        _uiState.update { it.copy(showErrors = true) }
    }

    fun onSubmit() {
        val state = _uiState.value
        val validation = state.validation
        if (!validation.isValid || state.isSaving) {
            _uiState.update { it.copy(showErrors = true) }
            return
        }

        _uiState.update { it.copy(isSaving = true) }
        viewModelScope.launch {
            repository.addTransaction(
                amountCents = requireNotNull(validation.amountCents),
                type = state.type,
                categoryId = requireNotNull(state.categoryId),
                dateTimeMillis = state.dateMillis,
                comment = state.comment,
            )
            _uiState.update {
                AddTransactionUiState(
                    categories = it.categories,
                    balanceCents = it.balanceCents,
                    savedEvent = true,
                )
            }
        }
    }

    fun onSavedEventHandled() {
        _uiState.update { it.copy(savedEvent = false) }
    }

    private companion object {
        const val MILLIS_IN_DAY = 86_400_000L
    }
}
