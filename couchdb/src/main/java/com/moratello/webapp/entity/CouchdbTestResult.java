package com.moratello.webapp.entity;

import java.io.Serializable;

import com.google.gson.JsonArray;

import lombok.Data;

@Data
public class CouchdbTestResult implements Serializable {

	private static final long serialVersionUID = -4687893061325288038L;

	// new documents
	//private JsonArray DocsArray;
	
	// time
	private long time;
	
}
