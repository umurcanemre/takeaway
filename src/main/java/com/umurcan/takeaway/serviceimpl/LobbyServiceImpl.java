package com.umurcan.takeaway.serviceimpl;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import com.umurcan.takeaway.domain.Game;
import com.umurcan.takeaway.domain.Player;
import com.umurcan.takeaway.enums.InputType;
import com.umurcan.takeaway.event.GameMoveEvent;
import com.umurcan.takeaway.service.LobbyService;
import com.umurcan.takeaway.ruleimpl.GameRules;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.val;

@RequiredArgsConstructor
@Service
public class LobbyServiceImpl implements LobbyService {
	@Autowired
	@NonNull
	private ApplicationEventPublisher applicationEventPublisher;
	@Autowired
	@NonNull
	private GameRules gameRules;

	private final AtomicInteger gameIdCounter = new AtomicInteger(1);
	private volatile int nextGameToPlacePlayer = 1;
	private final Map<Integer, Game> idGameCache = new ConcurrentHashMap<>();

	
	/**
	 * Create a game in the lobby and start waiting for players to join
	 * InputType shows whether game will be played manually via api endpoints or automatically
	 */
	@Override
	public Game initializeGame(Player player, int firstNumber, InputType inputType) {
		val gameId = gameIdCounter.getAndIncrement();
		val game = new Game.GameBuilder()
				.withGameId(gameId)
				.withGameNumber(firstNumber)
				.withInputType(inputType)
				.withPlayerId(player.getPlayerId())
				.withGameRules(gameRules)
				.build();

		idGameCache.put(gameId, game);
		return game;
	}

	/**
	 * Places a given player to a game that's waiting for players to join
	 * If the game is automatic and has all the players it needs, it also publishes an event the kick-off the automatic playing flow
	 */
	@Override
	public Game placeToGame(Player player) {
		for (int i = nextGameToPlacePlayer; i < gameIdCounter.get(); i++) {
			try {
				val game = idGameCache.get(i);
				if (game != null && game.getPlayerIds().size() < gameRules.getPlayerByGame()) {
					game.addPlayer(player);

					if (game.getPlayerIds().size() == gameRules.getPlayerByGame()) {
						nextGameToPlacePlayer++;
						if (game.getInputType() == InputType.AUTO) {
							applicationEventPublisher.publishEvent(new GameMoveEvent(game));
						}
					}
					return game;
				}
			} catch (IllegalArgumentException e) {
				// due to a possible race in requests, chosen game has started before the player could be placed.
				// Checking next game
			}
		}

		throw new NoSuchElementException("No joinable game found in lobby. Try again later or start a new game");
	}

	/**
	 * Retrieve a game by id
	 */
	@Override
	public Game getGameById(int id) {
		val game = idGameCache.get(id);
		if (game != null) {
			return game;
		} else {
			throw new NoSuchElementException("No game with the id of " + id + " found.");
		}
	}

}
