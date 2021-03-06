package com.umurcan.takeaway.service;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.util.NoSuchElementException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;

import com.umurcan.takeaway.serviceimpl.PlayerServiceImpl;
import com.umurcan.takeaway.strategy.GameStrategy;
import com.umurcan.takeaway.strategyimpl.GameStrategyImpl;

import lombok.val;

@SpringBootTest
public class PlayerServiceTests {

	PlayerServiceImpl service;
	GameStrategy strategy = Mockito.mock(GameStrategyImpl.class);

	@BeforeEach
	public void init() {
		service = new PlayerServiceImpl(strategy);
	}

	@Test
	public void registerPlayerTest() {
		for (int i = 1; i < 10; i++) {
			val player = service.registerPlayer();
			assertNotNull(player);
			assertEquals(player.getPlayerId(), i);
			assertEquals(player.getStrategy(), strategy);
		}
	}

	@Test
	public void getPlayerByIdTest_Success() {
		for (int i = 1; i < 10; i++) {
			val registeredPlayer = service.registerPlayer();

			val fetchedPlayer = service.getPlayerById(i);

			assertNotNull(fetchedPlayer);
			assertSame(registeredPlayer, fetchedPlayer);
		}
	}

	@Test
	public void getPlayerByIdTest_NoneFound() {
		assertThatExceptionOfType(NoSuchElementException.class)
			.isThrownBy(() -> { service.getPlayerById(0); })
			.withMessageContaining("No player found");
	}
}
