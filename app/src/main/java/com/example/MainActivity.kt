package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.db.LocalKartDatabase
import com.example.data.repository.LocalKartRepository
import com.example.ui.navigation.LocalKartApp
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.LocalKartViewModel

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    val database = LocalKartDatabase.getDatabase(applicationContext)
    val repository = LocalKartRepository(database.localKartDao())

    val factory = object : ViewModelProvider.Factory {
      @Suppress("UNCHECKED_CAST")
      override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return LocalKartViewModel(repository) as T
      }
    }

    setContent {
      MyApplicationTheme {
        val viewModel: LocalKartViewModel = viewModel(factory = factory)
        LocalKartApp(viewModel = viewModel)
      }
    }
  }
}

