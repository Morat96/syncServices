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
 * This function delete a number of random documents.
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
            if (split.length != 3) {
                response.addProperty("statusCode", 400);
                response.add("headers", header);
                bodyError.addProperty("message", "The path must be: ../createBenchJava/{dbname}/{count}");
                response.add("body", bodyError);
                return response;
            }
            db_selected = split[1];
            count = Integer.parseInt(split[2]);
        }
        
        // CouchDB server connection
        CloudantClient client = null;
        try {
            client = ClientBuilder.url(new URL("http://192.168.30.151:8081"))
                    .username("admin")
                    .password("sX5IWFOsWX3BKClsxB8G")
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
}