package com.moratello;

import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.HttpStatus;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;

import java.util.Optional;

import com.google.gson.JsonObject;
import java.math.BigInteger;
import java.util.LinkedList;

/**
 * RSA Decryption Algorithm. Javascript version.
 */

public class Function {
    
    @FunctionName("rsaJava")
    public HttpResponseMessage run(
            @HttpTrigger(
                name = "req",
                methods = {HttpMethod.GET, HttpMethod.POST},
                authLevel = AuthorizationLevel.ANONYMOUS)
                HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) {

        context.getLogger().info("RSA Decryption Algorithm.");

        // Parse query parameter
        final String n_ = request.getQueryParameters().get("n");
        final String e_ = request.getQueryParameters().get("e");
        final String cipher_ = request.getQueryParameters().get("cipher");

        BigInteger n = new BigInteger(n_);
        BigInteger e = new BigInteger(e_);
        BigInteger cipher = new BigInteger(cipher_);

        System.out.println("Public Keys");
        System.out.println("N: " + n);
        System.out.println("e: " + e);

        System.out.println("Cripted Message: " + cipher + "\n");

        LinkedList factorization = new LinkedList();

        long start = System.currentTimeMillis();

        factorization = tdFactors(n);

        long end = System.currentTimeMillis();
        System.out.println("Time for computing factorization: " + (end - start) + " ms");

        // response
        JsonObject response = new JsonObject();

        // check if N is a coprime number
        if (factorization.size() != 2) {
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST).body("N is not coprime number!").build();
        }

        BigInteger p = (BigInteger) factorization.get(0);
        BigInteger q = (BigInteger) factorization.get(1);

        System.out.println("p: " + p + "\n" + "q: " + q);

        BigInteger phi = (p.subtract(BigInteger.ONE)).multiply(q.subtract(BigInteger.ONE));

        System.out.println("phi: " + phi);

        // generate the d decryption exponent
        BigInteger d = inverse(e, phi);

        System.out.println("d: " + d);

        BigInteger originalMessage = cipher.modPow(d, n);

        System.out.println("Original Message: " + originalMessage);

        response.addProperty("p", p);
        response.addProperty("q", q);
        response.addProperty("Original Message", originalMessage);

        //return response;

        return request.createResponseBuilder(HttpStatus.OK).body(response).build();
    }

    //calculate multiplicative inverse of a%n using the extended euclidean GCD algorithm
    public static BigInteger inverse(BigInteger a, BigInteger N) {

        BigInteger[] ans = extendedEuclid(a, N);

        if (ans[1].compareTo(BigInteger.ZERO) == 1)
            return ans[1];
        else return ans[1].add(N);
    }

    //Calculate d = gcd(a,N) = ax+yN
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
    public static LinkedList tdFactors(BigInteger n) {

        BigInteger two = BigInteger.valueOf(2);
        LinkedList fs = new LinkedList();

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
