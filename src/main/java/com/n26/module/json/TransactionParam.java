package com.n26.module.json;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

public class TransactionParam {
	@NotNull
	@Min(0)
	private Double amount;

	@NotNull
	@Min(0)
	private Long timestamp;

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public Long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
	}
}