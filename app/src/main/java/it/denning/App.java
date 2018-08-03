package it.denning;

import android.content.Context;
import android.support.multidex.MultiDexApplication;
import android.util.Log;

import com.beardedhen.androidbootstrap.TypefaceProvider;
import com.crashlytics.android.Crashlytics;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.quickblox.auth.session.QBSettings;
import com.quickblox.chat.QBChatService;
import com.quickblox.chat.connections.tcp.QBTcpChatConnectionFabric;
import com.quickblox.chat.connections.tcp.QBTcpConfigurationBuilder;
import com.quickblox.core.LogLevel;
import com.quickblox.core.QBHttpConnectionConfig;
import com.quickblox.core.ServiceZone;
import com.quickblox.q_municate_auth_service.QMAuthService;
import com.quickblox.q_municate_core.utils.ConstsCore;
import com.quickblox.q_municate_db.managers.DataManager;
import com.quickblox.q_municate_user_cache.QMUserCacheImpl;
import com.quickblox.q_municate_user_service.QMUserService;
import com.quickblox.q_municate_user_service.cache.QMUserCache;

import io.fabric.sdk.android.Fabric;
import it.denning.utils.ActivityLifecycleHandler;
import it.denning.utils.StringObfuscator;
import it.denning.utils.helpers.ServiceManager;
import it.denning.utils.helpers.SharedHelper;
import it.denning.utils.image.ImageLoaderUtils;

public class App extends MultiDexApplication {

    private static final String TAG = App.class.getSimpleName();

    private static App instance;
    private SharedHelper appSharedHelper;
    private SessionListener sessionListener;


    public static App getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {

        super.onCreate();
        Log.i(TAG, "onCreate with update");
        initFabric();
        initApplication();
        registerActivityLifecycleCallbacks(new ActivityLifecycleHandler());

        TypefaceProvider.registerDefaultIconSets();
    }


    private void initFabric() {
        Crashlytics crashlyticsKit = new Crashlytics.Builder()
//                .core(new CrashlyticsCore.Builder().disabled(BuildConfig.DEBUG).build())
                .build();

        Fabric.with(this, crashlyticsKit);
    }

    private void initApplication() {
        instance = this;

        sessionListener = new SessionListener();
        getAppSharedHelper();
        initQb();
        initDb();
        initImageLoader(this);
        initServices();
    }

    private void initQb() {
        QBSettings.getInstance().init(getApplicationContext(),
                StringObfuscator.getApplicationId(),
                StringObfuscator.getAuthKey(),
                StringObfuscator.getAuthSecret());
        QBSettings.getInstance().setAccountKey(StringObfuscator.getAccountKey());

        QBSettings.getInstance().setLogLevel(LogLevel.DEBUG);

        initDomains();
        initHTTPConfig();

        QBTcpConfigurationBuilder configurationBuilder = new QBTcpConfigurationBuilder()
                .setAutojoinEnabled(false)
                .setSocketTimeout(0);

        QBChatService.setConnectionFabric(new QBTcpChatConnectionFabric(configurationBuilder));

        QBChatService.setDebugEnabled(true);
    }

    private void initDomains() {
        QBSettings.getInstance().setEndpoints(StringObfuscator.getApiEndpoint(), StringObfuscator.getChatEndpoint(), ServiceZone.PRODUCTION);
        QBSettings.getInstance().setZone(ServiceZone.PRODUCTION);
    }

    private void initHTTPConfig(){
        QBHttpConnectionConfig.setConnectTimeout(ConstsCore.HTTP_TIMEOUT_IN_SECONDS);
        QBHttpConnectionConfig.setReadTimeout(ConstsCore.HTTP_TIMEOUT_IN_SECONDS);
    }

    private void initDb() {
        DataManager.init(this);
    }

    private void initImageLoader(Context context) {
        ImageLoader.getInstance().init(ImageLoaderUtils.getImageLoaderConfiguration(context));
    }

    private void initServices() {
        QMAuthService.init();
        QMUserCache userCache = new QMUserCacheImpl(this);
        QMUserService.init(userCache);

        ServiceManager.getInstance();
    }

    public synchronized SharedHelper getAppSharedHelper() {
        return appSharedHelper == null
                ? appSharedHelper = new SharedHelper(this)
                : appSharedHelper;
    }

}