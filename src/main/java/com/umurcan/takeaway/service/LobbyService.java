package com.umurcan.takeaway.service;

import com.umurcan.takeaway.domain.Game;
import com.umurcan.takeaway.domain.Player;
import com.umurcan.takeaway.enums.InputType;

public interface LobbyService {
	Game initializeGame(Player player, int firstNumber, InputType inputType);
	Game placeToGame(Player player);
	Game getGameById(int id);
}
