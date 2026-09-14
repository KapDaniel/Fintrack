package com.fintrack.app.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.fintrack.app.ui.components.FinTrackBottomBar
import com.fintrack.app.ui.components.FinTrackNavRail
import com.fintrack.app.ui.components.FinTrackScaffoldDefaults
import com.fintrack.app.ui.screens.add.AddTransactionScreen
import com.fintrack.app.ui.screens.home.HomeScreen
import com.fintrack.app.ui.screens.summary.SummaryScreen
import com.fintrack.app.ui.screens.transactions.TransactionsScreen

/**
 * Сколько места экран должен оставить снизу под плавающую навигацию.
 * На планшетах навигация уезжает в боковой rail, и отступ становится меньше.
 */
val LocalContentBottomPadding = staticCompositionLocalOf { 0.dp }

/** Ширина, начиная с которой нижняя плашка заменяется боковым rail. */
private val RailBreakpoint = 600.dp

@Composable
fun FinTrackApp() {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val topDestination = TopDestination.fromRoute(currentRoute)

    BoxWithConstraints(Modifier.fillMaxSize()) {
        val useRail = maxWidth >= RailBreakpoint
        val showNavigation = topDestination != null
        val bottomPadding: Dp =
            FinTrackScaffoldDefaults.contentBottomPadding(withNavigation = showNavigation && !useRail)

        CompositionLocalProvider(LocalContentBottomPadding provides bottomPadding) {
            Row(Modifier.fillMaxSize()) {
                if (useRail && showNavigation) {
                    FinTrackNavRail(
                        current = topDestination,
                        onSelect = { navController.navigateToTop(it) },
                    )
                }
                Box(Modifier.fillMaxSize()) {
                    FinTrackNavHost(navController)
                    if (!useRail && showNavigation) {
                        FinTrackBottomBar(
                            current = topDestination,
                            onSelect = { navController.navigateToTop(it) },
                            modifier = Modifier.align(Alignment.BottomCenter),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FinTrackNavHost(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Routes.HOME,
        modifier = Modifier.fillMaxSize(),
    ) {
        composable(Routes.HOME) {
            HomeScreen(
                onOpenAllTransactions = { navController.navigate(Routes.TRANSACTIONS) },
                onAddTransaction = { navController.navigateToTop(TopDestination.ADD) },
            )
        }
        composable(Routes.ADD) {
            AddTransactionScreen(
                onSaved = { navController.navigateToTop(TopDestination.HOME) },
            )
        }
        composable(Routes.SUMMARY) {
            SummaryScreen()
        }
        composable(Routes.TRANSACTIONS) {
            TransactionsScreen(onBack = { navController.popBackStack() })
        }
    }
}

/**
 * Переход между вкладками: стек не растёт, состояние вкладки сохраняется,
 * повторное нажатие на активный пункт ничего не дублирует.
 */
private fun NavHostController.navigateToTop(destination: TopDestination) {
    navigate(destination.route) {
        popUpTo(graph.findStartDestination().id) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}
