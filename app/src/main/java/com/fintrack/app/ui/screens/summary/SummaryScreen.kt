package com.fintrack.app.ui.screens.summary

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fintrack.app.R
import com.fintrack.app.core.format.DateFormatter
import com.fintrack.app.core.format.MoneyFormatter
import com.fintrack.app.di.AppViewModelFactory
import com.fintrack.app.domain.model.CategoryTotal
import com.fintrack.app.domain.model.DistributionSlice
import com.fintrack.app.domain.model.Period
import com.fintrack.app.domain.model.TransactionType
import com.fintrack.app.ui.components.CategoryIcons
import com.fintrack.app.ui.components.EmptyState
import com.fintrack.app.ui.components.FinTrackScaffold
import com.fintrack.app.ui.components.SectionHeader
import com.fintrack.app.ui.components.StatCard
import com.fintrack.app.ui.components.StatCardKind
import com.fintrack.app.ui.components.TransactionRow
import com.fintrack.app.ui.navigation.LocalContentBottomPadding
import com.fintrack.app.ui.theme.FinChartRest
import com.fintrack.app.ui.theme.FinChartTint1
import com.fintrack.app.ui.theme.FinChartTint2
import com.fintrack.app.ui.theme.FinChartTint3
import com.fintrack.app.ui.theme.FinTheme
import com.fintrack.app.ui.theme.PillShape
import com.fintrack.app.ui.theme.Spacing

@Composable
fun SummaryScreen(
    viewModel: SummaryViewModel = viewModel(factory = AppViewModelFactory),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val colors = FinTheme.colors

    FinTrackScaffold(
        header = {
            Text(
                text = stringResource(R.string.summary_title),
                style = MaterialTheme.typography.titleLarge,
                color = colors.textInverse,
            )
            Text(
                text = stringResource(R.string.summary_subtitle),
                style = MaterialTheme.typography.bodyMedium,
                color = colors.textInverse,
                modifier = Modifier.padding(top = Spacing.x2),
            )
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
                PeriodSelector(
                    period = state.period,
                    available = state.availablePeriods,
                    onPrevious = viewModel::onPreviousPeriod,
                    onNext = viewModel::onNextPeriod,
                    onSelect = viewModel::onPeriodSelected,
                )
            }

            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = Spacing.x4),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.x3),
                ) {
                    StatCard(
                        label = stringResource(R.string.home_income),
                        value = MoneyFormatter.formatSigned(state.summary.incomeCents, TransactionType.INCOME),
                        spokenValue = MoneyFormatter.spoken(state.summary.incomeCents),
                        kind = StatCardKind.INCOME,
                        modifier = Modifier.weight(1f),
                    )
                    StatCard(
                        label = stringResource(R.string.home_expense),
                        value = MoneyFormatter.formatSigned(state.summary.expenseCents, TransactionType.EXPENSE),
                        spokenValue = MoneyFormatter.spoken(state.summary.expenseCents),
                        kind = StatCardKind.EXPENSE,
                        modifier = Modifier.weight(1f),
                    )
                }
            }

            item {
                StatCard(
                    label = stringResource(R.string.summary_balance),
                    value = MoneyFormatter.formatPeriodBalance(state.summary.balanceCents),
                    spokenValue = MoneyFormatter.spoken(state.summary.balanceCents),
                    kind = StatCardKind.BALANCE,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = Spacing.x3),
                )
            }

            if (state.isEmpty) {
                item {
                    EmptyState(
                        title = stringResource(R.string.summary_empty_title),
                        subtitle = stringResource(R.string.summary_empty_subtitle),
                    )
                }
                return@LazyColumn
            }

            item {
                SectionHeader(
                    title = stringResource(R.string.summary_categories),
                    modifier = Modifier.padding(top = Spacing.x5),
                )
            }
            items(state.summary.topCategories.chunked(2)) { pair ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = Spacing.x3),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.x3),
                ) {
                    pair.forEach { total ->
                        CategoryCard(total = total, modifier = Modifier.weight(1f))
                    }
                    if (pair.size == 1) {
                        Box(Modifier.weight(1f))
                    }
                }
            }

            if (state.summary.distribution.isNotEmpty()) {
                item {
                    SectionHeader(
                        title = stringResource(R.string.summary_distribution),
                        modifier = Modifier.padding(top = Spacing.x3),
                    )
                }
                item {
                    DistributionChart(slices = state.summary.distribution)
                }
            }

            item {
                SectionHeader(
                    title = stringResource(R.string.home_recent_title),
                    modifier = Modifier.padding(top = Spacing.x5),
                )
            }
            items(state.recent, key = { it.id }) { transaction ->
                TransactionRow(
                    transaction = transaction,
                    showDivider = transaction != state.recent.last(),
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PeriodSelector(
    period: Period,
    available: List<Period>,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    onSelect: (Period) -> Unit,
) {
    var showSheet by rememberSaveable { mutableStateOf(false) }
    val colors = FinTheme.colors

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(colors.surfaceAlt)
            .padding(horizontal = Spacing.x2, vertical = Spacing.x2),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        ArrowButton(
            iconRes = R.drawable.ic_chevron_left,
            contentDescription = stringResource(R.string.summary_prev_period),
            onClick = onPrevious,
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(12.dp))
                .clickable(
                    role = Role.Button,
                    onClickLabel = stringResource(R.string.summary_change_period),
                    onClick = { showSheet = true },
                )
                .padding(horizontal = Spacing.x2, vertical = Spacing.x1),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = stringResource(R.string.summary_period),
                style = MaterialTheme.typography.labelSmall,
                color = colors.textSecondary,
            )
            Text(
                text = DateFormatter.periodTitle(period),
                style = MaterialTheme.typography.titleMedium,
                color = colors.textPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        ArrowButton(
            iconRes = R.drawable.ic_chevron_right,
            contentDescription = stringResource(R.string.summary_next_period),
            onClick = onNext,
        )
    }

    if (showSheet) {
        ModalBottomSheet(onDismissRequest = { showSheet = false }) {
            Column(
                modifier = Modifier.padding(
                    start = Spacing.x4,
                    end = Spacing.x4,
                    bottom = Spacing.x6,
                ),
            ) {
                Text(
                    text = stringResource(R.string.summary_change_period),
                    style = MaterialTheme.typography.titleMedium,
                    color = colors.textPrimary,
                    modifier = Modifier.padding(bottom = Spacing.x3),
                )
                available.forEach { item ->
                    val isCurrent = item == period
                    Text(
                        text = DateFormatter.periodTitle(item),
                        style = MaterialTheme.typography.bodyLarge,
                        color = if (isCurrent) colors.textInverse else colors.textPrimary,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = Spacing.x1)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isCurrent) colors.navBar else Color.Transparent)
                            .clickable {
                                onSelect(item)
                                showSheet = false
                            }
                            .heightIn(min = Spacing.minTouch)
                            .padding(horizontal = Spacing.x3, vertical = Spacing.x3),
                    )
                }
            }
        }
    }
}

