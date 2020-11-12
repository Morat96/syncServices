package com.moratello.webapp.entity;

import java.io.Serializable;
import java.math.BigInteger;

import lombok.Data;

@Data
public class DecryptionResult implements Serializable {
	
	private static final long serialVersionUID = -6482756278744635237L;

	// phi = (p - 1) * (q - 1)
	private BigInteger phi;
	
	// d = modinv(e, phi)
	private BigInteger d;
	
	// original message
	private BigInteger M;
	
}
