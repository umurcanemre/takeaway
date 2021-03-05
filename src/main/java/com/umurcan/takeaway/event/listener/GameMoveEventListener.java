package com.umurcan.takeaway.event.listener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import com.umurcan.takeaway.event.GameMoveEvent;
import com.umurcan.takeaway.service.GameService;

import lombok.val;

@Component
public class GameMoveEventListener implements ApplicationListener<GameMoveEvent> {
	@Autowired
	private GameService gameService;
	
	@Override
	public void onApplicationEvent(GameMoveEvent event) {
		val game = event.getGame();
		
		gameService.makeMove(game.getGameId(), game.getUserInTurnId());
	}

}
