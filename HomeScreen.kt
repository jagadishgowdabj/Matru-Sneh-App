package com.example.matrusneh.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.matrusneh.R
import com.example.matrusneh.ui.theme.PastelLavender
import com.example.matrusneh.ui.theme.PrimaryPink
import com.example.matrusneh.ui.theme.SoftPink
import com.example.matrusneh.ui.theme.White
import com.example.matrusneh.ui.viewmodel.MainViewModel
import com.example.matrusneh.worker.ReminderWorker
import kotlinx.coroutines.delay
import java.util.concurrent.TimeUnit

@Composable
fun HomeScreen(viewModel: MainViewModel) {
    val user by viewModel.user.collectAsState()
    val kicksToday by viewModel.getKicksForToday().collectAsState()
    val context = LocalContext.current
    
    var isVisible by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        delay(100)
        isVisible = true
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        AnimatedVisibility(
            visible = isVisible,
            enter = fadeIn(animationSpec = tween(800))
        ) {
            Column {
                Text(
                    text = stringResource(R.string.home_greeting, user?.name ?: "Amma"),
                    style = MaterialTheme.typography.headlineMedium,
                    color = PrimaryPink
                )
                Text(
                    text = stringResource(R.string.home_pregnancy_week),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        AnimatedVisibility(
            visible = isVisible,
            enter = slideInVertically(initialOffsetY = { 100 }, animationSpec = tween(500, delayMillis = 200)) + fadeIn()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                HomeCard(
                    title = stringResource(R.string.card_kick_count),
                    value = kicksToday.toString(),
                    modifier = Modifier.weight(1f),
                    backgroundColor = SoftPink
                )
                HomeCard(
                    title = stringResource(R.string.card_next_checkup),
                    value = "12 Days", // Mock value
                    modifier = Modifier.weight(1f),
                    backgroundColor = PastelLavender
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        AnimatedVisibility(
            visible = isVisible,
            enter = slideInVertically(initialOffsetY = { 100 }, animationSpec = tween(500, delayMillis = 400)) + fadeIn()
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = White),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = stringResource(R.string.card_health_alerts),
                        style = MaterialTheme.typography.titleMedium,
                        color = PrimaryPink
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Everything looks normal today. Remember to drink 8 glasses of water!",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        AnimatedVisibility(
            visible = isVisible,
            enter = slideInVertically(initialOffsetY = { 100 }, animationSpec = tween(500, delayMillis = 600)) + fadeIn()
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = PrimaryPink.copy(alpha = 0.1f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Text(
                    text = stringResource(R.string.card_motivation),
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(16.dp),
                    color = PrimaryPink
                )
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        AnimatedVisibility(
            visible = isVisible,
            enter = fadeIn(animationSpec = tween(500, delayMillis = 800))
        ) {
            val isRemindersEnabled = user?.remindersEnabled ?: false
            Button(
                onClick = {
                    val newState = !isRemindersEnabled
                    viewModel.setReminderPreference(newState)
                    if (newState) {
                        val workRequest = PeriodicWorkRequestBuilder<ReminderWorker>(1, TimeUnit.DAYS).build()
                        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                            "DailyReminder",
                            ExistingPeriodicWorkPolicy.UPDATE,
                            workRequest
                        )
                    } else {
                        WorkManager.getInstance(context).cancelUniqueWork("DailyReminder")
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isRemindersEnabled) MaterialTheme.colorScheme.secondary else PrimaryPink
                )
            ) {
                Text(if (isRemindersEnabled) stringResource(R.string.btn_disable_reminder) else stringResource(R.string.btn_enable_reminder))
            }
        }
    }
}

@Composable
fun HomeCard(title: String, value: String, modifier: Modifier = Modifier, backgroundColor: androidx.compose.ui.graphics.Color) {
    Card(
        modifier = modifier.height(120.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = title, style = MaterialTheme.typography.titleSmall)
            Text(
                text = value,
                style = MaterialTheme.typography.headlineLarge,
                color = PrimaryPink
            )
        }
    }
}
