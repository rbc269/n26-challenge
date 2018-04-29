package com.n26.module.controller;

import javax.validation.Valid;

import com.n26.module.service.TransactionService;
import com.n26.module.json.TransactionParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/transactions")
public class TransactionController {
	@Autowired
	private TransactionService transactionService;
	
    @RequestMapping(method = RequestMethod.POST)
	public ResponseEntity<?> post(@RequestBody @Valid TransactionParam json) {
		return transactionService.process(json);
    }
}