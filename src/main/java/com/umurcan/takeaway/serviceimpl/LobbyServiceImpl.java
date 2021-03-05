package com.umurcan.takeaway.serviceimpl;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.stereotype.Service;

import com.umurcan.takeaway.domain.Game;
import com.umurcan.takeaway.domain.Player;
import com.umurcan.takeaway.service.LobbyService;

import lombok.val;

@Service
public class LobbyServiceImpl implements LobbyService {
	private final AtomicInteger gameIdCounter = new AtomicInteger(1);
	private volatile int nextGameToPlacePlayer = 1;
	private final Map<Integer, Game> idGameCache = new ConcurrentHashMap<>();
	
	@Override
	public Game initializeGame(Player player, int firstNumber) {
		val gameId = gameIdCounter.getAndIncrement();
		val game = new Game(gameId, firstNumber, player);
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

}
