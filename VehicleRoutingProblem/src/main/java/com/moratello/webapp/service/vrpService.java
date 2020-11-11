package com.moratello.webapp.service;

import java.io.IOException;

import com.moratello.webapp.entity.Result;

public interface vrpService {

	public Result solveInstance(String body, boolean vns, int k_opt, int n_iter) throws IOException, Exception;
	
}