@Composable
private fun ArrowButton(
    iconRes: Int,
    contentDescription: String,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .size(Spacing.minTouch)
            .clip(PillShape)
            .clickable(role = Role.Button, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            painter = painterResource(iconRes),
            contentDescription = contentDescription,
            tint = FinTheme.colors.navBar,
            modifier = Modifier.size(20.dp),
        )
    }
}

@Composable
private fun CategoryCard(
    total: CategoryTotal,
    modifier: Modifier = Modifier,
) {
    val colors = FinTheme.colors
    val isIncome = total.type == TransactionType.INCOME
    val accent = if (isIncome) colors.incomeFg else colors.expenseFg
    val percentLabel = stringResource(
        if (isIncome) R.string.summary_percent_of_income else R.string.summary_percent_of_expense,
        total.percent,
    )

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(colors.surfaceAlt)
            .padding(Spacing.x3)
            .clearAndSetSemantics {
                contentDescription = "${total.category.name}, " +
                    MoneyFormatter.spoken(total.amountCents) + ", $percentLabel"
            },
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.x2),
            modifier = Modifier.padding(bottom = Spacing.x2),
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(colors.surface),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    painter = painterResource(CategoryIcons.iconFor(total.category.iconKey)),
                    contentDescription = null,
                    tint = colors.navBar,
                    modifier = Modifier.size(16.dp),
                )
            }
            Text(
                text = total.category.name,
                style = MaterialTheme.typography.bodyMedium,
                color = colors.textPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        Text(
            text = MoneyFormatter.formatSigned(total.amountCents, total.type),
            style = MaterialTheme.typography.titleLarge,
            color = accent,
            modifier = Modifier.padding(bottom = Spacing.x2),
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(PillShape)
                .background(colors.surface),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(total.percent.coerceIn(0, 100) / 100f)
                    .height(6.dp)
                    .clip(PillShape)
                    .background(accent),
            )
        }
        Text(
            text = percentLabel,
            style = MaterialTheme.typography.labelSmall,
            color = colors.textSecondary,
            modifier = Modifier.padding(top = Spacing.x1),
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun DistributionChart(slices: List<DistributionSlice>) {
    val colors = FinTheme.colors
    val palette = listOf(FinChartTint1, FinChartTint2, FinChartTint3)
    val description = slices.joinToString(", ") { "${it.label} ${it.percent}%" }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clearAndSetSemantics {
                contentDescription = description
            },
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(14.dp)
                .clip(PillShape)
                .background(colors.surfaceAlt),
        ) {
            slices.forEachIndexed { index, slice ->
                if (slice.percent <= 0) return@forEachIndexed
                Box(
                    modifier = Modifier
                        .weight(slice.percent.toFloat())
                        // Именно fillMaxHeight: без него сегмент получает нулевую высоту.
                        .fillMaxHeight()
                        .background(if (slice.isOther) FinChartRest else palette[index % palette.size]),
                )
            }
        }
        // FlowRow, а не Row: четыре подписи в одну строку не помещаются даже на 360dp.
        FlowRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = Spacing.x3),
            horizontalArrangement = Arrangement.spacedBy(Spacing.x3),
            verticalArrangement = Arrangement.spacedBy(Spacing.x2),
        ) {
            slices.forEachIndexed { index, slice ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(PillShape)
                            .background(if (slice.isOther) FinChartRest else palette[index % palette.size]),
                    )
                    Text(
                        text = "${slice.label} · ${slice.percent}%",
                        style = MaterialTheme.typography.labelSmall,
                        color = colors.textSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }
    }
}
