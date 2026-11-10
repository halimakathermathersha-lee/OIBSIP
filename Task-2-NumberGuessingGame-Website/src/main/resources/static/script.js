const form = document.getElementById("guessForm");
const input = document.getElementById("guessInput");
const guessButton = document.getElementById("guessButton");
const playAgain = document.getElementById("playAgain");
const message = document.getElementById("message");
const attemptsLeft = document.getElementById("attemptsLeft");
const attemptsUsed = document.getElementById("attemptsUsed");
const roundSummary = document.getElementById("roundSummary");

let gameId = null;
let gameOver = false;

let roundCount = 0;
let roundHistory = [];
let roundRecorded = false;

async function api(path, body) {
    const r = await fetch(`/api/game/${path}`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(body || {})
    });

    const d = await r.json();

    if (!r.ok) {
        throw new Error(d.message || "Something went wrong.");
    }

    return d;
}

function show(d) {
    message.textContent = d.message;
    message.className = "message" +
        (d.won ? " success" : d.gameOver ? " error" : "");

    attemptsLeft.textContent = d.attemptsLeft;
    attemptsUsed.textContent = d.attemptsUsed;

    gameOver = d.gameOver;

    guessButton.disabled = gameOver;
    input.disabled = gameOver;

    document.querySelector(".number-orb span").textContent =
        d.won ? "✓" : "?";

    // Save completed round
    if (d.gameOver && !roundRecorded) {
        saveRound(d);
    }
}

function saveRound(d) {
    roundRecorded = true;
    roundCount++;

    const result = {
        round: roundCount,
        attempts: d.attemptsUsed,
        won: d.won
    };

    roundHistory.push(result);
    displayHistory();
}

function displayHistory() {
    roundSummary.innerHTML = "";

    roundHistory.forEach(item => {
        const p = document.createElement("p");

        if (item.won) {
            p.textContent =
                `Round ${item.round} — Guessed in ${item.attempts} attempts ✓`;
        } else {
            p.textContent =
                `Round ${item.round} — Couldn't guess in 7 attempts ✗`;
        }

        roundSummary.appendChild(p);
    });
}

async function startGame() {
    try {
        const d = await api("new");

        gameId = d.gameId;
        input.value = "";
        input.disabled = false;
        guessButton.disabled = false;

        roundRecorded = false;

        show(d);
        input.focus();

    } catch (e) {
        message.textContent = e.message;
        message.className = "message error";
    }
}

form.addEventListener("submit", async e => {
    e.preventDefault();

    if (gameOver || !gameId) return;

    const n = Number(input.value);

    if (!Number.isInteger(n) || n < 1 || n > 100) {
        message.textContent =
            "Please enter a whole number between 1 and 100.";
        message.className = "message error";
        input.focus();
        return;
    }

    guessButton.disabled = true;

    try {
        const d = await api("guess", {
            gameId,
            guess: n
        });

        show(d);
        input.value = "";

        if (!gameOver) {
            input.focus();
        }

    } catch (err) {
        message.textContent = err.message;
        message.className = "message error";

    } finally {
        if (!gameOver) {
            guessButton.disabled = false;
        }
    }
});

playAgain.addEventListener("click", startGame);

startGame();