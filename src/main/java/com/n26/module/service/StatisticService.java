package com.n26.module.service;

import java.util.List;

import com.n26.module.bo.StatisticBo;
import com.n26.module.bo.TransactionBo;
import org.springframework.http.ResponseEntity;

public interface StatisticService {
	public void insert(TransactionBo transaction);
	public ResponseEntity<StatisticBo> getCurrentStatistic();
	public ResponseEntity<List<StatisticBo>> getAllStatistics();
}