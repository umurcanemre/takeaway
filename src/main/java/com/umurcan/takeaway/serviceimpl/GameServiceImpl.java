package com.umurcan.takeaway.serviceimpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import com.umurcan.takeaway.domain.Game;
import com.umurcan.takeaway.enums.GameStatus;
import com.umurcan.takeaway.enums.InputType;
import com.umurcan.takeaway.event.GameMoveEvent;
import com.umurcan.takeaway.service.GameService;
import com.umurcan.takeaway.service.LobbyService;
import com.umurcan.takeaway.service.PlayerService;

import lombok.AllArgsConstructor;
import lombok.val;

@AllArgsConstructor
@Service
public class GameServiceImpl implements GameService{
	@Autowired
	private LobbyService lobbyService;
	@Autowired
	private PlayerService playerService;
	@Autowired
    private ApplicationEventPublisher applicationEventPublisher;
	
	
	/**
	 * Makes a move on the game for given player.
	 * If the game is automatic, will also publish the event to notify any listeners so the player that has the turn can play its turn.
	 */
	@Override
	public Game makeMove(int gameId, int playerId) {
		val game = lobbyService.getGameById(gameId);
		val player = playerService.getPlayerById(playerId);
		val move = player.getStrategy().decideMove(game.getGameNumber());
		
		game.makeMove(player, move);
		
		if(game.getInputType() == InputType.AUTO && game.getStatus() != GameStatus.FINISHED) {
			applicationEventPublisher.publishEvent( new GameMoveEvent(game) );
		}
		
		return game;
	}


	/**
	 * Get the trace log of game in it's current state
	 */
	@Override
	public String getGameLog(int gameId) {
		val game = lobbyService.getGameById(gameId);
		val sb = new StringBuilder();
		game.getGameLog().forEach(l -> sb.append(l).append(System.lineSeparator()));
		
		return sb.toString();
	}

}
