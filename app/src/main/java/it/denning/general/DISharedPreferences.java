package it.denning.general;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;

import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import it.denning.App;
import it.denning.model.Accounts;
import it.denning.model.Attendance;
import it.denning.model.Bank;
import it.denning.model.Contact;
import it.denning.model.DocumentModel;
import it.denning.model.Event;
import it.denning.model.FileNote;
import it.denning.model.FirmModel;
import it.denning.model.FirmURLModel;
import it.denning.model.LegalFirm;
import it.denning.model.MatterSimple;
import it.denning.model.News;
import it.denning.model.Property;

/**
 * Created by denningit on 20/04/2017.
 */

public class DISharedPreferences {
    public static String EMAIL_SHARED_KEY       = "denning.email";
    public static String USERNAME_SHARED_KEY    = "denning.username";
    public static String PASSWORD_SHARED_KEY    = "denning.password";
    public static String PHONENUMBER_SHARED_KEY = "denning.phone";
    public static String USERTYPE_SHARED_KEY    = "denning.usertype";
    public static String SERVERAPI_SHARED_KEY   = "denning.serverapi";
    public static String SESSIONID_SHARED_KEY   = "denning.sessionid";
    public static String FIRMNAME_SHARED_KEY    = "denning.firmname";
    public static String FIRMCITY_SHARED_KEY    = "denning.firmcity";
    public static String STATUS_SHARED_KEY      = "denning.status";
    public static String CHECK_CLIENT_KEY       = "denning.client";
    public static String CHECK_STAFF_KEY        = "denning.staff";
    public static String CHECK_PUBLIC_KEY       = "denning.public.user";
    public static String AVATAR_URL_KEY         = "denning.avatar.url";
    public static String USER_AGREEMENT_KEY     = "denning.user.agreement";

    public static String GENERAL_SEARCH = "General Search";
    public static String PUBLIC_SEARCH = "Public Search";

    public static String STAFF_USER     = "denning";
    public static String CLIENT_USER    = "client";
    public static String PUBLIC_USER    = "public";

    public static Boolean isDirectSearch = false;

    public static String CurrentSearchType = "Denning Search";
    public static Integer CurrentCategory = -1;

    public static List<FirmModel> denningArray = new ArrayList<>();
    public static List<FirmModel> personalArray = new ArrayList<>();

    public static List<DocumentModel> folders;
    public static DocumentModel documentModel;
    public static List<Event> events;
    public static List<News> news;
    public static Attendance attendance;
    public static FileNote fileNote;
    public static Accounts accounts;
    public static LegalFirm legalFirm;
    public static Property property;
    public static Contact contact;
    public static MatterSimple selectedMatterSimple;
    public static File file;
    public static String keyword = "";
    public static boolean isDenningFile = false;

    public static String documentView = "nothing";
    public static String tempServerAPI = "";
    public static String tempTheCode = "";

    /* Search Models */
    public static Bank bank = null;

    private Context context;
    private static DISharedPreferences instance;
    private static SharedPreferences sharedPref;

    public static DISharedPreferences getInstance(Context context) {
        if (instance == null) {
            instance = new DISharedPreferences(context);
        }
        return instance;
    }

    public static DISharedPreferences getInstance() {
        if (instance == null) {
            instance = new DISharedPreferences(App.getInstance());
        }
        return instance;
    }

    private DISharedPreferences(Context context) {
        this.context = context;
        this.sharedPref =  context.getSharedPreferences("denningIT", Context.MODE_PRIVATE);
    }

    public  void getFirmServerArrayFromResponse(FirmURLModel firmURLModel) {
        DISharedPreferences.denningArray = firmURLModel.catDenning;
        if (DISharedPreferences.denningArray == null) {
            DISharedPreferences.denningArray = new ArrayList<>();
        }

        DISharedPreferences.personalArray = firmURLModel.catPersonal;
        if (DISharedPreferences.personalArray == null) {
            DISharedPreferences.personalArray = new ArrayList<>();
        }
    }

    public  void determineUserType() {
        saveValue(CHECK_CLIENT_KEY, DISharedPreferences.personalArray.size() > 0);
        saveValue(CHECK_STAFF_KEY, DISharedPreferences.denningArray.size() > 0);
        saveValue(CHECK_PUBLIC_KEY, (DISharedPreferences.personalArray.size() == 0) && (DISharedPreferences.denningArray.size() == 0));

        String userType = "";
        if (DISharedPreferences.denningArray.size() > 0) {
            userType = this.STAFF_USER;
        } else if(DISharedPreferences.personalArray.size() > 0) {
            userType = this.CLIENT_USER;
        } else {
            userType = this.PUBLIC_USER;
        }
    }

