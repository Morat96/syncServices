package com.moratello.webapp.entity;

import java.io.Serializable;
import java.util.List;

import com.moratello.webapp.service.VehicleRoute;

import lombok.Data;

@Data
public class Result implements Serializable {

	private static final long serialVersionUID = 2825708713580330000L;
	
	private double cost;
	
	List<VehicleRoute> solution;

}
