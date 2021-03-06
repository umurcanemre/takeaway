package com.umurcan.takeaway.util;

import com.umurcan.takeaway.domain.Game;
import com.umurcan.takeaway.enums.Move;

public abstract class GameUtils {

	public static String getGameStatusText(Game game) {
		return "Game id : " + game.getGameId() + " is now in " + game.getStatus().toString() + " state.";
	}
	public static String getGameWonText(Game game) {
		return "Player with the id : " + game.getPlayerIds().get(game.getPlayerInTurnIndex()) + " has won!";
	}
	public static String getPlayerInTurnText(Game game) {
		return "Player with the id : " + game.getPlayerIds().get(game.getPlayerInTurnIndex()) + " has the turn";
	}
	public static String getMoveMadeText(int playerId, Move moveMade, int previousGameNumber, int gameNumber) {
		return "Player id : " + playerId + " added : (" + moveMade.getOperation() + ") to " + previousGameNumber+ ", current gameNumber : " + gameNumber;
	}
	
}
