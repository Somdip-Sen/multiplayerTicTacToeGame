<div align="center">

#  Multiplayer Tic-Tac-Toe Game for Android 📱

<img src="https://placehold.co/600x300/1e1e1e/c792ea?text=Tic-Tac-Toe+Game" alt="Tic-Tac-Toe Banner"/>

**A feature-rich Tic-Tac-Toe game for Android that supports single-player, and multiplayer over Wi-Fi Direct and Bluetooth.**

</div>

<p align="center">
  <img alt="GitHub repo size" src="https://img.shields.io/github/repo-size/Somdip-Sen/multiplayerTicTacToeGame?style=for-the-badge&color=blueviolet">
  <img alt="GitHub last commit" src="https://img.shields.io/github/last-commit/Somdip-Sen/multiplayerTicTacToeGame?style=for-the-badge&color=important">
  <img alt="GitHub" src="https://img.shields.io/github/license/Somdip-Sen/multiplayerTicTacToeGame?style=for-the-badge&color=blue">
  <img alt="GitHub stars" src="https://img.shields.io/github/stars/Somdip-Sen/multiplayerTicTacToeGame?style=for-the-badge&color=orange">
</p>

---

## ✨ Features

This isn't just a simple Tic-Tac-Toe game. It's packed with features to provide a complete and engaging experience.

* **🎮 Multiple Game Modes:**
    * **Single Device:** Two players can play on the same device.
    * **Wi-Fi Direct Multiplayer:** Play with a friend on a separate device over a local Wi-Fi network.
    * **Bluetooth Multiplayer:** A work-in-progress mode to play over Bluetooth.
* **🎨 Custom UI & Animations:** A custom-drawn game board with smooth animations for a polished feel.
* **🔊 Sound Effects:** Engaging sound effects for moves and game events.
* **📊 Score Keeping:** The app automatically tracks scores for each player across matches.
* **🌐 Real-time Sync:** In multiplayer mode, the game state is seamlessly synchronized between devices.

---

## 🛠️ Tech Stack

This project is a native Android application built using:

* **Language:** Java
* **Platform:** Android SDK
* **Networking:**
    * `WifiP2pManager` for Wi-Fi Direct peer-to-peer connections.
    * `BluetoothAdapter` & RFCOMM Sockets for Bluetooth communication.
* **UI:** Custom `View` for the game board and standard Android XML layouts.

---

## 🚀 Getting Started

To get a local copy up and running, follow these simple steps.

### Prerequisites

* **Android Studio:** Make sure you have the latest version of Android Studio installed.
* **Android Device/Emulator:** You'll need at least one Android device (or two for multiplayer) running Android 4.1 (Jelly Bean) or higher.

### Installation

1.  **Clone the repository:**
    ```sh
    git clone [https://github.com/Somdip-Sen/multiplayerTicTacToeGame.git](https://github.com/Somdip-Sen/multiplayerTicTacToeGame.git)
    ```
2.  **Open in Android Studio:**
    * Open Android Studio.
    * Click on `File` -> `Open`.
    * Navigate to the cloned repository folder and select it.
3.  **Build the Project:**
    * Let Android Studio sync the Gradle files.
    * Click on `Build` -> `Make Project`.
4.  **Run the App:**
    * Select an emulator or connect your Android device.
    * Click `Run` -> `Run 'app'`.

---

## 📖 How to Play

### Single Device Mode
1. Launch the app.
2. Select "Single Player" or a similar option on the main screen.
3. The game board will appear. Players take turns tapping on the grid to place their 'X' or 'O'.

### Wi-Fi Direct Multiplayer Mode
1. Both players must be connected to the same Wi-Fi network.
2. On the main menu, select "Multiplayer" -> "Wi-Fi".
3. One player acts as the **Host (Server)** and the other as the **Client**.
4. The client will search for and connect to the host's device.
5. Once connected, the game starts, and moves are synchronized across both devices.

---

## 📂 Project Structure

Here's a brief overview of the key files in the project:

| File/Folder | Description |
| :--- | :--- |
| `MainActivity.java` | The main entry point of the application. |
| `Game.java` | Activity that hosts the single-device game mode. |
| `Board.java` | Custom view that draws the Tic-Tac-Toe board and handles touch input. |
| `Game_logic.java` | Contains the core game logic for checking wins, draws, and managing turns. |
| `Multi_device_option.java`| Activity to let the user choose between Wi-Fi and Bluetooth multiplayer. |
| `WifiModeselect.java` | Handles Wi-Fi peer discovery and connection setup. |
| `Game_multi.java` | Activity for Wi-Fi multiplayer gameplay, managing socket communication. |
| `bluetooth_mode_select.java`| (Beta) Activity for setting up Bluetooth connections. |
| `BackgroundSoundService.java`| A service to manage background music and sound effects. |

---

## 🤝 Contributing

Contributions are what make the open-source community such an amazing place to learn, inspire, and create. Any contributions you make are **greatly appreciated**.

If you have a suggestion that would make this better, please fork the repo and create a pull request. You can also simply open an issue with the tag "enhancement".

1.  **Fork the Project**
2.  **Create your Feature Branch** (`git checkout -b feature/AmazingFeature`)
3.  **Commit your Changes** (`git commit -m 'Add some AmazingFeature'`)
4.  **Push to the Branch** (`git push origin feature/AmazingFeature`)
5.  **Open a Pull Request**

---

## 📜 License

Distributed under the MIT License. See `LICENSE` for more information.

---

<div align="center">

**Made with ❤️ by Somdip Sen as a B.Tech final year project**

</div>