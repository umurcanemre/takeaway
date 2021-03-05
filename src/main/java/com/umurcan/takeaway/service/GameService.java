package com.umurcan.takeaway.service;

public interface GameService {
	String makeMove(int gameId, int playerId);
	String getGameLog(int gameId);
}
