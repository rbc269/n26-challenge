package com.n26.module.service.impl;

import com.n26.module.service.StatisticService;
import com.n26.module.service.TransactionService;
import com.n26.module.util.CommonUtil;
import com.n26.module.bo.TransactionBo;
import com.n26.module.exception.ExpiredTransactionException;
import com.n26.module.exception.FutureTransactionException;
import com.n26.module.json.TransactionParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class TransactionServiceImpl implements TransactionService {
	@Autowired
	private StatisticService statisticService;

	@Override
	public ResponseEntity<TransactionBo> process(TransactionParam json) {
		TransactionBo transaction = new TransactionBo();
		transaction.setAmount(json.getAmount());
		transaction.setDate(CommonUtil.convertToLocalDateTime(json.getTimestamp()));
		try {
			statisticService.insert(transaction);
		} catch (ExpiredTransactionException | FutureTransactionException e) {
			return new ResponseEntity<TransactionBo>(HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<TransactionBo>(transaction, HttpStatus.CREATED);
	}
}