package com.example.mainui

// import android.view.WindowInsets

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
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
import com.example.mainui.ui.theme.MainUITheme
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import androidx.compose.foundation.layout.systemBarsPadding
import com.example.mainui.ui.auth.AuthViewModel
import com.example.mainui.ui.auth.EditProfileScreen
import com.example.mainui.ui.auth.LoginScreen
import com.example.mainui.ui.auth.RegisterScreen
import com.example.mainui.ui.auth.EditProfileScreen

// Reusable theme helpers
val TranquilBlue = Color(0xFF1693B2)          // link / accent
val TranquilSurface = Color(0xFFE7F2F4)       // card background
val TranquilText   = Color(0xFF022328)        // high-contrast text
val Inter = FontFamily(Font(R.font.inter_regular))
val InterBold = FontFamily(Font(R.font.inter_bold))

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

private fun backgroundBrush(darkMode: Boolean): Brush {
    return if (darkMode) {
        Brush.verticalGradient(
            listOf(Color.Black, Color(0xFF121212))
        )
    } else {
        Brush.verticalGradient(
            listOf(Color(0xFF46127A), Color(0xFF166D70))
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
            HorizontalDivider(
                modifier = Modifier
                    .width(48.dp)
                    .height(2.dp),
                thickness = DividerDefaults.Thickness, color = TranquilBlue.copy(alpha = 0.45f)
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
    authVm: AuthViewModel   // <-- NOTE this param
) = ScreenSurface(darkMode) {

    // read the current user from AuthViewModel
    val user by authVm.user.collectAsStateWithLifecycle()

    // fallbacks if user is null (e.g., not logged in yet)
    val displayName = user?.displayName ?: "—"
    val email       = user?.email ?: "—"
    val memberSince = user?.createdAt?.toMonthYear() ?: "—"

    Column(
        verticalArrangement = Arrangement.spacedBy(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("My Profile", fontFamily = InterBold, fontSize = 30.sp, color = Color.White)

        DsCard {
            ProfileRow("Display Name", displayName)
            ProfileRow("Email", email)
            ProfileRow("Member Since", memberSince)

            Spacer(Modifier.height(16.dp))
            Button(
                onClick = {
                    // Ensure previous save flags don't auto-pop the screen
                    authVm.resetSaveState()
                    navController.navigate("editProfile")
                },
                colors = ButtonDefaults.buttonColors(containerColor = TranquilBlue)
            ) {
                Text("Edit Profile", fontFamily = Inter, color = Color.White)
            }
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

// Settings Screen
@Composable
fun SettingsScreen(
    navController: NavHostController,
    darkMode: Boolean,
    onDarkModeChange: (Boolean) -> Unit,
    authVm: AuthViewModel
) = ScreenSurface(darkMode) {
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

            Spacer(Modifier.height(12.dp))

            Button(
                onClick = {
                    authVm.signOut()
                    // Send them to login and clear app stack so back won't return to Home
                    navController.navigate("login") {
                        popUpTo("home") { inclusive = true }
                        launchSingleTop = true
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = TranquilBlue)
            ) {
                Text("Sign out", fontFamily = Inter, color = Color.White)
            }
        }
    }
}

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

// Daily Check-In
@Composable
fun DailyCheckInScreen(
    navController: NavController,
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

    ScreenSurface(darkMode) {
        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Daily Check In", fontFamily = InterBold, fontSize = 30.sp, color = Color.White)
            Text("How are you feeling today?", fontFamily = Inter, fontSize = 18.sp, color = Color.White)

            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
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
                modifier = Modifier.fillMaxWidth().heightIn(min = 120.dp),
                minLines = 4
            )

            Button(
                onClick = {
                    moodVm.addEntry(
                        emotion = selectedEmotion.name,
                        score = emotionScore(selectedEmotion.name),
                        notes = notes
                    )
                    navController.navigate("history") { // go see what we saved
                        popUpTo("home") { inclusive = false }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = TranquilBlue),
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Text("Submit", fontFamily = Inter, color = Color.White)
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

// Journal
@Composable
fun JournalScreen(
    navController: NavHostController,
    darkMode: Boolean,
    journalVm: com.example.mainui.ui.JournalViewModel
) = ScreenSurface(darkMode) {
    var text by rememberSaveable { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Journal", fontFamily = InterBold, fontSize = 30.sp, color = Color.White)

        DsCard {
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                label = { Text("Write your thoughts...") },
                modifier = Modifier.fillMaxWidth().heightIn(min = 180.dp),
                minLines = 8
            )
            Spacer(Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(
                    onClick = {
                        journalVm.addEntry(text)
                        text = ""
                        navController.navigate("journalEntries")
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = TranquilBlue)
                ) { Text("Save Entry", fontFamily = Inter, color = Color.White) }

                OutlinedButton(onClick = { navController.navigate("journalEntries") }) {
                    Text("View Entries", fontFamily = Inter)
                }
            }
        }
    }
}

// --- Journal list screen ---
@Composable
fun JournalEntriesScreen(
    navController: NavHostController,
    darkMode: Boolean,
    journalVm: com.example.mainui.ui.JournalViewModel
) = ScreenSurface(darkMode) {

    val entries by journalVm.entries.collectAsStateWithLifecycle(emptyList())

    Scaffold(
        containerColor = Color.Transparent,
        contentColor = TranquilText,
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Button(
                    onClick = { navController.popBackStack() },
                    colors = ButtonDefaults.buttonColors(containerColor = TranquilBlue)
                ) { Text("Go back", fontFamily = Inter, color = Color.White) }
            }
        }
    ) { inner ->
        Column(
            modifier = Modifier
                .padding(inner)
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Journal Entries", fontFamily = InterBold, fontSize = 30.sp, color = Color.White)

            DsCard {
                if (entries.isEmpty()) {
                    Text("No entries yet", fontFamily = Inter, color = TranquilText)
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 0.dp, max = 520.dp),
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
        }
    }
}

// Mood History
@Composable
fun MoodHistoryScreen(
    navController: NavHostController,
    moodVm: com.example.mainui.ui.MoodViewModel,
    darkMode: Boolean
) = ScreenSurface(darkMode) {

    val entries by moodVm.entries.collectAsStateWithLifecycle()

    Scaffold(
        containerColor = Color.Transparent,
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Button(
                    onClick = { navController.popBackStack() },
                    colors = ButtonDefaults.buttonColors(containerColor = TranquilBlue)
                ) {
                    Text("Go back", fontFamily = Inter, color = Color.White)
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Mood History", fontFamily = InterBold, fontSize = 30.sp, color = Color.White)

            DsCard {
                Text("Trend (last 14)", fontFamily = InterBold, color = TranquilText)
                Spacer(Modifier.height(8.dp))
                MoodTrendChart(
                    scores = entries.take(14).map { it.score }.reversed(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                )
            }

            DsCard {
                Text("Entries", fontFamily = InterBold, color = TranquilText)
                Spacer(Modifier.height(8.dp))

                // Keep this list scrollable inside the card
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 0.dp, max = 420.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(entries) { e -> MoodRow(e) }
                }
            }
        }
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
    androidx.compose.foundation.Canvas(modifier = modifier) {
        val padding = 16.dp.toPx()
        val w = size.width - padding * 2
        val h = size.height - padding * 2

        val minY = 1f
        val maxY = 5f
        val stepX = if (scores.size == 1) 0f else w / (scores.size - 1).coerceAtLeast(1)
        val pts = scores.mapIndexed { idx, s ->
            val x = padding + stepX * idx
            val t = ((s - minY) / (maxY - minY)).coerceIn(0f, 1f)
            val y = padding + (1f - t) * h
            androidx.compose.ui.geometry.Offset(x, y)
        }

        // axis
        drawLine(
            color = TranquilBlue.copy(alpha = 0.6f),
            start = androidx.compose.ui.geometry.Offset(padding, padding + h),
            end = androidx.compose.ui.geometry.Offset(padding + w, padding + h),
            strokeWidth = 2f
        )
        // line
        for (i in 0 until pts.lastIndex) {
            drawLine(
                color = TranquilBlue,
                start = pts[i],
                end = pts[i + 1],
                strokeWidth = 6f
            )
        }
        // dots
        pts.forEach { p ->
            drawCircle(color = Color(0xFF0C6C7A), radius = 8f, center = p, style = Stroke(width = 8f))
            drawCircle(color = Color.White, radius = 4f, center = p)
        }
    }
}

// Advice
@Composable
fun AdviceScreen(navController: NavHostController, darkMode: Boolean) = ScreenSurface(darkMode) {
    Scaffold(
        containerColor = Color.Transparent,
        contentColor = TranquilText,
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Button(
                    onClick = { navController.popBackStack() },
                    colors = ButtonDefaults.buttonColors(containerColor = TranquilBlue)
                ) { Text("Go back", fontFamily = Inter, color = Color.White) }
            }
        }
    ) { inner ->
        LazyColumn(
            modifier = Modifier
                .padding(inner)
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Text("Advice & Coping", fontFamily = InterBold, fontSize = 28.sp, color = Color.White)
            }

            item {
                QuoteBlock()
            }

            item {
                DsCard {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            "Coping Strategies",
                            fontFamily = InterBold,
                            fontSize = 20.sp,
                            color = TranquilText
                        )

                        copingStrategies.forEach {
                            Text("• $it", fontFamily = Inter, fontSize = 16.sp, color = TranquilText)
                        }
                    }
                }
            }
        }
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
                darkMode = darkMode,
                authVm = authVm,
                onLoginSuccess = {
                    navController.navigate("home") {
                        popUpTo("home") { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onGoToRegister = { navController.navigate("register") }
            )
        }
        composable("register") {
            RegisterScreen(
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
                darkMode = darkMode,
                authVm = authVm,
                onDone = { navController.popBackStack() }
            )
        }

        // ---------- App ----------
        composable("home")     { HomeScreen(navController) }
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
        if (!authReady) {
            // SIMPLE GATE: show gradient with a small loading label
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