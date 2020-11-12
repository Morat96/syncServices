package com.moratello.webapp.entity;

import java.io.Serializable;

import com.cloudant.client.api.model.Response;
import java.util.List;

import lombok.Data;

@Data
public class CouchdbTestResult implements Serializable {

	private static final long serialVersionUID = -4687893061325288038L;

	// new documents
	private List<Response> DocsArray;
	
	// time
	private long time;
	
	public String toString() {
		return "{\"ciao\": \"ciao\"}";
	}
}
