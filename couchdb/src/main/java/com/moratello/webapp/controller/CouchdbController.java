package com.moratello.webapp.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.moratello.webapp.entity.CouchdbTestResult;
import com.moratello.webapp.entity.CouchdbTestResultQuery;
import com.moratello.webapp.service.CouchdbService;


@RestController
@RequestMapping("/api")
public class CouchdbController {

	@Autowired
	private CouchdbService couchdbService;
	
	private static final Logger logger = LoggerFactory.getLogger(CouchdbController.class);
	
	// http://localhost:8001/api/createBench/benchdb?size=2&ndocs=2&sorts=2
	@PostMapping(value = "/createBench/{dbname}", produces = "application/json")
	public ResponseEntity<CouchdbTestResult> postTest(@RequestParam("size") int size, 
			@RequestParam("ndocs") int ndocs, @RequestParam("sorts") int sorts, 
			@PathVariable("dbname") String dbname) {
		
		logger.info("****** COUCHDB BENCHMARK - Post Method ******"); 
		logger.info("Database: " + dbname);
		logger.info("Number of documents to create: " + ndocs);
		logger.info("Number of random words for each document: " + size);
		logger.info("Number of sorts: " + sorts);
		logger.info("**********************************************"); 
		
		CouchdbTestResult result = couchdbService.createDocs(dbname, size, ndocs, sorts);
		
		return new ResponseEntity<CouchdbTestResult>(result, HttpStatus.OK);

	}
	
	// http://localhost:8001/api/deleteBench/benchdb/5
	@DeleteMapping(value = "/deleteBench/{dbname}/{count}", produces = "application/json")
	public ResponseEntity<CouchdbTestResult> deleteTest(@PathVariable("dbname") String dbname,
			@PathVariable("count") int count) {
		
		logger.info("****** COUCHDB BENCHMARK - Delete Method ******"); 
		logger.info("Database: " + dbname);
		logger.info("Number of documents to delete: " + count);
		logger.info("***********************************************"); 
		
		CouchdbTestResult result = couchdbService.deleteDocs(dbname, count);
		
		return new ResponseEntity<CouchdbTestResult>(result, HttpStatus.OK);
		
	}
	
	// http://localhost:8001/api/updateBench/benchdb/5?size=2&sorts=2
	@PutMapping(value = "/updateBench/{dbname}/{count}", produces = "application/json")
	public ResponseEntity<CouchdbTestResult> putTest(@RequestParam("size") int size, 
			@RequestParam("sorts") int sorts, @PathVariable("dbname") String dbname,
			@PathVariable("count") int count) {
		
		logger.info("****** COUCHDB BENCHMARK - Update Method ******"); 
		logger.info("Database: " + dbname);
		logger.info("Number of documents to update: " + count);
		logger.info("Number of random words for each document: " + size);
		logger.info("Number of sorts: " + sorts);
		logger.info("***********************************************"); 
		
		CouchdbTestResult result = couchdbService.updateDocs(dbname, count, size, sorts);
		
		return new ResponseEntity<CouchdbTestResult>(result, HttpStatus.OK);
	}
	
	// http://localhost:8001/api/readBench/benchdb/ciao
	@GetMapping(value = "/readBench/{dbname}/{query}", produces = "application/json")
	public ResponseEntity<CouchdbTestResultQuery> getTest(@PathVariable("dbname") String dbname,
			@PathVariable("query") String query) {
		
		logger.info("****** COUCHDB BENCHMARK - Update Method ******"); 
		logger.info("Database: " + dbname);
		logger.info("Word to search: " + query);
		logger.info("***********************************************");
		
		CouchdbTestResultQuery result = couchdbService.getDocs(dbname, query);
		
		return new ResponseEntity<CouchdbTestResultQuery>(result, HttpStatus.OK);
	}
}
