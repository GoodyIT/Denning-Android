package com.quickblox.q_municate_core.myservice;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface APIInterface {
    @GET("/denningapi/v2/chat/contact?")
    Call<ChatContactModel> doGetListofContacts(@Query("userid") String userid);
}
