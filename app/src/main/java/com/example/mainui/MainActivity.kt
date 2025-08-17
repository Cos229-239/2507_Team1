package com.example.mainui

import android.os.Bundle
//import android.view.WindowInsets
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.runtime.saveable.rememberSaveable
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
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

import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.border

import androidx.compose.foundation.layout.heightIn
import androidx.compose.ui.unit.Dp

// Reusable theme helpers
private val TranquilBlue = Color(0xFF1693B2)          // link / accent
private val TranquilSurface = Color(0xFFE7F2F4)       // card background
private val TranquilText   = Color(0xFF022328)        // high-contrast text
private val Inter = FontFamily(Font(R.font.inter_regular))
private val InterBold = FontFamily(Font(R.font.inter_bold))

data class Emotion(val iconResId: Int, val name: String)

val emotions = listOf(
    Emotion(R.drawable.happy_icon, "Happy"),
    Emotion(R.drawable.content_icon, "Content"),
    Emotion(R.drawable.neutral_icon, "Neutral"),
    Emotion(R.drawable.sad_icon, "Sad"),
    Emotion(R.drawable.angry_icon, "Angry")
)

data class Quote(val text: String)

private val quotes = listOf(
    "Your mental health is just as important as your physical health.",
    "It's okay not to be okay",
    "You are worthy of happiness and peace of mind",
    "You are not alone in your struggles",
    "You are stronger than you realize",
    "Small steps can lead to big progress in mental health",
    "You are not a burden for seeking help for your mental health",
    "The experience I have had is that once you start talking about [experiencing a mental health struggle], you realize that actually you’re part of quite a big club - Prince Harry",
    "There is hope, even when your brain tells you there isn’t - John Green",
    "Tough times never last, but tough people do! - Robert Schuller",
    "Not until we are lost do we begin to understand ourselves - Henry David"
)

private fun randomQuote(): String = quotes.random()

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
    val appContext = LocalContext.current.applicationContext
    val navController = rememberNavController()

val darkModeFlow = remember(appContext) {
    ThemeSettings.darkModeFlow(appContext).distinctUntilChanged()
}
val darkMode by darkModeFlow.collectAsStateWithLifecycle(initialValue = false)

val scope = rememberCoroutineScope()
val setDarkMode: (Boolean) -> Unit = { enabled ->
    scope.launch { ThemeSettings.setDarkMode(appContext, enabled) }
}

MainUITheme(darkTheme = darkMode) {
    Scaffold(
        contentWindowInsets = WindowInsets.systemBars,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFF3E8FF)),
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
                                    modifier = Modifier.size(40.dp).padding(end = 6.dp)
                                )
                            }
                            Text(
                                buildAnnotatedString {
                                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) { append("Feel") }
                                    append("Scape")
                                },
                                fontSize = 28.sp,
                                color = Color(0xFF166D70)
                            )
                        }
                        Row {
                            IconButton(onClick = { navController.navigate("profile") }) {
                                Image(
                                    painter = painterResource(R.drawable.userprofile_icon),
                                    contentDescription = "Profile",
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                            IconButton(onClick = { navController.navigate("settings") }) {
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
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFF46127A), Color(0xFF166D70))
                    )
                )
                .padding(padding)
        ) {
            AppNavigation(
                navController = navController,
                darkMode = darkMode,
                onDarkModeChange = setDarkMode
            )
        }
    }
}


// Navigation Graph
@Composable
fun AppNavigation(
    navController: NavHostController,
    darkMode: Boolean,
    onDarkModeChange: (Boolean) -> Unit
) {
    NavHost(navController = navController, startDestination = "home") {
        composable("home") { HomeScreen(navController) }
        composable("profile") { ProfileScreen(navController) }
        composable("settings") { SettingsScreen(navController, darkMode, onDarkModeChange) }
        composable("checkin"){ DailyCheckInScreen(navController) }
        composable("journal")   { JournalScreen(navController) }
        composable("history")   { MoodHistoryScreen(navController) }
        composable("advice")    { AdviceScreen(navController) }
}

// Home Screen
@Composable
fun HomeScreen(navController: NavHostController) {
    var quote by rememberSaveable { mutableStateOf(randomQuote()) }
    var firstResumeHandled by remember { mutableStateOf(false) }

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                if (firstResumeHandled) {
                    quote = randomQuote()
                } else {
                    firstResumeHandled = true
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Text(
                text = "Your safe space for emotional wellness",
                color = Color.White,
                fontSize = 22.sp,
                lineHeight = 28.sp,
                fontFamily = Inter
            )
        }

        item {
            HomeButton(
                icon = R.drawable.smiley_icon,
                label = "Daily Check-In",
                onClick = { navController.navigate("checkin") }
            )
        }

        item {
            HomeButton(
                icon = R.drawable.journal_icon,
                label = "Journal",
                onClick = { navController.navigate("journal") }
            )
        }

        item {
            HomeButton(
                icon = R.drawable.history_icon,
                label = "Mood History",
                onClick = { navController.navigate("history") }
            )
        }

        item {
            HomeButton(
                icon = R.drawable.advice_icon,
                label = "Advice",
                onClick = { navController.navigate("advice") }
            )
        }

        item {
            QuoteCard(
                quote = quote,
                onRefresh = { quote = randomQuote() }
            )
        }
    }
}    


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
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            icon?.let {
                Image(
                    painter = painterResource(it),
                    contentDescription = label,
                    modifier = Modifier.size(28.dp)
                )
            }
            Spacer(Modifier.width(12.dp))
            Text(text = label, fontSize = 18.sp, fontFamily = Inter, color = TranquilText)
        }
    }
}

