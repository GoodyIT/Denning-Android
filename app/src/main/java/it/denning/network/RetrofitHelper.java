package it.denning.network;

import android.content.Context;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import it.denning.general.DISharedPreferences;
import it.denning.network.services.DenningService;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by denningit on 2017-12-01.
 */

public class RetrofitHelper {
    private Context mContext;
    private String sessionID;
    private String email;
    private String baseUrl;
    private final String publicBaseUrl = "https://www.denningonline.com.my/";

    private static RetrofitHelper instance;

    public static RetrofitHelper getInstance(Context context) {
        if (instance == null) {
            instance = new RetrofitHelper(context);
        }

        return instance;
    }

    public RetrofitHelper(Context mContext) {
        this.mContext = mContext;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    private void setInitialValues() {
        sessionID = DISharedPreferences.getInstance(mContext).getSessionID();
        email = DISharedPreferences.getInstance(mContext).getEmail();
        baseUrl = DISharedPreferences.getInstance(mContext).getServerAPI();
    }

    public DenningService getPrivateService() {
        setInitialValues();
        final Retrofit retrofit = createPrivateRetrofit();
        return retrofit.create(DenningService.class);
    }

    public DenningService getPublicServiceWithEmail(String email) {
        setInitialValues();
        baseUrl = "https://www.denningonline.com.my/";
        final Retrofit retrofit = createPublicRetrofitWithEmail(email);
        return  retrofit.create(DenningService.class);
    }

    public DenningService getPublicService() {
        setInitialValues();
        baseUrl = "https://www.denningonline.com.my/";
        final Retrofit retrofit = createPublicRetrofit();
        return  retrofit.create(DenningService.class);
    }

    public DenningService getGoogleService() {
        setBaseUrl("https://maps.googleapis.com/");
        final Retrofit retrofit = createGoogleRetrofit();
        return  retrofit.create(DenningService.class);
    }

    public OkHttpClient createClient(final String _email, final String _sessionID) {
        final OkHttpClient.Builder httpClient =
                new OkHttpClient.Builder();
        httpClient.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                final Request original = chain.request();

                Request.Builder builder = original.newBuilder()
                        .header("Content-Type", "application/json")
                        .header("webuser-sessionid", _sessionID)
                        .header("webuser-id", _email);
                Request newRequest = builder.build();

                return chain.proceed(newRequest);
            }
        })
                .connectTimeout(20, TimeUnit.SECONDS)
                .writeTimeout(20, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS);

        return httpClient.build();
    }

    private OkHttpClient createGoogleClient() {
        final OkHttpClient.Builder httpClient =
                new OkHttpClient.Builder();
        httpClient.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                final Request original = chain.request();

                Request.Builder builder = original.newBuilder()
                        .header("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
                Request newRequest = builder.build();

                return chain.proceed(newRequest);
            }
        })
                .connectTimeout(20, TimeUnit.SECONDS)
                .writeTimeout(20, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS);

        return httpClient.build();
    }

    public OkHttpClient createPrivateClient() {
        return createClient(email, sessionID);
    }

    public OkHttpClient createPublicClient() {
        return createClient("android@denning.com.my", "{334E910C-CC68-4784-9047-0F23D37C9CF9}");
    }

    public OkHttpClient createPublicClientWithEmail(String email) {
        return createClient(email, "{334E910C-CC68-4784-9047-0F23D37C9CF9}");
    }

    /**
     * Creates a pre configured Retrofit instance
     */
    private Retrofit createRetrofit(OkHttpClient client, String _baseUrl) {
        return new Retrofit.Builder()
                .baseUrl(_baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
    }

    private Retrofit createGoogleRetrofit() {
        return createRetrofit(createGoogleClient(), baseUrl);
    }

    private Retrofit createPrivateRetrofit() {
        return createRetrofit(createPrivateClient(), baseUrl);
    }

    private Retrofit createPublicRetrofit() {
        return createRetrofit(createPublicClient(), publicBaseUrl);
    }

    private Retrofit createPublicRetrofitWithEmail(String email) {
        return createRetrofit(createPublicClientWithEmail(email), publicBaseUrl);
    }

    /**
     * Download Reftrofit
     */
}