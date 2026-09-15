package com.fintrack.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.fintrack.app.R
import com.fintrack.app.ui.theme.FinTheme
import com.fintrack.app.ui.theme.PillShape
import com.fintrack.app.ui.theme.Spacing

/**
 * Круглая кнопка-колокольчик в шапке, как в макете.
 *
 * Функциональности за ней пока нет: приложение офлайновое, без backend и push,
 * поэтому по нажатию открывается тот же попап «Новых уведомлений нет», что был
 * нарисован в макете.
 */
@Composable
fun NotificationsButton(modifier: Modifier = Modifier) {
    var expanded by remember { mutableStateOf(false) }
    val colors = FinTheme.colors

    Box(modifier) {
        Box(
            modifier = Modifier
                .size(Spacing.minTouch)
                .clip(PillShape)
                .background(Color.White.copy(alpha = 0.22f))
                .clickable(
                    role = Role.Button,
                    onClickLabel = stringResource(R.string.notifications_open),
                    onClick = { expanded = true },
                ),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_bell),
                contentDescription = stringResource(R.string.notifications_open),
                tint = colors.textInverse,
                modifier = Modifier.size(20.dp),
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            containerColor = colors.surface,
        ) {
            Column(
                modifier = Modifier
                    .width(220.dp)
                    .padding(Spacing.x3),
            ) {
                Text(
                    text = stringResource(R.string.notifications_empty_title),
                    style = MaterialTheme.typography.titleSmall,
                    color = colors.textPrimary,
                )
                Text(
                    text = stringResource(R.string.notifications_empty_subtitle),
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.textSecondary,
                    modifier = Modifier.padding(top = Spacing.x1),
                )
            }
        }
    }
}
