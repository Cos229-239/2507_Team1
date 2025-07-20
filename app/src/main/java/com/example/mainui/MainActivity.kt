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
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button



import androidx.compose.ui.tooling.preview.Preview
import androidx.core.view.WindowCompat
import androidx.navigation.NavHostController
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
fun MyApp() {
    val navController = rememberNavController()

    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFF3E8FF)
                ),
                modifier = Modifier.shadow(4.dp),
                title = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = {
                                navController.navigate("home") {
                                    popUpTo("home") { inclusive = true }
                                }
                            }) {
                                Image(
                                    painter = painterResource(R.drawable.feelscape_logo),
                                    contentDescription = "FeelScape Logo",
                                    modifier = Modifier
                                        .size(40.dp)
                                        .padding(end = 6.dp)
                                )
                            }

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

                        Row {
                            IconButton(onClick = {
                                navController.navigate("profile")
                            }) {
                                Image(
                                    painter = painterResource(R.drawable.userprofile_icon),
                                    contentDescription = "Profile",
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                            IconButton(onClick = {
                                navController.navigate("settings")
                            }) {
                                Image(
                                    painter = painterResource(R.drawable.settings_icon),
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
                            )
                        )
                    )
                    .padding(padding)
            ) {
                AppNavigation(navController)
            }
        }
    )
}

// Navigation Graph
@Composable
fun AppNavigation(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "home") {
        composable("home") { HomeScreen(navController) }
        composable("profile") { ProfileScreen(navController) }
        composable("settings") { SettingsScreen(navController) }
        composable("checkin"){ DailyCheckInScreen(navController) }
    }
}

// Home Screen
@Composable
fun HomeScreen(navController: NavHostController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 30.dp, top = 40.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = "Your safe space for emotional wellness",
            color = Color.White,
            fontSize = 22.sp,
            lineHeight = 28.sp,
            fontFamily = FontFamily(Font(R.font.inter_regular))
        )

        DailyCheckInButton { navController.navigate("checkin") }
    }
}

// Profile Screen
@Composable
fun ProfileScreen(navController: NavHostController) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Profile Screen", color = Color.White)
    }
}

// Settings Screen
@Composable
fun SettingsScreen(navController: NavHostController) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Settings Screen", color = Color.White)
    }
}

@Composable
fun DailyCheckInScreen(navController: NavHostController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.linearGradient(
                    listOf(Color(0xFF46127A), Color(0xFF166D70))
                )
            )
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Daily Check-In",
            color = Color.White,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(24.dp))
        Text(
            text = "How are you feeling today?",
            color = Color.White,
            fontSize = 18.sp
        )
        Spacer(Modifier.height(32.dp))
        Button(onClick = { navController.popBackStack() }) {
            Text("Go back")
        }
    }
}

@Composable
fun DailyCheckInButton(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .background(Color(0xFFE9F4F6), shape = RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .shadow(2.dp, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(R.drawable.smiley_icon),
                contentDescription = "Smile Icon",
                modifier = Modifier
                    .size(32.dp)
                    .padding(end = 12.dp)
            )

            Text(
                text = "Daily Check-In",
                color = Color(0xFF166D70),
                fontSize = 18.sp,
                fontFamily = FontFamily(Font(R.font.inter_regular))
            )
        }
    }
}


