package com.moratello;

import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import com.cloudant.client.api.*;
import com.cloudant.client.api.model.Response;
import com.cloudant.client.org.lightcouch.NoDocumentException;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Collections;

import com.google.cloud.functions.HttpFunction;
import com.google.cloud.functions.HttpRequest;
import com.google.cloud.functions.HttpResponse;

/**
 * A benchmark for CouchDB databases.
 * Designed for Openwhisk.
 * This function delete a number of random documents.
 * @version 1.0.0
 * @author Matteo Moratello
 */
public class deleteBench implements HttpFunction {
    @Override
    public void service(HttpRequest request, HttpResponse response)
            throws Exception {

        String path = "";
        String[] split = null;
        String db_selected = "";
        int count = 0;

        response.appendHeader("Content-Type", "application/json");

        JsonObject output = new JsonObject();

        // obtain the database name from the path
        if (!request.getPath().isEmpty()) {
            path = request.getPath();
            split = path.split("/");
            if (split.length != 4) {
                response.setStatusCode(500);
                output.addProperty("Error", "The path must be: ../deleteBench/{dbname}/{count}");
                response.getWriter().write(output.toString());
                return;
            }
            db_selected = split[2];
            count = Integer.parseInt(split[3]);
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

        Database db = null;

        // Get a Database instance
        try {
            db = client.database(db_selected, false);
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
            response.setStatusCode(500);
            output.addProperty("message", "The number of docs to delete is " + count + ", but the docs in the database '" + db_selected + "' are " + docsCount);
            response.getWriter().write(output.toString());
            return;
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

        response.setStatusCode(200);
        response.getWriter().write(output.toString());
        return;
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