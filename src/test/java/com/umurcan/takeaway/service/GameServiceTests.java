package com.umurcan.takeaway.service;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationEventPublisher;

import com.umurcan.takeaway.domain.Game;
import com.umurcan.takeaway.domain.Player;
import com.umurcan.takeaway.enums.GameStatus;
import com.umurcan.takeaway.enums.InputType;
import com.umurcan.takeaway.enums.Move;
import com.umurcan.takeaway.event.GameMoveEvent;
import com.umurcan.takeaway.serviceimpl.GameServiceImpl;
import com.umurcan.takeaway.serviceimpl.LobbyServiceImpl;
import com.umurcan.takeaway.serviceimpl.PlayerServiceImpl;
import com.umurcan.takeaway.strategy.GameStrategy;
import com.umurcan.takeaway.strategyimpl.GameStrategyImpl;

import lombok.val;

@SpringBootTest
public class GameServiceTests {

	private GameServiceImpl service;
	private LobbyService lobbyService;
	private PlayerService playerService;
	private ApplicationEventPublisher applicationEventPublisher;
	private GameStrategy strategy;
	private Game gameInstance;
	private Player playerInstance;
	
	@BeforeEach
	public void init() {
		lobbyService = Mockito.mock(LobbyServiceImpl.class);
		playerService = Mockito.mock(PlayerServiceImpl.class);
		applicationEventPublisher = Mockito.mock(ApplicationEventPublisher.class);
		strategy = Mockito.mock(GameStrategyImpl.class);
		gameInstance = Mockito.mock(Game.class);
		playerInstance = Mockito.mock(Player.class);
		
		service = new GameServiceImpl(lobbyService, playerService, applicationEventPublisher);
	}
	
	@Test
	public void makeMove_Manual() {
		val gameId = 1;
		val playerId = 2;

		when(lobbyService.getGameById(gameId)).thenReturn(gameInstance);
		when(playerService.getPlayerById(playerId)).thenReturn(playerInstance);
		when(playerInstance.getStrategy()).thenReturn(strategy);
		when(gameInstance.getGameNumber()).thenReturn(9);
		when(gameInstance.getInputType()).thenReturn(InputType.MANUAL);
		when(strategy.decideMove(9)).thenReturn(Move.AS_IS);
		
		val response = service.makeMove(gameId, playerId);

		verify(lobbyService, times(1)).getGameById(gameId);
		verify(playerService, times(1)).getPlayerById(playerId);
		verify(gameInstance, times(1)).makeMove(playerInstance, Move.AS_IS);
		verifyNoInteractions(applicationEventPublisher);

		assertSame(gameInstance, response);
	}
	
	@Test
	public void makeMove_Auto() {
		val gameId = 1;
		val playerId = 2;

		when(lobbyService.getGameById(gameId)).thenReturn(gameInstance);
		when(playerService.getPlayerById(playerId)).thenReturn(playerInstance);
		when(playerInstance.getStrategy()).thenReturn(strategy);
		when(gameInstance.getGameNumber()).thenReturn(9);
		when(gameInstance.getInputType()).thenReturn(InputType.AUTO);
		when(strategy.decideMove(9)).thenReturn(Move.AS_IS);
		
		val response = service.makeMove(gameId, playerId);

		verify(lobbyService, times(1)).getGameById(gameId);
		verify(playerService, times(1)).getPlayerById(playerId);
		verify(gameInstance, times(1)).makeMove(playerInstance, Move.AS_IS);
		
		ArgumentCaptor<GameMoveEvent> event = ArgumentCaptor.forClass(GameMoveEvent.class);
		verify(applicationEventPublisher).publishEvent(event.capture());
		assertSame(gameInstance, event.getValue().getGame());
		
		assertSame(gameInstance, response);
	}

	
	@Test
	public void makeMove_autoFinishMove() {
		val gameId = 1;
		val playerId = 2;
		
		when(lobbyService.getGameById(gameId)).thenReturn(gameInstance);
		when(playerService.getPlayerById(playerId)).thenReturn(playerInstance);
		when(playerInstance.getStrategy()).thenReturn(strategy);
		when(gameInstance.getGameNumber()).thenReturn(9);
		when(gameInstance.getInputType()).thenReturn(InputType.AUTO);
		when(gameInstance.getStatus()).thenReturn(GameStatus.FINISHED);
		when(strategy.decideMove(9)).thenReturn(Move.AS_IS);
		
		val response = service.makeMove(gameId, playerId);

		verify(lobbyService, times(1)).getGameById(gameId);
		verify(playerService, times(1)).getPlayerById(playerId);
		verify(gameInstance, times(1)).makeMove(playerInstance, Move.AS_IS);
		verifyNoInteractions(applicationEventPublisher);

		assertSame(gameInstance, response);
	}
}
