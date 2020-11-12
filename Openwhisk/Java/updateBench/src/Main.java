import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import com.cloudant.client.api.*;
import com.cloudant.client.api.model.Response;
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

public class Main {

    public static JsonObject main(JsonObject args) {

        String path = "";
        String[] split = null;
        String db_selected = "";
        int count = 0;

        JsonObject response = new JsonObject();
        JsonObject header = new JsonObject();
        header.addProperty("Content-Type", "application/json");
        JsonObject bodyError = new JsonObject();

        // obtain the database name from the path
        if (args.has("__ow_path")) {
            path = args.getAsJsonPrimitive("__ow_path").getAsString();
            split = path.split("/");
            if (split.length != 5) {
                response.addProperty("statusCode", 400);
                response.add("headers", header);
                bodyError.addProperty("message", "The path must be: ../createBenchJava/{dbname}/{count}");
                response.add("body", bodyError);
                return response;
            }
            db_selected = split[3];
            count = Integer.parseInt(split[4]);
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
            db = client.database(db_selected, false);
        }
        catch(Exception e) {
            e.printStackTrace();
        }

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
            response.addProperty("statusCode", 400);
            response.add("headers", header);
            bodyError.addProperty("message", "The number of docs to delete is " + count + ", but the docs in the database '" + db_selected + "' are " + docsCount);
            response.add("body", bodyError);
            return response;
        }

        List<Integer> list = getRandomList(count, docsCount);

        //List<JsonObject> docsToUpdate = new ArrayList<JsonObject>();

        // create the object
        for (int i = 0; i < count; i++) {
            newDocs.get(i).addProperty("_id", ids.get(list.get(i)));
            newDocs.get(i).addProperty("_rev", revs.get(list.get(i)));
        }

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