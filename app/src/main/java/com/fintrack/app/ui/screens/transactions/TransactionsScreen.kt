package com.fintrack.app.ui.screens.transactions

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fintrack.app.R
import com.fintrack.app.core.format.DateFormatter
import com.fintrack.app.di.AppViewModelFactory
import com.fintrack.app.ui.components.EmptyState
import com.fintrack.app.ui.components.FinTrackScaffold
import com.fintrack.app.ui.navigation.LocalContentBottomPadding
import com.fintrack.app.ui.components.TransactionRow
import com.fintrack.app.ui.theme.FinTheme
import com.fintrack.app.ui.theme.PillShape
import com.fintrack.app.ui.theme.Spacing

@Composable
fun TransactionsScreen(
    onBack: () -> Unit,
    viewModel: TransactionsViewModel = viewModel(factory = AppViewModelFactory),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val colors = FinTheme.colors

    FinTrackScaffold(
        header = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Spacing.x3),
            ) {
                Box(
                    modifier = Modifier
                        .size(Spacing.minTouch)
                        .clip(PillShape)
                        .background(Color.White.copy(alpha = 0.22f))
                        .clickable(
                            role = Role.Button,
                            onClickLabel = stringResource(R.string.action_back),
                            onClick = onBack,
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_chevron_left),
                        contentDescription = stringResource(R.string.action_back),
                        tint = colors.textInverse,
                        modifier = Modifier.size(20.dp),
                    )
                }
                Text(
                    text = stringResource(R.string.transactions_title),
                    style = MaterialTheme.typography.titleLarge,
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
            if (state.isEmpty) {
                item {
                    EmptyState(
                        title = stringResource(R.string.home_empty_title),
                        subtitle = stringResource(R.string.home_empty_subtitle),
                    )
                }
            }
            state.groups.forEach { group ->
                item(key = "header-${group.dayStartMillis}") {
                    Text(
                        text = dayTitle(group.dayStartMillis),
                        style = MaterialTheme.typography.titleSmall,
                        color = colors.textSecondary,
                        modifier = Modifier.padding(top = Spacing.x4, bottom = Spacing.x2),
                    )
                }
                items(group.transactions, key = { it.id }) { transaction ->
                    TransactionRow(
                        transaction = transaction,
                        showDivider = transaction != group.transactions.last(),
                    )
                }
            }
        }
    }
}

@Composable
private fun dayTitle(dayStartMillis: Long): String = when {
    DateFormatter.isToday(dayStartMillis) -> stringResource(R.string.transactions_today)
    DateFormatter.isYesterday(dayStartMillis) -> stringResource(R.string.transactions_yesterday)
    else -> DateFormatter.fullDate(dayStartMillis)
}
