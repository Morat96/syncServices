package com.moratello.webapp.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.moratello.webapp.controller.CouchdbController;
import com.moratello.webapp.entity.CouchdbTestResult;

import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import com.cloudant.client.api.*;
import com.cloudant.client.api.model.Response;
import org.apache.commons.lang3.RandomStringUtils;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class CouchdbServiceImpl implements CouchdbService {

	private static final Logger logger = LoggerFactory.getLogger(CouchdbController.class);
	
	@Override
	public CouchdbTestResult doTest(int size, int ndocs, int sorts) {
		
		// CouchDB server connection
        CloudantClient client = null;
        try {
        	logger.info("Connecting...");
            client = ClientBuilder.url(new URL("http://10.100.206.79:5984"))
                    .username("admin")
                    .password("5rIVPxfad0iSxN8OWJDY")
                    .build();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        
        // Show the server version
        logger.info("Server Version: " + client.serverVersion());
        
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
            
            //logger.info("Doc [" + (doc + 1) + "] : " + document);

            newDoc.addProperty("content", document);

            newDocs.add(newDoc);

        }
        
        // Get a Database instance
        Database db = client.database("example_db", false);
        
        logger.info("example_db found");

        List<Response> responses = db.bulk(newDocs);

        long end = System.currentTimeMillis();
        logger.info("Time for computing sorting: " + (end - start) + " ms");

        JsonArray DocsArray = new JsonArray();
        for (int i = 0; i < responses.size(); i++) {

            JsonObject response = new JsonObject();
            response.addProperty("_id", responses.get(i).getId());
            response.addProperty("rev", responses.get(i).getRev());
            DocsArray.add(response);
        }
        
        CouchdbTestResult result = new CouchdbTestResult();
        
        //result.setDocsArray(DocsArray);
        result.setTime(end - start);
        
		return result;
	}
	
	// method that generate a document
    public static ArrayList<String> generateDocument(int size) {

        ArrayList<String> words = new ArrayList<String>();
        String randomWord = "";

        // generate a document compose of "size" random words
        for (int i = 0; i < size; i++) {
            new RandomStringUtils();
			randomWord = RandomStringUtils.randomAlphabetic(3, 9);
            words.add(randomWord);
        }

        return words;
    }
	
}
