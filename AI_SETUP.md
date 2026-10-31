# Payana AI setup (Google AI Studio)

## Current integration

Payana uses Google Gemini through the Google AI Studio REST API to explain an already-validated match. Matching, eligibility, detour limits, women-only filtering, and fare math remain deterministic Kotlin logic.

The app falls back to a local explanation when the key is missing, the request times out, or Gemini returns an error.

## Configure the key

1. Create or copy a Gemini API key from Google AI Studio.
2. Add it to the existing local.properties file:

GEMINI_API_KEY=your_key_here

3. Sync Gradle and rebuild the app.

The app calls POST https://generativelanguage.googleapis.com/v1beta/models/gemini-3.8-flash:generateContent and authenticates with the x-goog-api-key header.

## Important security limitation

For this private hackathon prototype, the key is injected into the debug APK through BuildConfig. A distributed APK can expose that key. Do not publish the APK or reuse this key for production. A backend proxy is required for a real release.

## Requirement note

Google AI Studio is technically integrated, but it may not satisfy a judging rule that explicitly requires DigitalOcean Inference or Gemma 4. If that rule is strict, use the current app flow as the demo and explain that the live provider was changed because DigitalOcean credentials were unavailable.
