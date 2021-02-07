package com.moratello.webapp.service;

import java.math.BigInteger;

import com.moratello.webapp.entity.DecryptionResult;

public interface DecryptionService {

	// C = encrypted message
	// n, e = private keys
	public DecryptionResult doDecryption(BigInteger C, BigInteger n, BigInteger e);
	
}

