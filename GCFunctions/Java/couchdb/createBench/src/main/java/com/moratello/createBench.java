package com.moratello;

import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import com.cloudant.client.api.*;
import com.cloudant.client.api.model.Response;
import com.cloudant.client.org.lightcouch.NoDocumentException;
import org.apache.commons.lang3.RandomStringUtils;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

import com.google.cloud.functions.HttpFunction;
import com.google.cloud.functions.HttpRequest;
import com.google.cloud.functions.HttpResponse;

/**
 * A benchmark for CouchDB databases.
 * Designed for Openwhisk.
 * This function creates a number of random documents and executes a series of sorts to them.
 * @version 1.0.0
 * @author Matteo Moratello
 */
public class createBench implements HttpFunction {
    @Override
    public void service(HttpRequest request, HttpResponse response)
            throws Exception {

        String path = "";
        String[] split = null;
        String db_selected = "";

        response.appendHeader("Content-Type", "application/json");

        JsonObject output = new JsonObject();

        // obtain the database name from the path
        if (!request.getPath().isEmpty()) {
            path = request.getPath();
            split = path.split("/");
            if (split.length != 3) {
                response.setStatusCode(500);
                output.addProperty("Error", "The path must be: ../createBenchJava/{dbname}");
                response.getWriter().write(output.toString());
                return;
            }
            db_selected = split[2];
        }

        // parameters
        int size = 1;
        int ndocs = 1;
        int sorts = 1;

        // check if parameter size is present in the request
        if(!request.getFirstQueryParameter("size").isPresent()) {
            response.setStatusCode(500);
            output.addProperty("Error", "The parameter size must be defined!");
            response.getWriter().write(output.toString());
            return;
        } else {
            System.out.println("Size: " + request.getFirstQueryParameter("size").get());
            size = Integer.parseInt(request.getFirstQueryParameter("size").get());
        }
        // check if parameter ndocs is present in the request
        if(!request.getFirstQueryParameter("ndocs").isPresent()) {
            response.setStatusCode(500);
            output.addProperty("Error", "The parameter ndocs must be defined!");
            response.getWriter().write(output.toString());
            return;
        } else {
            System.out.println("Ndocs: " + request.getFirstQueryParameter("ndocs").get());
            ndocs = Integer.parseInt(request.getFirstQueryParameter("ndocs").get());
        }
        // check if parameter sorts is present in the request
        if(!request.getFirstQueryParameter("sorts").isPresent()) {
            response.setStatusCode(500);
            output.addProperty("Error", "The parameter sorts must be defined!");
            response.getWriter().write(output.toString());
            return;
        } else {
            System.out.println("Sorts: " + request.getFirstQueryParameter("sorts"));
            sorts = Integer.parseInt(request.getFirstQueryParameter("sorts").get());
        }

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
            db = client.database(db_selected, false);
        }
        catch (NoDocumentException e) {
            e.printStackTrace();
        };

        List<Response> responses = db.bulk(newDocs);

        JsonObject body = new JsonObject();
        if (responses.size() != 0 && responses.get(0).getError() != null) {
            response.setStatusCode(responses.get(0).getStatusCode());
            output.addProperty("reason", responses.get(0).getReason());
            response.getWriter().write(output.toString());
            return;
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

        response.getWriter().write(output.toString());
        return;

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