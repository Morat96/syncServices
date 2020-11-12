package com.moratello.webapp.service;

import java.util.ArrayList;

public class Route {

    ArrayList<Customer> customers;
    double cost;
    int load; // load of the route (initially = 0)
    int capacity; // capacity variable indicating the capacity of the vehicles

    Route()
    {
        cost = 0;
        load = 0;
        capacity = 50;
        // A new arraylist of nodes is created
        customers = new ArrayList<Customer>();
    }
}