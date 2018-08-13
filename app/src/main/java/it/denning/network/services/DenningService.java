package it.denning.network.services;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

/**
 * Created by denningit on 2017-12-01.
 */

public interface DenningService {
    @GET("denningapi/v1/publicSearch/keyword")
    Call<JsonElement> queryPublicSearchKeywords(@Query("search") String keyword);

    @GET("v1/generalSearch/keyword")
    Call<JsonElement> queryGeneralSearchKeywords(@Query("search") String keyword);

    @GET("v1/generalSearch")
    Call<JsonElement> queryGeneralSearch(@Query("search") String keyword, @Query("category") Integer category, @Query("page") Integer page,  @Query("isAutoComplete") Integer isAutoComplete);

    @GET("denningapi/v1/publicSearch")
    Call<JsonElement> queryPublicSearch(@Query("search") String keyword, @Query("category") Integer category, @Query("page") Integer page, @Query("isAutoComplete") Integer isAutoComplete);

    /*
        Go to the search detail
     */
    @GET("v1/app/contact/{code}")
    Call<JsonElement> getContact(@Path("code") String code);

    @GET("v1/app/bank/branch/{code}")
    Call<JsonElement> getBank(@Path("code") String code);

    @GET("v1/app/GovOffice/{type}/{code}")
    Call<JsonElement> getGovOffice(@Path("code") String code, @Path("type") String type);

    @GET("denningapi/v1/GovOffice/{type}/{code}")
    Call<JsonElement> getPublicGovOffice(@Path("code") String code, @Path("type") String type);

    @GET("v1/app/Solicitor/{code}")
    Call<JsonElement> getLegalFirm(@Path("code") String code);

    @GET("denningapi/v1/app/Solicitor/{code}")
    Call<JsonElement> getPublicLegalFirm(@Path("code") String code);

    @GET("v1/app/Property/{code}")
    Call<JsonElement> getProperty(@Path("code") String code);

    @GET("v1/{code}/fileLedger")
    Call<JsonElement> getLedger(@Path("code") String code);

    @GET("v1/app/matter/{code}/fileFolder")
    Call<JsonElement> getDocument(@Path("code") String code);

    @GET("v1/app/contactFolder/{code}")
    Call<JsonElement> getDocumentFromContact(@Path("code") String code);

    @GET("v1/app/matter/{code}")
    Call<JsonElement> getMatter(@Path("code") String code);

    /*
        Deal with the buttons
     */

    @GET
    @Streaming
    Call<ResponseBody> downloadFile(@Url String url);

    @POST
    Call<JsonElement> postRequest(@Url String url, @Body JsonObject body);

    @PUT
    Call<JsonElement> putRequest(@Url String url, @Body JsonObject body);

    @GET("{url}")
    Call<JsonElement> getRequest(@Path("url") String url);

    @GET("/maps/api/geocode/json")
    Call<JsonElement> getEncodedRequest(@Query("key") String key,
                             @Query("latlng") String latlng);

//  post request for retrofit test
    @POST
    Call<JsonElement> myPostRequest(@Url String url, @Body JsonObject body);
}
