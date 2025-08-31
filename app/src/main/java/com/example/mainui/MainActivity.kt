package com.example.mainui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.mainui.data.entities.MoodEntry
import com.example.mainui.ui.MoodViewModel
import com.example.mainui.ui.auth.AuthViewModel
import com.example.mainui.ui.auth.EditProfileScreen
import com.example.mainui.ui.auth.LoginScreen
import com.example.mainui.ui.auth.RegisterScreen
import com.example.mainui.ui.theme.AccentPurple
import com.example.mainui.ui.theme.Inter
import com.example.mainui.ui.theme.InterBold
import com.example.mainui.ui.theme.LocalAppDarkMode
import com.example.mainui.ui.theme.MainUITheme
import com.example.mainui.ui.theme.NeonAccent
import com.example.mainui.ui.theme.PanelStroke
import com.example.mainui.ui.theme.PanelSurface
import com.example.mainui.ui.theme.TranquilBlue
import com.example.mainui.ui.theme.TranquilSurface
import com.example.mainui.ui.theme.TranquilText
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

val TitleTeal = Color(0xFF20B2AA)
@Composable
fun FrostedCard(
    modifier: Modifier = Modifier,
    shape: androidx.compose.ui.graphics.Shape = androidx.compose.foundation.shape.RoundedCornerShape(18.dp),
    content: @Composable androidx.compose.foundation.layout.ColumnScope.() -> Unit
) {
    androidx.compose.material3.Card(
        shape = shape,
        colors = androidx.compose.material3.CardDefaults.cardColors(containerColor = PanelSurface),
        elevation = androidx.compose.material3.CardDefaults.cardElevation(
            defaultElevation = 0.dp, pressedElevation = 0.dp, focusedElevation = 0.dp,
            hoveredElevation = 0.dp, draggedElevation = 0.dp, disabledElevation = 0.dp
        ),
        border = androidx.compose.foundation.BorderStroke(1.dp, PanelStroke),
        modifier = modifier
    ) {
        androidx.compose.foundation.layout.Column(Modifier.padding(20.dp), content = content)
    }
}

@Composable
fun NeonButton(
    onClick: () -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    val shape = RoundedCornerShape(20.dp)
    val brush = NeonAccent()
    val isDark = LocalAppDarkMode.current
    val textColor = if (isDark) Color(0xFFE0F7FA) else Color.White

    Box(
        modifier = modifier
            .height(52.dp)
            .shadow(10.dp, shape = shape, clip = false)
            .clip(shape)
            .background(brush)
            .border(1.dp, Color.White.copy(alpha = 0.15f), shape)
            .then(if (enabled) Modifier.clickable(onClick = onClick) else Modifier),
        contentAlignment = Alignment.Center
    ) {
        Text(text = label, fontFamily = InterBold, fontSize = 18.sp, color = textColor)
    }
}

@Composable
fun BackBar(navController: NavController, label: String = "Back") {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        NeonButton(
            onClick = { navController.popBackStack() },
            label = label,
            modifier = Modifier.fillMaxWidth(0.6f)
        )
    }
}

@Composable
fun AppHeader(
    navController: NavHostController,
    title: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Left: Logo -> Home
        IconButton(
            onClick = {
                navController.navigate("home") {
                    popUpTo("home") { inclusive = true }
                    launchSingleTop = true
                }
            },
            modifier = Modifier.size(96.dp)
        ) {
            Image(
                painter = painterResource(R.drawable.feelscape_logo),
                contentDescription = "FeelScape Logo",
                modifier = Modifier.fillMaxSize()
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
            fontFamily = Inter,
            color = TitleTeal
        )

        Row {
            IconButton(onClick = { navController.navigate("profile") }) {
                Image(
                    painter = painterResource(R.drawable.userprofile_icon),
                    contentDescription = "Profile",
                    modifier = Modifier.size(30.dp)
                )
            }
            IconButton(onClick = { navController.navigate("settings") }) {
                Image(
                    painter = painterResource(R.drawable.settings_icon),
                    contentDescription = "Settings",
                    modifier = Modifier.size(30.dp)
                )
            }
        }
    }
}

