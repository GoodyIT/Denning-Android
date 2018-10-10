package it.denning.share;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.quickblox.q_municate_db.utils.ErrorUtils;
import com.squareup.moshi.Json;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import info.hoang8f.android.segmented.SegmentedGroup;
import io.reactivex.disposables.CompositeDisposable;
import it.denning.R;
import it.denning.general.DIAlert;
import it.denning.general.DIConstants;
import it.denning.general.DIHelper;
import it.denning.general.DISharedPreferences;
import it.denning.general.EndlessRecyclerViewScrollListener;
import it.denning.general.MyCallbackInterface;
import it.denning.model.FirmModel;
import it.denning.model.ParentModel;
import it.denning.model.SearchKeyword;
import it.denning.model.SearchResultModel;
import it.denning.network.CompositeCompletion;
import it.denning.network.ErrorHandler;
import it.denning.network.NetworkManager;
import it.denning.network.RetrofitHelper;
import it.denning.network.services.DenningService;
import it.denning.search.SearchActivity;
import it.denning.search.utils.ClearableAutoCompleteTextView;
import it.denning.search.utils.OnItemClickListener;
import it.denning.ui.activities.base.BaseLoggableActivity;
import it.denning.utils.KeyboardUtils;
import it.denning.utils.MediaUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by denningit on 22/04/2017.
 */

public class DenningShareActivity extends BaseLoggableActivity implements OnItemClickListener{
    @BindView(R.id.upload_btn)
    Button uploadBtn;

    @BindView(R.id.search_autoCompleteTextView)
    ClearableAutoCompleteTextView searchView;

    @BindView(R.id.search_list)
    RecyclerView searchList;

    @BindView(R.id.search_layout)
    RelativeLayout searchLayout;

    @BindView(R.id.searh_toolbar_top)
    Toolbar toolbar;

    @BindView(R.id.file_attach_segmented)
    SegmentedGroup filterTabbar;

    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

    private Boolean isGeneralSearh;
    private CompositeDisposable mCompositeDisposable = new CompositeDisposable();
    private DenningService mSearchService;
    private Call<JsonElement> mSingle;
    Integer isAutoComplete = 1;
    Integer page = 1;
    Integer currentSearch;
    String  currentCode;
    String fileNo1, url;
    ArrayList<Parcelable> dataList = new ArrayList<>();
    JsonObject uploadParam = new JsonObject();

    DenningShareAdapter denningShareAdapter;
    LinearLayoutManager searchLayoutManager;
    private EndlessRecyclerViewScrollListener scrollListener;

    private String currentKeyword = "";

    @OnClick(R.id.back_btn)
    void onBack() {
        KeyboardUtils.hideKeyboard(this);
        finish();
    }

    @Override
    protected int getContentResId() {
        return R.layout.activity_file_share;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initFields();
        setSupportActionBar(toolbar);
        initActionBar();

        handleSendFile();

        determineServiceType();

        setupAutoComplete();

        setupSearchView();

        getBranch();
    }

    private void handleSendFile() {
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        if (Intent.ACTION_SEND.equals(action) && type != null) {
            dataList.add( getIntent().getParcelableExtra(Intent.EXTRA_STREAM));
        } else if (Intent.ACTION_SEND_MULTIPLE.equals(action) && type != null) {
           dataList.addAll(getIntent().getParcelableArrayListExtra(Intent.EXTRA_STREAM));
        }
    }

    private JsonArray buildShareItems() {
        JsonArray jsonArray = new JsonArray();
        for (Parcelable uri : dataList) {
            String mimeType = getContentResolver().getType((Uri) uri);
            String fileData = DIHelper.UriToBase64((Uri) uri);
            int fileLength = fileData.length();
            String fileName = ((Uri) uri).getLastPathSegment();

            JsonObject document = new JsonObject();
            document.addProperty("FileName", fileName);
            document.addProperty("MimeType", mimeType);
            document.addProperty("dateCreate", DIHelper.todayWithTime());
            document.addProperty("dateModify", DIHelper.todayWithTime());
            document.addProperty("fileLength", fileLength);
            document.addProperty("remarks", "file from Android");
            document.addProperty("base64", fileData);
            jsonArray.add(document);
        }
        return jsonArray;
    }

    private JsonObject buildParam() {

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("fileNo1", fileNo1);
        jsonObject.add("documents", buildShareItems());
        return jsonObject;
    }

    @OnClick(R.id.upload_btn)
    void didTapUpload() {
        if (url.matches("\\w*") && fileNo1.trim().length() == 0) {
            return;
        }
//        showActionBarProgress();
        showProgress();

        if (DISharedPreferences.getInstance().isStaff()) {
            NetworkManager.getInstance().sendPrivatePostRequest(url, buildParam(), new CompositeCompletion() {
                @Override
                public void parseResponse(JsonElement jsonElement) {
                    hideProgress();
                    ErrorUtils.showError(getApplicationContext(), "Successfully Uploaded");
                }
            }, new ErrorHandler() {
                @Override
                public void handleError(String error) {
                    hideProgress();
                    ErrorUtils.showError(getApplicationContext(), error);
                }
            });
        } else {
            NetworkManager.getInstance().sendPublicPostRequest(url, buildParam(), new CompositeCompletion() {
                @Override
                public void parseResponse(JsonElement jsonElement) {

                }
            }, new ErrorHandler() {
                @Override
                public void handleError(String error) {
//                    hideProgress();
                    ErrorUtils.showError(getApplicationContext(), error);
                }
            });
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mCompositeDisposable != null) {
            mCompositeDisposable.clear();
        }
    }

