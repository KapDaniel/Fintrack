package com.fintrack.app.ui.screens.add

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fintrack.app.R
import com.fintrack.app.core.format.DateFormatter
import com.fintrack.app.core.format.MoneyFormatter
import com.fintrack.app.di.AppViewModelFactory
import com.fintrack.app.domain.model.Category
import com.fintrack.app.domain.model.TransactionType
import com.fintrack.app.domain.usecase.ValidateTransactionInput
import com.fintrack.app.ui.components.CategoryIcons
import com.fintrack.app.ui.components.FinTrackScaffold
import com.fintrack.app.ui.components.NotificationsButton
import com.fintrack.app.ui.components.PrimaryButton
import com.fintrack.app.ui.navigation.LocalContentBottomPadding
import com.fintrack.app.ui.theme.BalanceLabelStyle
import com.fintrack.app.ui.theme.FinTheme
import com.fintrack.app.ui.theme.Spacing

@Composable
fun AddTransactionScreen(
    onSaved: () -> Unit,
    viewModel: AddTransactionViewModel = viewModel(factory = AppViewModelFactory),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val colors = FinTheme.colors

    LaunchedEffect(state.savedEvent) {
        if (state.savedEvent) {
            viewModel.onSavedEventHandled()
            onSaved()
        }
    }

    FinTrackScaffold(
        header = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = stringResource(R.string.add_title),
                    style = MaterialTheme.typography.titleLarge,
                    color = colors.textInverse,
                    modifier = Modifier.weight(1f, fill = false),
                )
                NotificationsButton()
            }
            Text(
                text = stringResource(R.string.add_subtitle),
                style = MaterialTheme.typography.bodyMedium,
                color = colors.textInverse,
                modifier = Modifier.padding(top = Spacing.x2),
            )
            Column(modifier = Modifier.padding(top = Spacing.x4)) {
                Text(
                    text = stringResource(R.string.add_available_label).uppercase(),
                    style = BalanceLabelStyle,
                    color = colors.textInverse,
                )
                Text(
                    text = MoneyFormatter.formatBalance(state.balanceCents),
                    style = MaterialTheme.typography.headlineMedium,
                    color = colors.textInverse,
                )
            }
        },
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .imePadding()
                .padding(
                    start = Spacing.screenPadding,
                    end = Spacing.screenPadding,
                    top = Spacing.x4,
                ),
        ) {
            AmountField(
                value = state.amountRaw,
                error = state.amountError,
                onValueChange = viewModel::onAmountChange,
            )
            CategoryField(
                categories = state.categories,
                selected = state.selectedCategory,
                showError = state.categoryError,
                onSelect = viewModel::onCategorySelected,
            )
            DateField(
                dateMillis = state.dateMillis,
                onDateSelected = viewModel::onDateSelected,
            )
            TypeField(
                selected = state.type,
                onSelect = viewModel::onTypeSelected,
            )
            CommentField(
                value = state.comment,
                onValueChange = viewModel::onCommentChange,
            )
            // Кнопка остаётся активной: иначе ошибки валидации некуда показать —
            // пользователь просто упирается в серую кнопку без объяснения.
            PrimaryButton(
                label = stringResource(R.string.add_submit),
                onClick = viewModel::onSubmit,
                enabled = !state.isSaving,
                modifier = Modifier.padding(top = Spacing.x2),
            )
            Box(Modifier.padding(bottom = LocalContentBottomPadding.current))
        }
    }
}

@Composable
private fun FieldContainer(
    label: String,
    modifier: Modifier = Modifier,
    optional: Boolean = false,
    content: @Composable () -> Unit,
) {
    Column(modifier = modifier.padding(bottom = Spacing.x4)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = label,
                style = MaterialTheme.typography.titleSmall,
                color = FinTheme.colors.textPrimary,
            )
            if (optional) {
                Text(
                    text = " " + stringResource(R.string.add_comment_optional),
                    style = MaterialTheme.typography.bodySmall,
                    color = FinTheme.colors.textSecondary,
                )
            }
        }
        Box(Modifier.padding(top = Spacing.x2)) { content() }
    }
}

@Composable
private fun fieldColors() = OutlinedTextFieldDefaults.colors(
    focusedContainerColor = FinTheme.colors.surfaceAlt2,
    unfocusedContainerColor = FinTheme.colors.surfaceAlt2,
    errorContainerColor = FinTheme.colors.surfaceAlt2,
    focusedBorderColor = FinTheme.colors.navBar,
    unfocusedBorderColor = FinTheme.colors.border,
    errorBorderColor = FinTheme.colors.danger,
    focusedTextColor = FinTheme.colors.textPrimary,
    unfocusedTextColor = FinTheme.colors.textPrimary,
)

@Composable
private fun FieldMessage(
    text: String,
    isError: Boolean,
    iconRes: Int,
) {
    val colors = FinTheme.colors
    Row(
        modifier = Modifier.padding(top = Spacing.x2),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.x1),
    ) {
        Icon(
            painter = painterResource(iconRes),
            contentDescription = null,
            tint = if (isError) colors.danger else colors.textSecondary,
            modifier = Modifier.size(16.dp),
        )
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = if (isError) colors.danger else colors.textSecondary,
        )
    }
}

