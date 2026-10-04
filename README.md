# DroneControl — Android Drone Ground Control Station (GCS)

A modular, production-quality Android Ground Control Station (GCS) application built with **Kotlin**, **Jetpack Compose**, and **MVVM** architecture with reactive **Kotlin Coroutines** and **StateFlow**.

Designed for seamless drone telemetry and command operation, starting with a built-in offline aerodynamic physics simulator (**MockDroneConnection**) and structured with clean transport and protocol abstractions to seamlessly connect to an **ESP32 MAVLink telemetry bridge** over Wi-Fi (UDP/TCP) or Bluetooth (BLE/SPP).

---

## 🏗 System Architecture

The application strictly decouples the UI from the communication and hardware layer:

```
┌────────────────────────────────────────────────────────┐
│                      Jetpack Compose UI                │
│    (Dashboard, Connection, Telemetry, Map, Settings)   │
└───────────────────────────▲────────────────────────────┘
                            │ StateFlow / Events
┌───────────────────────────▼────────────────────────────┐
│                      DroneViewModel                    │
│           (Transforms models & handles UI logic)       │
└───────────────────────────▲────────────────────────────┘
                            │ Coroutine Dispatch
┌───────────────────────────▼────────────────────────────┐
│                      DroneRepository                   │
│        (Single source of truth & link switching)       │
└───────────────────────────▲────────────────────────────┘
                            │ Interface Contract
┌───────────────────────────▼────────────────────────────┐
│                      DroneConnection                   │
│     ┌─────────────────────┴─────────────────────┐      │
│     │ MockDroneConnection  │  ESP32MavlinkConn  │      │
└─────┼──────────────────────┼────────────────────┼──────┘
      │                      │                    │
┌─────▼──────────────────────▼────────────────────▼──────┐
│                         Transport                      │
│     ┌───────────────────────────────────────────┐      │
│     │ MockTransport │ WifiTransport │ Bluetooth │      │
└─────┴───────────────────────────────────────────┴──────┘
```

### Decoupling Rules Enforced:
1. **Zero Hardware in UI**: Compose components never access Bluetooth, Wi-Fi sockets, ESP32 APIs, or MAVLink serializers directly.
2. **Interface Abstraction**: `DroneConnection` and `Transport` interfaces allow hot-swapping simulated and physical hardware links without modifying a single line of Compose UI.
3. **Reactive Unidirectional Data Flow**: Drone status, physics ticks, and telemetry updates propagate as immutable snapshots via `StateFlow<Telemetry>`.

---

## 📁 Package Organization

```
com.dronecontrol
├── data/                    # Domain models, enums & repository
│   ├── ConnectionState.kt   # DISCONNECTED, CONNECTING, CONNECTED, ERROR
│   ├── ConnectionType.kt    # SIMULATOR, WIFI, BLUETOOTH
│   ├── DroneRepository.kt   # Mediates between ViewModel and DroneConnection
│   ├── FlightCommand.kt     # Sealed class for arm, takeoff, mode, stick controls
│   ├── FlightMode.kt        # STABILIZE, ALT_HOLD, POS_HOLD, RTL, LAND, etc.
│   ├── GpsFix.kt            # NO_FIX, FIX_2D, FIX_3D, RTK
│   └── Telemetry.kt         # Immutable real-time avionics telemetry snapshot
│
├── drone/                   # Drone control interfaces and simulators
│   ├── DroneConnection.kt   # High-level drone control interface
│   └── MockDroneConnection.kt # Realistic 10Hz physics, battery, and GPS simulation
│
├── transport/               # Low-level communication channels
│   ├── Transport.kt         # Bidirectional byte/packet transport interface
│   ├── MockTransport.kt     # In-memory virtual transport for offline simulator
│   ├── WifiTransport.kt     # UDP / TCP socket transport for ESP32 AP
│   └── BluetoothTransport.kt # BLE / Classic SPP transport placeholder
│
├── mavlink/                 # MAVLink v1/v2 framing & protocol abstraction
│   ├── MavlinkMessage.kt    # Heartbeat, Attitude, GlobalPositionInt stubs
│   ├── MavlinkPacket.kt     # MAVLink packet framing definition
│   └── MavlinkHandler.kt    # Protocol parser & encoder interfaces
│
├── navigation/              # App destinations and navigation scaffold
│   ├── Screen.kt            # 5 Top-level screen destinations
│   └── AppNavigation.kt     # NavHost and dark GCS bottom navigation bar
│
├── ui/                      # Jetpack Compose UI
│   ├── theme/               # Dark military/aerospace GCS color palette & typography
│   │   ├── Color.kt
│   │   ├── Theme.kt
│   │   └── Type.kt
│   ├── components/          # Reusable tactical widgets
│   │   ├── StatusHeader.kt  # Cockpit status bar & "SIMULATION MODE" badge
│   │   ├── TelemetryBadge.kt# Compact GCS chip
│   │   ├── VirtualJoystick.kt # Interactive dual virtual analog joysticks
│   │   ├── FlightActionControls.kt # ARM/DISARM, TAKEOFF, LAND, RTL, HOLD
│   │   ├── ArtificialHorizon.kt # Avionics Attitude Director Indicator (ADI)
│   │   ├── CompassRose.kt   # 360° heading compass dial
│   │   └── ModeSelectorDialog.kt # Mode switching modal
│   └── screens/             # Top-level GCS screens
│       ├── DashboardScreen.kt # Primary cockpit HUD & joysticks
│       ├── ConnectionScreen.kt # Link switcher & transport console
│       ├── TelemetryScreen.kt # Comprehensive avionics telemetry audit
│       ├── MapScreen.kt       # Tactical radar map & breadcrumb trail
│       └── SettingsScreen.kt  # Safety parameters & architecture docs
│
├── viewmodel/               # MVVM ViewModel & UI State
│   ├── DroneUiState.kt      # Unified immutable UI state
│   ├── DroneViewModel.kt    # Presentation logic & coroutines handler
│   └── DroneViewModelFactory.kt # DI Factory
│
└── utils/                   # Constants and avionics formatters
    ├── Constants.kt
    └── Formatters.kt
```

