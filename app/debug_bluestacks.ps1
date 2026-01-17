./gradlew assembleDebug
adb -s 127.0.0.1 install -r app\build\outputs\apk\debug\app-debug.apk
adb -s 127.0.0.1 shell am start -n com.fajary.zygisk_overlayingviewui/.MainActivity