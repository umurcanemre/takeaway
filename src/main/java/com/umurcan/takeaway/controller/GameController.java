package com.umurcan.takeaway.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.umurcan.takeaway.service.GameService;

@RestController
@RequestMapping("/play")
public class GameController {
	@Autowired
	private GameService gameService;

	@PostMapping("/makemove")
	public String makeMove(@RequestParam int playerId, @RequestParam int gameId) {
		return gameService.makeMove(gameId, playerId).getGameInfo();
	}
	
	@GetMapping
	public String getGameLog(@RequestParam int gameId) {
		return gameService.getGameLog(gameId);
	}
}
