# Connections SDK Android Sample

Minimal Android sample app demonstrating the Connections SDK for rowing machine integration.

## What it shows

- Module initialization with `ConnectionsModule.create`
- Device scanning via `DeviceListProvider`
- Connecting to a rowing machine via `ConnectionManager`
- Live metrics via `RowerDataSource`: stroke rate, split, power, distance
- Fake device support for running without hardware (debug builds)

## Requirements

- Android Studio Meerkat or newer
- Android 8.0+ (API 26) device or emulator
- MoveLab Nexus credentials (contact MoveLab to obtain)

## Getting Started

1. Clone this repo
2. Add your credentials to `~/.gradle/gradle.properties` (create if it doesn't exist):
   ```
   nexusUser=<your username>
   nexusPassword=<your password>
   ```
3. Open the project in Android Studio
4. Press **Sync Project with Gradle Files**
5. Build and run the `app` configuration

In debug builds a simulated ("Fake") rowing machine appears automatically in the device list — no hardware required.

## Authentication

The SDK is hosted on the MoveLab Nexus repository. Contact MoveLab to obtain credentials.

The `gradle.properties` file is gitignored to prevent accidentally committing credentials. Alternatively, you can set the `NEXUS_USER` and `NEXUS_PASSWORD` environment variables instead of using the properties file.

## Key Files

| File | Purpose |
|---|---|
| `app/src/main/java/mls/connections/sample/ConnectionsViewModel.kt` | Core SDK integration |
| `app/src/main/java/mls/connections/sample/ui/DeviceListScreen.kt` | Device scanning UI |
| `app/src/main/java/mls/connections/sample/ui/MetricsScreen.kt` | Live metrics display |
| `app/src/main/java/mls/connections/sample/MainActivity.kt` | Entry point, screen routing |
| `app/build.gradle.kts` | SDK dependency declaration |
