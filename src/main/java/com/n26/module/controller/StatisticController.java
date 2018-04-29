package com.n26.module.controller;

import com.n26.module.service.StatisticService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/statistics")
public class StatisticController {
	@Autowired
	private StatisticService statisticService;
	
    @RequestMapping(method = RequestMethod.GET)
	public ResponseEntity<?> getCurrentStatistic() {
		return statisticService.getCurrentStatistic();
	}
	
    @RequestMapping(path = "/all", method = RequestMethod.GET)
	public ResponseEntity<?> getAllStatistics() {
		return statisticService.getAllStatistics();
	}
}