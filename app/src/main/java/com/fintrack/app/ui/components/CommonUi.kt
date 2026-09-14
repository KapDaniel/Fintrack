package com.fintrack.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.fintrack.app.R
import com.fintrack.app.ui.theme.FinTheme
import com.fintrack.app.ui.theme.PillShape
import com.fintrack.app.ui.theme.Spacing

/** Заголовок секции с необязательной кнопкой-ссылкой справа. */
@Composable
fun SectionHeader(
    title: String,
    modifier: Modifier = Modifier,
    actionLabel: String? = null,
    onActionClick: (() -> Unit)? = null,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = Spacing.x3),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = FinTheme.colors.textPrimary,
            modifier = Modifier.weight(1f, fill = false),
        )
        if (actionLabel != null && onActionClick != null) {
            LinkButton(label = actionLabel, onClick = onActionClick)
        }
    }
}

/** Кнопка-ссылка «Все операции ›» на светло-синей подложке. */
@Composable
fun LinkButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = FinTheme.colors
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(colors.surfaceAlt)
            .clickable(role = Role.Button, onClick = onClick)
            .sizeIn(minHeight = Spacing.minTouch)
            .padding(horizontal = Spacing.x3),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.x1),
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = colors.navBar,
        )
        Icon(
            painter = painterResource(R.drawable.ic_chevron_right),
            contentDescription = null,
            tint = colors.navBar,
            modifier = Modifier.size(16.dp),
        )
    }
}

/** Основная синяя кнопка во всю ширину. */
@Composable
fun PrimaryButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = Spacing.minTouch),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = FinTheme.colors.navBar,
            contentColor = FinTheme.colors.textInverse,
        ),
    ) {
        Text(text = label, style = MaterialTheme.typography.labelLarge)
    }
}

/**
 * Заглушка для пустого списка. Показывается вместо операций, категорий и
 * диаграммы, когда данных ещё нет.
 */
@Composable
fun EmptyState(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    actionLabel: String? = null,
    onActionClick: (() -> Unit)? = null,
) {
    val colors = FinTheme.colors
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = Spacing.x5),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Spacing.x2),
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(PillShape)
                .background(colors.surfaceAlt),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_wallet),
                contentDescription = null,
                tint = colors.navBar,
                modifier = Modifier.size(28.dp),
            )
        }
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = colors.textPrimary,
            textAlign = TextAlign.Center,
        )
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodySmall,
            color = colors.textSecondary,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = Spacing.x4),
        )
        if (actionLabel != null && onActionClick != null) {
            PrimaryButton(
                label = actionLabel,
                onClick = onActionClick,
                modifier = Modifier.padding(top = Spacing.x2),
            )
        }
    }
}
