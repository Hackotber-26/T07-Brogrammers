package com.brogrammers.payana.core

import com.brogrammers.payana.data.RideMatch
import com.brogrammers.payana.data.RideRequest
import com.brogrammers.payana.matching.MatchingEngine

interface RideMatchingService {
    fun findMatches(request: RideRequest): List<RideMatch>
}

/** Application-facing matching service. Eligibility remains deterministic Kotlin logic. */
class DeterministicRideMatchingService(
    private val commuterRepository: CommuterRepository = InMemoryCommuterRepository(),
) : RideMatchingService {
    override fun findMatches(request: RideRequest): List<RideMatch> {
        validate(request)
        return MatchingEngine.findMatches(
            request = request,
            commuters = commuterRepository.getAvailableCommuters(),
        )
    }

    private fun validate(request: RideRequest) {
        require(request.startStationId.isNotBlank()) { "Start station is required" }
        require(request.destinationId.isNotBlank()) { "Destination is required" }
        require(request.departureMinutes in 0..(24 * 60 - 1)) { "Departure time is invalid" }
        require(request.passengerCount > 0) { "Passenger count must be greater than zero" }
        require(request.preferences.maxDetourMinutes >= 0) { "Maximum detour cannot be negative" }
        require(request.preferences.departureWindowMinutes >= 0) { "Departure window cannot be negative" }
    }
}
