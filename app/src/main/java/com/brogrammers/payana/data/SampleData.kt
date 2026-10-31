package com.brogrammers.payana.data

/**
 * Self-contained fictional Bengaluru Metro + last-mile demo data.
 * Not live station feeds or real fare quotes.
 */
object SampleData {

    val stations: List<MetroStation> = listOf(
        MetroStation("indiranagar", "Indiranagar", "Purple"),
        MetroStation("hal", "HAL Airport Road", "Purple"),
        MetroStation("mg_road", "MG Road", "Purple"),
        MetroStation("cubbon", "Cubbon Park", "Purple"),
        MetroStation("majestic", "Majestic (Nadaprabhu Kempegowda)", "Interchange"),
        MetroStation("jayanagar", "Jayanagar", "Green"),
        MetroStation("banashankari", "Banashankari", "Green"),
        MetroStation("silk_board", "Silk Board", "Yellow"),
        MetroStation("rv_road", "RV Road", "Green"),
        MetroStation("lalbagh", "Lalbagh", "Green"),
    )

    val destinations: List<Destination> = listOf(
        Destination("koramangala", "Koramangala 5th Block", "silk_board", "south_east"),
        Destination("hsr", "HSR Layout BDA Complex", "silk_board", "south_east"),
        Destination("indiranagar_100", "Indiranagar 100 Feet Road", "indiranagar", "east"),
        Destination("brigade", "Brigade Road", "mg_road", "central"),
        Destination("ub_city", "UB City", "mg_road", "central"),
        Destination("jayanagar_4th", "Jayanagar 4th Block", "jayanagar", "south"),
        Destination("bsk_temple", "Banashankari Temple Street", "banashankari", "south"),
        Destination("jp_nagar", "JP Nagar 6th Phase", "banashankari", "south"),
        Destination("cubbon_gate", "Cubbon Park Gate", "cubbon", "central"),
        Destination("electronic_city", "Electronic City Phase 1", "silk_board", "south_east"),
    )

    /**
     * Fictional seed riders covering:
     * - several compatible matches
     * - excessive detour rejection
     * - departure-time rejection
     * - insufficient capacity rejection
     * - women-only preference scenario
     * - no-match scenario (via restrictive request)
     */
    val seedCommuters: List<Commuter> = listOf(
        Commuter(
            id = "c1",
            displayName = "Ananya R. (demo)",
            startStationId = "indiranagar",
            destinationId = "koramangala",
            departureMinutes = 9 * 60, // 09:00
            groupSize = 1,
            seatsAvailable = 3,
            womenOnlyGroup = true,
            assumedRideFarePaise = 18_000, // ₹180
        ),
        Commuter(
            id = "c2",
            displayName = "Rohan M. (demo)",
            startStationId = "indiranagar",
            destinationId = "hsr",
            departureMinutes = 9 * 60 + 8, // 09:08
            groupSize = 2,
            seatsAvailable = 2,
            womenOnlyGroup = false,
            assumedRideFarePaise = 20_000, // ₹200
        ),
        Commuter(
            id = "c3",
            displayName = "Priya S. (demo)",
            startStationId = "mg_road",
            destinationId = "brigade",
            departureMinutes = 9 * 60 + 5,
            groupSize = 1,
            seatsAvailable = 3,
            womenOnlyGroup = true,
            assumedRideFarePaise = 8_000, // ₹80
        ),
        Commuter(
            id = "c4",
            displayName = "Karthik V. (demo)",
            startStationId = "jayanagar",
            destinationId = "jayanagar_4th",
            departureMinutes = 9 * 60 + 10,
            groupSize = 1,
            seatsAvailable = 3,
            womenOnlyGroup = false,
            assumedRideFarePaise = 7_000,
        ),
        // Excessive detour vs Indiranagar → Koramangala requests (different corridor)
        Commuter(
            id = "c5",
            displayName = "Farhan A. (demo)",
            startStationId = "indiranagar",
            destinationId = "jp_nagar",
            departureMinutes = 9 * 60 + 5,
            groupSize = 1,
            seatsAvailable = 3,
            womenOnlyGroup = false,
            assumedRideFarePaise = 25_000,
        ),
        // Departure too early for typical 09:00 window (±20)
        Commuter(
            id = "c6",
            displayName = "Neha K. (demo)",
            startStationId = "indiranagar",
            destinationId = "koramangala",
            departureMinutes = 8 * 60 + 20, // 08:20
            groupSize = 1,
            seatsAvailable = 2,
            womenOnlyGroup = true,
            assumedRideFarePaise = 18_000,
        ),
        // Insufficient seats for a 3-passenger request
        Commuter(
            id = "c7",
            displayName = "Vikram T. (demo)",
            startStationId = "indiranagar",
            destinationId = "hsr",
            departureMinutes = 9 * 60 + 2,
            groupSize = 3,
            seatsAvailable = 1,
            womenOnlyGroup = false,
            assumedRideFarePaise = 19_000,
        ),
        Commuter(
            id = "c8",
            displayName = "Meera D. (demo)",
            startStationId = "silk_board",
            destinationId = "electronic_city",
            departureMinutes = 18 * 60 + 30, // 18:30
            groupSize = 1,
            seatsAvailable = 2,
            womenOnlyGroup = true,
            assumedRideFarePaise = 22_000,
        ),
        Commuter(
            id = "c9",
            displayName = "Arjun P. (demo)",
            startStationId = "banashankari",
            destinationId = "bsk_temple",
            departureMinutes = 18 * 60 + 25,
            groupSize = 2,
            seatsAvailable = 2,
            womenOnlyGroup = false,
            assumedRideFarePaise = 6_000,
        ),
        Commuter(
            id = "c10",
            displayName = "Sana H. (demo)",
            startStationId = "mg_road",
            destinationId = "ub_city",
            departureMinutes = 10 * 60,
            groupSize = 1,
            seatsAvailable = 3,
            womenOnlyGroup = true,
            assumedRideFarePaise = 9_000,
        ),
    )

    fun stationName(id: String): String =
        stations.firstOrNull { it.id == id }?.name ?: id

    fun destinationName(id: String): String =
        destinations.firstOrNull { it.id == id }?.name ?: id

    fun destination(id: String): Destination? =
        destinations.firstOrNull { it.id == id }

    fun station(id: String): MetroStation? =
        stations.firstOrNull { it.id == id }
}
