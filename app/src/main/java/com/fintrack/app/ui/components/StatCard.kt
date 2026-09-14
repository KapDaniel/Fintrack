package com.fintrack.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.unit.dp
import com.fintrack.app.R
import com.fintrack.app.ui.theme.FinTheme
import com.fintrack.app.ui.theme.PillShape
import com.fintrack.app.ui.theme.Spacing

enum class StatCardKind { INCOME, EXPENSE, BALANCE }

/**
 * Карточка «Доходы» / «Расходы» / «Баланс за период».
 *
 * Баланс — широкая карточка с градиентом и крупной цифрой (26sp), как в макете.
 */
@Composable
fun StatCard(
    label: String,
    value: String,
    spokenValue: String,
    kind: StatCardKind,
    modifier: Modifier = Modifier,
) {
    val colors = FinTheme.colors
    val isBalance = kind == StatCardKind.BALANCE

    val background: Modifier = when (kind) {
        StatCardKind.INCOME -> Modifier.background(colors.incomeBg)
        StatCardKind.EXPENSE -> Modifier.background(colors.expenseBg)
        StatCardKind.BALANCE -> Modifier.background(
            Brush.linearGradient(listOf(colors.balanceCardStart, colors.balanceCardEnd))
        )
    }

    val valueColor = when (kind) {
        StatCardKind.INCOME -> colors.incomeFg
        StatCardKind.EXPENSE -> colors.expenseFg
        StatCardKind.BALANCE -> colors.textPrimary
    }

    val iconRes = when (kind) {
        StatCardKind.INCOME -> R.drawable.ic_arrow_up_right
        StatCardKind.EXPENSE -> R.drawable.ic_arrow_down_right
        StatCardKind.BALANCE -> R.drawable.ic_piggy_bank
    }

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .then(background)
            .padding(if (isBalance) Spacing.x4 else Spacing.x3)
            .clearAndSetSemantics { contentDescription = "$label: $spokenValue" },
        verticalArrangement = Arrangement.spacedBy(Spacing.x2),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = label,
                style = if (isBalance) {
                    MaterialTheme.typography.titleMedium
                } else {
                    MaterialTheme.typography.titleSmall
                },
                color = colors.textPrimary,
                modifier = Modifier.weight(1f, fill = false),
            )
            Box(
                modifier = Modifier
                    .size(if (isBalance) 36.dp else 28.dp)
                    .clip(PillShape)
                    .background(Color.White.copy(alpha = if (isBalance) 0.8f else 0.6f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    painter = painterResource(iconRes),
                    contentDescription = null,
                    tint = valueColor,
                    modifier = Modifier.size(16.dp),
                )
            }
        }
        Text(
            text = value,
            style = if (isBalance) {
                MaterialTheme.typography.headlineMedium
            } else {
                MaterialTheme.typography.titleLarge
            },
            color = valueColor,
        )
    }
}
