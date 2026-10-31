package com.brogrammers.payana.core

import com.brogrammers.payana.data.RideMatch

data class JoinConfirmation(
    val matchId: String,
    val message: String,
)

interface JoinRequestService {
    fun confirmJoin(match: RideMatch): JoinConfirmation
}

/** Local-only demo confirmation. It does not contact or notify another person. */
class LocalJoinRequestService : JoinRequestService {
    override fun confirmJoin(match: RideMatch): JoinConfirmation {
        return JoinConfirmation(
            matchId = match.commuter.id,
            message = "Demo request confirmed for ${match.commuter.displayName}. No real message was sent.",
        )
    }
}
