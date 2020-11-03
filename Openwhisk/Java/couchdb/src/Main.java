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
 * @version 1.0.0
 * @author Matteo Moratello
 */

public class Main {

    public static JsonObject main(JsonObject args) {

        // CouchDB server connection
        CloudantClient client = null;
        try {
            client = ClientBuilder.url(new URL("http://10.100.206.79:5984"))
                    .username("admin")
                    .password("5rIVPxfad0iSxN8OWJDY")
                    .build();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        // Show the server version
        System.out.println("Server Version: " + client.serverVersion());

        // parameters
        int size = 1;
        int ndocs = 1;
        int sorts = 1;

        // pars parameters
        if (args.has("size"))
            size = args.getAsJsonPrimitive("size").getAsInt();
        if (args.has("ndocs"))
            ndocs = args.getAsJsonPrimitive("ndocs").getAsInt();
        if (args.has("sorts"))
            sorts = args.getAsJsonPrimitive("sorts").getAsInt();

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
        Database db = client.database("example_db", false);

        List<Response> responses = db.bulk(newDocs);

        long end = System.currentTimeMillis();
        System.out.println("Time for computing sorting: " + (end - start) + " ms");

        JsonArray DocsArray = new JsonArray();
        for (int i = 0; i < responses.size(); i++) {

            JsonObject response = new JsonObject();
            response.addProperty("_id", responses.get(i).getId());
            response.addProperty("rev", responses.get(i).getRev());
            DocsArray.add(response);
        }

        JsonObject response = new JsonObject();
        response.add("docs", DocsArray);
        response.addProperty("time", (end - start) + " ms");
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
