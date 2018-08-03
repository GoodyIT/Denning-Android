package it.denning.network;

import com.google.gson.JsonElement;

/**
 * Created by denningit on 2017-12-02.
 */

public interface CompositeCompletion {
    public void parseResponse(JsonElement jsonElement);
}
