# A5 - Tic Tac Toe App

Shubam Amod Dani, 2022A7PS0019G, f20220019@goa.bits-pilani.ac.in<br/>
Hruday K, 2022A7PS1161G, f20221161@goa.bits-pilani.ac.in

---

## Overview

This Tic Tac Toe app allows users to play the classic game in two modes:

- _Single-player mode:_ The user plays against the app's AI, where the app randomly selects its moves.
- _Two-player mode:_ Users can log in on different devices and play against each other in real-time.

The app integrates Firebase for user authentication and real-time game updates. Users can sign in, create games, and view their win/loss statistics on a dashboard.

---

## Features

1. _Sign-in Screen:_ Users can register and log in using their email and password. A sign-out option is available on all screens after sign-in.
2. _Dashboard:_ Displays user statistics (wins and losses), a list of open games, and a floating action button (+) to create new games.
3. _Game Creation:_
   - _Single-player:_ The user makes the first move, and the app plays as the opponent.
   - _Two-player:_ Players take turns making moves, with real-time updates using Firebase.
4. _Game Outcome:_
   - Displays a dialog for a win, loss, or draw.
   - Updates user statistics accordingly.
5. _Back Button:_ If a user clicks the back button during a game, they are prompted to confirm whether they want to forfeit the game.

---

## How We Completed the Tasks

1. _Task 1: User Authentication_  
   We used Firebase for user authentication, allowing users to sign in and register with their email and password. The app checks the user’s credentials and stores them securely.

2. _Task 2: Single-player Game_  
   In the single-player mode, the user plays against an AI that makes random moves. The game checks for a win or draw after every move and displays an appropriate dialog.

3. _Task 3: Two-player Game_  
   In two-player mode, players can join games created by others using Firebase for real-time communication. The game checks for the outcome after every turn and updates the UI accordingly.

---

## Known Bugs

- None

---

## Testing & Accessibility

- We used _Espresso_ for UI testing, ensuring that the app works as expected.
- The app was tested for accessibility using the _Accessibility Scanner_ and _TalkBack_, ensuring that it is usable by people with disabilities.
- Testing the app with TalkBack was effective, confirming that all buttons, input fields, and labels provided clear, descriptive feedback. Navigation between screens using TalkBack gestures was seamless. Accessibility improvements, including contrast adjustments and added hint text, enhanced the overall usability of the app.
- Used TalkBack for an enhanced user experience and verified that all interactive UI elements provided appropriate feedback and descriptions.
- Modified color contrast and added hint text based on Accessibility Scanner suggestions.
- Implemented at least 5 Espresso accessibility tests that passed successfully.

---

## How to Use the App

### 1. **Start a New Game**

- Login using email id and password.
- Launch the app and click the **"+" button** to start a new game.
- Choose a game mode:
  - **One-Player**: Compete against the bot.
  - **Two-Player**: Enter a room ID to connect with a friend.

### 2. **Make Your Move**

- Tap any cell on the grid to place your marker (`X` or `O`).
- In **One-Player Mode**, the bot will make random moves.
- In **Two-Player Mode**, play alternates with your opponent in real-time.

### 3. **Game Options**

- **Forfeit**: End the current game from the **options menu**.
- **Logout**: Securely log out via the **overflow menu** (three dots).

---

## Peer Programming rating

- 5/5.

---

## Estimated Time Spent

- _Time Taken:_ 75 hours.

---

## Difficulty Rating

- _Difficulty:_ 10/10

---

## Setup Instructions

### Firebase Configuration

1. Create a new Firebase project at [Firebase Console](https://console.firebase.google.com/)
2. Enable Authentication and Realtime Database
3. Download the `google-services.json` file from your Firebase project settings
4. Copy the template file: `cp app/google-services.json.template app/google-services.json`
5. Replace the placeholder values in `app/google-services.json` with your actual Firebase configuration values:
   - `YOUR_PROJECT_NUMBER`: Your Firebase project number
   - `YOUR_PROJECT_ID`: Your Firebase project ID
   - `YOUR_MOBILE_SDK_APP_ID`: Your mobile SDK app ID
   - `YOUR_API_KEY_HERE`: Your Firebase API key
   - Update the `firebase_url` with your database URL and region

⚠️ **Security Note**: Never commit `google-services.json` to version control as it contains sensitive API keys.

---
