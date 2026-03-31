package com.example.jangomi.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object TransactionList : Screen("transaction_list")
    data object Statistics : Screen("statistics")
    data object TransactionEdit : Screen("transaction_edit?id={id}") {
        fun createRoute(id: Long? = null) =
            if (id != null) "transaction_edit?id=$id" else "transaction_edit"
    }
}

data class TopLevelDestination(
    val screen: Screen,
    val icon: ImageVector,
    val label: String
)

val topLevelDestinations = listOf(
    TopLevelDestination(Screen.Home, Icons.Default.Home, "홈"),
    TopLevelDestination(Screen.TransactionList, Icons.Default.List, "내역"),
    TopLevelDestination(Screen.Statistics, Icons.Default.BarChart, "통계")
)
