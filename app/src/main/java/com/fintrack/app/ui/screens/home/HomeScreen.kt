package com.fintrack.app.ui.screens.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fintrack.app.R
import com.fintrack.app.core.format.MoneyFormatter
import com.fintrack.app.di.AppViewModelFactory
import com.fintrack.app.domain.model.TransactionType
import com.fintrack.app.ui.components.EmptyState
import com.fintrack.app.ui.components.FinTrackScaffold
import com.fintrack.app.ui.components.SectionHeader
import com.fintrack.app.ui.components.StatCard
import com.fintrack.app.ui.components.StatCardKind
import com.fintrack.app.ui.components.TransactionRow
import com.fintrack.app.ui.navigation.LocalContentBottomPadding
import com.fintrack.app.ui.theme.BalanceLabelStyle
import com.fintrack.app.ui.theme.FinTheme
import com.fintrack.app.ui.theme.Spacing

@Composable
fun HomeScreen(
    onOpenAllTransactions: () -> Unit,
    onAddTransaction: () -> Unit,
    viewModel: HomeViewModel = viewModel(factory = AppViewModelFactory),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    HomeContent(
        state = state,
        onOpenAllTransactions = onOpenAllTransactions,
        onAddTransaction = onAddTransaction,
    )
}

@Composable
private fun HomeContent(
    state: HomeUiState,
    onOpenAllTransactions: () -> Unit,
    onAddTransaction: () -> Unit,
) {
    var expanded by rememberSaveable { mutableStateOf(false) }
    val colors = FinTheme.colors

    FinTrackScaffold(
        header = {
            Text(
                text = stringResource(R.string.home_title),
                style = MaterialTheme.typography.titleLarge,
                color = colors.textInverse,
            )
            Text(
                text = stringResource(R.string.home_greeting),
                style = MaterialTheme.typography.bodyMedium,
                color = colors.textInverse,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = Spacing.x2),
            )
            Column(modifier = Modifier.padding(top = Spacing.x4)) {
                Text(
                    text = stringResource(R.string.home_balance_label).uppercase(),
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
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(
                start = Spacing.screenPadding,
                end = Spacing.screenPadding,
                top = Spacing.x4,
                bottom = LocalContentBottomPadding.current,
            ),
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.x3),
                ) {
                    StatCard(
                        label = stringResource(R.string.home_income),
                        value = MoneyFormatter.formatSigned(state.incomeCents, TransactionType.INCOME),
                        spokenValue = MoneyFormatter.spoken(state.incomeCents),
                        kind = StatCardKind.INCOME,
                        modifier = Modifier.weight(1f),
                    )
                    StatCard(
                        label = stringResource(R.string.home_expense),
                        value = MoneyFormatter.formatSigned(state.expenseCents, TransactionType.EXPENSE),
                        spokenValue = MoneyFormatter.spoken(state.expenseCents),
                        kind = StatCardKind.EXPENSE,
                        modifier = Modifier.weight(1f),
                    )
                }
            }

            item {
                SectionHeader(
                    title = stringResource(R.string.home_recent_title),
                    actionLabel = if (state.isEmpty) null else stringResource(R.string.home_all_transactions),
                    onActionClick = if (state.isEmpty) null else onOpenAllTransactions,
                    modifier = Modifier.padding(top = Spacing.x5),
                )
            }

            if (state.isEmpty) {
                item {
                    EmptyState(
                        title = stringResource(R.string.home_empty_title),
                        subtitle = stringResource(R.string.home_empty_subtitle),
                        actionLabel = stringResource(R.string.home_empty_action),
                        onActionClick = onAddTransaction,
                    )
                }
            } else {
                items(state.recent, key = { it.id }) { transaction ->
                    TransactionRow(
                        transaction = transaction,
                        showDivider = transaction != state.recent.last() || state.hasMore,
                    )
                }

                if (state.hasMore) {
                    item {
                        ExpandToggle(
                            expanded = expanded,
                            onToggle = { expanded = !expanded },
                        )
                    }
                    item {
                        AnimatedVisibility(visible = expanded) {
                            Column {
                                state.more.forEach { transaction ->
                                    TransactionRow(
                                        transaction = transaction,
                                        showDivider = transaction != state.more.last(),
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ExpandToggle(
    expanded: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = FinTheme.colors
    val label = stringResource(if (expanded) R.string.home_show_less else R.string.home_show_more)
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(colors.surface)
            .clickable(role = Role.Button, onClick = onToggle)
            .heightIn(min = Spacing.minTouch)
            .padding(vertical = Spacing.x1),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.x1),
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = colors.navBar,
        )
        Icon(
            painter = painterResource(R.drawable.ic_chevron_down),
            contentDescription = null,
            tint = colors.navBar,
            modifier = Modifier
                .size(16.dp)
                .rotate(if (expanded) 180f else 0f),
        )
    }
}
