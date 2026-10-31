package com.brogrammers.payana.core

import com.brogrammers.payana.data.Commuter
import com.brogrammers.payana.data.SampleData

/** In-memory source for fictional demo commuters. Data resets with the app process. */
interface CommuterRepository {
    fun getAvailableCommuters(): List<Commuter>
}

class InMemoryCommuterRepository(
    private val commuters: List<Commuter> = SampleData.seedCommuters,
) : CommuterRepository {
    override fun getAvailableCommuters(): List<Commuter> = commuters.toList()
}
