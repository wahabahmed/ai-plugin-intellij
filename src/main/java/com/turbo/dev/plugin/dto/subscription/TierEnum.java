package com.turbo.dev.plugin.dto.subscription;

public enum TierEnum {
	FREE(0, 100000L), BASIC(1500, 25000000L), PREMIUM(2500, 50000000L), ENTERPRISE(4500, -1);

	private long price;
	private long tokens;

	TierEnum(int price, long tokens) {
		this.price = price;
		this.tokens = tokens;
	}

	public long getTokens() {
		return this.tokens;
	}

	public long getPrice() {
		return this.price;
	}
}
