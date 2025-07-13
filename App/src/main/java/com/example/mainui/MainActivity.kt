package com.example.mainui

import android.os.Bundle
//import android.view.WindowInsets
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.material3.TopAppBar
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.sp
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.ui.Alignment
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.ui.draw.shadow


import androidx.compose.ui.tooling.preview.Preview
import androidx.core.view.WindowCompat
import com.example.mainui.ui.theme.MainUITheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            MyApp()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyApp(navController: NavController = rememberNavController()) {
    Scaffold(
        topBar = {
            TopAppBar(
                modifier = Modifier.shadow(elevation = 4.dp),
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFF3E8FF),
                    titleContentColor = Color(0xFF166D70)
                ),
                title = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Logo + FeelScape text
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Image(
                                painter = painterResource(id = R.drawable.feelscape_logo),
                                contentDescription = "FeelScape Logo",
                                modifier = Modifier
                                    .size(40.dp)
                                    .padding(end = 6.dp)
                            )

                            Text(
                                buildAnnotatedString {
                                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                        append("Feel")
                                    }
                                    append("Scape")
                                },
                                fontSize = 28.sp,
                                color = Color(0xFF166D70)
                            )
                        }

                        // Profile and Settings icons
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = {
                                navController.navigate("profile")
                            }) {
                                Image(
                                    painter = painterResource(id = R.drawable.userprofile_icon),
                                    contentDescription = "Profile",
                                    modifier = Modifier.size(32.dp)
                                )
                            }

                            IconButton(onClick = {
                                navController.navigate("settings")
                            }) {
                                Image(
                                    painter = painterResource(id = R.drawable.settings_icon),
                                    contentDescription = "Settings",
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                        }
                    }
                }
            )
        },
        content = { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                Color(0xFF46127A),
                                Color(0xFF166D70)
                            ),
                            start = Offset(0f, 0f),
                            end = Offset.Infinite
                        )
                    )
                    .padding(padding)
            ) {
                Text(
                    text = "Main Frame Content",
                    color = Color.White,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    )
}

