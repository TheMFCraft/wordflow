# WordFlow

Nativer Android-Vokabeltrainer (Kotlin + Jetpack Compose).

- Setup: Cylone ID, Sprachen wählen, Tagesziel
- Home zeigt nur die gewählten Sprachen, darin benannte Kapitel und Vokabeln
- Bottom Bar: Home, Plus (Sprache/Kapitel/Vokabel/Scan), Einstellungen
- WordFlow PLUS in den Einstellungen (Cloud Sync, AI Trainer)

## Bauen

JDK 21 und Android SDK (API 36):

```bash
cd android
./gradlew assembleDebug
```

APK: `android/app/build/outputs/apk/debug/app-debug.apk`

Cylone ID lokal in `android/local.properties` (nicht committen):

```
cylone.clientId=…
cylone.clientSecret=…
```

Redirect URI: `app.wordflow.trainer://oauth/callback`
