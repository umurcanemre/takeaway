package com.umurcan.takeaway.ruleimpl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.umurcan.takeaway.domain.Game;
import com.umurcan.takeaway.domain.Player;
import com.umurcan.takeaway.enums.GameStatus;
import com.umurcan.takeaway.enums.Move;

import lombok.Getter;

@Component
@Getter
public class GameRules {
	private final int playerByGame;
	private final int gameDivident;
	private final int winnerNumber;
	private final int minimumStartNumber;

	public GameRules(@Value("${game.rule.playercount}") int playerByGame,
			@Value("${game.rule.divident}") int gameDivident, 
			@Value("${game.rule.winnerNumber}") int winnerNumber,
			@Value("${game.rule.minimumStartNumber}") int minimumStartNumber) {
		super();
		this.playerByGame = playerByGame;
		this.gameDivident = gameDivident;
		this.winnerNumber = winnerNumber;
		this.minimumStartNumber = minimumStartNumber;
	}

	public void validatePlayerJoin(Game game, Player player) {
		if (game.getPlayerIds().contains(player.getPlayerId())) {
			throw new IllegalArgumentException("Given user is already a player for this game");
		}
		if (!game.getStatus().equals(GameStatus.WAITING_PLAYERS)) {
			throw new IllegalArgumentException("Game already started, cannot join");
		}
	}

	public void validateMove(Game game, Player player, Move move) {
		if (game.getStatus() == GameStatus.FINISHED) {
			throw new IllegalStateException("Game already ended. Cannot make any more moves.");
		}
		if (game.getPlayerIds().size() <= game.getPlayerInTurnIndex()
				|| player.getPlayerId() != game.getPlayerIds().get(game.getPlayerInTurnIndex())) {
			throw new IllegalArgumentException("The turn is on a different player");
		}
		if ((game.getGameNumber() + move.getOperation()) % gameDivident != 0) {
			throw new IllegalArgumentException("Illegal move. The result is not divisible by " + gameDivident);
		}
	}
}
