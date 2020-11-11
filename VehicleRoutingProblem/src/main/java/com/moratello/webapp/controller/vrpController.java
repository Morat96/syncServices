package com.moratello.webapp.controller;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.moratello.webapp.entity.Result;

@RestController
@RequestMapping("/api")
public class vrpController {
	
	private static final Logger logger = LoggerFactory.getLogger(vrpController.class);
	
	@Autowired
	private com.moratello.webapp.service.vrpService vrpService;
	
	// http://localhost:8005/api/solveVRP?vns=false
	@PostMapping(value = "/solveVRP", produces = "application/json")
	public ResponseEntity<Result> solveVRP(@RequestParam("vns") boolean vns, @RequestParam(value = "k_opt", required = false) Integer k_opt, 
			@RequestParam(value = "n_iter", required = false) Integer n_iter, @RequestBody String body) throws IOException, Exception {
		
		logger.info("********* Resolving VRP *********");
		logger.info("Use Variable Neighbour Search: " + vns);
		logger.info("Number ");
		
		if (k_opt == null || n_iter == null) {
			k_opt = 1;
			n_iter = 1;
		} else {
			logger.info("Number of cities swaps: " + k_opt);
			logger.info("Number of iterations: " + n_iter);
		}
		
		logger.info("*********************************");
		
		Result result = vrpService.solveInstance(body, vns, k_opt, n_iter);
		
		return new ResponseEntity<Result>(result, HttpStatus.OK);
	}

}


