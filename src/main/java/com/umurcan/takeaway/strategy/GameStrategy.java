package com.umurcan.takeaway.strategy;

import com.umurcan.takeaway.enums.Move;

public interface GameStrategy {
	Move decideMove(int gameNumber);
}
