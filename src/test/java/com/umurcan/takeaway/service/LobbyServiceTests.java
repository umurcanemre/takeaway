package com.umurcan.takeaway.service;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.verify;

import java.util.NoSuchElementException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationEventPublisher;

import com.umurcan.takeaway.domain.Player;
import com.umurcan.takeaway.enums.InputType;
import com.umurcan.takeaway.event.GameMoveEvent;
import com.umurcan.takeaway.ruleimpl.GameRules;
import com.umurcan.takeaway.serviceimpl.LobbyServiceImpl;

import lombok.val;

@SpringBootTest
public class LobbyServiceTests {
	private LobbyServiceImpl service;
	private ApplicationEventPublisher applicationEventPublisher;
	private GameRules gameRules;
	
	@BeforeEach
	public void init() {
		applicationEventPublisher = Mockito.mock(ApplicationEventPublisher.class);
		gameRules =  Mockito.mock(GameRules.class);
		
		service = new LobbyServiceImpl(applicationEventPublisher, gameRules);
	}

	@Test
	public void initiateGameTest_Auto() {
		val game = service.initializeGame(new Player(1, null), 100, InputType.AUTO);
		
		assertNotNull(game);
		assertEquals(100, game.getGameNumber());
		assertEquals(InputType.AUTO, game.getInputType());
		assertEquals(1, game.getPlayerIds().size());
		assertEquals(1, game.getPlayerIds().get(0));
	}

	@Test
	public void initiateGameTest_Manual() {
		val game = service.initializeGame(new Player(1, null), 100, InputType.MANUAL);
		
		assertNotNull(game);
		assertEquals(100, game.getGameNumber());
		assertEquals(InputType.MANUAL, game.getInputType());
		assertEquals(1, game.getPlayerIds().size());
		assertEquals(1, game.getPlayerIds().get(0));
	}
	
	@Test
	public void getGameById_Success() {
		val gameAuto = service.initializeGame(new Player(1, null), 100, InputType.AUTO);
		val gameManual = service.initializeGame(new Player(1, null), 100, InputType.MANUAL);

		assertSame(gameAuto, service.getGameById(1));
		assertSame(gameManual, service.getGameById(2));
	}

	@Test
	public void getGameById_Fail() {
		assertThatExceptionOfType(NoSuchElementException.class)
			.isThrownBy(() -> { service.getGameById(0); })
			.withMessageContaining("No game with the id ");
	}
	
	@Test
	public void placeToGame_Success() {
		Mockito.when(gameRules.getPlayerByGame()).thenReturn(2);
		
		val gameAuto = service.initializeGame(new Player(1, null), 100, InputType.AUTO);
		val gameManual = service.initializeGame(new Player(1, null), 100, InputType.MANUAL);

		val gameAuto_result = service.placeToGame(new Player(2, null));
		val gameManual_result = service.placeToGame(new Player(2, null));
		
		ArgumentCaptor<GameMoveEvent> event = ArgumentCaptor.forClass(GameMoveEvent.class);
		verify(applicationEventPublisher).publishEvent(event.capture());
		assertSame(gameAuto, event.getValue().getGame());
		
		assertSame(gameAuto, gameAuto_result);
		assertSame(gameManual, gameManual_result);
	}

	
	@Test
	public void placeToGame_SuccessWith3Players() {
		Mockito.when(gameRules.getPlayerByGame()).thenReturn(3);
		
		val gameAuto = service.initializeGame(new Player(1, null), 100, InputType.AUTO);
		val gameManual = service.initializeGame(new Player(1, null), 100, InputType.MANUAL);

		val gameAuto_result = service.placeToGame(new Player(2, null));
		val gameAuto_result_2nd = service.placeToGame(new Player(3, null));
		
		val gameManual_result = service.placeToGame(new Player(2, null));
		val gameManual_result_2nd = service.placeToGame(new Player(3, null));
		
		ArgumentCaptor<GameMoveEvent> event = ArgumentCaptor.forClass(GameMoveEvent.class);
		verify(applicationEventPublisher).publishEvent(event.capture());
		assertSame(gameAuto, event.getValue().getGame());
		
		assertSame(gameAuto, gameAuto_result);
		assertSame(gameManual, gameManual_result);
		
		assertSame(gameAuto, gameAuto_result_2nd);
		assertSame(gameManual, gameManual_result_2nd);
	}
	

	@Test
	public void placeToGame_NoGameInLobby() {
		assertThatExceptionOfType(NoSuchElementException.class)
			.isThrownBy(() -> { service.placeToGame(new Player(1, null)); })
			.withMessageContaining("No joinable game found in lobby.");
	}
}
