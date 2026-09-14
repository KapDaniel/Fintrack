package com.fintrack.app.ui.theme

import androidx.compose.ui.unit.dp

/**
 * Шкала отступов макета: 4 / 8 / 16 / 24 / 32 / 40 / 48 / 64.
 */
object Spacing {
    val x1 = 4.dp
    val x2 = 8.dp
    val x3 = 16.dp
    val x4 = 24.dp
    val x5 = 32.dp
    val x6 = 40.dp
    val x7 = 48.dp
    val x8 = 64.dp

    /** Минимальный тач-таргет Android (в макете было 44px — подняли до нормы). */
    val minTouch = 48.dp

    /** Горизонтальные поля контента. */
    val screenPadding = x4

    /** Максимальная ширина колонки контента на планшетах. */
    val contentMaxWidth = 600.dp

    /** Насколько лист контента наезжает на оранжевую шапку. */
    val sheetOverlap = 20.dp
}
