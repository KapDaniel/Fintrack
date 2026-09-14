package com.fintrack.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.fintrack.app.ui.navigation.TopDestination
import com.fintrack.app.ui.theme.FinTheme
import com.fintrack.app.ui.theme.PillShape
import com.fintrack.app.ui.theme.Spacing

private val ActiveItemBackground = Color.White.copy(alpha = 0.14f)
private val ActiveIconBackground = Color.White.copy(alpha = 0.26f)
private val InactiveContent = Color.White.copy(alpha = 0.88f)

/**
 * Плавающая нижняя навигация из макета: синяя плашка с отступами 16dp,
 * радиусом 24dp и подсветкой активного пункта.
 *
 * [navigationBarsPadding] обязателен — без него плашка уезжает под жестовую
 * панель системы.
 */
@Composable
fun FinTrackBottomBar(
    current: TopDestination?,
    onSelect: (TopDestination) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = Spacing.x3, vertical = Spacing.x3),
        contentAlignment = Alignment.Center,
    ) {
        Surface(
            modifier = Modifier.widthIn(max = Spacing.contentMaxWidth),
            shape = RoundedCornerShape(24.dp),
            color = FinTheme.colors.navBar,
            shadowElevation = 8.dp,
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Spacing.x2),
                horizontalArrangement = Arrangement.spacedBy(Spacing.x1),
            ) {
                TopDestination.entries.forEach { destination ->
                    NavItem(
                        destination = destination,
                        selected = destination == current,
                        onClick = { onSelect(destination) },
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
    }
}

/**
 * Вертикальный вариант навигации для планшетов и ландшафта: на широком экране
 * нижняя плашка во всю ширину выглядит чужеродно.
 */
@Composable
fun FinTrackNavRail(
    current: TopDestination?,
    onSelect: (TopDestination) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .navigationBarsPadding()
            .padding(Spacing.x3),
        contentAlignment = Alignment.Center,
    ) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = FinTheme.colors.navBar,
            shadowElevation = 8.dp,
        ) {
            Column(
                modifier = Modifier
                    .width(88.dp)
                    .padding(Spacing.x2),
                verticalArrangement = Arrangement.spacedBy(Spacing.x1),
            ) {
                TopDestination.entries.forEach { destination ->
                    NavItem(
                        destination = destination,
                        selected = destination == current,
                        onClick = { onSelect(destination) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(64.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun NavItem(
    destination: TopDestination,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val label = stringResource(destination.labelRes)
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(if (selected) ActiveItemBackground else Color.Transparent)
            .clickable(role = Role.Tab, onClick = onClick)
            .heightIn(min = Spacing.minTouch)
            .padding(vertical = Spacing.x1)
            // TalkBack читает одну осмысленную фразу вместо иконки и подписи по отдельности.
            .clearAndSetSemantics {
                contentDescription = label
                role = Role.Tab
                this.selected = selected
            },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Box(
            modifier = Modifier
                .size(width = 32.dp, height = 24.dp)
                .clip(PillShape)
                .background(if (selected) ActiveIconBackground else Color.Transparent),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = painterResource(destination.iconRes),
                contentDescription = null,
                tint = if (selected) Color.White else InactiveContent,
                modifier = Modifier.size(18.dp),
            )
        }
        Text(
            text = label,
            color = if (selected) Color.White else InactiveContent,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = if (selected) FontWeight.ExtraBold else FontWeight.SemiBold,
            textAlign = TextAlign.Center,
        )
    }
}
