package com.umurcan.takeaway.service;

import com.umurcan.takeaway.domain.Player;

public interface PlayerService {
	Player registerPlayer();
	Player getPlayerById(int id);
}
