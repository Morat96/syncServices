package com.moratello.webapp.service;

import com.moratello.webapp.entity.CouchdbTestResult;

public interface CouchdbService {

	public CouchdbTestResult doTest(int size, int ndocs, int sorts);
	
}
