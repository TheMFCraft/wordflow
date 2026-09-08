# WordFlow

Android-Vokabeltrainer im WordFlow-Design. Karteikarten, Streaks und Statistik — offline auf dem Gerät.

## App bauen

Voraussetzungen: Node.js 22+, JDK 21, Android SDK (API 35/36).

```bash
npm install
npx cap sync android
cd android
./gradlew assembleDebug
```

Die Debug-APK liegt danach unter:

`android/app/build/outputs/apk/debug/app-debug.apk`

In Android Studio: `npx cap open android`.
