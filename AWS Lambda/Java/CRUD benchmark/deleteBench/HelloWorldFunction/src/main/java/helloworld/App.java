package helloworld;

import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import com.cloudant.client.api.*;
import com.cloudant.client.api.model.Response;
import com.cloudant.client.org.lightcouch.NoDocumentException;
import org.apache.commons.lang3.RandomStringUtils;

import java.net.MalformedURLException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.stream.Collectors;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;

/**
 * A benchmark for CouchDB databases.
 * Designed for Openwhisk.
 * This function delete a number of random documents.
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

        // get Parameters
        String dbname = pathPar.get("dbname");
        int count = Integer.parseInt(pathPar.get("count"));

        JsonObject output = new JsonObject();
        JsonObject bodyError = new JsonObject();

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

        Database db = null;

        // Get a Database instance
        try {
            db = client.database(dbname, false);
        }
        catch (NoDocumentException e) {
            e.printStackTrace();
        };

        Map<String,String> allDocIdsAndRevs = null;

        try {
            allDocIdsAndRevs = db.getAllDocsRequestBuilder().build().getResponse().getIdsAndRevs();
        }
        catch(Exception e) {
            e.printStackTrace();
        }

        // number of documents
        int docsCount = allDocIdsAndRevs.size();

        List<String> ids = new ArrayList<String>();
        List<String> revs = new ArrayList<String>();

        for (String i : allDocIdsAndRevs.keySet()) {
            ids.add(i);
            revs.add(allDocIdsAndRevs.get(i));
        }

        if (count > docsCount) {
            bodyError.addProperty("message", "The number of docs to delete is " + count + ", but the docs in the database '" + dbname + "' are " + docsCount);
            return response
                    .withStatusCode(500)
                    .withBody(bodyError.toString());
        }

        List<Integer> list = getRandomList(count, docsCount);

        List<JsonObject> docsToDelete = new ArrayList<JsonObject>();

        // create the object
        for (int i = 0; i < count; i++) {
            JsonObject resp = new JsonObject();
            resp.addProperty("_id", ids.get(list.get(i)));
            resp.addProperty("_rev", revs.get(list.get(i)));
            resp.addProperty("_deleted", true);
            docsToDelete.add(resp);
        }

        // delete documents from the database
        List<Response> responses = db.bulk(docsToDelete);

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

    /**
     * Get an array composed of random values
     * @param size size of the array to generate
     * @param max generate numbers between 0 and (max - 1)
     * @returns array of random numbers
     */
    public static List<Integer> getRandomList(int size, int max) {

        ArrayList<Integer> arr = new ArrayList<>(max);
        for (int i = 0; i < max; i++) arr.add(i);
        Collections.shuffle(arr);
        List<Integer> newArr = arr.subList(0, size);
        return newArr;
    }
}
