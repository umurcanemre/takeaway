package com.umurcan.takeaway.service;

import com.umurcan.takeaway.domain.Game;
import com.umurcan.takeaway.domain.Player;

public interface LobbyService {
	Game initializeGame(Player player, int firstNumber);
	Game placeToGame(Player player);
}
