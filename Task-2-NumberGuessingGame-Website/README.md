# Number Guessing Game — Java Website

Responsive browser version with game logic handled by Java Spring Boot.

## Features
- Random number from 1 to 100
- 7 attempts per round
- Higher/lower hints
- Attempts used and remaining
- Play Again
- Responsive desktop/mobile layout

## Run locally
Requirements: JDK 21 and Maven 3.9+.
```bash
mvn spring-boot:run
```
Open http://localhost:8080

## Build
```bash
mvn clean package
java -jar target/number-guessing-game-1.0.0.jar
```

## Deploy on Render
1. Upload the contents of this folder to a new GitHub repository.
2. Render → New → Web Service → connect the repository.
3. Select Docker runtime; keep Dockerfile at repository root.
4. Deploy. No database or environment variables are required.

## API
- POST `/api/game/new`
- POST `/api/game/guess` with JSON `{"gameId":"...","guess":42}`

Note: the desktop Swing window cannot be shown as a native browser page. This project keeps game logic in Java and provides a responsive web interface.
