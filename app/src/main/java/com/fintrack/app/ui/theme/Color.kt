package com.fintrack.app.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

/*
 * Палитра перенесена один в один из CSS-переменных макета FinTrack
 * (:root в Main.dc.html / Add.dc.html / Summary.dc.html).
 */

val FinBg = Color(0xFFEEF1F8)
val FinSurface = Color(0xFFFFFFFF)
val FinSurfaceAlt = Color(0xFFEAF2FF)
val FinSurfaceAlt2 = Color(0xFFF4F8FF)
val FinBorder = Color(0xFFDCE4F2)

val FinTextPrimary = Color(0xFF161B26)
val FinTextSecondary = Color(0xFF5B6472)
val FinTextInverse = Color(0xFFFFFFFF)

val FinOrange500 = Color(0xFFE8650F)
val FinOrange600 = Color(0xFFD9550D)

val FinNav500 = Color(0xFF3D6BFA)
val FinNav600 = Color(0xFF2E5CF6)
val FinNav700 = Color(0xFF274CD6)

val FinIncomeBg = Color(0xFFDDF6EA)
val FinIncomeFg = Color(0xFF0A7A54)
val FinExpenseBg = Color(0xFFFDE6E4)
val FinExpenseFg = Color(0xFFC13527)

val FinDanger = Color(0xFFD33A2F)

val FinBalanceCardStart = Color(0xFFDCE6FF)
val FinBalanceCardEnd = Color(0xFFC9D9FF)

/** Оттенки для диаграммы распределения расходов (3 категории + «Другое»). */
val FinChartTint1 = FinExpenseFg
val FinChartTint2 = Color(0xFFF0847A)
val FinChartTint3 = Color(0xFFF6ADA6)
val FinChartRest = FinBorder

/** Тёмная схема: те же роли, приглушённые подложки. */
val FinDarkBg = Color(0xFF11151F)
val FinDarkSurface = Color(0xFF181D29)
val FinDarkSurfaceAlt = Color(0xFF212838)
val FinDarkSurfaceAlt2 = Color(0xFF1C2231)
val FinDarkBorder = Color(0xFF2E3648)
val FinDarkTextPrimary = Color(0xFFF2F5FA)
val FinDarkTextSecondary = Color(0xFFA5AEBF)
val FinDarkIncomeBg = Color(0xFF103A2C)
val FinDarkIncomeFg = Color(0xFF5FD9A8)
val FinDarkExpenseBg = Color(0xFF3A211E)
val FinDarkExpenseFg = Color(0xFFF08C80)

/**
 * Семантические цвета, которых нет в Material 3: доход, расход, вспомогательные
 * поверхности и градиенты. Раздаются через [LocalFinColors].
 */
@Immutable
data class FinColors(
    val screenBackground: Color,
    val surface: Color,
    val surfaceAlt: Color,
    val surfaceAlt2: Color,
    val border: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val textInverse: Color,
    val headerStart: Color,
    val headerEnd: Color,
    val navBar: Color,
    val incomeBg: Color,
    val incomeFg: Color,
    val expenseBg: Color,
    val expenseFg: Color,
    val danger: Color,
    val balanceCardStart: Color,
    val balanceCardEnd: Color,
)

val LightFinColors = FinColors(
    screenBackground = FinBg,
    surface = FinSurface,
    surfaceAlt = FinSurfaceAlt,
    surfaceAlt2 = FinSurfaceAlt2,
    border = FinBorder,
    textPrimary = FinTextPrimary,
    textSecondary = FinTextSecondary,
    textInverse = FinTextInverse,
    headerStart = FinOrange500,
    headerEnd = FinOrange600,
    navBar = FinNav600,
    incomeBg = FinIncomeBg,
    incomeFg = FinIncomeFg,
    expenseBg = FinExpenseBg,
    expenseFg = FinExpenseFg,
    danger = FinDanger,
    balanceCardStart = FinBalanceCardStart,
    balanceCardEnd = FinBalanceCardEnd,
)

val DarkFinColors = FinColors(
    screenBackground = FinDarkBg,
    surface = FinDarkSurface,
    surfaceAlt = FinDarkSurfaceAlt,
    surfaceAlt2 = FinDarkSurfaceAlt2,
    border = FinDarkBorder,
    textPrimary = FinDarkTextPrimary,
    textSecondary = FinDarkTextSecondary,
    textInverse = FinTextInverse,
    headerStart = FinOrange500,
    headerEnd = FinOrange600,
    navBar = FinNav600,
    incomeBg = FinDarkIncomeBg,
    incomeFg = FinDarkIncomeFg,
    expenseBg = FinDarkExpenseBg,
    expenseFg = FinDarkExpenseFg,
    danger = FinDarkExpenseFg,
    balanceCardStart = Color(0xFF25324F),
    balanceCardEnd = Color(0xFF1D2740),
)
