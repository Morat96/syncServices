package com.moratello.webapp.service;

import java.math.BigInteger;
import java.util.LinkedList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.moratello.webapp.controller.DecryptionController;
import com.moratello.webapp.entity.DecryptionResult;

@Service
public class DecryptionServiceImpl implements DecryptionService {
	
	private static final Logger logger = LoggerFactory.getLogger(DecryptionController.class);
	
	@Override
	public DecryptionResult doDecryption(BigInteger C, BigInteger n, BigInteger e) {
		
		logger.info("Public Keys");
		logger.info("N: " + n);
		logger.info("e: " + e);

		logger.info("Cripted Message: " + C);
		
		logger.info("Factorization...");

        LinkedList<BigInteger> factorization = new LinkedList<BigInteger>();

        long start = System.currentTimeMillis();

        factorization = tdFactors(n);

        long end = System.currentTimeMillis();
        logger.info("Time for computing factorization: " + (end - start) + " ms");

        // check if N is a coprime number
        if (factorization.size() != 2) {
            System.out.println("N is not coprime number!");
            return new DecryptionResult();
        }

        BigInteger p = (BigInteger) factorization.get(0);
        BigInteger q = (BigInteger) factorization.get(1);

        logger.info("p: " + p);
        logger.info("q: " + q);

        BigInteger phi = (p.subtract(BigInteger.ONE)).multiply(q.subtract(BigInteger.ONE));

        logger.info("phi: " + phi);

        // generate the d decryption exponent
        BigInteger d = inverse(e, phi);

        logger.info("d: " + d);

        BigInteger originalMessage = C.modPow(d, n);

        logger.info("Original Message: " + originalMessage);

        // Check
        BigInteger check = originalMessage.modPow(e, n);

        logger.info("Check Message: " + check);
        
        DecryptionResult result = new DecryptionResult();
        
        result.setPhi(phi);
        result.setD(d);
        result.setM(originalMessage);
        
		return result;
	}

	// compute multiplicative inverse of a%n using the extended euclidean GCD algorithm
    public static BigInteger inverse(BigInteger a, BigInteger N) {

        BigInteger[] ans = extendedEuclid(a, N);

        if (ans[1].compareTo(BigInteger.ZERO) == 1)
            return ans[1];
        else return ans[1].add(N);
    }

    // Compute d = gcd(a,N) = ax+yN
    public static BigInteger[] extendedEuclid(BigInteger a, BigInteger N) {

        BigInteger[] ans = new BigInteger[3];
        BigInteger ax, yN;

        if (N.equals(BigInteger.ZERO)) {
            ans[0] = a;
            ans[1] = BigInteger.ONE;
            ans[2] = BigInteger.ZERO;
            return ans;
        }

        ans = extendedEuclid(N, a.mod(N));
        ax = ans[1];
        yN = ans[2];
        ans[1] = yN;
        BigInteger temp = a.divide(N);
        temp = yN.multiply(temp);
        ans[2] = ax.subtract(temp);
        return ans;
    }

    // compute factorization
    public static LinkedList<BigInteger> tdFactors(BigInteger n) {

        BigInteger two = BigInteger.valueOf(2);
        LinkedList<BigInteger> fs = new LinkedList<BigInteger>();

        if (n.compareTo(two) < 0) {
            throw new IllegalArgumentException("must be greater than one");
        }

        while (n.mod(two).equals(BigInteger.ZERO)) {
            fs.add(two);
            n = n.divide(two);
        }

        if (n.compareTo(BigInteger.ONE) > 0) {
            BigInteger f = BigInteger.valueOf(3);
            while (f.multiply(f).compareTo(n) <= 0) {
                if (n.mod(f).equals(BigInteger.ZERO)) {
                    fs.add(f);
                    n = n.divide(f);
                } else {
                    f = f.add(two);
                }
            }
            fs.add(n);
        }

        return fs;
    }
}
