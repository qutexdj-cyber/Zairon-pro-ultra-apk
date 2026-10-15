package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.ui.ZaironApp
import com.example.ui.ZaironViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: ZaironViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ZaironApp(viewModel = viewModel)
        }
    }
}
