package com.moratello;

import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.HttpStatus;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;
import com.microsoft.azure.functions.annotation.BindingName;

import java.util.Optional;

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
import java.util.Map;
import java.util.Collections;

/**
 * A benchmark for CouchDB databases.
 * Designed for Openwhisk.
 * This function updates a number of random documents and executes a series of sorts to them.
 * @version 1.0.0
 * @author Matteo Moratello
 */

public class Function {

    @FunctionName("updateBench")
    public HttpResponseMessage run(
            @HttpTrigger(
                name = "req",
                methods = {HttpMethod.GET, HttpMethod.POST},
                authLevel = AuthorizationLevel.ANONYMOUS,
                route = "updateBench/{dbname}/{count}")
                HttpRequestMessage<Optional<String>> request,
                @BindingName("dbname") String dbname,
                @BindingName("count") Integer count,
            final ExecutionContext context) {
        context.getLogger().info("Java HTTP trigger processed a request.");

        // Parse query parameter
        int size = 1;
        int sorts = 1;

        if (request.getQueryParameters().get("size") == null) {
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST).body("The parameter 'size' must be defined!").build();
        } else {
            size = Integer.parseInt(request.getQueryParameters().get("size"));
        }

        if (request.getQueryParameters().get("sorts") == null) {
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST).body("The parameter 'sorts' must be defined!").build();
        } else {
            size = Integer.parseInt(request.getQueryParameters().get("sorts"));
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

        for (int doc = 0; doc < count; doc++) {

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
            // Get a Database instance
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
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST).body("The number of docs to delete is " + count + ", but the docs in the database '" + dbname + "' are " + docsCount).build();
        }

        List<Integer> list = getRandomList(count, docsCount);

        //List<JsonObject> docsToUpdate = new ArrayList<JsonObject>();

        // create the object
        for (int i = 0; i < count; i++) {
            newDocs.get(i).addProperty("_id", ids.get(list.get(i)));
            newDocs.get(i).addProperty("_rev", revs.get(list.get(i)));
        }

        List<Response> responses = db.bulk(newDocs);

        JsonObject body = new JsonObject();
        if (responses.size() != 0 && responses.get(0).getError() != null) {
            body.addProperty("reason", responses.get(0).getReason());
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST).body(body).build();
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

        return request.createResponseBuilder(HttpStatus.OK).body(body).build();
    }

    /**
     * Method that generates a document
     */
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
