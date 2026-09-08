# WordFlow

Native Android-Vokabeltrainer (Kotlin + Jetpack Compose).

- Welcome-Setup: Name, Sprache, Tagesziel
- Echte Wortschätze (ES, FR, JA, IT, EN) mit Beispielen
- Karteikarten, Streaks, Statistik
- Foto-Scan: Liste fotografieren, Trenner setzen, Vokabeln importieren (ML Kit OCR)

## App bauen

JDK 21 und Android SDK (API 35/36):

```bash
cd android
./gradlew assembleDebug
```

APK: `android/app/build/outputs/apk/debug/app-debug.apk`
