package com.umurcan.takeaway.strategy;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.NoSuchElementException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.umurcan.takeaway.enums.Move;
import com.umurcan.takeaway.strategyimpl.GameStrategyImpl;

@SpringBootTest
public class GameStrategyTest {
	private GameStrategyImpl strategy;
	
	@BeforeEach
	public void init() {
		strategy = new GameStrategyImpl(3);
	}

	@Test
	public void decideMoveTest() {
		assertEquals(Move.AS_IS, strategy.decideMove(9));
		assertEquals(Move.INCREMENT, strategy.decideMove(8));
		assertEquals(Move.DECREMENT, strategy.decideMove(10));
	}
	@Test
	public void decideMoveTest_fail() {
		strategy = new GameStrategyImpl(4);
		
		assertThatExceptionOfType(NoSuchElementException.class)
			.isThrownBy(() -> { strategy.decideMove(6); })
			.withMessage("Couldn't decide on move");
	}
}
