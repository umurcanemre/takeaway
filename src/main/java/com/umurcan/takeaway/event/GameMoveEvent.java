package com.umurcan.takeaway.event;

import org.springframework.context.ApplicationEvent;

import com.umurcan.takeaway.domain.Game;

public class GameMoveEvent extends ApplicationEvent {
	private static final long serialVersionUID = 8477033942906152041L;
	
	public GameMoveEvent(Game source) {
		super(source);
	}
	
	public Game getGame() {
		return (Game)source;
	}
}
