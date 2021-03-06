package com.umurcan.takeaway.domain;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.umurcan.takeaway.enums.GameStatus;
import com.umurcan.takeaway.enums.InputType;
import com.umurcan.takeaway.enums.Move;
import com.umurcan.takeaway.ruleimpl.GameRules;

import lombok.val;

@SpringBootTest
public class GameTest {
	private Game game;
	private GameRules rules;

	@BeforeEach
	public void init() {
		rules = mock(GameRules.class);

		game = new Game.GameBuilder()
				.withGameId(1).
				withGameNumber(56)
				.withGameRules(rules)
				.withInputType(InputType.AUTO)
				.build();
	}

	@Test
	public void gameBuilderTest_Fail_missingRule() {

		assertThatExceptionOfType(IllegalArgumentException.class)
			.isThrownBy(() -> {
				new Game.GameBuilder()
				.withGameId(1)
				.withGameNumber(56)
				.withGameRules(null)
				.withInputType(InputType.AUTO)
				.build();
			})
			.withMessage("Rules object is missing");
	}

	@Test
	public void gameBuilderTest_Fail() {
		when(rules.getMinimumStartNumber()).thenReturn(2);
		
		assertThatExceptionOfType(IllegalArgumentException.class)
			.isThrownBy(() -> {
				new Game.GameBuilder()
				.withGameId(1)
				.withGameNumber(1)
				.withGameRules(rules)
				.withInputType(InputType.AUTO)
				.build();
			})
			.withMessageContaining("Illegal opening value ");
	}
	
	@Test
	public void addPlayerTest() {
		when(rules.getPlayerByGame()).thenReturn(2);
		val addedPlayerId = 2;
		val player = new Player(addedPlayerId, null);
		
		game.addPlayer(player);

		verify(rules,times(1)).validatePlayerJoin(game, player);
		assertEquals(2, game.getPlayerIds().size());
		assertEquals(addedPlayerId, game.getPlayerIds().get(game.getPlayerInTurnIndex()));
		assertEquals(GameStatus.INPROGRESS, game.getStatus());
		
		assertThatExceptionOfType(UnsupportedOperationException.class)
			.isThrownBy(() -> { game.addPlayer(new Player(-1, null)); });
	}
	
	@Test
	public void addPlayerTest_3playerGame() {
		when(rules.getPlayerByGame()).thenReturn(3);
		val addedPlayerId = 2;
		
		val addedPlayer1 = new Player(addedPlayerId, null);
		game.addPlayer(addedPlayer1);

		verify(rules,times(1)).validatePlayerJoin(game, addedPlayer1);
		assertEquals(2, game.getPlayerIds().size());
		assertEquals(addedPlayerId, game.getPlayerIds().get(game.getPlayerInTurnIndex()));
		assertEquals(GameStatus.WAITING_PLAYERS, game.getStatus());

		val addedPlayer2 = new Player(addedPlayerId +1 , null);
		game.addPlayer(addedPlayer2);

		verify(rules,times(1)).validatePlayerJoin(game, addedPlayer2);
		assertEquals(3, game.getPlayerIds().size());
		assertEquals(addedPlayerId, game.getPlayerIds().get(game.getPlayerInTurnIndex()));
		assertEquals(addedPlayerId + 1, game.getPlayerIds().get(game.getPlayerInTurnIndex() + 1));
		assertEquals(GameStatus.INPROGRESS, game.getStatus());
		
		assertThatExceptionOfType(UnsupportedOperationException.class)
			.isThrownBy(() -> { game.addPlayer(new Player(-1, null)); });
	}
	
	@Test
	public void makeMoveTest() {
		when(rules.getPlayerByGame()).thenReturn(2);
		when(rules.getGameDivident()).thenReturn(3);
		when(rules.getWinnerNumber()).thenReturn(1);
		val player1 = new Player(1, null);
		val player2 = new Player(2, null);
		game.addPlayer( player2 );

		game.makeMove(player2, Move.INCREMENT);
		assertEquals(19, game.getGameNumber());
		assertEquals(GameStatus.INPROGRESS, game.getStatus());
		assertEquals(0, game.getPlayerInTurnIndex());
		
		game.makeMove(player1, Move.DECREMENT);
		assertEquals(6, game.getGameNumber());
		assertEquals(GameStatus.INPROGRESS, game.getStatus());
		assertEquals(1, game.getPlayerInTurnIndex());

		game.makeMove(player2, Move.AS_IS);
		assertEquals(2, game.getGameNumber());
		assertEquals(GameStatus.INPROGRESS, game.getStatus());
		assertEquals(0, game.getPlayerInTurnIndex());
		
		game.makeMove(player1, Move.INCREMENT);
		assertEquals(1, game.getGameNumber());
		assertEquals(GameStatus.FINISHED, game.getStatus());
		assertEquals(0, game.getPlayerInTurnIndex());
	}
}
