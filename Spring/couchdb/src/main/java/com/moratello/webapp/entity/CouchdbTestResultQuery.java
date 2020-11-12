package com.moratello.webapp.entity;

import java.io.Serializable;
import java.util.List;

import com.moratello.webapp.service.Doc;

import lombok.Data;

@Data
public class CouchdbTestResultQuery implements Serializable {
	
	private static final long serialVersionUID = -5411644221808179236L;
	
	// query result
	private List<Doc> Docs;
	
	// time
	private long time;

}
