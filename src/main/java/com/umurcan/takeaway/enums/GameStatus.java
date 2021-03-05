package com.umurcan.takeaway.enums;
import lombok.Getter;

@Getter
public enum GameStatus {
	FINISHED(null), // In reverse order in order to be able to use the last step as the next step of step before
	INPROGRESS (GameStatus.FINISHED),
	WAITING_PLAYERS (GameStatus.INPROGRESS);
	
	private GameStatus(GameStatus nextPhase) {
		this.nextPhase = nextPhase;
	}
	
	GameStatus nextPhase;
}