@Composable
fun ScreenWithHeader(
    navController: NavController,
    darkMode: Boolean,
    screenLabel: String? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    ScreenSurface(darkMode) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // ----- Top header -----
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(72.dp)
                    .padding(horizontal = 4.dp)
            ) {
                // Left: Logo (Home)
                Row(
                    modifier = Modifier.align(Alignment.CenterStart),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = {
                            navController.navigate("home") {
                                popUpTo("home") { inclusive = true }
                                launchSingleTop = true
                            }
                        },
                        modifier = Modifier.size(105.dp)
                    ) {
                        Image(
                            painter = painterResource(R.drawable.feelscape_logo),
                            contentDescription = "FeelScape Logo",
                            modifier = Modifier
                                .size(44.dp)
                                .padding(2.dp),
                            contentScale = ContentScale.Fit
                        )
                    }
                }

                // Center: FeelScape
                Text(
                    buildAnnotatedString {
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) { append("Feel") }
                        append("Scape")
                    },
                    fontSize = 28.sp,
                    fontFamily = Inter,
                    color = TitleTeal,
                    modifier = Modifier.align(Alignment.Center)
                )

                // Right: Profile + Settings
                Row(
                    modifier = Modifier.align(Alignment.CenterEnd),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { navController.navigate("profile") },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Image(
                            painter = painterResource(R.drawable.userprofile_icon),
                            contentDescription = "Profile",
                            modifier = Modifier
                                .size(30.dp)
                                .padding(2.dp),
                            contentScale = ContentScale.Fit
                        )
                    }
                    IconButton(
                        onClick = { navController.navigate("settings") },
                        modifier = Modifier.size(25.dp)
                    ) {
                        Image(
                            painter = painterResource(R.drawable.settings_icon),
                            contentDescription = "Settings",
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }

            // Divider under header
            HorizontalDivider(thickness = 1.dp, color = TranquilBlue.copy(alpha = 0.15f))

            // Optional screen label (inside Column → use CenterHorizontally)
            if (!screenLabel.isNullOrBlank()) {
                Text(
                    text = screenLabel,
                    fontFamily = InterBold,
                    fontSize = 22.sp,
                    color = Color.White,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }

            // Content area (direct child of Column → weight is valid)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
                content = content
            )
        }
    }
}

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

val copingStrategies = listOf(
    "Take deep, mindful breaths",
    "Go for a short walk or do light exercise",
    "Write down your thoughts in a journal",
    "Listen to calming music or nature sounds",
    "Talk to a trusted friend or family member",
    "Drink a glass of water",
    "Practice a grounding exercise (like 5-4-3-2-1 technique)",
    "Do a quick body scan meditation",
    "Give yourself permission to rest",
    "Limit social media use for a while",
    "Do something creative (draw, write, play music)",
    "Remind yourself: this feeling is temporary"
)


private fun randomQuote(): String = quotes.random()

// Background gradient
private fun backgroundBrush(darkMode: Boolean): Brush {
    return if (darkMode) {
        Brush.verticalGradient(
            colors = listOf(
                Color(0xFF2A2139), // muted purple-gray
                Color(0xFF20353A)  // muted teal-navy
            )
        )
    } else {
        Brush.verticalGradient(
            colors = listOf(Color(0xFF46127A), Color(0xFF166D70))
        )
    }
}

// Date helper
private fun Long.toMonthYear(): String {
    val fmt = java.text.SimpleDateFormat("MMMM yyyy", java.util.Locale.getDefault())
    return fmt.format(java.util.Date(this))
}

@Composable
fun ScreenSurface(
    darkMode: Boolean,
    content: @Composable () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundBrush(darkMode))
            .statusBarsPadding()
            .padding(horizontal = 24.dp, vertical = 12.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        content()
    }
}


@Composable
private fun DsCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) = androidx.compose.material3.Card(
    colors = androidx.compose.material3.CardDefaults.cardColors(containerColor = PanelSurface),
    shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
    elevation = androidx.compose.material3.CardDefaults.cardElevation(
        defaultElevation = 0.dp, pressedElevation = 0.dp, focusedElevation = 0.dp,
        hoveredElevation = 0.dp, draggedElevation = 0.dp, disabledElevation = 0.dp
    ),
    border = androidx.compose.foundation.BorderStroke(1.dp, PanelStroke),
    modifier = modifier.fillMaxWidth()
) {
    androidx.compose.foundation.layout.Column(Modifier.padding(24.dp)) { content() }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            MyApp()
            val moodViewModel: MoodViewModel = viewModel()
        }
    }
}

