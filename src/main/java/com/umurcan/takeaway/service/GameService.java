package com.umurcan.takeaway.service;

import com.umurcan.takeaway.domain.Game;

public interface GameService {
	Game makeMove(int gameId, int playerId);
	String getGameLog(int gameId);
}
