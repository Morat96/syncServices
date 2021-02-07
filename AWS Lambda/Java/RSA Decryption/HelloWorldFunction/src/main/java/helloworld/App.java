package helloworld;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;

import java.math.BigInteger;
import java.util.LinkedList;

/**
 * Handler for requests to Lambda function.
 */
public class App implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    public APIGatewayProxyResponseEvent handleRequest(final APIGatewayProxyRequestEvent input, final Context context) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("X-Custom-Header", "application/json");

        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent()
                .withHeaders(headers);

        Map<String, String> parameters = input.getQueryStringParameters();

        System.out.println("n:" + parameters.get("n"));
        System.out.println("e:" + parameters.get("e"));
        System.out.println("cipher:" + parameters.get("cipher"));

        // parameters
        String n_ = "";
        String e_ = "";
        String cipher_ = "";

        // parameter n -> public key
        if (parameters.get("n") == null) {
            return response
                    .withBody("{ \"message\": \"Parameter n must be defined\" }")
                    .withStatusCode(500);
        }
        else {
            n_ = parameters.get("n");
        }
        // parameter e -> public key
        if (parameters.get("e") == null) {
            return response
                    .withBody("{ \"message\": \"Parameter e must be defined\" }")
                    .withStatusCode(500);
        }
        else {
            e_ = parameters.get("e");
        }
        // parameter cipher -> crypt message
        if (parameters.get("e") == null) {
            return response
                    .withBody("{ \"message\": \"Parameter cipher must be defined\" }")
                    .withStatusCode(500);
        }
        else {
            cipher_ = parameters.get("cipher");
        }

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
            return response
                    .withBody("{ \"message\": \"N is not coprime number!\" }")
                    .withStatusCode(500);
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

        String output = String.format("{ \"p\": \"%s\", \"q\": \"%s\", \"Original Message\": \"%s\" }", p, q, originalMessage);

        return response
                .withStatusCode(200)
                .withBody(output);
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
