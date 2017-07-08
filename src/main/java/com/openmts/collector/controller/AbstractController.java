package com.openmts.collector.controller;

import io.vertx.ext.web.RoutingContext;

/**
 * Created by yo on 08/07/2017.
 */
abstract public class AbstractController {


    protected final RoutingContext routingContext;

    public AbstractController(RoutingContext context) {
        this.routingContext = context;
        response();
    }

    abstract public void response();
}
