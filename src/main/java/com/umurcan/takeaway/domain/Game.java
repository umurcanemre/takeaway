package com.umurcan.takeaway.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.umurcan.takeaway.enums.GameStatus;
import com.umurcan.takeaway.enums.InputType;
import com.umurcan.takeaway.enums.Move;
import com.umurcan.takeaway.ruleimpl.GameRules;
import com.umurcan.takeaway.util.GameUtils;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.val;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class Game {
	private final int gameId;
	@NonNull
	private int gameNumber;
	private List<Integer> playerIds = new ArrayList<>();
	private GameStatus status = GameStatus.WAITING_PLAYERS;
	private int playerInTurnIndex;
	private List<String> gameLog = new ArrayList<>();
	private final InputType inputType;
	private final GameRules rules;
	
	public static class GameBuilder {
		int gameId;
		int gameNumber;
		int playerId;
		InputType inputType;
		GameRules rules;

		public GameBuilder withGameId(int gameId) {
			this.gameId = gameId;
			return this;
		}
		public GameBuilder withGameNumber(int gameNumber) {
			this.gameNumber = gameNumber;
			return this;
		}
		public GameBuilder withPlayerId(int playerId) {
			this.playerId = playerId;
			return this;
		}
		public GameBuilder withInputType(InputType inputType) {
			this.inputType = inputType;
			return this;
		}
		public GameBuilder withGameRules(GameRules rules) {
			this.rules = rules;
			return this;
		}
		public Game build() {
			if(rules == null) {
				throw new IllegalArgumentException("Rules object is missing");
			}
			if(gameNumber < rules.getMinimumStartNumber()) {                                                               
				throw new IllegalArgumentException("Illegal opening value : " + gameNumber + " cannot be less then " + rules.getMinimumStartNumber());   
			}       
			
			val game = new Game(gameId, gameNumber, inputType, rules);
			game.getPlayerIds().add(playerId);
			game.playerInTurnIndex = 1;// first player with the index of 0 already played when submitting the first number
			game.logGameState();
			return game;
		}
		
	}
	
	/**
	 * Add a  player to players of the game
	 * Once the game has desired amount of players, it's going to mark the game in progress
	 * @param player
	 */
	public synchronized void addPlayer(Player player) { 
		rules.validatePlayerJoin(this, player);
		playerIds.add(player.getPlayerId());
		
		//start game
		if(playerIds.size() == rules.getPlayerByGame()) {
			playerIds = Collections.unmodifiableList(playerIds);
			status = status.getNextPhase();
			logGameState();
			gameLog.add(getGameInfo());
		}
	}
	
	/**
	 *  Makes the move on the game for the given player. Checks if the game is finished, marks the winner if so
	 * @param player
	 * @param move
	 */
	public synchronized void makeMove(Player player, Move move) {
		rules.validateMove(this, player, move);
		
		val previousGameNumber = gameNumber;
		gameNumber = (gameNumber + move.getOperation()) / rules.getGameDivident();
		gameLog.add(GameUtils.getMoveMadeText(player.getPlayerId(), move, previousGameNumber, gameNumber));
		
		//check if game ended
		if(gameNumber == rules.getWinnerNumber()) {
			status = status.getNextPhase();
			gameLog.add(getGameInfo());
		}
		else {
			playerInTurnIndex = playerInTurnIndex + 1 == playerIds.size() ? 0 : playerInTurnIndex + 1;
		}
		logGameState();
	}
	
	
	/**
	 * @returns a text of games current state to be logged/presented
	 */
	public synchronized String getGameInfo () {
		val sb = new StringBuilder().append(GameUtils.getGameStatusText(this))
				.append(System.lineSeparator());
		
		if(gameNumber == 1) {
			sb.append(GameUtils.getGameWonText(this));
		} else if (playerIds.size() > playerInTurnIndex){
			sb.append(GameUtils.getPlayerInTurnText(this));
		}
		
		return sb.toString();
	}
	
	public synchronized int getUserInTurnId() {
		return status == GameStatus.INPROGRESS ? getPlayerIds().get(playerInTurnIndex) : -1 ;
	}
	
	private void logGameState() {
		log.debug(getGameInfo());
	}
}
