package com.umurcan.takeaway.serviceimpl;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.umurcan.takeaway.domain.Player;
import com.umurcan.takeaway.service.PlayerService;
import com.umurcan.takeaway.strategy.GameStrategy;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.val;

@Service
@RequiredArgsConstructor
public class PlayerServiceImpl implements PlayerService {
	private final AtomicInteger playerIdCounter = new AtomicInteger(1);
	private final Map<Integer,Player> idPlayerCache = new ConcurrentHashMap<>();
	
	@Autowired @NonNull
	private GameStrategy gameStrategy;

	/**
	 * Create a player and register to players
	 */
	@Override
	public Player registerPlayer() {
		val player = new Player(playerIdCounter.getAndIncrement(), gameStrategy);
		idPlayerCache.put(player.getPlayerId(), player);
		
		return player;
	}

	/**
	 * Retrieve a player by it's id
	 */
	@Override
	public Player getPlayerById(int id) {
		val player = idPlayerCache.get(id);
		if(player != null) {
			return player;
		} else {
			throw new NoSuchElementException("No player found by the id : " + id);
		}
	}

}
