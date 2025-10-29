# 🍅 Pomodoro Timer

A professional macOS Pomodoro Timer built with JavaFX and **Hexagonal Architecture**.

## ✨ Features

### 🎯 Core Functionality
- **Pomodoro Technique** - 25-minute work sessions with 5-minute breaks
- **Long Breaks** - 15-minute break after 4 completed Pomodoros
- **Daily Statistics** - Tracks completed Pomodoros per day
- **Persistent Stats** - Saves your progress to disk

### 🖥️ Three Display Modes

#### 1. Full Mode (Focused)
- **Size:** 600×500px
- All controls visible
- Start/Pause/Reset buttons
- Minute spinner for custom durations
- Session type indicator
- Cycle progress (● ○ ○ ○)
- Daily Pomodoro count

#### 2. Compact Mode (Unfocused)
- **Size:** 220×120px
- Floating mini-timer
- Always on top
- Visible across all macOS Spaces/desktops
- Click to return to full mode
- Hover effect

#### 3. Menu Bar Integration
- **Real-time timer** in macOS menu bar
- Shows MM:SS format (e.g., "25:00")
- Updates every second
- Right-click menu for quick actions
- Dark mode compatible

### 🎨 Design
- Modern dark theme UI
- Smooth animations
- Focus-aware view switching
- Native macOS integration
- Clean, minimal, professional

---

## 🏗️ Architecture

This project uses **Hexagonal Architecture** (Ports & Adapters) for:
- ✅ Clean separation of concerns
- ✅ Framework independence (domain layer has no JavaFX)
- ✅ Easy testing (mockable ports)
- ✅ Flexibility (swap implementations easily)

### Layer Structure

```
📦 timer-app
├── 🟢 domain/                  # Pure business logic
│   ├── model/
│   │   ├── Timer.java
│   │   ├── Session.java
│   │   ├── DailyStatistics.java
│   │   └── SessionType.java
│   └── service/
│       └── SessionRules.java
│
├── 🔵 application/             # Use cases & ports
│   ├── port/in/               # Driving ports
│   │   ├── StartTimerUseCase.java
│   │   ├── PauseTimerUseCase.java
│   │   ├── ResetTimerUseCase.java
│   │   └── GetTimerStateQuery.java
│   ├── port/out/              # Driven ports
│   │   ├── TimerPort.java
│   │   ├── NotificationPort.java
│   │   ├── PersistencePort.java
│   │   └── AnimationPort.java
│   └── service/
│       └── TimerApplicationService.java
│
├── 🟡 adapter/                 # Framework integration
│   ├── in/ui/
│   │   ├── TimerViewController.java
│   │   └── ViewMode.java
│   └── out/
│       ├── timer/JavaFxTimerAdapter.java
│       ├── notification/SoundNotificationAdapter.java
│       ├── persistence/FileStatisticsAdapter.java
│       ├── animation/JavaFxAnimationAdapter.java
│       └── systemtray/SystemTrayAdapter.java
│
├── ⚙️ infrastructure/          # Reusable components
│   ├── sound/SoundManager.java
│   └── animation/...
│
└── config/
    └── DependencyContainer.java
```

---

## 🚀 Getting Started

### Prerequisites
- Java 21+
- Maven 3.6+
- macOS (for full menu bar integration)

### Build & Run

```bash
# Compile
mvn clean compile

# Run
mvn javafx:run

# Package (future)
mvn package
```

---

## 🎮 Usage

### Starting a Session
1. Launch the app
2. Click **Start** to begin a 25-minute Pomodoro
3. Or adjust the minute spinner for custom duration

### Controls
- **Start/Resume** - Begin or continue timer
- **Pause** - Pause the countdown
- **Reset** - Stop and reset to initial time

### Menu Bar
- **Click icon** - Shows right-click menu
- **Double-click** - Show/hide main window
- **Menu options:**
  - Show Timer
  - Start/Pause
  - Reset
  - Quit

### View Modes
- **Focus window** - Shows full controls
- **Click away** - Auto-switches to compact floating timer
- **Close window** - Timer continues in menu bar

---

## 📂 Data Storage

Statistics are stored in:
```
~/.pomodoro-timer/pomodoro-stats.properties
```

Format:
```properties
date=2025-10-29
count=8
```

---

## 🧪 Testing

The hexagonal architecture makes testing easy:

```java
// Mock the ports
TimerPort mockTimer = mock(TimerPort.class);
NotificationPort mockNotification = mock(NotificationPort.class);

// Test the application service
TimerApplicationService service = new TimerApplicationService(
    mockTimer, mockNotification, ...
);

// No JavaFX needed!
service.startSession();
verify(mockTimer).startTicking(any());
```

---

## 🔧 Configuration

### Pomodoro Durations
Edit `SessionType.java`:
```java
WORK(25, "Work Session"),        // 25 minutes
SHORT_BREAK(5, "Short Break"),   // 5 minutes
LONG_BREAK(15, "Long Break")     // 15 minutes
```

### Menu Bar Icon Size
Edit `SystemTrayAdapter.java`:
```java
int width = 60;  // Icon width
int height = 32; // Icon height
Font font = new Font("SF Mono", Font.PLAIN, 22); // Font size
```

### Window Sizes
Edit `TimerViewController.java`:
```java
// Full mode
scene = new Scene(fullModeLayout, 600, 500);

// Compact mode
stage.setWidth(220);
stage.setHeight(120);
```

---

## 🎯 Architecture Benefits

### Testability
- Domain layer: 100% framework-free
- Unit tests without mocking JavaFX
- Integration tests with mock adapters

### Flexibility
- **Swap UI**: Replace JavaFX with Swing/Web
- **Swap Storage**: Replace files with database
- **Swap Notifications**: Replace sound with push notifications

### Maintainability
- Clear boundaries between layers
- Single Responsibility Principle
- Easy to understand and modify

---

## 📝 License

This project is open source and available under the MIT License.

---

## 🙏 Credits

Built with:
- [JavaFX 21](https://openjfx.io/) - UI framework
- [Maven](https://maven.apache.org/) - Build tool
- Hexagonal Architecture pattern
- macOS System Tray API

---

## 🚧 Future Enhancements

- [ ] Customizable session durations
- [ ] Sound/notification preferences
- [ ] Statistics dashboard
- [ ] Export statistics
- [ ] Keyboard shortcuts
- [ ] Multiple timer presets
- [ ] Cloud sync (optional)

---

Made with ❤️ and ☕ using the Pomodoro Technique
