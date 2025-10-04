# Kairo Launcher (Android 9–12)

A modern, lightweight home screen replacement with a unique **Radial Dock**.

## Build without Android Studio (GitHub Actions)
1. Create a new repo on GitHub and upload this folder.
2. Go to **Actions** tab; enable workflows if prompted.
3. Run the **Build APK** workflow. After it finishes, download the artifact `Kairo-APK` to get `app-release.apk`.

## Local CLI (optional)
If you have Java 17 and Gradle installed:
```bash
gradle build
```
APK will be at `app/build/outputs/apk/release/app-release.apk`.