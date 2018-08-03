package it.denning.general;

import okhttp3.Call;
import okhttp3.OkHttpClient;

/**
 * Created by hothongmee on 09/09/2017.
 */

public class OkHttpUtils {
    public static void cancelCallWithTag(OkHttpClient client, String tag) {
        for(Call call : client.dispatcher().queuedCalls()) {
            if(call.request().tag().equals(tag))
                call.cancel();
        }
        for(Call call : client.dispatcher().runningCalls()) {
            if(call.request().tag().equals(tag))
                call.cancel();
        }
    }
}
