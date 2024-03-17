package com.example.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.ui.details.DetailsScreen
import com.example.ui.list.ListScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val controller = rememberNavController()
            NavHost(navController = controller, startDestination = "/list") {
                composable("/list") {
                    ListScreen { id ->
                        controller.navigate("/movie/$id")
                    }
                }
                composable("/movie/{id}", arguments = listOf(navArgument("id") {
                    defaultValue = 0
                })) {
                    DetailsScreen(it.arguments?.getInt("id") ?: 0) {
                        controller.popBackStack()
                    }
                }
            }
        }
    }
}