@Composable
private fun HomeButton(
    icon: Int? = null,
    label: String,
    onClick: () -> Unit
) {
    androidx.compose.material3.Card(
        onClick = onClick,
        colors = androidx.compose.material3.CardDefaults.cardColors(containerColor = PanelSurface),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
        elevation = androidx.compose.material3.CardDefaults.cardElevation(
            defaultElevation = 0.dp, pressedElevation = 0.dp, focusedElevation = 0.dp,
            hoveredElevation = 0.dp, draggedElevation = 0.dp, disabledElevation = 0.dp
        ),
        border = androidx.compose.foundation.BorderStroke(1.dp, PanelStroke),
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
                    modifier = Modifier.size(32.dp)
                )
            }
            Spacer(Modifier.width(12.dp))
            Text(text = label, fontSize = 22.sp, fontFamily = InterBold, color = TranquilSurface)
        }
    }
}

@Composable
private fun QuoteCard(
    quote: String,
    onRefresh: () -> Unit
) {
    val shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp)

    androidx.compose.material3.Card(
        colors = androidx.compose.material3.CardDefaults.cardColors(containerColor = PanelSurface),
        shape = shape,
        elevation = androidx.compose.material3.CardDefaults.cardElevation(
            defaultElevation = 0.dp, pressedElevation = 0.dp, focusedElevation = 0.dp,
            hoveredElevation = 0.dp, draggedElevation = 0.dp, disabledElevation = 0.dp
        ),
        border = androidx.compose.foundation.BorderStroke(1.dp, PanelStroke),
        modifier = Modifier.fillMaxWidth()
    ) {
        androidx.compose.foundation.layout.Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            androidx.compose.animation.Crossfade(targetState = quote, label = "quoteCrossfade") { q ->
                androidx.compose.material3.Text(
                    text = "“$q”",
                    color = Color.White,
                    fontFamily = Inter,
                    fontSize = 16.sp,
                    lineHeight = 22.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(Modifier.height(10.dp))
            androidx.compose.material3.HorizontalDivider(
                modifier = Modifier
                    .width(48.dp)
                    .height(2.dp),
                thickness = DividerDefaults.Thickness,
                color = AccentPurple.copy(alpha = 0.6f)
            )
            Spacer(Modifier.height(8.dp))

            androidx.compose.material3.TextButton(onClick = onRefresh) {
                androidx.compose.material3.Text(
                    "New quote",
                    fontFamily = InterBold,
                    color = AccentPurple
                )
            }
        }
    }
}

