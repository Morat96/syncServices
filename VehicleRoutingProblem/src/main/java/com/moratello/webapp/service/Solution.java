package com.moratello.webapp.service;

import java.util.ArrayList;

public class Solution implements Cloneable {

    double cost;
    ArrayList<Route> routes;

    Solution ()
    {
        routes = new ArrayList<Route>();
        cost = 0;
    }

    public Object clone() throws
            CloneNotSupportedException
    {
        return super.clone();
    }
}