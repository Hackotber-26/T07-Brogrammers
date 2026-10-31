package com.brogrammers.payana.core

import com.brogrammers.payana.data.MatchPreferences
import com.brogrammers.payana.data.RideMatch
import com.brogrammers.payana.data.RideRequest
import com.brogrammers.payana.matching.MatchingEngine
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CoreServicesTest {
    private val request = RideRequest(
        startStationId = "indiranagar",
        destinationId = "koramangala",
        departureMinutes = 9 * 60,
        passengerCount = 1,
        preferences = MatchPreferences(maxDetourMinutes = 15, womenOnly = false),
    )

    @Test
    fun matchingServiceUsesRepositoryDataAndHardConstraints() {
        val matches = DeterministicRideMatchingService().findMatches(request)
        assertTrue(matches.isNotEmpty())
        assertTrue(matches.none { it.estimatedDetourMinutes > request.preferences.maxDetourMinutes })
    }

    @Test(expected = IllegalArgumentException::class)
    fun matchingServiceRejectsInvalidPassengerCount() {
        DeterministicRideMatchingService().findMatches(request.copy(passengerCount = 0))
    }

    @Test
    fun missingAiKeyStillReturnsLocalExplanation() = runBlocking {
        val match = DeterministicRideMatchingService().findMatches(request).first()
        val result = FallbackMatchExplanationService(
            primary = object : MatchExplanationService {
                override suspend fun explain(request: RideRequest, match: RideMatch): ExplanationResult {
                    throw IllegalStateException("network unavailable")
                }
            },
        ).explain(request, match)
        assertFalse(result.fromAi)
        assertTrue(result.text.isNotBlank())
    }

    @Test
    fun joinConfirmationIsLocalDemoState() {
        val match = DeterministicRideMatchingService().findMatches(request).first()
        val confirmation = LocalJoinRequestService().confirmJoin(match)
        assertTrue(confirmation.matchId == match.commuter.id)
        assertTrue(confirmation.message.contains("No real message"))
    }
}
