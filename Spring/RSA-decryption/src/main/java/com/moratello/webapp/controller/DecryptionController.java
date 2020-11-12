package com.moratello.webapp.controller;

import java.math.BigInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.moratello.webapp.entity.DecryptionResult;
import com.moratello.webapp.service.DecryptionService;

@RestController
@RequestMapping("/api")
public class DecryptionController {
	
	@Autowired
	private DecryptionService decryptionService;
	
	private static final Logger logger = LoggerFactory.getLogger(DecryptionController.class);
	
	//computeMessage?n=68561013233270273&e=65537&cipher=1234
	@GetMapping(value = "/computeMessage", produces = "application/json")
	public ResponseEntity<DecryptionResult> getOriginalMessage(@RequestParam("n") BigInteger n, 
			@RequestParam("e") BigInteger e, @RequestParam("cipher") BigInteger Cipher) 
	{
		
		logger.info("****** Obtaining the original message from the cipher: " + Cipher + " ******"); 
		
		DecryptionResult result = decryptionService.doDecryption(Cipher, n, e);
		
		return new ResponseEntity<DecryptionResult>(result, HttpStatus.OK);
	}
}