---

## 🎮 Screens & Features

### 1. Dashboard
- **HUD Cockpit**:
  - Live **Artificial Horizon (ADI)** calculating pitch ladder and roll bank angle.
  - Directional **Compass Rose** displaying heading (0-360°) and cardinal orientation.
  - Tactical **Mini Radar** preview showing GPS coordinates and armed status.
- **Flight Actions**:
  - **ARM / DISARM** with safety confirmation modal to prevent accidental motor activation.
  - **TAKEOFF** with interactive target altitude slider (AGL).
  - **LAND**, **RTL (Return to Launch)**, and **HOLD (Loiter)**.
- **Dual Virtual Joysticks (Mode 2)**:
  - Left Stick: Throttle (Altitude rate) + Yaw (Heading turn rate).
  - Right Stick: Pitch (Forward/Backward tilt) + Roll (Left/Right tilt).
  - Spring auto-center with normalized telemetry feedback (`X: +0.00, Y: +0.00`).

### 2. Connection
- Modes:
  - **Simulator**: Launches built-in mock drone with 10Hz aerodynamics and state machine.
  - **Wi-Fi (UDP/TCP)**: Configures IP address (`192.168.4.1`) and port (`14550`) for ESP32 AP.
  - **Bluetooth (BLE/SPP)**: Configures Bluetooth device address for ESP32.
- **Transport Communications Log**: Live terminal window streaming incoming and outgoing packet traffic.

### 3. Telemetry
- Detailed breakdown of all avionics subsystems:
  - **Power**: Voltage (LiPo discharge curve), percentage bar, estimated current draw.
  - **Spatial Navigation**: GPS fix type, satellite count, latitude, longitude, MSL altitude, AGL altitude, distance to home.
  - **Dynamics**: Pitch, roll, yaw, climb rate, ground speed.
  - **Autopilot**: Active flight mode, arm status, stream cadence.

### 4. Map
- Tactical radar grid with 50m/100m/200m range rings.
- Dynamic drone marker rotating with compass heading.
- Home takeoff point marker with distance indicator.
- Real-time flight breadcrumb trail.
- Zoom and reset controls.

### 5. Settings
- Architecture explanation and pipeline diagrams.
- Configurable safety limits (Max ceiling 120m, RTL altitude, Battery failsafes).
- Control preferences (Auto-center sticks, metric units).

---

## ⚡ Mock Drone Simulation

The `MockDroneConnection` runs a background coroutine at 10Hz generating realistic physics:
- **Autonomous Takeoff**: Climbs at +2.0 m/s until the target altitude is reached, then levels off.
- **Autonomous Land**: Descends gently at -1.8 m/s, slows to -0.6 m/s near ground, and automatically disarms upon touchdown.
- **Return to Launch (RTL)**: Computes Haversine bearing to the home point, flies home at 5.0 m/s, and initiates landing.
- **Manual Flight Dynamics**: Joystick inputs bank the attitude indicator and shift latitude and longitude coordinates in real-time.
- **Battery Curve**: Realistic 3S LiPo voltage modeling (~12.6V down to ~10.5V) with armed load drop.

---

## 🛠 Building & Running in Android Studio

1. **Open in Android Studio**:
   - Open Android Studio.
   - Select **File → Open...** and choose the `drone_control` directory (`/home/koushik/drone_control`).
2. **Gradle Sync**:
   - Android Studio will detect Gradle 8.7, JDK 17/21, and Android Gradle Plugin 8.4.1.
   - Gradle sync will resolve all standard Jetpack Compose libraries from Google & Maven Central.
3. **Run on Device or Emulator**:
   - Select an Android device or emulator running API 24 or newer.
   - Click **Run 'app'** (Shift + F10).
   - The app starts immediately in **SIMULATION MODE** ready for flight testing!
