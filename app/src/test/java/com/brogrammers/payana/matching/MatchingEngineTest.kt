package com.brogrammers.payana.matching

import com.brogrammers.payana.data.MatchPreferences
import com.brogrammers.payana.data.RideRequest
import com.brogrammers.payana.data.SampleData
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class MatchingEngineTest {

    private fun request(
        start: String = "indiranagar",
        dest: String = "koramangala",
        departure: Int = 9 * 60,
        passengers: Int = 1,
        maxDetour: Int = 15,
        womenOnly: Boolean = false,
    ) = RideRequest(
        startStationId = start,
        destinationId = dest,
        departureMinutes = departure,
        passengerCount = passengers,
        preferences = MatchPreferences(
            maxDetourMinutes = maxDetour,
            womenOnly = womenOnly,
        ),
    )

    @Test
    fun findsCompatibleMatchesForMorningIndiranagarRequest() {
        val matches = MatchingEngine.findMatches(request())
        assertTrue(matches.isNotEmpty())
        assertTrue(matches.any { it.commuter.id == "c1" })
        assertTrue(matches.any { it.commuter.id == "c2" })
    }

    @Test
    fun rejectsExcessiveDetour() {
        val matches = MatchingEngine.findMatches(request(maxDetour = 15))
        assertFalse(matches.any { it.commuter.id == "c5" }) // jp_nagar from indiranagar
    }

    @Test
    fun rejectsDepartureOutsideWindow() {
        val matches = MatchingEngine.findMatches(request())
        assertFalse(matches.any { it.commuter.id == "c6" }) // 08:20
    }

    @Test
    fun rejectsInsufficientCapacity() {
        val matches = MatchingEngine.findMatches(request(passengers = 3))
        assertFalse(matches.any { it.commuter.id == "c7" }) // only 1 seat
    }

    @Test
    fun womenOnlyUsesExplicitAttributeOnly() {
        val matches = MatchingEngine.findMatches(request(womenOnly = true))
        assertTrue(matches.isNotEmpty())
        assertTrue(matches.all { it.commuter.womenOnlyGroup })
        assertFalse(matches.any { it.commuter.id == "c2" }) // Rohan is not women-only
        assertTrue(matches.any { it.commuter.id == "c1" })
    }

    @Test
    fun noMatchWhenConstraintsImpossible() {
        val matches = MatchingEngine.findMatches(
            request(
                start = "cubbon",
                dest = "cubbon_gate",
                departure = 5 * 60, // 05:00 — no seed data
                maxDetour = 5,
            ),
        )
        assertTrue(matches.isEmpty())
    }

    @Test
    fun sortsByDetourThenDepartureDelta() {
        val matches = MatchingEngine.findMatches(request())
        if (matches.size >= 2) {
            for (i in 0 until matches.lastIndex) {
                val a = matches[i]
                val b = matches[i + 1]
                assertTrue(
                    a.estimatedDetourMinutes < b.estimatedDetourMinutes ||
                        (a.estimatedDetourMinutes == b.estimatedDetourMinutes &&
                            a.departureDeltaMinutes <= b.departureDeltaMinutes),
                )
            }
        }
    }

    @Test
    fun fareSplitUsesIntegerPaise() {
        val split = FareCalculator.splitFare(18_000, 3)
        assertEquals(18_000, split.totalFarePaise)
        assertEquals(3, split.passengerCount)
        assertEquals(6_000, split.perPersonPaise)
        assertEquals("₹60", FareCalculator.formatRupees(split.perPersonPaise))
    }

    @Test
    fun sampleDataCoversDemoStations() {
        assertTrue(SampleData.stations.size >= 8)
        assertTrue(SampleData.seedCommuters.size >= 8)
    }
}
