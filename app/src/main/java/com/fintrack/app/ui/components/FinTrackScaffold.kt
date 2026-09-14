package com.fintrack.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.fintrack.app.ui.theme.FinTheme
import com.fintrack.app.ui.theme.SheetShape
import com.fintrack.app.ui.theme.Spacing

/**
 * Общая оболочка экрана: оранжевая шапка, белый «лист» контента со скруглением
 * 32dp, наезжающий на шапку, и место под плавающую навигацию.
 *
 * Фон корневого Box — цвет листа: лист сдвинут вверх на [Spacing.sheetOverlap],
 * и снизу остаётся полоска той же высоты, которая должна быть незаметной.
 */
@Composable
fun FinTrackScaffold(
    header: @Composable ColumnScope.() -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit,
) {
    val colors = FinTheme.colors
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colors.surface),
    ) {
        WaveHeader(content = header)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .offset(y = -Spacing.sheetOverlap),
        ) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                shape = SheetShape,
                color = colors.surface,
            ) {
                Box(Modifier.fillMaxSize()) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .widthIn(max = Spacing.contentMaxWidth)
                            .align(Alignment.TopCenter),
                        content = content,
                    )
                }
            }
        }
    }
}

object FinTrackScaffoldDefaults {

    /** Высота плавающей навигации вместе с её внешними отступами. */
    val navigationBarHeight: Dp = 104.dp

    /**
     * Нижний отступ контента, чтобы последний элемент списка не уезжал под
     * плавающую навигацию и системную жестовую панель.
     */
    @Composable
    fun contentBottomPadding(withNavigation: Boolean = true): Dp {
        val systemInset = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
        return if (withNavigation) navigationBarHeight + systemInset else systemInset + Spacing.x4
    }
}
