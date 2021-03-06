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

/**
 * A benchmark for CouchDB databases.
 * Designed for Openwhisk.
 * This function creates a number of random documents and executes a series of sorts to them.
 * @version 1.0.0
 * @author Matteo Moratello
 */

public class Main {

    public static JsonObject main(JsonObject args) {

        String path = "";
        String[] split = null;
        String db_selected = "";

        JsonObject response = new JsonObject();
        JsonObject header = new JsonObject();
        header.addProperty("Content-Type", "application/json");
        JsonObject bodyError = new JsonObject();

        // obtain the database name from the path
        if (args.has("__ow_path")) {
            path = args.getAsJsonPrimitive("__ow_path").getAsString();
            split = path.split("/");
            if (split.length != 4) {
                response.addProperty("statusCode", 500);
                response.add("headers", header);
                bodyError.addProperty("message", "The path must be: ../createBenchJava/{dbname}");
                response.add("body", bodyError);
                return response;
            }
            db_selected = split[3];
        }

        // parameters
        int size = 1;
        int ndocs = 1;
        int sorts = 1;
        
        // pars parameters
        if (args.has("size")) {
            size = args.getAsJsonPrimitive("size").getAsInt();
        }
        else {
            response.addProperty("statusCode", 500);
            response.add("headers", header);
            bodyError.addProperty("message", "The parameter 'size' must be defined!");
            response.add("body", bodyError);
            return response;
        }
            
        if (args.has("ndocs")) {
            ndocs = args.getAsJsonPrimitive("ndocs").getAsInt();
        }
        else {
            response.addProperty("statusCode", 500);
            response.add("headers", header);
            bodyError.addProperty("message", "The parameter 'ndocs' must be defined!");
            response.add("body", bodyError);
            return response;
        }

        if (args.has("sorts")) {
            sorts = args.getAsJsonPrimitive("sorts").getAsInt();
        }
        else {
            response.addProperty("statusCode", 500);
            response.add("headers", header);
            bodyError.addProperty("message", "The parameter 'sorts' must be defined!");
            response.add("body", bodyError);
            return response;
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
            response.addProperty("statusCode", responses.get(0).getStatusCode());
            response.add("headers", header);
            body.addProperty("reason", responses.get(0).getReason());
            response.add("body", body);
            return response;
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

        response.addProperty("statusCode", 200);
        response.add("headers", header);

        body.add("docs", DocsArray);
        body.addProperty("time", end - start);
        response.add("body", body);
        return response;
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