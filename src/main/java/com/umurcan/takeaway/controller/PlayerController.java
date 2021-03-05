package com.umurcan.takeaway.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.umurcan.takeaway.domain.Player;
import com.umurcan.takeaway.service.PlayerService;

@RestController
@RequestMapping("/players")
public class PlayerController {
	@Autowired
	PlayerService service;
	
	@PostMapping
	public Player createPlayer() {
		return service.registerPlayer();
	}
	
	@GetMapping("/{id}")
	public Player getPlayerById(@PathVariable int id) {
		return service.getPlayerById(id);
	}
}
