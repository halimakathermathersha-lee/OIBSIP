package com.halima.numberguessing;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.security.SecureRandom;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api/game")
public class GameController {
 private static final int MIN=1, MAX=100, MAX_ATTEMPTS=7;
 private final SecureRandom random=new SecureRandom();
 private final Map<String,GameState> games=new ConcurrentHashMap<>();

 @PostMapping("/new")
 public GameResponse newGame() {
  String id=UUID.randomUUID().toString();
  GameState state=new GameState(random.nextInt(MAX)+MIN);
  games.put(id,state);
  return response(id,state,"I'm thinking of a number between 1 and 100. You have 7 attempts!");
 }
 @PostMapping("/guess")
 public GameResponse guess(@RequestBody GuessRequest request) {
  if(request==null || request.gameId()==null || request.guess()==null)
   throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Game ID and guess are required.");
  GameState s=games.get(request.gameId());
  if(s==null) throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Game not found. Start a new game.");
  synchronized(s) {
   if(s.gameOver) return response(request.gameId(),s,"Game over. Start a new game!");
   int n=request.guess();
   if(n<MIN || n>MAX) return response(request.gameId(),s,"Please enter a number from 1 to 100.");
   s.attempts++;
   String msg;
   if(n==s.secret) { s.won=true; s.gameOver=true; msg="Correct! You guessed the number in "+s.attempts+" attempt(s)."; }
   else if(s.attempts>=MAX_ATTEMPTS) { s.gameOver=true; msg="Game over! The number was "+s.secret+"."; }
   else if(n<s.secret) msg="Too low! Try a higher number.";
   else msg="Too high! Try a lower number.";
   return response(request.gameId(),s,msg);
  }
 }
 private GameResponse response(String id,GameState s,String msg) {
  return new GameResponse(id,msg,s.attempts,MAX_ATTEMPTS-s.attempts,s.won,s.gameOver,MIN,MAX,MAX_ATTEMPTS);
 }
 public record GuessRequest(String gameId,Integer guess) {}
 public record GameResponse(String gameId,String message,int attemptsUsed,int attemptsLeft,boolean won,boolean gameOver,int min,int max,int maxAttempts) {}
 private static class GameState {
  final int secret; int attempts=0; boolean won=false,gameOver=false;
  GameState(int secret){this.secret=secret;}
 }
}