@Composable
fun HomeScreen(navController: NavHostController, darkMode: Boolean) {
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

    ScreenWithHeader(
        navController = navController,
        darkMode = darkMode,
    ) {
        Text(
            text = "Your safe space for emotional wellness",
            color = Color.White,
            fontSize = 15.sp,
            lineHeight = 28.sp,
            fontFamily = InterBold
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
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

// Profile Screen
@Composable
fun ProfileScreen(
    navController: NavHostController,
    darkMode: Boolean,
    authVm: AuthViewModel
) = ScreenWithHeader(
    navController = navController,
    darkMode = darkMode,
    screenLabel = "My Profile"
) {
    val user by authVm.user.collectAsStateWithLifecycle()
    val displayName = user?.displayName ?: "—"
    val email       = user?.email ?: "—"
    val memberSince = user?.createdAt?.toMonthYear() ?: "—"

    DsCard {
        ProfileRow("Display Name", displayName)
        ProfileRow("Email", email)
        ProfileRow("Member Since", memberSince)
        Spacer(Modifier.height(16.dp))
        NeonButton(
            onClick = {
                authVm.resetSaveState()
                navController.navigate("editProfile")
            },
            label = "Edit Profile",
            modifier = Modifier.fillMaxWidth(0.6f)
        )
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

// Settings Screen
@Composable
fun SettingsScreen(
    navController: NavHostController,
    darkMode: Boolean,
    onDarkModeChange: (Boolean) -> Unit,
    authVm: AuthViewModel
) = ScreenWithHeader(
    navController = navController,
    darkMode = darkMode,
    screenLabel = "Settings"
) {
    DsCard {
        SettingsSwitch("Dark Mode", darkMode, onDarkModeChange)
        SettingsSwitch("Push Notifications", true, onCheckedChange = {})
        SettingsSwitch("Weekly Reports", true, onCheckedChange = {})
        HorizontalDivider(
            modifier = Modifier.padding(vertical = 12.dp),
            thickness = 1.dp,
            color = TranquilBlue
        )
        Text("About FeelScape v1.0", fontFamily = Inter, fontSize = 14.sp, color = TranquilText)
        Spacer(Modifier.height(12.dp))
        NeonButton(
            onClick = {
                authVm.signOut()
                navController.navigate("login") {
                    popUpTo("home") { inclusive = true }
                    launchSingleTop = true
                }
            },
            label = "Sign Out",
            modifier = Modifier.fillMaxWidth(0.6f)
        )
    }
}

@Composable
fun EmotionButton(
    emotion: Emotion,
    isSelected: Boolean,
    onClick: () -> Unit,
    size: Dp = 72.dp
) {
    val shape = RoundedCornerShape(16.dp)
    val brush = NeonAccent()
    val textColor = if (LocalAppDarkMode.current) Color(0xFFE0F7FA) else Color.White
    val borderColor = if (isSelected) TranquilBlue else Color.White.copy(alpha = 0.15f)

    Box(
        modifier = Modifier
            .size(size)
            .shadow(6.dp, shape = shape, clip = false)
            .clip(shape)
            .background(brush)
            .border(2.dp, borderColor, shape)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(emotion.iconResId),
                contentDescription = emotion.name,
                modifier = Modifier.size(size * 0.6f)
            )

            // Wrap text in a Box that reserves space + offsets upward
            Box(
                modifier = Modifier.height(20.dp)
            ) {
                Text(
                    text = emotion.name,
                    fontSize = 11.sp,
                    fontFamily = Inter,
                    color = TranquilText,
                    maxLines = 1,
                    modifier = Modifier.align(Alignment.TopCenter)
                )
            }
        }
    }
}

// Daily Check-In
@Composable
fun DailyCheckInScreen(
    navController: NavHostController,
    moodVm: com.example.mainui.ui.MoodViewModel,
    darkMode: Boolean
) {
    var selectedEmotion by remember { mutableStateOf(emotions.first()) }
    var notes by rememberSaveable { mutableStateOf("") }

    fun emotionScore(name: String) = when (name) {
        "Angry" -> 1
        "Sad" -> 2
        "Neutral" -> 3
        "Content" -> 4
        "Happy" -> 5
        else -> 3
    }

    ScreenWithHeader(
        navController = navController,
        darkMode = darkMode,
        screenLabel = "Daily Check-In"
    ) {
        // subtitle under the header
        Text(
            "How are you feeling today?",
            fontFamily = Inter,
            fontSize = 18.sp,
            color = Color.White
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            val boxSize = 64.dp
            emotions.forEach { emotion ->
                EmotionButton(
                    emotion = emotion,
                    isSelected = selectedEmotion == emotion,
                    onClick = { selectedEmotion = emotion },
                    size = 60.dp
                )
            }
        }

        OutlinedTextField(
            value = notes,
            onValueChange = { notes = it },
            label = { Text("Elaborate on how you're feeling...", fontStyle = FontStyle.Italic) },
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 120.dp),
            minLines = 4,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = TranquilBlue,
                unfocusedBorderColor = PanelStroke,
                focusedTextColor = Color(0xFFB3E2E8),
                unfocusedTextColor = Color(0xFFE5F6F8),
                focusedLabelColor = TranquilBlue,
                unfocusedLabelColor = TranquilBlue,
                cursorColor = TranquilBlue
            )
        )

        NeonButton(
            onClick = {
                moodVm.addEntry(
                    emotion = selectedEmotion.name,
                    score = emotionScore(selectedEmotion.name),
                    notes = notes
                )
                navController.navigate("history") { popUpTo("home") { inclusive = false } }
            },
            label = "Submit",
            modifier = Modifier.fillMaxWidth(0.6f)
        )
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

// Journal
@Composable
fun JournalScreen(
    navController: NavHostController,
    darkMode: Boolean,
    journalVm: com.example.mainui.ui.JournalViewModel
) {
    var text by rememberSaveable { mutableStateOf("") }

    ScreenWithHeader(
        navController = navController,
        darkMode = darkMode,
        screenLabel = "Journal"
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                label = { Text("Write your thoughts...", fontStyle = FontStyle.Italic) },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 180.dp),
                minLines = 8,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = TranquilBlue,
                    unfocusedBorderColor = PanelStroke,
                    focusedTextColor = Color(0xFFB3E2E8),
                    unfocusedTextColor = Color(0xFFB3E2E8),
                    focusedLabelColor = TranquilBlue,
                    unfocusedLabelColor = TranquilBlue,
                    cursorColor = TranquilBlue
                )
            )

            NeonButton(
                onClick = {
                    journalVm.addEntry(text)
                    text = ""
                    navController.navigate("journalEntries")
                },
                label = "Save Entry",
                modifier = Modifier.fillMaxWidth(0.6f)
            )

            Spacer(Modifier.height(12.dp))

            NeonButton(
                onClick = { navController.navigate("journalEntries") },
                label = "View Entries",
                modifier = Modifier.fillMaxWidth(0.6f)
            )
        }
    }
}