    public  void saveValue(String name, Object value) {
        SharedPreferences.Editor editor = sharedPref.edit();
        if (value instanceof String) {
            editor.putString(name, (String)value);
        } else if (value instanceof Boolean) {
            editor.putBoolean(name, (Boolean) value);
        }
        editor.commit();
    }

    public Object getValue(String name, Object defaultValue) {
        if (defaultValue instanceof String) {
            return this.sharedPref.getString(name, (String)defaultValue);
        } else if (defaultValue instanceof Boolean) {
            return this.sharedPref.getBoolean(name, (Boolean) defaultValue);
        }

        return "";
    }

    public void saveUserInfoFromResponse(FirmURLModel firmURLModel) {
        getFirmServerArrayFromResponse(firmURLModel);

        determineUserType();

        saveValue(USERNAME_SHARED_KEY, firmURLModel.name);
        saveValue(EMAIL_SHARED_KEY, firmURLModel.email);
        saveValue(PHONENUMBER_SHARED_KEY, firmURLModel.hpNumber);
        saveValue(SESSIONID_SHARED_KEY, firmURLModel.sessionID);
        saveValue(STATUS_SHARED_KEY, firmURLModel.status);
        saveValue(USERTYPE_SHARED_KEY, firmURLModel.userType);
        saveValue(AVATAR_URL_KEY, firmURLModel.avatar_url);
    }

    public void saveUserEmail(String email) {
        saveValue(DISharedPreferences.EMAIL_SHARED_KEY, email);
    }

    public void saveUserType(String userType) {
        saveValue(DISharedPreferences.USERTYPE_SHARED_KEY, userType);
    }

    public  void saveUserPassword(String password) {
        saveValue(PASSWORD_SHARED_KEY, password);
    }

    public void saveSessionID(String sessionID) {
        saveValue(SESSIONID_SHARED_KEY, sessionID);
    }

    public  void saveUserInfoFromNewDeviceLogin(FirmURLModel firmURLModel) {
        getFirmServerArrayFromResponse(firmURLModel);

        determineUserType();
    }

    public  void saveUserInfoFromChangePassword(FirmURLModel firmURLModel) {
        saveUserInfoFromNewDeviceLogin(firmURLModel);
    }

    public void saveServerAPI(FirmModel firmModel) {
        saveValue(SERVERAPI_SHARED_KEY, firmModel.APIServer);
        saveValue(FIRMNAME_SHARED_KEY, firmModel.LawFirm.name);
        saveValue(FIRMCITY_SHARED_KEY, firmModel.LawFirm.address.city);
    }

    public void setUserAgreement() {
        saveValue(USER_AGREEMENT_KEY, true);
    }

    public String getServerAPI() {
        return (String) getValue(SERVERAPI_SHARED_KEY, "http://43.252.215.81/");
    }

    public String getUserType() {
        return (String) getValue(USERTYPE_SHARED_KEY, this.CLIENT_USER);
    }

    public String getEmail() {
        return (String) getValue(EMAIL_SHARED_KEY, "");
    }

    public String getUsername() {
        return (String) getValue(USERNAME_SHARED_KEY, "");
    }

    public String getPassword() {
        return (String) getValue(PASSWORD_SHARED_KEY, "123456");
    }

    public String getSessionID() {
        return (String) getValue(SESSIONID_SHARED_KEY, "");
    }

    public String getPhoneNumber() {
        return (String) getValue(PHONENUMBER_SHARED_KEY, "");
    }

    public String getStatusCode() {
        return (String) getValue(STATUS_SHARED_KEY, "200");
    }

    public String getAvatarUrl() {return  (String) getValue(AVATAR_URL_KEY, "");}

    public Boolean isUserAgreement() {return (Boolean) getValue(USER_AGREEMENT_KEY, false);}

    public Boolean isClient() {return (Boolean) getValue(CHECK_CLIENT_KEY, false);}

    public Boolean isLoggedIn() {return getEmail().length() > 0;}

    public Boolean isStaff() {return (Boolean) getUserType().equals(DISharedPreferences.STAFF_USER);}

    public void clearData() {
        saveValue(USERNAME_SHARED_KEY, "");
        saveValue(EMAIL_SHARED_KEY, "");
        saveValue(PHONENUMBER_SHARED_KEY, "");
        saveValue(SESSIONID_SHARED_KEY, "");
        saveValue(STATUS_SHARED_KEY,  "");
        saveValue(USERTYPE_SHARED_KEY, "");
        saveValue(AVATAR_URL_KEY, "");
    }
}
