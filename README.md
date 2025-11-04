# 🍅 Pomodoro Timer

A Pomodoro Timer built with JavaFX and Spring Shell using **Hexagonal Architecture**.

That means, Core is encapsulated and exposed only using Ports implemented by Adapters.

## ✨ Features

### 🎯 Core Functionality
- **Pomodoro Technique** - 25-minute work sessions with 5-minute breaks
- **Long Breaks** - 15-minute break after 4 completed Pomodoros
- **Daily Statistics** - Tracks completed Pomodoros per day
- **Persistent Stats** - Saves your progress to disk

## 🏗️ Architecture

This project uses **Hexagonal Architecture** (Ports & Adapters) for:
- ✅ Clean separation of concerns
- ✅ Framework independence (domain layer has no JavaFX)
- ✅ Easy testing (mockable ports)
- ✅ Flexibility (swap implementations easily)

### Layer Structure

```
📦 pomodoro-timer 
├── 🟢 domain/                  # Pure business logic
├── 🔵 application/             # Use cases & ports
│   ├── port/in/                # Driving ports
│   ├── port/out/               # Driven ports
│   ├── dto/                    # Dtos
│   └── service/                # Port In Implementation
│
├── 🟡 adapters/                 # Framework integration
└── config/
```

---

## 🚀 Getting Started

### Prerequisites
- Java 21+
- Maven 3.6+

### Build & Run

```bash
# Compile and install
mvn clean install
cd app-bootstrap

# Run using JavaFX
./run-javafx.sh

# or

# Run using spring shell
./run-shell.sh
```

---

## 🎮 Usage

### JavaFX 

1. Launch the app
2. Click **Start** to begin a 25-minute Pomodoro
3. Or adjust the minute spinner for custom duration

#### Controls
- **Start/Resume** - Begin or continue timer
- **Pause** - Pause the countdown
- **Reset** - Stop and reset to initial time

### Shell

1. Launch the app
2. type `timer start` or `timer start <minutes>` to start a custom pomodoro
3. type `timer status` to see the current timer status or `timer watch` to "watch" the pomodoro timer

type `help` to see other commands

---

## 📂 Data Storage

- History is saved in `~/.pomodoro-timer/timer-history.json`
- Create the directory if it doesn't exist

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
