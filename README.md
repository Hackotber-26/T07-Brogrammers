# Payana

Payana is an Android app prototype for finding compatible last-mile rides from
metro stations. It helps commuters share an auto-rickshaw ride by matching
departure times, routes, available seats, detour limits, and optional
women-only preferences.

This repository contains the Brogrammers hackathon prototype. The app uses
fictional Bengaluru Metro and commuter data so the complete experience can be
demonstrated without a backend or live ride service.

## Features

- Metro station and destination selection
- Deterministic ride matching with hard eligibility rules
- Departure-time compatibility filtering
- Capacity and passenger-count validation
- Configurable maximum detour
- Optional women-only matching using explicit demo attributes
- Illustrative fare splitting in Indian rupees
- Match explanations generated locally, with optional Google Gemini
- OpenStreetMap map cards for the demo journey
- Join confirmation and ride-progress demo screens
- Unit tests for matching, fare calculations, and core services

## Tech stack

- Kotlin
- Android SDK (compile/target SDK 37, minimum SDK 24)
- Jetpack Compose and Material 3
- Kotlin Coroutines
- OpenStreetMap via osmdroid
- Google Gemini REST API for optional match explanations
- Gradle Kotlin DSL

## Getting started

### Requirements

- Android Studio with Android SDK 37 installed
- JDK 11
- An Android emulator or physical Android device

### Build and run

1. Clone the repository:

   ```bash
   git clone https://github.com/Hackotber-26/T07-Brogrammers.git
   cd T07-Brogrammers
   ```

2. Open the project in Android Studio.

3. Allow Gradle to sync, then run the `app` configuration on an emulator or
   connected device.

   From the command line, the debug APK can be built with:

   ```bash
   ./gradlew assembleDebug
   ```

The generated APK is written to
`app/build/outputs/apk/debug/app-debug.apk`.

## Optional Gemini setup

The app works without an API key and uses its deterministic local explanation
when Gemini is unavailable.

To enable AI-polished explanations:

1. Create a Gemini API key in Google AI Studio.
2. Add the key to a local `local.properties` file at the project root:

   ```properties
   GEMINI_API_KEY=your_key_here
   ```

3. Sync Gradle and rebuild the app.

The key is injected into the debug APK through `BuildConfig` and sent to the
Gemini REST endpoint from the app. **Do not distribute an APK containing this
key or use this approach in production.** A production release should call
Gemini through a secured backend proxy with key rotation and usage controls.

See [AI_SETUP.md](AI_SETUP.md) for additional integration details and
limitations.

## How matching works

Matching is intentionally deterministic. AI is only used to rewrite an
already-validated explanation; it never decides whether a ride is eligible.

A candidate must:

1. Start at the same metro station.
2. Travel to the same destination or compatible destination corridor.
3. Depart within the requested time window.
4. Have enough available seats.
5. Stay within the requested detour limit.
6. Satisfy the women-only preference when it is enabled.

Matches are sorted by estimated detour, departure-time difference, and
commuter display name. Route and detour values are simplified demo heuristics,
not live navigation or traffic results.

## Demo flow

1. Complete the onboarding and profile screens.
2. Select a starting metro station and destination.
3. Choose departure time, passenger count, detour limit, and optional
   women-only matching.
4. Review compatible rides and their estimated fare shares.
5. Open a match to view the map and explanation.
6. Confirm the join request and continue through the ride-progress screens.

The seed data in
`app/src/main/java/com/brogrammers/payana/data/SampleData.kt` includes
successful matches as well as examples rejected for excessive detour,
departure mismatch, insufficient capacity, and incompatible preferences.

## Project structure

```text
app/src/main/java/com/brogrammers/payana/
├── ai/          Gemini integration and explanation fallback
├── core/        Matching, explanation, and join-request services
├── data/        Domain models and fictional demo data
├── matching/    Eligibility rules and fare calculations
└── ui/          Compose app, screens, and theme
```

## Testing

Run the local unit tests with:

```bash
./gradlew test
```

The tests cover matching constraints, match ordering, women-only filtering,
fare splitting, fallback explanations, and local join confirmation.

## Limitations

- Station, destination, commuter, route, detour, and fare data are fictional
  demo data.
- The app does not provide live ride discovery, booking, payments, identity
  verification, or messaging.
- Fare values are illustrative estimates, not live auto-rickshaw quotes.
- Map and route information is not a real-time navigation result.
- Gemini is optional and may fall back to a local explanation on missing keys,
  timeouts, or API errors.
