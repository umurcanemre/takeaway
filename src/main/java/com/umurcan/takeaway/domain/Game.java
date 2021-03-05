package com.umurcan.takeaway.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.umurcan.takeaway.enums.GameStatus;
import com.umurcan.takeaway.enums.InputType;
import com.umurcan.takeaway.enums.Move;

import lombok.Getter;
import lombok.val;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public class Game {
	private final int gameId;
	private int gameNumber;
	private List<Integer> playerIds = new ArrayList<>();
	private GameStatus status;
	private int playerInTurnIndex;
	private List<String> gameLog = new ArrayList<>();
	private final InputType inputType;
	
	public Game(int gameId, int firstNumber, Player firstPlayer, InputType inputType) {
		if(firstNumber < 2) {
			throw new IllegalArgumentException("Illegal opening value : " + firstNumber);
		}
		this.gameId = gameId;
		this.inputType = inputType;
		status = GameStatus.WAITING_PLAYERS;
		gameNumber = firstNumber;
		playerIds.add(firstPlayer.getPlayerId());
		playerInTurnIndex = 1; // first player with the index of 0 already played when submitting the first number
		logStatus();
		gameLog.add(getGameStatusText());
	}
	
	public synchronized void addPlayer(Player player) { 
		validatePlayerJoin(player);
		playerIds.add(player.getPlayerId());
		
		//start game
		//TODO : pass dynamic rule for player count
		if(playerIds.size() > 1) {
			playerIds = Collections.unmodifiableList(playerIds);
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
			playerInTurnIndex = playerInTurnIndex + 1 == playerIds.size() ? 0 : playerInTurnIndex + 1;
		}
		logStatus();
	}
	
	public synchronized String getGameInfo () {
		val sb = new StringBuilder().append(getGameStatusText())
				.append(System.lineSeparator());
		
		if(gameNumber == 1) {
			sb.append(getGameWonText());
		} else if (playerIds.size() > playerInTurnIndex){
			sb.append(getPlayerInTurnText());
		}
		
		return sb.toString();
	}
	
	public synchronized int getUserInTurnId() {
		return status == GameStatus.INPROGRESS ? getPlayerIds().get(playerInTurnIndex) : -1 ;
	}

	private String getGameStatusText() {
		return "Game id : " + gameId + " is now in " + status.toString() + " state.";
	}
	private String getGameWonText() {
		return "Player with the id : " + playerIds.get(playerInTurnIndex) + " has won!";
	}
	private String getPlayerInTurnText() {
		return "Player with the id : " + playerIds.get(playerInTurnIndex) + " has the turn";
	}
	private String getMoveMadeText(int playerId, Move moveMade, int previousGameNumber) {
		return "Player id : " + playerId + "added : (" + moveMade.getOperation() + ") to " + previousGameNumber+ ", current gameNumber : " + gameNumber;
	}
	
	private void validatePlayerJoin(Player player) {
		if(playerIds.contains(player.getPlayerId())) {
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
		if(playerIds.size() <= playerInTurnIndex || player.getPlayerId() != playerIds.get(playerInTurnIndex)) {
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
