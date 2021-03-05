package com.umurcan.takeaway.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.umurcan.takeaway.enums.GameStatus;
import com.umurcan.takeaway.enums.Move;

import lombok.Getter;
import lombok.val;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public class Game {
	private final int gameId;
	private int gameNumber;
	private List<Player> players = new ArrayList<>();
	private GameStatus status;
	private int playerInTurnIndex;
	private List<String> gameLog = new ArrayList<>();
	
	public Game(int gameId, int firstNumber, Player firstPlayer) {
		if(firstNumber < 2) {
			throw new IllegalArgumentException("Illegal opening value : " + firstNumber);
		}
		this.gameId = gameId;
		status = GameStatus.WAITING_PLAYERS;
		gameNumber = firstNumber;
		players.add(firstPlayer);
		playerInTurnIndex = 1; // first player with the index of 0 already played when submitting the first number
		logStatus();
		gameLog.add(getGameStatusText());
	}
	
	public synchronized void addPlayer(Player player) { 
		validatePlayerJoin(player);
		players.add(player);
		
		//start game
		//TODO : pass dynamic rule for player count
		if(players.size() > 1) {
			players = Collections.unmodifiableList(players);
			status = status.getNextPhase();
			logStatus();
			gameLog.add(getGameInfo());
		}
	}
	
	public synchronized void makeMove(Player player, Move move) {
		validateMove(player, move);
		
		val previousGameNumber = gameNumber;
		gameNumber = (gameNumber + move.getOperation()) / 3;
		gameLog.add(getMoveMadeText(player.getPlayerId(), move, previousGameNumber));
		
		//check if game ended
		if(gameNumber == 1) {
			status = status.getNextPhase();
			gameLog.add(getGameInfo());
		}
		else {
			playerInTurnIndex = playerInTurnIndex + 1 == players.size() ? 0 : playerInTurnIndex + 1;
		}
		logStatus();
	}
	
	public synchronized String getGameInfo () {
		val sb = new StringBuilder().append(getGameStatusText())
				.append(System.lineSeparator());
		
		if(gameNumber == 1) {
			sb.append(getGameWonText());
		} else if (players.size() > playerInTurnIndex){
			sb.append(getPlayerInTurnText());
		}
		
		return sb.toString();
	}

	private String getGameStatusText() {
		return "Game id : " + gameId + " is now in " + status.toString() + " state.";
	}
	private String getGameWonText() {
		return "Player with the id : " + players.get(playerInTurnIndex).getPlayerId() + " has won!";
	}
	private String getPlayerInTurnText() {
		return "Player with the id : " + players.get(playerInTurnIndex).getPlayerId() + " has the turn";
	}
	private String getMoveMadeText(int playerId, Move moveMade, int previousGameNumber) {
		return "Player id : " + playerId + "added : (" + moveMade.getOperation() + ") to " + previousGameNumber+ ", current gameNumber : " + gameNumber;
	}
	
	private void validatePlayerJoin(Player player) {
		if(players.contains(player)) {
			throw new IllegalArgumentException("Given user is already a player for this game");
		}
		if(!status.equals(GameStatus.WAITING_PLAYERS)) {
			throw new IllegalArgumentException("Game already started, cannot join");
		}
	}
	
	private void validateMove(Player player, Move move) {
		if(this.status == GameStatus.FINISHED) { 
			throw new IllegalStateException("Game already ended. Cannot make any more moves.");
		}
		if(players.size() <= playerInTurnIndex || !player.equals(players.get(playerInTurnIndex))) {
			throw new IllegalArgumentException("The turn is on a different player");
		}
		if((gameNumber + move.getOperation()) % 3 != 0) {
			throw new IllegalArgumentException("Illegal move. The result is not divisible by 3");
		}
	}
	
	private void logStatus() {
		log.trace(getGameInfo());
	}
}
