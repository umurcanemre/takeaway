package com.umurcan.takeaway.enums;

import lombok.Getter;

@Getter
public enum Move {
	DECREMENT(-1),
	AS_IS(0),
	INCREMENT(1);
	
	int operation;
	
	private Move(int operation) {
		this.operation = operation;
	}
}
