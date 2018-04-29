package com.n26.module.service;

import org.springframework.http.ResponseEntity;

import com.n26.module.bo.TransactionBo;
import com.n26.module.json.TransactionParam;

public interface TransactionService {
	public ResponseEntity<TransactionBo> process(TransactionParam json);
}