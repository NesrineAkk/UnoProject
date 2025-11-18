# UNO Game with Custom Graphical Layer

A complete implementation of the classic UNO card game featuring a custom-built graphical interface layer on top of Java Swing.

## 📋 Project Overview

This project was developed as an end-of-semester assignment at the University of Science and Technology Houari Boumediene (USTHB). It transforms a text-based UNO game into a fully graphical experience by creating a custom abstraction layer that simplifies Swing component management and provides a consistent, modern user interface.

## ✨ Key Features

- **Custom Graphical Layer**: Purpose-built UI components that extend Swing functionality
- **Complete UNO Implementation**: Full game logic following official UNO rules
- **Modern Visual Design**: Rounded corners, hover effects, shadows, and gradients
- **Flexible Game Modes**: Play against AI opponents (1-3 bots) or with multiple players (2-4)
- **Player Customization**: Name entry and avatar selection system
- **Interactive Components**: Animated cards, clickable elements, and responsive feedback
- **Intuitive Navigation**: Smooth screen transitions using CardLayout

## 🎮 Game Features

- **Draw Pile Management**: Full 108-card UNO deck with shuffling
- **Discard Pile**: Dynamic display of the current card with visual effects
- **Player Hands**: Support for both horizontal (human) and vertical (AI) card displays
- **Special Cards**: Implementation of action cards (Skip, Reverse, Draw Two, Wild, etc.)
- **Score Tracking**: Built-in scoring system
- **Game Rules Display**: In-app reference for UNO rules

## 🏗️ Architecture

The project is structured in three main layers:

### 1. Custom Graphical Layer
Reusable UI components that abstract Swing complexity:

- **Visual Components**: `MyButton`, `UILabel`, `UIImage`, `UITextField`
- **Containers**: `UIContainer`, `UICard`, `MyFrame`, `MyDialog`
- **Utilities**: `UIBorderFactory`, `UICore`

### 2. Screen Components
Individual screens managing different game states:

- `WelcomeScreen`: Initial landing page
- `NameEntryScreen`: Player name and avatar selection
- `MainMenuScreen`: Main navigation hub
- `GameRulesScreen`: Official UNO rules display
- `PlayerSelectionScreen`: Choose number of players
- `AIPlayerSelectionScreen`: Choose number of AI opponents
- `GameScreen`: Main gameplay interface

### 3. Game Logic
Core game management:

- `UnoGameApp`: Main application coordinator
- `GameManager`: Game logic handler
- `DrawPilePanel`: Draw pile management
- `DiscardPilePanel`: Discard pile display
- `PlayerHandPanel`: Player hand visualization

## 🚀 Getting Started

### Prerequisites

- Java Development Kit (JDK) 8 or higher
- Java Swing library (included in JDK)

### Installation

1. Clone the repository:
```bash
git clone https://github.com/yourusername/uno-game.git
cd uno-game
```

2. Compile the project:
```bash
javac -d bin src/**/*.java
```

3. Run the application:
```bash
java -cp bin UnoMain
```

Or simply run the `UnoGameApp` main class:
```bash
java UnoGameApp
```

## 🎯 How to Play

1. **Launch the game** and click "Continue" on the welcome screen
2. **Enter your name** and select an avatar
3. **Choose game mode**:
   - "Create A Game": Select number of players (2-4)
   - "Play With PC": Choose number of AI opponents (1-3)
4. **Play the game**:
   - Click cards in your hand to play them
   - Click the draw pile to draw a card
   - Match cards by color or number
   - Use action cards strategically
   - Don't forget to call "UNO!" when you have one card left

## 🛠️ Technical Implementation

### Custom UI Components

**MyButton**: Enhanced JButton with rounded corners, hover effects, and custom styling

**UICard**: Specialized component for card display with selection animations

**UIContainer**: Flexible layout container with advanced alignment options

**UIImage**: Image component with shadow effects and dynamic resizing

### Visual Effects

- Radial gradients for depth
- Drop shadows for elevation
- Rounded borders for modern aesthetics
- Hover animations for interactivity
- Smooth transitions between screens

### Design Patterns

- **Model-View-Controller (MVC)**: Separation of game logic, UI, and event handling
- **Factory Pattern**: `UIBorderFactory` for creating consistent borders
- **Observer Pattern**: Listener system for card drawing and game events
- **Singleton Pattern**: `UICore` for global style constants

## 👥 Team

- **AKKOUCHI Nesrine**
- **DELHOUM Fatma Zohra Lina**
- **NOUREDDINE Sofia**
- **OUARI Yasmine**


**Supervised by**: MAYATA Raouf

## 📅 Academic Year

2024-2025 | University of Science and Technology Houari Boumediene (USTHB)

## 📄 License

This project was developed for educational purposes as part of an end-of-semester assignment.

## 🙏 Acknowledgments

- Faculty of Computer Science, USTHB
- Professor MAYATA Raouf for supervision and guidance
- The official UNO game for rules and inspiration

## 📸 Screenshots

The project includes several polished screens:
- Welcome screen with UNO branding
- Player setup with avatar selection
- Main menu with multiple options
- In-game interface with all components visible
- Rules display for quick reference

## 🔮 Future Enhancements

Potential improvements for future versions:
- Multiplayer network support
- Enhanced AI difficulty levels
- Sound effects and music
- Animation improvements
- Tournament mode
- Statistics tracking
- Custom card themes

---

**Note**: This README reflects the project state as of May 4, 2025. For questions or contributions, please contact the development team.
