package com.moratello.webapp.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.moratello.webapp.entity.CouchdbTestResult;
import com.moratello.webapp.service.CouchdbService;


@RestController
@RequestMapping("/api")
public class CouchdbController {

	@Autowired
	private CouchdbService couchdbService;
	
	private static final Logger logger = LoggerFactory.getLogger(CouchdbController.class);
	
	// http://localhost:8001/api/computeTest?size=2&ndocs=2&sorts=2
	@GetMapping(value = "/computeTest", produces = "application/json")
	public ResponseEntity<CouchdbTestResult> getTest(@RequestParam("size") int size, 
			@RequestParam("ndocs") int ndocs, @RequestParam("sorts") int sorts) {
		
		logger.info("****** COUCHDB BENCHMARK ******"); 
		logger.info("Number of documents: " + ndocs);
		logger.info("Number of random words for each document: " + size);
		logger.info("Number of sorts: " + sorts);
		logger.info("*******************************"); 
		
		CouchdbTestResult result = couchdbService.doTest(size, ndocs, sorts);
		
		return new ResponseEntity<CouchdbTestResult>(result, HttpStatus.OK);

	}
}
