package com.fintrack.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.fintrack.app.R
import com.fintrack.app.core.format.DateFormatter
import com.fintrack.app.core.format.MoneyFormatter
import com.fintrack.app.domain.model.Transaction
import com.fintrack.app.domain.model.TransactionType
import com.fintrack.app.ui.theme.FinTheme
import com.fintrack.app.ui.theme.PillShape
import com.fintrack.app.ui.theme.Spacing

/**
 * Строка операции: иконка категории с бейджем типа, название, дата и сумма.
 *
 * Вся строка озвучивается одной фразой — иначе TalkBack читает пять обрывков,
 * а типографский минус в сумме произносится непредсказуемо.
 */
@Composable
fun TransactionRow(
    transaction: Transaction,
    modifier: Modifier = Modifier,
    showDivider: Boolean = true,
) {
    val colors = FinTheme.colors
    val isIncome = transaction.type == TransactionType.INCOME
    val accent = if (isIncome) colors.incomeFg else colors.expenseFg

    val spokenTemplate = if (isIncome) R.string.cd_income_amount else R.string.cd_expense_amount
    val spokenAmount = stringResource(spokenTemplate, MoneyFormatter.spoken(transaction.amountCents))
    val spokenRow = "${transaction.title}, $spokenAmount, ${DateFormatter.spoken(transaction.dateTimeMillis)}"

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = Spacing.minTouch)
                .padding(vertical = Spacing.x2)
                .clearAndSetSemantics { contentDescription = spokenRow },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.x3),
        ) {
            CategoryBadge(
                iconKey = transaction.category.iconKey,
                isIncome = isIncome,
                accent = accent,
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = transaction.title,
                    style = MaterialTheme.typography.bodyLarge,
                    color = colors.textPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = DateFormatter.listDateTime(transaction.dateTimeMillis),
                    style = MaterialTheme.typography.labelSmall,
                    color = colors.textSecondary,
                    modifier = Modifier.padding(top = 2.dp),
                )
            }
            Text(
                text = MoneyFormatter.formatSigned(transaction.amountCents, transaction.type),
                style = MaterialTheme.typography.bodyLarge,
                color = accent,
                maxLines = 1,
            )
        }
        if (showDivider) {
            HorizontalDivider(thickness = 1.dp, color = colors.border)
        }
    }
}

@Composable
private fun CategoryBadge(
    iconKey: String,
    isIncome: Boolean,
    accent: Color,
    modifier: Modifier = Modifier,
) {
    val colors = FinTheme.colors
    Box(modifier = modifier.size(40.dp)) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(colors.surfaceAlt),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = painterResource(CategoryIcons.iconFor(iconKey)),
                contentDescription = null,
                tint = colors.navBar,
                modifier = Modifier.size(20.dp),
            )
        }
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(x = 4.dp, y = 4.dp)
                .size(20.dp)
                .clip(PillShape)
                .background(accent)
                .border(2.dp, colors.surface, PillShape),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = painterResource(
                    if (isIncome) R.drawable.ic_arrow_up_right else R.drawable.ic_arrow_down_right
                ),
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(12.dp),
            )
        }
    }
}
