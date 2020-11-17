package com.moratello;

import com.google.cloud.functions.HttpFunction;
import com.google.cloud.functions.HttpRequest;
import com.google.cloud.functions.HttpResponse;

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
import java.util.stream.Collectors;

/**
 * A benchmark for CouchDB databases.
 * Designed for Openwhisk.
 * This function delete a number of random documents.
 * @version 1.0.0
 * @author Matteo Moratello
 */
public class readBench implements HttpFunction {
    @Override
    public void service(HttpRequest request, HttpResponse response)
            throws Exception {

        String path = "";
        String[] split = null;
        String db_selected = "";
        String query = "";

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
            query = split[3];
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

        List<Doc> allDocs = null;

        // Obtain all docs
        try {
            allDocs = db.getAllDocsRequestBuilder().includeDocs(true).build().getResponse().getDocsAs(Doc.class);
        }
        catch(Exception e) {
            e.printStackTrace();
        }

        // number of documents
        int docsCount = allDocs.size();

        JsonArray DocsArray = new JsonArray();

        // Make the query searching for documents which contain the word 'query'
        for (int i = 0; i < docsCount; i++) {
            if(allDocs.get(i).getContent() != null && allDocs.get(i).getContent().contains(query)){
                JsonObject resp = new JsonObject();
                resp.addProperty("id", allDocs.get(i).getId());
                resp.addProperty("rev", allDocs.get(i).getRev());
                resp.addProperty("content", allDocs.get(i).getContent());
                DocsArray.add(resp);
            }
        }

        long end = System.currentTimeMillis();
        System.out.println("Time for computing sorting: " + (end - start) + " ms");

        JsonObject body = new JsonObject();
        body.add("docs", DocsArray);
        body.addProperty("time", (end - start) + " ms");
        output.add("body", body);

        response.setStatusCode(200);
        response.getWriter().write(output.toString());
        return;
    }
}
