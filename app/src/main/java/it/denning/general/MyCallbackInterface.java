package it.denning.general;

import com.google.gson.JsonElement;

/**
 * Created by hothongmee on 10/11/2017.
 */

public interface MyCallbackInterface {
    public void nextFunction();

    public void nextFunction(JsonElement jsonElement);
}
