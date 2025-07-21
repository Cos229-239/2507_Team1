package com.example.mainui

import android.os.Bundle
//import android.view.WindowInsets
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
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
import androidx.compose.material3.Card
import androidx.compose.ui.draw.clip

import androidx.compose.ui.tooling.preview.Preview
import androidx.core.view.WindowCompat
import androidx.navigation.NavHostController
import com.example.mainui.ui.theme.MainUITheme

// Reusable theme helpers
private val TranquilBlue = Color(0xFF1693B2)          // link / accent
private val TranquilSurface = Color(0xFFE7F2F4)       // card background
private val TranquilText   = Color(0xFF022328)        // high-contrast text
private val Inter = FontFamily(Font(R.font.inter_regular))
private val InterBold = FontFamily(Font(R.font.inter_bold))

@Composable
private fun ScreenSurface(content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF46127A), Color(0xFF166D70))
                )
            )
            .systemBarsPadding()
            .padding(horizontal = 24.dp, vertical = 32.dp),
        contentAlignment = Alignment.TopCenter
    ) { content() }
}

@Composable
private fun DsCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) = Card(
    colors = CardDefaults.cardColors(containerColor = TranquilSurface),
    shape = RoundedCornerShape(16.dp),
    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    modifier = modifier.fillMaxWidth()
) {
    Column(Modifier.padding(24.dp)) { content() }
}

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
        composable("journal")   { JournalScreen(navController) }
        composable("history")   { MoodHistoryScreen(navController) }
        composable("advice")    { AdviceScreen(navController) }

        composable("profile")   { ProfileScreen(navController) }
        composable("settings")  { SettingsScreen(navController) }
    }
}

// Home Screen
@Composable
fun HomeScreen(navController: NavHostController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 40.dp, start = 30.dp, end = 30.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // welcome text
        Text(
            text = "Your safe space for emotional wellness",
            color = Color.White,
            fontSize = 22.sp,
            lineHeight = 28.sp,
            fontFamily = Inter
        )

        // Daily Check-In
        HomeButton(
            icon = R.drawable.smiley_icon,
            label = "Daily Check-In",
            onClick = { navController.navigate("checkin") }
        )

        // *****************************************************
        // ** Justin, I need your help with adding the icons. **
        // *****************************************************

        // Journal
        HomeButton(
            icon = R.drawable.journal_icon,
            label = "Journal",
            onClick = { navController.navigate("journal") }
        )

        // Mood History
        HomeButton(
            icon = R.drawable.history_icon,
            label = "Mood History",
            onClick = { navController.navigate("history") }
        )

        // Advice
        HomeButton(
            icon = R.drawable.advice_icon,
            label = "Advice",
            onClick = { navController.navigate("advice") }
        )
    }
}

// Re-usable Home button
@Composable
private fun HomeButton(
    icon: Int? = null,
    label: String,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = TranquilSurface),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // icon only if the drawable exists
            icon?.let {
                Image(
                    painter = painterResource(it),
                    contentDescription = label,
                    modifier = Modifier
                        .size(32.dp)
                        .padding(end = 12.dp)
                )
            }
            Text(
                text = label,
                fontSize = 18.sp,
                fontFamily = Inter,
                color = TranquilText
            )
        }
    }
}
// ------------------------
// Home Screen Buttons
// ------------------------

// Daily Check-In button
@Composable
fun DailyCheckInScreen(navController: NavHostController) =
    ScreenSurface {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                "Daily Check-In",
                fontFamily = InterBold,
                fontSize = 30.sp,
                color = Color.White
            )
            Spacer(Modifier.height(24.dp))
            Text(
                "How are you feeling today?",
                fontFamily = Inter,
                fontSize = 18.sp,
                color = Color.White
            )
            Spacer(Modifier.height(32.dp))
            Button(
                onClick = { navController.popBackStack() },
                colors = ButtonDefaults.buttonColors(containerColor = TranquilBlue)
            ) {
                Text("Go back", fontFamily = Inter, color = Color.White)
            }
        }
    }