// --- Journal list screen ---
@Composable
fun JournalEntriesScreen(
    navController: NavHostController,
    darkMode: Boolean,
    journalVm: com.example.mainui.ui.JournalViewModel
) {
    val entries by journalVm.entries.collectAsStateWithLifecycle(emptyList())

    ScreenWithHeader(
        navController = navController,
        darkMode = darkMode,
        screenLabel = "Journal Entries"
    ) {
        FrostedCard(modifier = Modifier.weight(1f)) {
            if (entries.isEmpty()) {
                Text("No entries yet", fontFamily = Inter, color = TranquilText)
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(entries) { e ->
                        val date = java.text.SimpleDateFormat(
                            "MMM d, yyyy h:mm a",
                            java.util.Locale.getDefault()
                        ).format(java.util.Date(e.timestamp))

                        Column {
                            Text(date, fontFamily = InterBold, color = TranquilText)
                            Spacer(Modifier.height(4.dp))
                            Text(e.text, fontFamily = Inter, color = TranquilText)
                            HorizontalDivider(Modifier.padding(top = 8.dp))
                        }
                    }
                }
            }
        }

        BackBar(navController, label = "Back")
    }
}

// Mood History
@Composable
fun MoodHistoryScreen(
    navController: NavHostController,
    moodVm: com.example.mainui.ui.MoodViewModel,
    darkMode: Boolean
) {
    val entries by moodVm.entries.collectAsStateWithLifecycle()

    ScreenWithHeader(
        navController = navController,
        darkMode = darkMode,
        screenLabel = "Mood History"
    ) {
        // Trend card
        FrostedCard {
            Text("Trend (last 14)", fontFamily = InterBold, color = TranquilText)
            Spacer(Modifier.height(8.dp))
            MoodTrendChart(
                scores = entries.take(14).map { it.score }.reversed(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
            )
        }

        // Entries list takes the remaining space
        FrostedCard(modifier = Modifier.weight(1f)) {
            Text("Entries", fontFamily = InterBold, color = TranquilText)
            Spacer(Modifier.height(8.dp))
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(entries) { e -> MoodRow(e) }
            }
        }

        BackBar(navController, label = "Back")
    }
}

@Composable
private fun MoodRow(entry: MoodEntry) {
    val date = java.text.SimpleDateFormat("MMM d, yyyy h:mm a")
        .format(java.util.Date(entry.timestamp))
    Column(Modifier.fillMaxWidth()) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("$date • ${entry.emotion}", fontFamily = InterBold, color = TranquilText)
        }
        if (entry.notes.isNotBlank()) {
            Text(entry.notes, fontFamily = Inter, color = TranquilText, fontSize = 14.sp)
        }
        HorizontalDivider(
            Modifier.padding(top = 8.dp),
            DividerDefaults.Thickness,
            DividerDefaults.color
        )
    }
}

@Composable
private fun MoodTrendChart(scores: List<Int>, modifier: Modifier = Modifier) {
    if (scores.isEmpty()) {
        Text("No data yet", fontFamily = Inter, color = TranquilText)
        return
    }

    // Capture composable colors before entering the draw scope
    val accent = TranquilBlue
    val axisColor = accent.copy(alpha = 0.6f)
    val dotRing = if (isSystemInDarkTheme()) Color(0xFF0B555F) else Color(0xFF0C6C7A)
    val dotFill = Color.White

    Canvas(modifier = modifier) {
        val padding = 16.dp.toPx()
        val w = size.width - padding * 2
        val h = size.height - padding * 2

        val minY = 1f
        val maxY = 5f
        val stepX = if (scores.size <= 1) 0f else w / (scores.size - 1).coerceAtLeast(1)

        val pts = scores.mapIndexed { idx, s ->
            val x = padding + stepX * idx
            val t = ((s - minY) / (maxY - minY)).coerceIn(0f, 1f)
            val y = padding + (1f - t) * h
            Offset(x, y)
        }

        // axis
        drawLine(
            color = axisColor,
            start = Offset(padding, padding + h),
            end = Offset(padding + w, padding + h),
            strokeWidth = 2f
        )

        // line
        for (i in 0 until pts.lastIndex) {
            drawLine(
                color = accent,
                start = pts[i],
                end = pts[i + 1],
                strokeWidth = 6f
            )
        }

        // dots
        pts.forEach { p ->
            drawCircle(color = dotRing, radius = 8f, center = p, style = Stroke(width = 8f))
            drawCircle(color = dotFill, radius = 4f, center = p)
        }
    }
}

