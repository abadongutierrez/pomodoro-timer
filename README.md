# 🍅 Pomodoro Timer

A Pomodoro Timer built with JavaFX and **Hexagonal Architecture**.

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
├── 🔵 application/             # Use cases & ports
│   ├── port/in/                # Driving ports
│   ├── port/out/               # Driven ports
│   └── service/                # Port In Implementation
│
├── 🟡 adapter/                 # Framework integration
├── ⚙️ infrastructure/          # Reusable components
└── config/
```

---

## 🚀 Getting Started

### Prerequisites
- Java 21+
- Maven 3.6+

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

WIP

---

---

## 🔧 Configuration

WIP

---

## 📝 License

This project is open source and available under the MIT License.

---

## 🚧 Future Enhancements

- [ ] Add AI
- [ ] Add Blockchain
- [ ] Add Bluetooth 

j/k WIP


---
