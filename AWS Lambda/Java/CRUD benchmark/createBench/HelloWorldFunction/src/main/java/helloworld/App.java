package helloworld;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;

import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import com.cloudant.client.api.*;
import com.cloudant.client.api.model.Response;
import com.cloudant.client.org.lightcouch.NoDocumentException;
import org.apache.commons.lang3.RandomStringUtils;

/**
 * A benchmark for CouchDB databases.
 * Designed for Openwhisk.
 * This function creates a number of random documents and executes a series of sorts to them.
 * @version 1.0.0
 * @author Matteo Moratello
 */

public class App implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    public APIGatewayProxyResponseEvent handleRequest(final APIGatewayProxyRequestEvent input, final Context context) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("X-Custom-Header", "application/json");

        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent()
                .withHeaders(headers);

        // Path parameter
        Map<String, String> pathPar = input.getPathParameters();
        // Query path parameters
        Map<String, String> queryPar = input.getQueryStringParameters();

        // Show parameters
        System.out.println("dbname: " + pathPar.get("dbname"));
        System.out.println("size: " + queryPar.get("size"));
        System.out.println("ndocs: " + queryPar.get("ndocs"));
        System.out.println("sorts: " + queryPar.get("sorts"));

        // get Parameters
        String dbname = pathPar.get("dbname");
        // parameters
        int size = 1;
        int ndocs = 1;
        int sorts = 1;

        // parameter size
        if (queryPar.get("size") == null) {
            return response
                    .withBody("{ \"message\": \"Parameter size must be defined\" }")
                    .withStatusCode(500);
        }
        else {
            size = Integer.parseInt(queryPar.get("size"));
        }

        // parameter size
        if (queryPar.get("ndocs") == null) {
            return response
                    .withBody("{ \"message\": \"Parameter ndocs must be defined\" }")
                    .withStatusCode(500);
        }
        else {
            ndocs = Integer.parseInt(queryPar.get("ndocs"));
        }

        // parameter size
        if (queryPar.get("sorts") == null) {
            return response
                    .withBody("{ \"message\": \"Parameter sorts must be defined\" }")
                    .withStatusCode(500);
        }
        else {
            sorts = Integer.parseInt(queryPar.get("sorts"));
        }

        JsonObject output = new JsonObject();

        // CouchDB server connection
        CloudantClient client = null;
        try {
            client = ClientBuilder.url(new URL("https://7ec84ee2-f691-4edb-a024-11e71e1153a8-bluemix.cloudantnosqldb.appdomain.cloud"))
                    .username("7ec84ee2-f691-4edb-a024-11e71e1153a8-bluemix")
                    .password("3519a078d03db2a98c59f7541c935dda799d990d0f3a4c91e891f4bb34bb7991")
                    .build();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        // Show the server version
        System.out.println("Server Version: " + client.serverVersion());

        long start = System.currentTimeMillis();

        List<JsonObject> newDocs = new ArrayList<JsonObject>();

        for (int doc = 0; doc < ndocs; doc++) {

            JsonObject newDoc = new JsonObject();
            ArrayList<String> words = generateDocument(size);

            if (sorts > 1) {
                // some sorting of documents
                for (int i = 0; i < sorts - 1; i++) {
                    // do sort
                    Collections.sort(words);
                    // do shuffle
                    Collections.shuffle(words);
                }
            }

            Collections.sort(words);

            String document = String.join(" ", words);

            newDoc.addProperty("content", document);

            newDocs.add(newDoc);

        }

        Database db = null;

        // Get a Database instance
        try {
            db = client.database(dbname, false);
        }
        catch (NoDocumentException e) {
            e.printStackTrace();
        };

        List<Response> responses = db.bulk(newDocs);

        JsonObject body = new JsonObject();
        if (responses.size() != 0 && responses.get(0).getError() != null) {
            body.addProperty("reason", responses.get(0).getReason());
            return response
                    .withStatusCode(responses.get(0).getStatusCode())
                    .withBody(body.toString());
        }

        long end = System.currentTimeMillis();
        System.out.println("Time for computing sorting: " + (end - start) + " ms");

        JsonArray DocsArray = new JsonArray();
        for (int i = 0; i < responses.size(); i++) {

            JsonObject resp = new JsonObject();
            resp.addProperty("_id", responses.get(i).getId());
            resp.addProperty("_rev", responses.get(i).getRev());
            DocsArray.add(resp);
        }

        body.add("docs", DocsArray);
        body.addProperty("time", (end - start) + " ms");
        output.add("body", body);

        return response
                .withStatusCode(200)
                .withBody(output.toString());
    }

    // method that generate a document
    public static ArrayList<String> generateDocument(int size) {

        ArrayList<String> words = new ArrayList<String>();
        String randomWord = "";

        // generate a document compose of "size" random words
        for (int i = 0; i < size; i++) {
            randomWord = new RandomStringUtils().randomAlphabetic(3, 9);
            words.add(randomWord);
        }

        return words;
    }
}
