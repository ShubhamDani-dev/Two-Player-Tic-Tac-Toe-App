# A5 - Tic Tac Toe App


Shubam Amod Dani, 2022A7PS0019G, f20220019@goa.bits-pilani.ac.in<br/>
Hruday K,         2022A7PS1161G, f20221161@goa.bits-pilani.ac.in


---

## Overview
This Tic Tac Toe app allows users to play the classic game in two modes:

- *Single-player mode:* The user plays against the app's AI, where the app randomly selects its moves.
- *Two-player mode:* Users can log in on different devices and play against each other in real-time.

The app integrates Firebase for user authentication and real-time game updates. Users can sign in, create games, and view their win/loss statistics on a dashboard.

---

## Features
1. *Sign-in Screen:* Users can register and log in using their email and password. A sign-out option is available on all screens after sign-in.
2. *Dashboard:* Displays user statistics (wins and losses), a list of open games, and a floating action button (+) to create new games.
3. *Game Creation:*
    - *Single-player:* The user makes the first move, and the app plays as the opponent.
    - *Two-player:* Players take turns making moves, with real-time updates using Firebase.
4. *Game Outcome:*
    - Displays a dialog for a win, loss, or draw.
    - Updates user statistics accordingly.
5. *Back Button:* If a user clicks the back button during a game, they are prompted to confirm whether they want to forfeit the game.

---

## How We Completed the Tasks

1. *Task 1: User Authentication*  
   We used Firebase for user authentication, allowing users to sign in and register with their email and password. The app checks the user’s credentials and stores them securely.

2. *Task 2: Single-player Game*  
   In the single-player mode, the user plays against an AI that makes random moves. The game checks for a win or draw after every move and displays an appropriate dialog.

3. *Task 3: Two-player Game*  
   In two-player mode, players can join games created by others using Firebase for real-time communication. The game checks for the outcome after every turn and updates the UI accordingly.

---

## Known Bugs
- None

---

## Testing & Accessibility
- We used *Espresso* for UI testing, ensuring that the app works as expected.
- The app was tested for accessibility using the *Accessibility Scanner* and *TalkBack*, ensuring that it is usable by people with disabilities.
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
- *Time Taken:* 75 hours.

---

## Difficulty Rating
- *Difficulty:* 10/10

---