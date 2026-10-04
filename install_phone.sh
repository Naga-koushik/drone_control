#!/bin/bash
set -e

echo "=== Deploying DroneControl to connected Android device ==="

# Check connected devices
DEVICE_COUNT=$(adb devices | grep -v "List of devices attached" | grep "device$" | wc -l)

if [ "$DEVICE_COUNT" -eq 0 ]; then
    echo "⚠️  No authorized device found."
    echo "Please connect your phone with a USB cable and ensure:"
    echo "  1. Developer Options -> USB Debugging is ON."
    echo "  2. You tapped 'Always allow from this computer' on your phone."
    echo ""
    echo "Currently attached devices:"
    adb devices
    exit 1
fi

echo "✓ Device detected. Building and installing latest debug APK..."
./gradlew installDebug

echo "✓ Launching DroneControl on phone..."
adb shell am start -n com.dronecontrol/.MainActivity

echo "🚀 App successfully launched on your phone!"
