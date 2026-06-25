package com.wizeline.bazvalidation.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.wizeline.bazvalidation.ui.detail.UserDetailScreen
import com.wizeline.bazvalidation.ui.list.UserListScreen

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = "users"
    ) {
        composable("users") {
            UserListScreen(
                onUserClick = { userId ->
                    navController.navigate("users/$userId")
                }
            )
        }
        composable(
            route = "users/{userId}",
            arguments = listOf(
                navArgument("userId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getInt("userId") ?: return@composable
            UserDetailScreen(
                userId = userId,
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}
