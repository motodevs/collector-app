package com.openvehicletracking.collector.db;


import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.UpdateOptions;

import java.io.Serializable;
import java.util.Objects;

/**
 * Created by oksuz on 23/09/2017.
 *
 */
public class Record implements Serializable {

    private final MongoCollection collection;
    private final UpdateOptions updateOptions = new UpdateOptions();
    private final String record;
    private Query condition;

    public Record(MongoCollection collection, JsonObject record) {
        Objects.requireNonNull(record, "recorc cannot be null");
        this.collection = collection;
        this.record = record.toString();
    }

    public MongoCollection getCollection() {
        return collection;
    }

    public JsonObject getRecord() {
        return new JsonObject(record);
    }

    public Query getCondition() {
        return condition;
    }

    public UpdateOptions getUpdateOptions() {
        return updateOptions;
    }

    public Record setCondition(Query updateQuery) {
        this.condition = updateQuery;
        return this;
    }

    public Record isMulti(boolean multi) {
        updateOptions.setMulti(multi);
        return this;
    }

    public Record isUpsert(boolean upsert) {
        updateOptions.setUpsert(upsert);
        return this;
    }
}
