package com.umurcan.takeaway.domain;

import com.umurcan.takeaway.strategy.GameStrategy;

import lombok.Data;

@Data
public class Player {
	private final int playerId;
	private final GameStrategy strategy;
}