    private void showHorizontalProgress() {
        progressBar.setVisibility(View.VISIBLE);
    }

    private void hideHorizontalProgress() {
        progressBar.setVisibility(View.INVISIBLE);
    }

    private void initFields() {
        hideHorizontalProgress();

        if (!DISharedPreferences.getInstance().isStaff() && !DISharedPreferences.getInstance().isClient()) {
            DIAlert.showSimpleAlertWithCompletion(this, R.string.warning_title, R.string.alert_please_log_in, new MyCallbackInterface() {
                @Override
                public void nextFunction() {
                    finish();
                }

                @Override
                public void nextFunction(JsonElement jsonElement) {

                }
            });
        }

        currentKeyword = getIntent().getStringExtra("keyword");
        uploadBtn.setEnabled(false);
    }

    private void determineServiceType() {
        if (DISharedPreferences.getInstance(this).getUserType().equals(DISharedPreferences.STAFF_USER)) {
            mSearchService = new RetrofitHelper(this).getPrivateService();
            isGeneralSearh = true;
        }
    }

    public class AutoCompleteTextWatcher implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (s.toString().trim().length() != 0) {
                getSearchKeywords(s.toString().trim());
            }
        }
    }

    public class BranchTextWatcher implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            denningShareAdapter.filterBranchList(s.toString().trim());
        }
    }

    private void setupAutoComplete() {
        searchView.setShowAlways(true);
        searchView.setHint(DISharedPreferences.CurrentSearchType);
        searchView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    KeyboardUtils.hideKeyboard(DenningShareActivity.this);
                    currentKeyword = v.getText().toString();
                    isAutoComplete = 1;
                    denningShareAdapter.clear();
                    page = 1;
                    getSearchResult();
                }
                return false;
            }
        });

        if (DISharedPreferences.getInstance().isStaff()) {
            searchView.addTextChangedListener(new AutoCompleteTextWatcher());

            searchView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                        KeyboardUtils.hideKeyboard(DenningShareActivity.this);
                        currentKeyword = v.getText().toString();
                        isAutoComplete = 1;
                        denningShareAdapter.clear();
                        page = 1;
                        getSearchResult();
                    }
                    return false;
                }
            });


            searchView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    currentKeyword = (String) parent.getItemAtPosition(position);
                    KeyboardUtils.hideKeyboard(DenningShareActivity.this);
                    searchView.dismissDropDown();
                    isAutoComplete = 0;
                    denningShareAdapter.clear();
                    page = 1;
                    getSearchResult();
                }
            });
        } else {
            searchView.addTextChangedListener(new BranchTextWatcher());
        }

        searchView.setText(currentKeyword);
    }

    private void setupSearchView() {
        searchList.setHasFixedSize(true);
        searchLayoutManager = new LinearLayoutManager(this, OrientationHelper.VERTICAL, false);
        searchLayoutManager.setItemPrefetchEnabled(false);
        searchList.setLayoutManager(searchLayoutManager);
        searchList.setItemAnimator(new DefaultItemAnimator());
        denningShareAdapter = new DenningShareAdapter(new ArrayList<ParentModel>(),this);
        searchList.setAdapter(denningShareAdapter);
        searchList.setItemViewCacheSize(0);
        denningShareAdapter.setClickListener(this);
    }

    private void setupEndlessScroll() {
        scrollListener = new EndlessRecyclerViewScrollListener(searchLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                getSearchResult();
            }
        };
        searchList.clearOnScrollListeners();
        searchList.addOnScrollListener(scrollListener);
    }

    public void initActionBar() {
        DISharedPreferences.CurrentSearchType = DISharedPreferences.GENERAL_SEARCH;
        DISharedPreferences.CurrentCategory = 0;
        searchView.setHint("Denning Folders");

        filterTabbar.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int checkedIndex = 0;
                switch (checkedId) {
                    case R.id.attach_all:
                        checkedIndex = 0;
                        break;
                    case R.id.attach_file:
                        checkedIndex = 1;
                        break;
                    case R.id.attach_contact:
                        checkedIndex = 2;
                        break;
                    case R.id.transit_btn:
                        uploadBtn.setEnabled(true);
                        fileNo1 = "Transit Folder";
                        checkedIndex = 3;
                        url = DISharedPreferences.getInstance().getServerAPI() + DIConstants.MATTER_STAFF_TRANSIT_FOLDER;
                        break;
                }
                searchFromFilter(checkedIndex);
            }
        });
    }

    private void determineKeywordType(String keyword) {
        mSingle =  mSearchService.queryGeneralSearchKeywords(keyword);
    }

    private void determineSearchType() {
        switch (currentSearch) {
            case DIConstants.DENNING_SEARCH:
                if (isGeneralSearh) {
                    mSingle = mSearchService.queryGeneralSearch(currentKeyword, DISharedPreferences.CurrentCategory, page, isAutoComplete);
                } else {
                    mSingle = mSearchService.queryPublicSearch(currentKeyword, DISharedPreferences.CurrentCategory, page, isAutoComplete);
                }
                break;
        }
    }

    private void getBranch() {
        if (DISharedPreferences.getInstance().isStaff()) {
            return;
        }
        showProgress();
        NetworkManager.getInstance().sendPublicGetRequest(DIConstants.THIRD_PARTY_UPLOAD_CATEGORY, new CompositeCompletion() {
            @Override
            public void parseResponse(JsonElement jsonElement) {
                hideProgress();
                FirmModel[] models = new Gson().fromJson(jsonElement, FirmModel[].class);
                denningShareAdapter.swapItems(new ArrayList<ParentModel>(Arrays.asList(models)));
            }
        }, new ErrorHandler() {
            @Override
            public void handleError(String error) {
                hideProgress();
            }
        });
    }

    // Get the autocomplete result
    private void displaySearchKeywords(JsonElement jsonArray) {
        SearchKeyword[] keywords = new Gson().fromJson(jsonArray, SearchKeyword[].class);
        List<String> keywordList = new ArrayList<>();
        for (SearchKeyword searchKeyword : keywords) {
            keywordList.add(searchKeyword.getKeyword());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, keywordList);
        searchView.setAdapter(adapter);
        if (keywordList.size() > 0) {
            searchView.showDropDownIfFocused();
        }

        hideHorizontalProgress();
    }

    private void getSearchKeywords(String keyword) {
        showHorizontalProgress();
        determineKeywordType(keyword);

        mCompositeDisposable.clear();

        excecuteCompositeDisposable(new CompositeCompletion() {
            @Override
            public void parseResponse(JsonElement jsonElement) {
                displaySearchKeywords(jsonElement);
            }
        });
    }

    private void searchFromFilter(int tab) {
       denningShareAdapter.tab = tab;
       denningShareAdapter.filterSearchList();
    }

    private void getSearchResult() {
        showHorizontalProgress();
        currentSearch = DIConstants.DENNING_SEARCH;
        determineSearchType();
        mCompositeDisposable.clear();
        excecuteCompositeDisposable(new CompositeCompletion() {
            @Override
            public void parseResponse(JsonElement jsonElement) {
                parseSearchResult(jsonElement);
            }
        });
    }

    private void parseSearchResult(JsonElement jsonArray) {
        SearchResultModel[] searchArray = new Gson().fromJson(jsonArray, SearchResultModel[].class);
        if (searchArray.length > 0) {
            page++;
        }
        denningShareAdapter.swapItems(new ArrayList<ParentModel>(Arrays.asList(searchArray)));

        setupEndlessScroll();
        hideHorizontalProgress();
    }

    @Override
    public void onClick(View view, int position) {
        if (position == -1) {
            url = "";
            fileNo1 = "";
            uploadBtn.setEnabled(false);
            return;
        }
        uploadBtn.setEnabled(true);
        if (DISharedPreferences.getInstance().isStaff()) {
            final SearchResultModel searchResultModel = (SearchResultModel) denningShareAdapter.getModelArrayList().get(position);
            currentCode = searchResultModel.key;
            fileNo1 = searchResultModel.key;
            Integer type = DIHelper.determinSearchType(searchResultModel.form);
            switch (type) {
                case DIConstants.MATTER_TYPE:
                    url = DISharedPreferences.getInstance().getServerAPI() + DIConstants.MATTER_STAFF_CONTACT_FOLDER;
                    break;
                case DIConstants.CONTACT_TYPE:
                    url = DISharedPreferences.getInstance().getServerAPI() + DIConstants.MATTER_STAFF_FILEFOLDER;
                    break;
                default:
                    break;
            }
        } else {
            FirmModel firmModel = (FirmModel) denningShareAdapter.getModelArrayList().get(position);
            fileNo1 = firmModel.theCode;
            url = firmModel.APIServer + DIConstants.MATTER_CLIENT_FILEFOLDER;
        }
    }

    private void excecuteCompositeDisposable(final CompositeCompletion completion) {
        mSingle.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                if (!response.isSuccessful()) {
                    if (response.code() == 408) {
                        ErrorUtils.showError(DenningShareActivity.this,"Session expired. Please log in again.");
                        DISharedPreferences.getInstance().isSessionExpired = true;
                    } else {
                        ErrorUtils.showError(DenningShareActivity.this, response.message());
                    }
                } else {
                    completion.parseResponse(response.body());
                }
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable e) {
                hideProgress();
                hideHorizontalProgress();
                ErrorUtils.showError(DenningShareActivity.this, e.getMessage());
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("BaseActivity", "onActivityResult");
        if(requestCode == MediaUtils.DENNING_FILE_REQUEST_CODE){
            setRequestedOrientation(resultCode);
            finish();
        }
    }
}

