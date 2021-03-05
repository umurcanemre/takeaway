package com.umurcan.takeaway.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.umurcan.takeaway.domain.Game;
import com.umurcan.takeaway.enums.InputType;
import com.umurcan.takeaway.service.LobbyService;
import com.umurcan.takeaway.service.PlayerService;

import lombok.val;

@RestController
@RequestMapping("/lobby")
public class LobbyController {
	@Autowired
	private LobbyService service;
	@Autowired
	private PlayerService playerService;
	
	@PostMapping("/new")
	public Game initiateGame(@RequestParam int playerId, @RequestParam int startNumber, @RequestParam InputType inputType) {
		val player = playerService.getPlayerById(playerId);
		return service.initializeGame(player, startNumber, inputType);
	}
	
	@PostMapping("/join")
	public Game joinGame(@RequestParam int playerId) {
		val player = playerService.getPlayerById(playerId);
		return service.placeToGame(player);
	}
	
}
