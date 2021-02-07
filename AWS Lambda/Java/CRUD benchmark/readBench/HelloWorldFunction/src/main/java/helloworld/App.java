package helloworld;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import java.net.MalformedURLException;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;


import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import com.cloudant.client.api.*;
import com.cloudant.client.api.model.Response;
import com.cloudant.client.org.lightcouch.NoDocumentException;
import org.apache.commons.lang3.RandomStringUtils;

/**
 * Handler for requests to Lambda function.
 */
public class App implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    public APIGatewayProxyResponseEvent handleRequest(final APIGatewayProxyRequestEvent input, final Context context) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("X-Custom-Header", "application/json");

        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent()
                .withHeaders(headers);

        // Path parameter
        Map<String, String> pathPar = input.getPathParameters();

        // Show parameters
        System.out.println("dbname: " + pathPar.get("dbname"));
        System.out.println("Query: " + pathPar.get("query"));

        // get Parameters
        String dbname = pathPar.get("dbname");
        String query = pathPar.get("query");

        JsonObject output = new JsonObject();

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
            db = client.database(dbname, false);
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

        return response
                .withStatusCode(200)
                .withBody(output.toString());
    }
}
