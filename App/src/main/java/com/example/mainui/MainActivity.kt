package com.example.mainui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.material3.TopAppBar
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.ExperimentalMaterial3Api

import androidx.compose.ui.tooling.preview.Preview
import com.example.mainui.ui.theme.MainUITheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApp()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyApp(){
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("FeelScape") }
            )
        },
        content = { padding ->
            Box(modifier = Modifier.padding(padding)) {
                Text("Hello from main frame!")
            }
        }
    )
}