@Composable
private fun AmountField(
    value: String,
    error: MoneyFormatter.AmountError?,
    onValueChange: (String) -> Unit,
) {
    FieldContainer(label = stringResource(R.string.add_amount_label)) {
        Column {
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = Spacing.minTouch),
                placeholder = { Text(stringResource(R.string.add_amount_placeholder)) },
                prefix = { Text("$") },
                singleLine = true,
                isError = error != null,
                shape = RoundedCornerShape(16.dp),
                colors = fieldColors(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            )
            if (error != null) {
                FieldMessage(
                    text = stringResource(R.string.add_amount_error),
                    isError = true,
                    iconRes = R.drawable.ic_alert_circle,
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CategoryField(
    categories: List<Category>,
    selected: Category?,
    showError: Boolean,
    onSelect: (Category) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }

    FieldContainer(label = stringResource(R.string.add_category_label)) {
        Column {
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = it },
            ) {
                OutlinedTextField(
                    value = selected?.name ?: "",
                    onValueChange = {},
                    readOnly = true,
                    placeholder = { Text(stringResource(R.string.add_category_placeholder)) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    isError = showError,
                    shape = RoundedCornerShape(16.dp),
                    colors = fieldColors(),
                    modifier = Modifier
                        .menuAnchor(androidx.compose.material3.MenuAnchorType.PrimaryNotEditable)
                        .fillMaxWidth()
                        .heightIn(min = Spacing.minTouch),
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                ) {
                    categories.forEach { category ->
                        DropdownMenuItem(
                            text = { Text(category.name) },
                            leadingIcon = {
                                Icon(
                                    painter = painterResource(CategoryIcons.iconFor(category.iconKey)),
                                    contentDescription = null,
                                    tint = FinTheme.colors.navBar,
                                    modifier = Modifier.size(20.dp),
                                )
                            },
                            onClick = {
                                onSelect(category)
                                expanded = false
                            },
                        )
                    }
                }
            }
            FieldMessage(
                text = if (showError) {
                    stringResource(R.string.add_category_error)
                } else {
                    stringResource(R.string.add_category_hint)
                },
                isError = showError,
                iconRes = if (showError) R.drawable.ic_alert_circle else R.drawable.ic_info,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DateField(
    dateMillis: Long,
    onDateSelected: (Long) -> Unit,
) {
    var showPicker by rememberSaveable { mutableStateOf(false) }
    val colors = FinTheme.colors

    FieldContainer(label = stringResource(R.string.add_date_label)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(colors.surfaceAlt2)
                .clickable(
                    role = Role.Button,
                    onClickLabel = stringResource(R.string.add_date_pick),
                    onClick = { showPicker = true },
                )
                .heightIn(min = Spacing.minTouch)
                .padding(horizontal = Spacing.x3),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.x2),
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_calendar),
                contentDescription = null,
                tint = colors.textSecondary,
                modifier = Modifier.size(20.dp),
            )
            Text(
                text = DateFormatter.formField(dateMillis),
                style = MaterialTheme.typography.bodyLarge,
                color = colors.textPrimary,
            )
        }
    }

    if (showPicker) {
        val pickerState = rememberDatePickerState(initialSelectedDateMillis = dateMillis)
        DatePickerDialog(
            onDismissRequest = { showPicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        pickerState.selectedDateMillis?.let(onDateSelected)
                        showPicker = false
                    },
                ) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showPicker = false }) { Text("Отмена") }
            },
        ) {
            DatePicker(state = pickerState)
        }
    }
}

@Composable
private fun TypeField(
    selected: TransactionType,
    onSelect: (TransactionType) -> Unit,
) {
    val colors = FinTheme.colors
    FieldContainer(label = stringResource(R.string.add_type_label)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(colors.surfaceAlt2)
                .padding(3.dp),
            horizontalArrangement = Arrangement.spacedBy(3.dp),
        ) {
            SegmentButton(
                label = stringResource(R.string.add_type_expense),
                iconRes = R.drawable.ic_arrow_down_right,
                selected = selected == TransactionType.EXPENSE,
                selectedBackground = colors.expenseBg,
                selectedContent = colors.expenseFg,
                onClick = { onSelect(TransactionType.EXPENSE) },
                modifier = Modifier.weight(1f),
            )
            SegmentButton(
                label = stringResource(R.string.add_type_income),
                iconRes = R.drawable.ic_arrow_up_right,
                selected = selected == TransactionType.INCOME,
                selectedBackground = colors.incomeBg,
                selectedContent = colors.incomeFg,
                onClick = { onSelect(TransactionType.INCOME) },
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun SegmentButton(
    label: String,
    iconRes: Int,
    selected: Boolean,
    selectedBackground: Color,
    selectedContent: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = FinTheme.colors
    val content = if (selected) selectedContent else colors.textSecondary
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(if (selected) selectedBackground else Color.Transparent)
            .clickable(role = Role.RadioButton, onClick = onClick)
            .heightIn(min = Spacing.minTouch),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        Icon(
            painter = painterResource(iconRes),
            contentDescription = null,
            tint = content,
            modifier = Modifier.size(16.dp),
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = content,
            modifier = Modifier.padding(start = Spacing.x1),
        )
    }
}

@Composable
private fun CommentField(
    value: String,
    onValueChange: (String) -> Unit,
) {
    FieldContainer(
        label = stringResource(R.string.add_comment_label),
        optional = true,
    ) {
        Column {
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 88.dp),
                placeholder = { Text(stringResource(R.string.add_comment_placeholder)) },
                minLines = 3,
                shape = RoundedCornerShape(16.dp),
                colors = fieldColors(),
            )
            FieldMessage(
                text = "${value.length}/${ValidateTransactionInput.MAX_COMMENT_LENGTH} · " +
                    stringResource(R.string.add_comment_hint),
                isError = false,
                iconRes = R.drawable.ic_message,
            )
        }
    }
}