// Advice
@Composable
fun AdviceScreen(navController: NavHostController, darkMode: Boolean) {
    ScreenWithHeader(
        navController = navController,
        darkMode = darkMode,
        screenLabel = "Advice"
    ) {
        // Scrollable card takes the available space
        FrostedCard(modifier = Modifier.weight(1f)) {
            Text("Coping Strategies", fontFamily = InterBold, fontSize = 20.sp, color = TranquilText)
            Spacer(Modifier.height(8.dp))
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(copingStrategies) { tip ->
                    Text("• $tip", fontFamily = Inter, fontSize = 16.sp, color = TranquilText)
                }
            }
        }

        // Fixed quote and back button at the bottom
        QuoteBlock()
        BackBar(navController, label = "Back")
    }
}


@Composable
fun AppNavigation(
    navController: NavHostController,
    darkMode: Boolean,
    onDarkModeChange: (Boolean) -> Unit,
    moodVm: com.example.mainui.ui.MoodViewModel,
    journalVm: com.example.mainui.ui.JournalViewModel,
    authVm: AuthViewModel,
    currentUser: Any?
) {
    val startDest = if (currentUser == null) "login" else "home"

    NavHost(navController = navController, startDestination = startDest) {

        // ---------- Auth ----------
        composable("login") {
            LoginScreen(
                navController = navController,
                darkMode = darkMode,
                authVm = authVm
            )
        }

        composable("register") {
            RegisterScreen(
                navController = navController,
                darkMode = darkMode,
                authVm = authVm,
                onRegisterSuccess = {
                    navController.navigate("home") {
                        popUpTo("home") { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }
        composable("editProfile") {
            EditProfileScreen(
                navController = navController,
                darkMode = darkMode,
                authVm = authVm,
                onDone = { navController.popBackStack() }
            )
        }

        // ---------- App ----------
        composable("home")     { HomeScreen(navController, darkMode) }
        composable("profile")  { ProfileScreen(navController, darkMode, authVm) }
        composable("settings") { SettingsScreen(navController, darkMode, onDarkModeChange, authVm = authVm) }
        composable("checkin")  { DailyCheckInScreen(navController, moodVm, darkMode) }
        composable("journal")  { JournalScreen(navController, darkMode, journalVm) }
        composable("history")  { MoodHistoryScreen(navController, moodVm, darkMode) }
        composable("advice")   { AdviceScreen(navController, darkMode) }
        composable("journalEntries") {
            JournalEntriesScreen(navController, darkMode, journalVm)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyApp() {
    val appContext = LocalContext.current.applicationContext
    val navController = rememberNavController()

    // ViewModels first
    val moodVm: MoodViewModel = viewModel()
    val journalVm: com.example.mainui.ui.JournalViewModel = viewModel()
    val authVm: AuthViewModel = viewModel()

    // Observe auth state (must come before any logic that uses it)
    val currentUser by authVm.user.collectAsStateWithLifecycle(initialValue = null)
    val authReady by authVm.authReady.collectAsStateWithLifecycle(false)

    // Dark mode
    val darkModeFlow = remember(appContext) {
        ThemeSettings.darkModeFlow(appContext).distinctUntilChanged()
    }
    val darkMode by darkModeFlow.collectAsStateWithLifecycle(initialValue = false)

    val scope = rememberCoroutineScope()
    val setDarkMode: (Boolean) -> Unit = { enabled ->
        scope.launch { ThemeSettings.setDarkMode(appContext, enabled) }
    }

    MainUITheme(darkTheme = darkMode) {
        androidx.compose.runtime.CompositionLocalProvider(
            LocalAppDarkMode provides darkMode
        ) {
            if (!authReady) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(backgroundBrush(darkMode))
                        .systemBarsPadding(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Loading…", color = Color.White, fontFamily = Inter)
                }
            } else {
                Scaffold(
                    contentWindowInsets = WindowInsets.systemBars
                ) { padding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(backgroundBrush(darkMode))
                            .padding(padding)
                    ) {
                        AppNavigation(
                            navController = navController,
                            darkMode = darkMode,
                            onDarkModeChange = setDarkMode,
                            moodVm = moodVm,
                            journalVm = journalVm,
                            authVm = authVm,
                            currentUser = currentUser
                        )
                    }
                }
            }
        }
    }
}