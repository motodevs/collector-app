package com.openvehicletracking.collector.codec;

import com.google.gson.Gson;
import com.openvehicletracking.collector.db.UpdateResult;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.MessageCodec;

/**
 * Created by yo on 08/10/2017.
 */
public class UpdateResultCodec implements MessageCodec<UpdateResult, UpdateResult> {

    private final Gson gson = new Gson();

    @Override
    public void encodeToWire(Buffer buffer, UpdateResult updateResult) {
        String rec = gson.toJson(updateResult);
        buffer.appendInt(rec.getBytes().length);
        buffer.appendString(rec);
    }

    @Override
    public UpdateResult decodeFromWire(int pos, Buffer buffer) {
        int length = buffer.getInt(pos);
        int begin = pos + 4;
        int end = begin + length;
        String toRecord = buffer.getString(begin, end);
        return gson.fromJson(toRecord, UpdateResult.class);
    }

    @Override
    public UpdateResult transform(UpdateResult updateResult) {
        return updateResult;
    }

    @Override
    public String name() {
        return this.getClass().getSimpleName();
    }

    @Override
    public byte systemCodecID() {
        return -1;
    }
}