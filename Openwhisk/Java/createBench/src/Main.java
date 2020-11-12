import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import com.cloudant.client.api.*;
import com.cloudant.client.api.model.Response;
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
                response.addProperty("statusCode", 400);
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
            response.addProperty("statusCode", 400);
            response.add("headers", header);
            bodyError.addProperty("message", "The parameter 'size' must be defined!");
            response.add("body", bodyError);
            return response;
        }
            
        if (args.has("ndocs")) {
            ndocs = args.getAsJsonPrimitive("ndocs").getAsInt();
        }
        else {
            response.addProperty("statusCode", 400);
            response.add("headers", header);
            bodyError.addProperty("message", "The parameter 'ndocs' must be defined!");
            response.add("body", bodyError);
            return response;
        }

        if (args.has("sorts")) {
            sorts = args.getAsJsonPrimitive("sorts").getAsInt();
        }
        else {
            response.addProperty("statusCode", 400);
            response.add("headers", header);
            bodyError.addProperty("message", "The parameter 'sorts' must be defined!");
            response.add("body", bodyError);
            return response;
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

        // Get a Database instance
        Database db = client.database(db_selected, false);

        List<Response> responses = db.bulk(newDocs);

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

        JsonObject body = new JsonObject();
        body.add("docs", DocsArray);
        body.addProperty("time", (end - start) + " ms");
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