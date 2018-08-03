package it.denning.navigation.home.attendance;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.quickblox.q_municate_db.utils.ErrorUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.OnClick;
import it.denning.R;
import it.denning.general.DIAlert;
import it.denning.general.DIGeneralInterface;
import it.denning.general.DIHelper;
import it.denning.general.DISharedPreferences;
import it.denning.general.MyCallbackInterface;
import it.denning.model.Attendance;
import it.denning.network.CompositeCompletion;
import it.denning.network.ErrorHandler;
import it.denning.network.NetworkManager;
import it.denning.ui.activities.base.BaseActivity;
import it.denning.utils.KeyboardUtils;
import it.denning.utils.LocationUtil.PermissionUtils;

/**
 * Created by denningit on 2017-12-22.
 */

public class AttendanceActivity extends BaseActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,ActivityCompat.OnRequestPermissionsResultCallback,
        PermissionUtils.PermissionResultCallback {

    private final String TAG = AttendanceActivity.class.getSimpleName();

    private final static int PLAY_SERVICES_REQUEST = 1000;
    private final static int REQUEST_CHECK_SETTINGS = 2000;

    @BindView(R.id.top_layout)
    RelativeLayout topLayout;

    @BindView(R.id.time_textview)
    TextView time;

    @BindView(R.id.date_textview)
    TextView date;

    @BindView(R.id.name_textview)
    TextView name;

    @BindView(R.id.partner_textvidew)
    TextView userRole;

    @BindView(R.id.total_textview)
    TextView totalHours;

    @BindView(R.id.clock_btn)
    Button clockBtn;

    @BindView(R.id.break_btn)
    Button breakBtn;

    @BindView(R.id.toolbar_title)
    protected TextView toolbarTitle;

    @BindView(R.id.recycler_list)
    protected RecyclerView recyclerView;

    @OnClick(R.id.back_btn)
    void onBack() {
        KeyboardUtils.hideKeyboard(this);
        finish();
    }

    private Boolean isAttended = false, isBreaking = false;
    private AttendanceAdapter adapter;
    LinearLayoutManager linearLayoutManager;
    private Attendance attendance = null;
    private Location mLastLocation;
    String currentLocation = "";
    private GoogleApiClient mGoogleApiClient;

    double latitude;
    double longitude;

    ArrayList<String> permissions=new ArrayList<>();
    PermissionUtils permissionUtils;

    boolean isPermissionGranted = false;

    public static void start(Context context) {
        Intent intent = new Intent(context, AttendanceActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected int getContentResId() {
        return R.layout.activity_attendance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initFields();
        initActionBar();

        initLocationHelper();

        updateHeaderInfo();

        setupRecyclerView();
    }

    private void initFields() {
        toolbarTitle.setText(R.string.calendar_title);

        attendance = DISharedPreferences.attendance;
    }

    private void initLocationHelper() {
        permissionUtils=new PermissionUtils(AttendanceActivity.this);

        permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);

        permissionUtils.check_permission(permissions,"Need GPS permission for getting your location",1);

        // check availability of play services
        if (checkPlayServices()) {

            // Building the GoogleApi client
            buildGoogleApiClient();
        }
    }

    private void updateHeaderInfo() {
        String[] array = attendance.dtDate.split(" ");
        time.setText(array[1]);
        date.setText(DIHelper.getOnlyDateFromDateTime(attendance.dtDate));
        name.setText(attendance.clsStaff.strName);
        userRole.setText(attendance.clsStaff.strPositionTitle);
        totalHours.setText(attendance.totalWorkingHours);

        clockBtn.setText(attendance.btnLeft);
        if (attendance.btnRight.trim().length() != 0) {
            breakBtn.setText(attendance.btnRight);
        }

        isBreaking = attendance.btnRight.equals("END BREAK");

        if (attendance.btnLeft.toString().equals("CLOCK-OUT")) {
            isAttended = true;
            clockBtn.setBackgroundColor(getResources().getColor(R.color.accent));
            topLayout.setBackgroundColor(getResources().getColor(R.color.baby_blue));
        } else {
            isAttended = false;
            clockBtn.setBackgroundColor(getResources().getColor(R.color.baby_blue));
            topLayout.setBackgroundColor(getResources().getColor(R.color.accent));
        }

        if (!isBreaking) {
            breakBtn.setText("Start Break");
        }

        breakBtn.setEnabled(isAttended);
    }

    private void setupRecyclerView() {
        adapter = new AttendanceAdapter(attendance.theListing);
        linearLayoutManager = new LinearLayoutManager(this, OrientationHelper.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addItemDecoration(new it.denning.general.DividerItemDecoration(ContextCompat.getDrawable(this, R.drawable.item_decorator)));

        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
    }

    private void getLocation(final DIGeneralInterface myInterface) {
        if (isPermissionGranted) {
            try
            {
                FusedLocationProviderClient mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
                mFusedLocationClient.getLastLocation()
                        .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {
                                // Got last known location. In some rare situations, this can be null.
                                if (location != null) {
                                    mLastLocation = location;
                                    // Logic to handle location object
                                    latitude = location.getLatitude();
                                    longitude = location.getLongitude();

                                    getAddress();

                                    myInterface.completionBlock();
                                } else {
                                    enableButtons(false);
                                    DIAlert.showSimpleAlert(AttendanceActivity.this, R.string.warning_title, R.string.alert_location);
                                }
                            }
                        });

            }
            catch (SecurityException e)
            {
                e.printStackTrace();
            }
        }
    }

    private void enableButtons(Boolean enable) {
        clockBtn.setEnabled(enable);
        clockBtn.setEnabled(enable);
    }

    private void handleClockResponse(JsonObject jsonObject) {
        hideActionBarProgress();
        attendance = new Gson().fromJson(jsonObject, Attendance.class);
        updateHeaderInfo();
        adapter.addItems(attendance.theListing);
    }

    @OnClick(R.id.break_btn)
    void onBreak() {
        getLocation(new DIGeneralInterface() {
            @Override
            public void completionBlock() {
                _breakFunc();
            }
        });
    }

    public void _breakFunc() {
        showActionBarProgress();
        if (!isBreaking) {
            NetworkManager.getInstance().attendanceStartBreak(mLastLocation, currentLocation, new CompositeCompletion() {
                @Override
                public void parseResponse(JsonElement jsonElement) {
                    handleClockResponse(jsonElement.getAsJsonObject());
                }
            }, new ErrorHandler() {
                @Override
                public void handleError(String error) {
                    hideActionBarProgress();
                    ErrorUtils.showError(AttendanceActivity.this, error);
                }
            });
        } else {
            NetworkManager.getInstance().attendanceEndBreak(mLastLocation, currentLocation, new CompositeCompletion() {
                @Override
                public void parseResponse(JsonElement jsonElement) {
                    handleClockResponse(jsonElement.getAsJsonObject());
                }
            }, new ErrorHandler() {
                @Override
                public void handleError(String error) {
                    hideActionBarProgress();
                    ErrorUtils.showError(AttendanceActivity.this, error);
                }
            });
        }
    }

    @OnClick(R.id.clock_btn)
    void onClock() {
        getLocation(new DIGeneralInterface() {
            @Override
            public void completionBlock() {
                _clockFunc();
            }
        });
    }

    public void _clockFunc() {
        showActionBarProgress();
        if (!isAttended) {
            NetworkManager.getInstance().attendanceClockIn(mLastLocation, currentLocation, new CompositeCompletion() {
                @Override
                public void parseResponse(JsonElement jsonElement) {
                    handleClockResponse(jsonElement.getAsJsonObject());
                }
            }, new ErrorHandler() {
                @Override
                public void handleError(String error) {
                    hideActionBarProgress();
                    ErrorUtils.showError(AttendanceActivity.this, error);
                }
            });
        } else {
            NetworkManager.getInstance().attendanceClockOut(mLastLocation, currentLocation, new CompositeCompletion() {
                @Override
                public void parseResponse(JsonElement jsonElement) {
                    handleClockResponse(jsonElement.getAsJsonObject());
                }
            }, new ErrorHandler() {
                @Override
                public void handleError(String error) {
                    hideActionBarProgress();
                    ErrorUtils.showError(AttendanceActivity.this, error);
                }
            });
        }
    }

    public Address getAddress(double latitude, double longitude)
    {
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(latitude,longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            return addresses.get(0);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;

    }


    public void getAddress()
    {
        Address locationAddress=getAddress(latitude,longitude);

        if(locationAddress!=null)
        {
            String address = locationAddress.getAddressLine(0);
//            String address1 = locationAddress.getAddressLine(1);
//            String city = locationAddress.getLocality();
//            String state = locationAddress.getAdminArea();
//            String country = locationAddress.getCountryName();
//            String postalCode = locationAddress.getPostalCode();

            if(!TextUtils.isEmpty(address))
            {
                currentLocation=address;

//                if (!TextUtils.isEmpty(address1))
//                    currentLocation+="\n"+address1;
//
//                if (!TextUtils.isEmpty(city))
//                {
//                    currentLocation+="\n"+city;
//
//                    if (!TextUtils.isEmpty(postalCode))
//                        currentLocation+=" - "+postalCode;
//                }
//                else
//                {
//                    if (!TextUtils.isEmpty(postalCode))
//                        currentLocation+="\n"+postalCode;
//                }
//
//                if (!TextUtils.isEmpty(state))
//                    currentLocation+="\n"+state;
//
//                if (!TextUtils.isEmpty(country))
//                    currentLocation+="\n"+country;

               enableButtons(true);
            }

        }

    }

    /**
     * Creating google api client object
     * */

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();

        mGoogleApiClient.connect();

        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());

        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult locationSettingsResult) {

                final Status status = locationSettingsResult.getStatus();

                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // All location settings are satisfied. The client can initialize location requests here
                        getLocation(new DIGeneralInterface() {
                            @Override
                            public void completionBlock() {

                            }
                        });
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(AttendanceActivity.this, REQUEST_CHECK_SETTINGS);

                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        break;
                }
            }
        });
    }

    /**
     * Method to verify google play services on the device
     * */

    private boolean checkPlayServices() {

        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = googleApiAvailability.isGooglePlayServicesAvailable(this);

        if (resultCode != ConnectionResult.SUCCESS) {
            if (googleApiAvailability.isUserResolvableError(resultCode)) {
                googleApiAvailability.getErrorDialog(this,resultCode,
                        PLAY_SERVICES_REQUEST).show();
            } else {
                DIAlert.showSimpleAlertWithCompletion(AttendanceActivity.this, R.string.warning_title, R.string.alert_device_not_support, new MyCallbackInterface() {
                    @Override
                    public void nextFunction() {
                        finish();
                    }

                    @Override
                    public void nextFunction(JsonElement jsonElement) {

                    }
                });
            }
            return false;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        final LocationSettingsStates states = LocationSettingsStates.fromIntent(data);
        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        // All required changes were successfully made
                        getLocation(new DIGeneralInterface() {
                            @Override
                            public void completionBlock() {

                            }
                        });
                        break;
                    case Activity.RESULT_CANCELED:
                        // The user was asked to change settings, but chose not to
                        break;
                    default:
                        break;
                }
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkPlayServices();
    }

    /**
     * Google api callback methods
     */
    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = "
                + result.getErrorCode());
    }

    @Override
    public void onConnected(Bundle arg0) {

        // Once connected with google api, get the location
        getLocation(new DIGeneralInterface() {
            @Override
            public void completionBlock() {
            }
        });
    }

    @Override
    public void onConnectionSuspended(int arg0) {
        mGoogleApiClient.connect();
    }

    // Permission check functions
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        // redirects to utils
        permissionUtils.onRequestPermissionsResult(requestCode,permissions,grantResults);
    }

    @Override
    public void PermissionGranted(int request_code) {
        Log.i("PERMISSION","GRANTED");
        isPermissionGranted=true;
    }

    @Override
    public void PartialPermissionGranted(int request_code, ArrayList<String> granted_permissions) {
        Log.i("PERMISSION PARTIALLY","GRANTED");
    }

    @Override
    public void PermissionDenied(int request_code) {
        Log.i("PERMISSION","DENIED");
    }

    @Override
    public void NeverAskAgain(int request_code) {
        Log.i("PERMISSION","NEVER ASK AGAIN");
    }
}
