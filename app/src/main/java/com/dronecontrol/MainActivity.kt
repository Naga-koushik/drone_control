package com.dronecontrol

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.dronecontrol.navigation.AppNavigation
import com.dronecontrol.ui.theme.DroneControlTheme
import com.dronecontrol.viewmodel.DroneViewModel
import com.dronecontrol.viewmodel.DroneViewModelFactory

/**
 * Entry-point Activity for the DroneControl GCS application.
 * Remains lean and delegates all UI routing and state management to Compose and ViewModel.
 */
class MainActivity : ComponentActivity() {

    private val viewModel: DroneViewModel by viewModels {
        val app = application as DroneControlApplication
        DroneViewModelFactory(app.repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val uiState by viewModel.uiState.collectAsState()
            DroneControlTheme(darkTheme = uiState.isDarkMode) {
                AppNavigation(viewModel = viewModel)
            }
        }
    }
}
