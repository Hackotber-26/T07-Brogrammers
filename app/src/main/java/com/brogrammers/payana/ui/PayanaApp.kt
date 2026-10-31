package com.brogrammers.payana.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.Modifier
import com.brogrammers.payana.ai.MatchExplainer
import com.brogrammers.payana.data.MatchPreferences
import com.brogrammers.payana.data.RideMatch
import com.brogrammers.payana.data.RideRequest
import com.brogrammers.payana.data.ScreenState
import com.brogrammers.payana.matching.MatchingEngine
import com.brogrammers.payana.ui.screens.ErrorScreen
import com.brogrammers.payana.ui.screens.HomeScreen
import com.brogrammers.payana.ui.screens.LoadingScreen
import com.brogrammers.payana.ui.screens.MatchDetailScreen
import com.brogrammers.payana.ui.screens.NoMatchScreen
import com.brogrammers.payana.ui.screens.OnboardingScreen
import com.brogrammers.payana.ui.screens.ProfileSetupScreen
import com.brogrammers.payana.ui.screens.ResultsScreen
import com.brogrammers.payana.ui.screens.AcceptedRideScreen
import com.brogrammers.payana.ui.screens.TrackingScreen
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun PayanaApp(explainer: MatchExplainer) {
    var screen by remember { mutableStateOf<ScreenState>(ScreenState.Home) }
    val context = LocalContext.current
    val preferences = remember { context.getSharedPreferences("payana_demo", android.content.Context.MODE_PRIVATE) }
    var onboardingComplete by remember { mutableStateOf(preferences.getBoolean("onboarding_complete", false)) }
    var profileComplete by remember { mutableStateOf(preferences.getBoolean("profile_complete", false)) }
    val scope = rememberCoroutineScope()

    fun runSearch(request: RideRequest) {
        screen = ScreenState.Loading("Finding compatible rides…")
        scope.launch {
            try {
                delay(450)
                val matches = MatchingEngine.findMatches(request)
                screen = if (matches.isEmpty()) {
                    ScreenState.NoMatch(request)
                } else {
                    ScreenState.Results(request, matches)
                }
            } catch (e: Exception) {
                screen = ScreenState.Error(
                    message = e.message ?: "Matching failed unexpectedly.",
                    returnTo = ScreenState.Home,
                )
            }
        }
    }

    fun openDetails(request: RideRequest, match: RideMatch) {
        screen = ScreenState.Loading("Writing a short match explanation…")
        scope.launch {
            val result = try {
                explainer.explain(request, match)
            } catch (_: Exception) {
                com.brogrammers.payana.ai.ExplanationResult(
                    text = match.compatibilitySummary,
                    fromAi = false,
                )
            }
            screen = ScreenState.Details(
                request = request,
                match = match,
                aiExplanation = result.text,
                explanationFromAi = result.fromAi,
                requestConfirmed = false,
            )
        }
    }

    Scaffold(modifier = Modifier.fillMaxSize()) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background),
        ) {
            if (!onboardingComplete) {
                OnboardingScreen(
                    onFinished = {
                        preferences.edit().putBoolean("onboarding_complete", true).apply()
                        onboardingComplete = true
                    },
                )
            } else if (!profileComplete) {
                ProfileSetupScreen(
                    onComplete = { name, gender, shareName ->
                        preferences.edit()
                            .putString("profile_name", name)
                            .putString("profile_gender", gender)
                            .putBoolean("share_name", shareName)
                            .putBoolean("profile_complete", true)
                            .apply()
                        profileComplete = true
                    },
                )
            } else {
                when (val s = screen) {
                    is ScreenState.Home -> HomeScreen(onFindRide = ::runSearch)
                    is ScreenState.Loading -> LoadingScreen(s.message)
                    is ScreenState.Results -> ResultsScreen(
                        request = s.request,
                        matches = s.matches,
                        onBack = { screen = ScreenState.Home },
                        onSelect = { match -> openDetails(s.request, match) },
                    )
                    is ScreenState.NoMatch -> NoMatchScreen(
                        request = s.request,
                        onRetry = { runSearch(s.request) },
                        onRelaxDetour = {
                            val relaxed = s.request.copy(
                                preferences = MatchPreferences(
                                    maxDetourMinutes = 30,
                                    womenOnly = s.request.preferences.womenOnly,
                                    departureWindowMinutes = s.request.preferences.departureWindowMinutes,
                                ),
                            )
                            runSearch(relaxed)
                        },
                        onHome = { screen = ScreenState.Home },
                    )
                    is ScreenState.Details -> MatchDetailScreen(
                        request = s.request,
                        match = s.match,
                        aiExplanation = s.aiExplanation,
                        explanationFromAi = s.explanationFromAi,
                        requestConfirmed = s.requestConfirmed,
                        onConfirmRequest = { screen = ScreenState.Accepted(s.request, s.match) },
                        onBack = {
                            val matches = MatchingEngine.findMatches(s.request)
                            screen = if (matches.isEmpty()) ScreenState.NoMatch(s.request) else ScreenState.Results(s.request, matches)
                        },
                        onHome = { screen = ScreenState.Home },
                    )
                    is ScreenState.Accepted -> AcceptedRideScreen(
                        request = s.request,
                        match = s.match,
                        onTrack = { screen = ScreenState.Tracking(s.request, s.match) },
                        onHome = { screen = ScreenState.Home },
                    )
                    is ScreenState.Tracking -> TrackingScreen(
                        request = s.request,
                        match = s.match,
                        onFinish = { screen = ScreenState.Home },
                    )
                    is ScreenState.Error -> ErrorScreen(message = s.message, onDismiss = { screen = s.returnTo })
                }
            }
        }
    }
}
