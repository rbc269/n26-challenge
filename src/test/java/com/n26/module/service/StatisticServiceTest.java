package com.n26.module.service;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.n26.module.bo.StatisticBo;
import com.n26.module.bo.TransactionBo;
import com.n26.module.exception.ExpiredTransactionException;
import com.n26.module.exception.FutureTransactionException;
import com.n26.module.service.impl.StatisticServiceImpl;

public class StatisticServiceTest {
	@InjectMocks
	private StatisticService serviceMock = new StatisticServiceImpl();
	private StatisticServiceImpl serviceSpy = Mockito.spy(new StatisticServiceImpl());
	@Mock
	private Map<Long, StatisticBo> statisticHistory;

	@DataProvider(name = "TransactionExpired")
	public Object[][] getTransactionExpired() {
		return new Object[][] { { getMockTransactionExpired() } };
	}

	@DataProvider(name = "TransactionFuture")
	public Object[][] getTransactionFuture() {
		return new Object[][] { { getMockTransactionFuture() } };
	}

	@DataProvider(name = "TransactionNow")
	public Object[][] getTransactionNow() {
		return new Object[][] { { getMockTransactionNow() } };
	}

	@BeforeMethod
	public void setUp() {
		Mockito.when(serviceSpy.getExpirationMilliSec()).thenReturn(Long.valueOf(60000l));
		MockitoAnnotations.initMocks(this);
	}

	@Test()
	public void test_FindAll() {
		Mockito.when(statisticHistory.values()).thenReturn(getMockStatisticList());
		ResponseEntity<List<StatisticBo>> result = serviceMock.getAllStatistics();
		assertNotNull(result);
		StatisticBo resultBo = result.getBody().get(0);
		assertEquals(resultBo.getSum(), 1000.0);
		assertEquals(resultBo.getAvg(), 100.0);
		assertEquals(resultBo.getMax(), 200.0);
		assertEquals(resultBo.getMin(), 50.0);
		assertEquals(resultBo.getCount(), Long.valueOf(10l));
	}

	@Test()
	public void test_FindCurrent() {
		Mockito.when(statisticHistory.get(Mockito.anyLong())).thenReturn(getMockStatisticCurrent());
		ResponseEntity<StatisticBo> result = serviceMock.getCurrentStatistic();
		assertEquals(result.getBody().getSum(), 134.8);
		assertEquals(result.getBody().getAvg(), 33.7);
		assertEquals(result.getBody().getMax(), 45.2);
		assertEquals(result.getBody().getMin(), 22.4);
		assertEquals(result.getBody().getCount(), Long.valueOf(5l));
	}

	@Test(dataProvider = "TransactionNow")
	public void test_Success_Insert(TransactionBo transaction) {
		serviceSpy.insert(transaction);
	}

	@Test(expectedExceptions = ExpiredTransactionException.class, dataProvider = "TransactionExpired")
	public void test_ExpiredTransaction_Error_Insert(TransactionBo transaction) {
		serviceSpy.insert(transaction);
	}

	@Test(expectedExceptions = FutureTransactionException.class, dataProvider = "TransactionFuture")
	public void test_FutureTransaction_Error_Insert(TransactionBo transaction) {
		serviceSpy.insert(transaction);
	}

	private static TransactionBo getMockTransactionNow() {
		TransactionBo transaction = new TransactionBo();
		transaction.setAmount(12.3);
		transaction.setDate(LocalDateTime.now()); // now
		return transaction;
	}

	private static TransactionBo getMockTransactionExpired() {
		TransactionBo transaction = new TransactionBo();
		transaction.setAmount(12.3);
		transaction.setDate(LocalDateTime.now().minus(1, ChronoUnit.DAYS)); // yesterday
		return transaction;
	}

	private static TransactionBo getMockTransactionFuture() {
		TransactionBo transaction = new TransactionBo();
		transaction.setAmount(12.3);
		transaction.setDate(LocalDateTime.now().plus(1, ChronoUnit.DAYS)); // tomorrow
		return transaction;
	}

	private static StatisticBo getMockStatisticCurrent() {
		StatisticBo statistic = new StatisticBo();
		statistic.setDate(LocalDateTime.now());
		statistic.setSum(134.8);
		statistic.setAvg(33.7);
		statistic.setMax(45.2);
		statistic.setMin(22.4);
		statistic.setCount(5l);
		return statistic;
	}

	private static List<StatisticBo> getMockStatisticList() {
		StatisticBo statistic = new StatisticBo();
		statistic.setDate(LocalDateTime.now());
		statistic.setSum(1000.0);
		statistic.setAvg(100.0);
		statistic.setMax(200.0);
		statistic.setMin(50.0);
		statistic.setCount(10l);
		List<StatisticBo> list = new ArrayList<>();
		list.add(statistic);
		return list;
	}
}