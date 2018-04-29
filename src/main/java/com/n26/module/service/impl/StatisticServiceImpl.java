package com.n26.module.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.PriorityBlockingQueue;

import com.n26.module.service.StatisticService;
import com.n26.module.util.CommonUtil;
import com.n26.module.exception.ExpiredTransactionException;
import com.n26.module.exception.FutureTransactionException;
import com.n26.module.bo.StatisticBo;
import com.n26.module.bo.TransactionBo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class StatisticServiceImpl implements StatisticService {
	private Object StatsObj = new Object();
	private Map<Long, StatisticBo> statisticHistory;
	private Queue<Long> statisticTimestamps;
	@Value("${statisticService.expireMS}")
	private Long expirationMilliSec;

	public StatisticServiceImpl() {
		statisticHistory = new ConcurrentHashMap<Long, StatisticBo>();
		statisticTimestamps = new PriorityBlockingQueue<Long>();
	}

	private StatisticBo initStatistic(Long timestamp) {
		StatisticBo statistic = new StatisticBo();
		statistic.setDate(CommonUtil.convertToLocalDateTime(timestamp));
		statistic.setMax(Double.MIN_VALUE);
		statistic.setMin(Double.MAX_VALUE);
		statistic.setSum(0.0);
		statistic.setCount(0l);
		return statistic;
	}

	@Override
	public ResponseEntity<StatisticBo> getCurrentStatistic() {
		Long currentTimestamp = CommonUtil.converToTimeStamp(LocalDateTime.now());
		StatisticBo statistic = statisticHistory.get(currentTimestamp);
		if (statistic == null) {
			statistic = initStatistic(currentTimestamp);
		}
		return new ResponseEntity<StatisticBo>(statistic, HttpStatus.OK);
	}

	@Override
	public ResponseEntity<List<StatisticBo>> getAllStatistics() {
		List<StatisticBo> result = new ArrayList<StatisticBo>(statisticHistory.values());
		result.sort((StatisticBo o1, StatisticBo o2) -> o1.getDate().compareTo(o2.getDate()));
		return new ResponseEntity<List<StatisticBo>>(result, HttpStatus.OK);
	}
	
	@Override
	public void insert(TransactionBo transaction) {
		Long currentTimestamp = CommonUtil.converToTimeStamp(LocalDateTime.now());
		Long transactionTimestamp = CommonUtil.converToTimeStamp(transaction.getDate());
		if (transactionTimestamp + getExpirationMilliSec() < currentTimestamp)
			throw new ExpiredTransactionException();
		if (currentTimestamp + getExpirationMilliSec() < transactionTimestamp)
			throw new FutureTransactionException();
		synchronized (StatsObj) {
			for (Long i = currentTimestamp; i < transactionTimestamp + getExpirationMilliSec(); i += 1000) {
				StatisticBo statistic = statisticHistory.get(i);
				if (statistic == null) {
					statistic = initStatistic(i);
					statisticHistory.put(i, statistic);
					statisticTimestamps.add(i);
				}
				if (transaction.getAmount() > statistic.getMax())
					statistic.setMax(transaction.getAmount());
				if (transaction.getAmount() < statistic.getMin())
					statistic.setMin(transaction.getAmount());
				statistic.setSum(statistic.getSum() + transaction.getAmount());
				statistic.setCount(statistic.getCount() + 1);
				statistic.setAvg(statistic.getSum() / statistic.getCount());
			}
		}
	}
	
	@Scheduled(fixedDelayString = "${statisticService.removeMS}")
	private void deleteExpiredStatistics() {
		Long currentTimestamp = CommonUtil.converToTimeStamp(LocalDateTime.now());
		if (statisticTimestamps.isEmpty() || statisticTimestamps.peek() >= currentTimestamp)
			return;
		synchronized (StatsObj) {
			while (!statisticTimestamps.isEmpty() && statisticTimestamps.peek() < currentTimestamp) {
				Long key = statisticTimestamps.poll();
				statisticHistory.remove(key);
			}
		}
	}

	public Long getExpirationMilliSec() {
		return expirationMilliSec;
	}

	public void setExpirationMilliSec(Long expirationMilliSec) {
		this.expirationMilliSec = expirationMilliSec;
	}
}