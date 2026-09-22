# Hosting Android updates

1. Build the APK with a version code greater than the installed version:

   ```powershell
   .\gradlew.bat :app:assembleDebug
   ```

2. Upload `app\build\outputs\apk\debug\app-debug.apk` to an HTTPS host.
3. Upload `latest.json` to the same host and replace `apkUrl` with the public APK URL.
4. Set `AppUpdateConfig.manifestUrl` in `AppUpdateManager.kt` to the public `latest.json` URL.
5. Build and distribute the new APK.

The update APK must use the same signing key as the installed APK. A debug APK can only update an APK signed with the same debug key. Android displays the package installer confirmation; a normal app cannot silently install updates.

For the Play Store release, publish a signed release with an increased `versionCode`. Google Play then delivers updates through Play Store automatic updates. This custom checker is for direct APK distribution.