// Journal
@Composable
fun JournalScreen(navController: NavHostController) = ScreenSurface {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Journal Screen", fontFamily = InterBold, fontSize = 28.sp, color = Color.White)
        Spacer(Modifier.height(24.dp))
        Button(
            onClick = { navController.popBackStack() },
            colors = ButtonDefaults.buttonColors(containerColor = TranquilBlue),
            modifier = Modifier.padding(top = 24.dp)
        ) {
            Text("Go back", fontFamily = Inter, color = Color.White)
        }
    }
}

// Mood History
@Composable
fun MoodHistoryScreen(navController: NavHostController) = ScreenSurface {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Mood History Screen", fontFamily = InterBold, fontSize = 28.sp, color = Color.White)
        Spacer(Modifier.height(24.dp))
        Button(
            onClick = { navController.popBackStack() },
            colors = ButtonDefaults.buttonColors(containerColor = TranquilBlue),
            modifier = Modifier.padding(top = 24.dp)
        ) {
            Text("Go back", fontFamily = Inter, color = Color.White)
        }
    }
}

// Advice
@Composable
fun AdviceScreen(navController: NavHostController) = ScreenSurface {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Advice Screen", fontFamily = InterBold, fontSize = 28.sp, color = Color.White)
        Spacer(Modifier.height(24.dp))
        Button(
            onClick = { navController.popBackStack() },
            colors = ButtonDefaults.buttonColors(containerColor = TranquilBlue),
            modifier = Modifier.padding(top = 24.dp)
        ) {
            Text("Go back", fontFamily = Inter, color = Color.White)
        }
    }
}

// Profile Screen
@Composable
fun ProfileScreen(navController: NavHostController) = ScreenSurface {
    Column(
        verticalArrangement = Arrangement.spacedBy(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "My Profile",
            fontFamily = InterBold,
            fontSize = 30.sp,
            color = Color.White
        )

        DsCard {
            ProfileRow("Display Name", "Alex Doe")
            ProfileRow("Email", "alex@feelscape.app")
            ProfileRow("Member Since", "July 2025")
            Spacer(Modifier.height(16.dp))
            Button(
                onClick = { /* TODO edit screen */ },
                colors = ButtonDefaults.buttonColors(containerColor = TranquilBlue)
            ) {
                Text("Edit Profile", fontFamily = Inter, color = Color.White)
            }
        }
    }
}

@Composable
private fun ProfileRow(label: String, value: String) {
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontFamily = Inter, color = TranquilText)
        Text(value, fontFamily = InterBold, color = TranquilText)
    }
}

// Settings Screen
@Composable
fun SettingsScreen(navController: NavHostController) = ScreenSurface {
    Column(
        verticalArrangement = Arrangement.spacedBy(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Settings",
            fontFamily = InterBold,
            fontSize = 30.sp,
            color = Color.White
        )

        DsCard {
            SettingsSwitch("Dark Mode", false)
            SettingsSwitch("Push Notifications", true)
            SettingsSwitch("Weekly Reports", true)
            HorizontalDivider(
                modifier = Modifier.padding(vertical = 12.dp),
                thickness = 1.dp,
                color = TranquilBlue
            )
            Text(
                "About FeelScape v1.0",
                fontFamily = Inter,
                fontSize = 14.sp,
                color = TranquilText
            )
        }
    }
}

@Composable
private fun SettingsSwitch(label: String, enabled: Boolean) {
    var checked by remember { mutableStateOf(enabled) }
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, fontFamily = Inter, color = TranquilText)
        Switch(
            checked = checked,
            onCheckedChange = { checked = it },
            colors = SwitchDefaults.colors(
                checkedThumbColor = TranquilBlue,
                checkedTrackColor = TranquilBlue.copy(alpha = .5f)
            )
        )
    }
}