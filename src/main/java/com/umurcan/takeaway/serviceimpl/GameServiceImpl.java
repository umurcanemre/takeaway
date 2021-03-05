package com.umurcan.takeaway.serviceimpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
	
	
	@Override
	public String makeMove(int gameId, int playerId) {
		val game = lobbyService.getGameById(gameId);
		val player = playerService.getPlayerById(playerId);
		
		game.makeMove(player, player.getStrategy().decideMove(game.getGameNumber())); //TODO: refactor
		
		return game.getGameInfo();
	}


	@Override
	public String getGameLog(int gameId) {
		val game = lobbyService.getGameById(gameId);
		val sb = new StringBuilder();
		game.getGameLog().forEach(l -> sb.append(l).append(System.lineSeparator()));
		
		return sb.toString();
	}

}
