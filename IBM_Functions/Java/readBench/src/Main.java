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

public class Main {

    public static JsonObject main(JsonObject args) {

        String path = "";
        String[] split = null;
        String db_selected = "";
        String query = "";

        JsonObject response = new JsonObject();
        JsonObject header = new JsonObject();
        header.addProperty("Content-Type", "application/json");
        JsonObject bodyError = new JsonObject();

        // obtain the database name from the path
        if (args.has("__ow_path")) {
            path = args.getAsJsonPrimitive("__ow_path").getAsString();
            split = path.split("/");
            if (split.length != 6) {
                response.addProperty("statusCode", 400);
                response.add("headers", header);
                bodyError.addProperty("message", "The path must be: ../createBenchJava/{dbname}/{query}");
                response.add("body", bodyError);
                return response;
            }
            db_selected = split[4];
            query = split[5];
        }
        
        String host = "";
        String username = "";
        String password = "";
        
        if (args.has("host")) {
            host = args.getAsJsonPrimitive("host").getAsString();
        }
        if (args.has("username")) {
            username = args.getAsJsonPrimitive("username").getAsString();
        }
        if (args.has("password")) {
            password = args.getAsJsonPrimitive("password").getAsString();
        }
        
        // CouchDB server connection
        CloudantClient client = null;
        try {
            client = ClientBuilder.url(new URL(host))
                    .username(username)
                    .password(password)
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
        
        response.addProperty("statusCode", 200);
        response.add("headers", header);

        JsonObject body = new JsonObject();
        body.add("docs", DocsArray);
        body.addProperty("time", (end - start) + " ms");
        response.add("body", body);
        return response;
    }
}