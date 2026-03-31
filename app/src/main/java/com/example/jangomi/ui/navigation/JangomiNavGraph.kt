package com.example.jangomi.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.jangomi.ui.home.HomeScreen
import com.example.jangomi.ui.statistics.StatisticsScreen
import com.example.jangomi.ui.transaction.edit.TransactionEditScreen
import com.example.jangomi.ui.transaction.list.TransactionListScreen

@Composable
fun JangomiNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
        modifier = modifier
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                onTransactionClick = { id ->
                    navController.navigate(Screen.TransactionEdit.createRoute(id))
                },
                onAddClick = {
                    navController.navigate(Screen.TransactionEdit.createRoute())
                }
            )
        }

        composable(Screen.TransactionList.route) {
            TransactionListScreen(
                onTransactionClick = { id ->
                    navController.navigate(Screen.TransactionEdit.createRoute(id))
                },
                onAddClick = {
                    navController.navigate(Screen.TransactionEdit.createRoute())
                }
            )
        }

        composable(Screen.Statistics.route) {
            StatisticsScreen()
        }

        composable(
            route = Screen.TransactionEdit.route,
            arguments = listOf(
                navArgument("id") {
                    type = NavType.LongType
                    defaultValue = -1L
                }
            )
        ) { backStack ->
            val id = backStack.arguments?.getLong("id")?.takeIf { it != -1L }
            TransactionEditScreen(
                transactionId = id,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
