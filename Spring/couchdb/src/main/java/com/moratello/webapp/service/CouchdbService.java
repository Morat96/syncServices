package com.moratello.webapp.service;

import com.moratello.webapp.entity.CouchdbTestResult;
import com.moratello.webapp.entity.CouchdbTestResultQuery;

public interface CouchdbService {

	public CouchdbTestResult createDocs(String dbname, int size, int ndocs, int sorts);
	
	public CouchdbTestResult deleteDocs(String dbname, int count);
	
	public CouchdbTestResult updateDocs(String dbname, int count, int size, int sorts);
	
	public CouchdbTestResultQuery getDocs(String dbname, String query);
	
}
