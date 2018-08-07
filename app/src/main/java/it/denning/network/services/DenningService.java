package it.denning.network.services;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import butterknife.BindView;
import io.reactivex.Single;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
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
    Single<JsonElement> queryPublicSearchKeywords(@Query("search") String keyword);

    @GET("v1/generalSearch/keyword")
    Single<JsonElement> queryGeneralSearchKeywords(@Query("search") String keyword);

    @GET("v1/generalSearch")
    Single<JsonElement> queryGeneralSearch(@Query("search") String keyword, @Query("category") Integer category, @Query("page") Integer page,  @Query("isAutoComplete") Integer isAutoComplete);

    @GET("denningapi/v1/publicSearch")
    Single<JsonElement> queryPublicSearch(@Query("search") String keyword, @Query("category") Integer category, @Query("page") Integer page, @Query("isAutoComplete") Integer isAutoComplete);

    /*
        Go to the search detail
     */
    @GET("v1/app/contact/{code}")
    Single<JsonElement> getContact(@Path("code") String code);

    @GET("v1/app/bank/branch/{code}")
    Single<JsonElement> getBank(@Path("code") String code);

    @GET("v1/app/GovOffice/{type}/{code}")
    Single<JsonElement> getGovOffice(@Path("code") String code, @Path("type") String type);

    @GET("denningapi/v1/GovOffice/{type}/{code}")
    Single<JsonElement> getPublicGovOffice(@Path("code") String code, @Path("type") String type);

    @GET("v1/app/Solicitor/{code}")
    Single<JsonElement> getLegalFirm(@Path("code") String code);

    @GET("denningapi/v1/app/Solicitor/{code}")
    Single<JsonElement> getPublicLegalFirm(@Path("code") String code);

    @GET("v1/app/Property/{code}")
    Single<JsonElement> getProperty(@Path("code") String code);

    @GET("v1/{code}/fileLedger")
    Single<JsonElement> getLedger(@Path("code") String code);

    @GET("v1/app/matter/{code}/fileFolder")
    Single<JsonElement> getDocument(@Path("code") String code);

    @GET("v1/app/contactFolder/{code}")
    Single<JsonElement> getDocumentFromContact(@Path("code") String code);

    @GET("v1/app/matter/{code}")
    Single<JsonElement> getMatter(@Path("code") String code);

    /*
        Deal with the buttons
     */

    @GET
    @Streaming
    Call<ResponseBody> downloadFile(@Url String url);

    @POST
    Single<JsonElement> postRequest(@Url String url, @Body JsonObject body);

    @PUT
    Single<JsonElement> putRequest(@Url String url, @Body JsonObject body);

    @GET("{url}")
    Single<JsonElement> getRequest(@Path("url") String url);

    @GET("/maps/api/geocode/json")
    Single<JsonElement> getEncodedRequest(@Query("key") String key,
                             @Query("latlng") String latlng);
}
