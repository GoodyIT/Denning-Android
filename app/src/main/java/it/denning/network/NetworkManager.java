package it.denning.network;

import android.content.Context;
import android.location.Location;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.quickblox.q_municate_db.utils.ErrorUtils;

import org.json.JSONException;

import java.net.SocketTimeoutException;
import java.util.Map;
import java.util.Set;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import it.denning.App;
import it.denning.general.DIConstants;
import it.denning.general.DIHelper;
import it.denning.general.DISharedPreferences;
import it.denning.general.MyCallbackInterface;
import it.denning.general.MyNameCodeCallback;
import it.denning.general.MySimpleCallback;
import it.denning.model.NameCode;
import it.denning.model.Payment;
import it.denning.model.StaffModel;
import it.denning.network.services.DenningService;
import it.denning.utils.helpers.ServiceManager;
import retrofit2.HttpException;

/**
 * Created by denningit on 2017-12-09.
 */

public class NetworkManager {
    public static final String TAG = ServiceManager.class.getSimpleName();

    private static NetworkManager instance;

    private CompositeDisposable mCompositeDisposable;
    private Single<JsonElement> mSingle;

    private Context context;

    public static NetworkManager getInstance() {
        if (instance == null) {
            instance = new NetworkManager();
        }

        return instance;
    }

    private NetworkManager() {
        this.context = App.getInstance();
        if (mCompositeDisposable == null) {
            mCompositeDisposable = new CompositeDisposable();
        }
    }

    private DenningService getGoogleService() {
        return RetrofitHelper.getInstance(context).getGoogleService();
    }

    private DenningService getPrivateService() {
        return RetrofitHelper.getInstance(context).getPrivateService();
    }

    private DenningService getPublicService() {
        return RetrofitHelper.getInstance(context).getPublicService();
    }

