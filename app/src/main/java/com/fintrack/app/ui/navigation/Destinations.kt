package com.fintrack.app.ui.navigation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.fintrack.app.R

object Routes {
    const val HOME = "home"
    const val ADD = "add"
    const val SUMMARY = "summary"
    const val TRANSACTIONS = "transactions"
}

/**
 * Три пункта нижней навигации. Экран «Все операции» сюда не входит:
 * он открывается с «Главной» и закрывается кнопкой «Назад».
 */
enum class TopDestination(
    val route: String,
    @StringRes val labelRes: Int,
    @DrawableRes val iconRes: Int,
) {
    HOME(Routes.HOME, R.string.nav_home, R.drawable.ic_home),
    ADD(Routes.ADD, R.string.nav_add, R.drawable.ic_add_circle),
    SUMMARY(Routes.SUMMARY, R.string.nav_summary, R.drawable.ic_bar_chart),
    ;

    companion object {
        fun fromRoute(route: String?): TopDestination? = entries.firstOrNull { it.route == route }
    }
}
