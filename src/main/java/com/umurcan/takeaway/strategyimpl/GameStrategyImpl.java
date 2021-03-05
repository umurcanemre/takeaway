package com.umurcan.takeaway.strategyimpl;

import java.util.NoSuchElementException;

import org.springframework.stereotype.Service;

import com.umurcan.takeaway.enums.Move;
import com.umurcan.takeaway.strategy.GameStrategy;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class GameStrategyImpl implements GameStrategy {

	@Override
	public Move decideMove(int gameNumber) {
		for(Move move : Move.values()) { 
			if ( (gameNumber + move.getOperation() % 3) == 0) {
				return move;
			}
		}
		
		log.error("UNHANDLED/UNEXPECTED CASE -- FATAL");
		throw new NoSuchElementException("Couldn't decide on move");
	}

}
