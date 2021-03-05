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

import lombok.val;

@Service
public class LobbyServiceImpl implements LobbyService {
	@Autowired
    private ApplicationEventPublisher applicationEventPublisher;
	
	private final AtomicInteger gameIdCounter = new AtomicInteger(1);
	private volatile int nextGameToPlacePlayer = 1;
	private final Map<Integer, Game> idGameCache = new ConcurrentHashMap<>();
	
	@Override
	public Game initializeGame(Player player, int firstNumber, InputType inputType) {
		val gameId = gameIdCounter.getAndIncrement();
		val game = new Game(gameId, firstNumber, player, inputType);
		idGameCache.put(gameId, game);
		return game;
	}

	@Override
	public Game placeToGame(Player player) {
		for(int i = nextGameToPlacePlayer; i < gameIdCounter.get(); i++) {
			try {
				val game = idGameCache.get(i);
				if(game != null) {
					game.addPlayer(player);
					
					if(game.getInputType() == InputType.AUTO) {
						applicationEventPublisher.publishEvent(new GameMoveEvent(game));
					}
					
					return game;
				}
			}
			catch (IllegalArgumentException e) {
				// due to a possible race in requests, this game has started before the player could be placed.
				// Checking next game
			}
		}
		
		throw new NoSuchElementException("No joinable game found in lobby. Try again later or start a new game");
	}

	@Override
	public Game getGameById(int id) {
		val game = idGameCache.get(id);
		if(game != null) {
			return game;
		} else {
			throw new NoSuchElementException();
		}
	}

}