@Composable
private fun QuoteCard(
    quote: String,
    onRefresh: () -> Unit
) {
    val shape = RoundedCornerShape(16.dp)

    Card(
        colors = CardDefaults.cardColors(containerColor = TranquilSurface),
        shape = shape,
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(
                BorderStroke(1.dp, SolidColor(TranquilBlue.copy(alpha = 0.25f))),
                shape
            )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Animation between-quotes
            Crossfade(targetState = quote, label = "quoteCrossfade") { q ->
                Text(
                    text = "“$q”",
                    color = TranquilText,
                    fontFamily = Inter,
                    fontSize = 16.sp,
                    lineHeight = 22.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(Modifier.height(10.dp))

            // Thin accent divider
            Divider(
                modifier = Modifier
                    .width(48.dp)
                    .height(2.dp),
                color = TranquilBlue.copy(alpha = 0.45f)
            )

            Spacer(Modifier.height(8.dp))


            TextButton(onClick = onRefresh) {
                Text(
                    "New quote",
                    fontFamily = InterBold,
                    color = TranquilBlue
                )
            }
        }
    }
}

@Composable
private fun QuoteBlock(modifier: Modifier = Modifier) {
    var quote by rememberSaveable { mutableStateOf(randomQuote()) }
    var firstResumeHandled by remember { mutableStateOf(false) }

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                if (firstResumeHandled) quote = randomQuote() else firstResumeHandled = true
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    QuoteCard(
        quote = quote,
        onRefresh = { quote = randomQuote() }
    )
}

// ------------------------
// Home Screen Buttons
// ------------------------

// Daily Check-In button

@Composable
fun EmotionBox(
    emotion: Emotion,
    isSelected: Boolean,
    onClick: () -> Unit,
    size: Dp = 64.dp
) {
    val borderColor = if (isSelected) TranquilBlue else Color.Transparent

    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected)
                TranquilSurface.copy(alpha = 0.95f) else TranquilSurface
        ),
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .size(size)
            .border(2.dp, borderColor, RoundedCornerShape(14.dp))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .clickable(onClick = onClick)
                .padding(6.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(emotion.iconResId),
                contentDescription = emotion.name,
                modifier = Modifier.size(size * 0.45f) // icon scales with box
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = emotion.name,
                fontSize = 11.sp,
                fontFamily = Inter,
                color = TranquilText,
                maxLines = 1
            )
        }
    }
}

@Composable
fun DailyCheckInScreen(navController: NavController) {
    var selectedEmotion by remember { mutableStateOf(emotions.first()) }
    var notes by rememberSaveable { mutableStateOf("") }

    ScreenSurface {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Daily Check In",
                fontFamily = InterBold,
                fontSize = 30.sp,
                color = Color.White
            )

            Text(
                "How are you feeling today?",
                fontFamily = Inter,
                fontSize = 18.sp,
                color = Color.White
            )

            // One row, five buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                val boxSize = 64.dp
                emotions.forEach { emotion ->
                    EmotionBox(
                        emotion = emotion,
                        isSelected = selectedEmotion == emotion,
                        onClick = { selectedEmotion = emotion },
                        size = boxSize
                    )
                }
            }

            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Elaborate on how you are feeling...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 120.dp),
                minLines = 4
            )

            Button(
                onClick = { navController.popBackStack() },
                colors = ButtonDefaults.buttonColors(containerColor = TranquilBlue),
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Text("Submit", fontFamily = Inter, color = Color.White)
            }
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

        Spacer(modifier = Modifier.weight(1f))

        QuoteBlock()
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

        Spacer(modifier = Modifier.weight(1f))

        QuoteBlock()
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

        Spacer(modifier = Modifier.weight(1f))

        QuoteBlock()
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
fun SettingsScreen(navController: NavHostController, darkMode: Boolean, onDarkModeChange: (Boolean) -> Unit) = ScreenSurface {
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
            SettingsSwitch(
                label = "Dark Mode",
                checked = darkMode,
                onCheckedChange = onDarkModeChange
            )
            SettingsSwitch("Push Notifications", true, onCheckedChange = {}) // Needs to be finished!
            SettingsSwitch("Weekly Reports", true, onCheckedChange = {}) // Needs to be finished!
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
private fun SettingsSwitch(label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    //var checked by remember { mutableStateOf(enabled) }
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, fontFamily = Inter, color = TranquilText)
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = TranquilBlue,
                checkedTrackColor = TranquilBlue.copy(alpha = .5f)
            )
        )
    }
}