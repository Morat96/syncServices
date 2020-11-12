package com.moratello.webapp.service;

import java.util.List;

import lombok.Data;

@Data
public class VehicleRoute {

	int load;
	Double cost;
	
	List<Coord> coords;
}
