# compose-aot — minimal Compose app with stable AOT (R8 full mode + D8)

Nothing in this project depends on a machine, user, or absolute path:

- **SDK**: resolved from `ANDROID_HOME` / `ANDROID_SDK_ROOT` env vars
  (a git-ignored `local.properties` with `sdk.dir=` also works).
  Needs `platforms;android-35` plus build-tools `37.0.0`.
  ARM64 Linux note: stock Google build-tools ship an x86_64-only `aapt2`
  that cannot start on ARM64 — install the native aarch64 build-tools
  from https://github.com/sankarru/fixed into `$ANDROID_HOME/build-tools/`.
- **Gradle**: `./gradlew` wrapper pins Gradle 8.14.3; no local install needed.
- **JDK**: 21 (any JDK 17+ runs the build; AGP 8.13 requires 17+).

## Build

```sh
export ANDROID_HOME=/path/to/Android/Sdk   # if not already set
./gradlew assembleRelease
```

Output: `app/build/outputs/apk/release/app-release.apk` (signed with the
debug keystore by default; `app/build/outputs/mapping/release/mapping.txt`
holds the R8 mapping).

Install + launch:

```sh
adb install app/build/outputs/apk/release/app-release.apk
adb shell am start -n com.example.aotmin/.MainActivity
```

## What's wired

- `gradle.properties`: `android.enableR8.fullMode=true`.
- `app/build.gradle.kts` release block: `isMinifyEnabled`, `isShrinkResources`,
  default optimize rules + `app/proguard-rules.pro` (Kotlin, coroutines,
  kotlinx.serialization, enums, Parcelable, JNI edge, entry points).
- `app/src/main/baselineProfiles/baseline-prof.txt` + `profileinstaller`
  dependency: dexopt AOT-compiles the startup path at install time.
- D8 is the dexer (AGP default since 3.1; R8 full mode uses the D8 backend).
