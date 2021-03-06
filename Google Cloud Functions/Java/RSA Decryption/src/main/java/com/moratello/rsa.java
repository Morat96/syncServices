package com.moratello;

import com.google.cloud.functions.HttpFunction;
import com.google.cloud.functions.HttpRequest;
import com.google.cloud.functions.HttpResponse;

import com.google.gson.JsonObject;

import java.math.BigInteger;

import java.util.LinkedList;
import java.util.Map;
import java.util.List;

public class rsa implements HttpFunction {
    @Override
    public void service(HttpRequest request, HttpResponse response)
            throws Exception {

        // response
        JsonObject output = new JsonObject();

        // check if public key n is present in the request
        if(!request.getFirstQueryParameter("n").isPresent()) {
            response.setStatusCode(500);
            response.appendHeader("Content-Type", "application/json");
            output.addProperty("Error", "The parameter n must be defined!");
            response.getWriter().write(output.toString());
            return;
        }

        // check if public key e is present in the request
        if(!request.getFirstQueryParameter("e").isPresent()) {
            response.setStatusCode(500);
            response.appendHeader("Content-Type", "application/json");
            output.addProperty("Error", "The parameter e must be defined!");
            response.getWriter().write(output.toString());
            return;
        }

        // check if the crypted message is present in the request
        if(!request.getFirstQueryParameter("cipher").isPresent()) {
            response.setStatusCode(500);
            response.appendHeader("Content-Type", "application/json");
            output.addProperty("Error", "The cipher must be defined!");
            response.getWriter().write(output.toString());
            return;
        }

        // get parameters
        Map<String, List<String>> args = request.getQueryParameters();

        String n_ = args.get("n").get(0);
        String e_ = args.get("e").get(0);
        String cipher_ = args.get("cipher").get(0);

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

        // check if N is a coprime number
        if (factorization.size() != 2) {
            System.out.println("N is not coprime number!");
            response.setStatusCode(500);
            response.appendHeader("Content-Type", "application/json");
            output.addProperty("Error", "N is not coprime number!");
            response.getWriter().write(output.toString());
            return;
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

        response.setStatusCode(200);
        response.appendHeader("Content-Type", "application/json");
        output.addProperty("p", p);
        output.addProperty("q", q);
        output.addProperty("Original Message", originalMessage);
        response.getWriter().write(output.toString());
        return;
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