    private void sendRequest(final CompositeCompletion completion, final ErrorHandler errorHandler) {
        mCompositeDisposable.add(mSingle.
                subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Function<JsonElement, JsonElement>() {
                    @Override
                    public JsonElement apply(JsonElement jsonElement) throws Exception {
                        return jsonElement;
                    }
                })
                .subscribeWith(new DisposableSingleObserver<JsonElement>() {
                    @Override
                    public void onSuccess(JsonElement jsonElement) {
                        completion.parseResponse(jsonElement);
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (e instanceof HttpException && ((HttpException) e).code() == 410) {
                            errorHandler.handleError("Session expired. Please log in again.");
                        } else if (e instanceof SocketTimeoutException){
                            errorHandler.handleError("Cannot reach the server.");
                        } else if (e instanceof HttpException && ((HttpException) e).code() == 400) {
                            e.printStackTrace();
                        } else {
                            errorHandler.handleError(e.getMessage());
                        }
                        e.printStackTrace();
                    }
                })
        );
    }

    public void sendPublicPostRequest(String url, JsonObject param, final CompositeCompletion completion, final ErrorHandler errorHandler) {
        mSingle = getPublicService().postRequest(url, buildParamWithDefault(param));
        sendRequest(completion, errorHandler);
    }

    public void sendPublicPutRequest(String url, JsonObject param, final CompositeCompletion completion, final ErrorHandler errorHandler) {
        mSingle = getPublicService().putRequest(url, buildParamWithDefault(param));
        sendRequest(completion, errorHandler);
    }

    public void sendPrivatePostRequest(String url, JsonObject param, final CompositeCompletion completion, final ErrorHandler errorHandler) {
        mSingle = getPrivateService().postRequest(url, buildParamWithDefault(param));
        sendRequest(completion, errorHandler);
    }

    public void sendPrivatePutRequest(String url, JsonObject param, final CompositeCompletion completion, final ErrorHandler errorHandler) {
        mSingle = getPrivateService().putRequest(url, buildParamWithDefault(param));
        sendRequest(completion, errorHandler);
    }

    public void sendGoogleGet(String latlng, final CompositeCompletion completion) {
        mSingle = getGoogleService().getEncodedRequest(DIConstants.kGoogleMapKey, latlng);
        sendRequest(completion, new ErrorHandler() {
            @Override
            public void handleError(String error) {
                ErrorUtils.showError(context, error);
            }
        });
    }

    public void sendPublicGetRequest(String url, final CompositeCompletion completion, final ErrorHandler errorHandler) {
        mSingle = getPublicService().getRequest(url);
        sendRequest(completion, errorHandler);
    }

    public void sendPrivateGetRequest(String url, final CompositeCompletion completion, final ErrorHandler errorHandler) {
        mSingle = getPrivateService().getRequest(url);
        sendRequest(completion, errorHandler);
    }

    public void sendPrivateGetRequestWithoutError(String url, final CompositeCompletion completion, final Context context) {
        mSingle = getPrivateService().getRequest(url);
        sendPrivateGetRequest(url, completion, new ErrorHandler() {
            @Override
            public void handleError(String error) {
                ErrorUtils.showError(context, error);
            }
        });
    }

    private JsonObject buildDefaultParam() {

        JsonObject json = new JsonObject();

        json.addProperty("ipWAN", DIHelper.getIPWAN());
        json.addProperty("ipLAN", DIHelper.getIPLAN());
        json.addProperty("OS", DIHelper.getOS());
        json.addProperty("device", DIHelper.getDevice());
        json.addProperty("deviceName", DIHelper.getDeviceName());
        json.addProperty("MAC", DIHelper.getMAC(App.getInstance()));
        return json;
    }

    private JsonObject buildParamWithDefault(JsonObject input) {
        return mergeJSONObjects(input, buildDefaultParam());
    }

    public static JsonObject mergeJSONObjects(JsonObject json1, JsonObject json2) {
        Set<Map.Entry<String, JsonElement>> map1 = json1.entrySet();
        Set<Map.Entry<String, JsonElement>> map2 = json2.entrySet();
        JsonObject mergedJSON = new JsonObject();

        for (Map.Entry<String, JsonElement> entry : map1) {
            mergedJSON.add(entry.getKey(), entry.getValue());
        }

        for (Map.Entry<String, JsonElement> entry : map2) {
            mergedJSON.add(entry.getKey(), entry.getValue());
        }

        return mergedJSON;
    }

    public void clearQueue() {
        mCompositeDisposable.clear();
    }

    public void mainLogin(JsonObject param, final CompositeCompletion completion, final ErrorHandler errorHandler) {
        sendPublicPostRequest(DIConstants.AUTH_SIGNIN_URL, param, completion, errorHandler);
    }

    public void publicSignIn(String url, final CompositeCompletion completion, final ErrorHandler errorHandler) {
        JsonObject param = new JsonObject();
        param.addProperty("email", DISharedPreferences.getInstance(context).getEmail());
        param.addProperty("sessionID", DISharedPreferences.getInstance(context).getSessionID());
        param.addProperty("password", DISharedPreferences.getInstance(context).getPassword());

        sendPublicPostRequest(url, param, completion, errorHandler);
    }

    public void staffSignIn(final CompositeCompletion completion, final ErrorHandler errorHandler) {
        publicSignIn(DISharedPreferences.getInstance(context).getServerAPI() + DIConstants.STAFF_SIGNIN_URL, completion, errorHandler);
    }

    public void clientSignIn(final CompositeCompletion completion, final ErrorHandler errorHandler) {
        publicSignIn(DISharedPreferences.tempServerAPI + DIConstants.CLIENT_SIGNIN_URL, completion, errorHandler);
    }

    public void clientFirstLogin(final CompositeCompletion completion, final ErrorHandler errorHandler) {
        publicSignIn(DISharedPreferences.tempServerAPI + DIConstants.CLIENT_FIRST_SIGNIN_URL, completion, errorHandler);
    }

    public void loadAds(final CompositeCompletion completion, final ErrorHandler errorHandler) {
        sendPublicGetRequest(DIConstants.HOME_ADS_GET_URL, completion, errorHandler);
    }

    private JsonObject buildAttendanceParam(Location location, String place) {
        JsonObject param = new JsonObject();
        String locationString = location.getLatitude() + "," + location.getLongitude();
        param.addProperty("strLocationLong", locationString);
        param.addProperty("strLocationName", place);
        param.addProperty("strRemarks", "Start Work");

        return param;
    }

    public void attendanceClockIn(Location location, String place, final CompositeCompletion completion, final ErrorHandler errorHandler) {
        String url = DISharedPreferences.getInstance().getServerAPI() + DIConstants.ATTENDANCE_CLOCK_IN;

        sendPrivatePostRequest(url, buildAttendanceParam(location, place), completion, errorHandler);
    }

    public void attendanceClockOut(Location location, String place, final CompositeCompletion completion, final ErrorHandler errorHandler) {
        String url = DISharedPreferences.getInstance().getServerAPI() + DIConstants.ATTENDANCE_CLOCK_IN;

        sendPrivatePutRequest(url, buildAttendanceParam(location, place), completion, errorHandler);
    }

    public void attendanceStartBreak(Location location, String place, final CompositeCompletion completion, final ErrorHandler errorHandler) {
        String url = DISharedPreferences.getInstance().getServerAPI() + DIConstants.ATTENDANCE_BREAK;

        sendPrivatePostRequest(url, buildAttendanceParam(location, place), completion, errorHandler);
    }

    public void attendanceEndBreak(Location location, String place, final CompositeCompletion completion, final ErrorHandler errorHandler) {
        String url = DISharedPreferences.getInstance().getServerAPI() + DIConstants.ATTENDANCE_BREAK;

        sendPrivatePutRequest(url, buildAttendanceParam(location, place), completion, errorHandler);
    }

    public void getSubmittedBy(final MyNameCodeCallback callback){
        NetworkManager.getInstance().sendPrivateGetRequestWithoutError(DIConstants.LEAVE_SUBMITTED_BY_URL, new CompositeCompletion() {
            @Override
            public void parseResponse(JsonElement jsonElement) {
                manageSubmittedByResponse(jsonElement, callback);
            }
        }, App.getInstance());
    }

    private void manageSubmittedByResponse(JsonElement jsonElement, MyNameCodeCallback callback) {
        NameCode submittedBy = new NameCode();
        submittedBy.name = jsonElement.getAsJsonObject().get("strName").getAsString();
        submittedBy.code = jsonElement.getAsJsonObject().get("code").getAsString();
        callback.next(submittedBy);
    }

    public void getPrimaryClient(final MyCallbackInterface callbackInterface) {
       sendPrivateGetRequestWithoutError(DIConstants.CONTACT_GET_URL, new CompositeCompletion() {
           @Override
           public void parseResponse(JsonElement jsonElement) {
               callbackInterface.nextFunction(jsonElement);
           }
       }, App.getInstance());
    }
}