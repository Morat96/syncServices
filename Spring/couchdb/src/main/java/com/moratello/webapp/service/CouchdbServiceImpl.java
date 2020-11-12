package com.moratello.webapp.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.moratello.webapp.controller.CouchdbController;
import com.moratello.webapp.entity.CouchdbTestResult;
import com.moratello.webapp.entity.CouchdbTestResultQuery;
import com.google.gson.JsonObject;
import com.cloudant.client.api.*;
import com.cloudant.client.api.model.Response;
import org.apache.commons.lang3.RandomStringUtils;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
public class CouchdbServiceImpl implements CouchdbService {

	private static final Logger logger = LoggerFactory.getLogger(CouchdbController.class);
	
	@Value("${database.host}")
    private String host;
	
	@Value("${database.username}")
    private String username;
	
	@Value("${database.password}")
    private String password;
	
	@Override
	public CouchdbTestResult createDocs(String dbname, int size, int ndocs, int sorts) {
		
		// CouchDB server connection
        CloudantClient client = null;
        try {
        	logger.info("Connecting...");
            client = ClientBuilder.url(new URL(host))
                    .username(username)
                    .password(password)
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
        Database db = client.database(dbname, false);
        
        logger.info("example_db found");

        List<Response> responses = db.bulk(newDocs);

        long end = System.currentTimeMillis();
        logger.info("Time for computing sorting: " + (end - start) + " ms");
        
        CouchdbTestResult result = new CouchdbTestResult();
        
        result.setDocsArray(responses);
        result.setTime(end - start);
        
		return result;
	}

	@Override
	public CouchdbTestResult deleteDocs(String dbname, int count) {
		
		// CouchDB server connection
        CloudantClient client = null;
        try {
        	logger.info("Connecting...");
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
            db = client.database(dbname, false);
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
        	return new CouchdbTestResult();
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
		    
        CouchdbTestResult result = new CouchdbTestResult();
        
        result.setDocsArray(responses);
        result.setTime(end - start);
        
		return result;
	}
	
	@Override
	public CouchdbTestResult updateDocs(String dbname, int count, int size, int sorts) {
		
		// CouchDB server connection
        CloudantClient client = null;
        try {
        	logger.info("Connecting...");
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
            db = client.database(dbname, false);
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
        	return new CouchdbTestResult();
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
           
        CouchdbTestResult result = new CouchdbTestResult();
        
        result.setDocsArray(responses);
        result.setTime(end - start);
        
		return result;
	}
	
	@Override
	public CouchdbTestResultQuery getDocs(String dbname, String query) {
		
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
            db = client.database(dbname, false);
        }
        catch(Exception e) {
            e.printStackTrace();
        }

        List<Doc> allDocs = null;

        // Obtain all documents
        try {
            allDocs = db.getAllDocsRequestBuilder().includeDocs(true).build().getResponse().getDocsAs(Doc.class);
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        
        // number of documents
        int docsCount = allDocs.size();
        
        List<Doc> docsFiltered = new ArrayList<Doc>();

        // Make the query searching for documents which contain the word 'query'
        for (int i = 0; i < docsCount; i++) {
            if(allDocs.get(i).getContent() != null && allDocs.get(i).getContent().contains(query)){
            	docsFiltered.add(allDocs.get(i));
            }
        }
        
        System.out.println(docsFiltered);
        
        CouchdbTestResultQuery result = new CouchdbTestResultQuery();
        
        long end = System.currentTimeMillis();
        System.out.println("Time for computing sorting: " + (end - start) + " ms");
        
        result.setDocs(docsFiltered);
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
