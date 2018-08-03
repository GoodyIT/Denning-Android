package it.denning.ui.activities.denningfile;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
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
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.quickblox.q_municate_db.utils.ErrorUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import info.hoang8f.android.segmented.SegmentedGroup;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import it.denning.R;
import it.denning.general.DIConstants;
import it.denning.general.DIHelper;
import it.denning.general.DISharedPreferences;
import it.denning.general.EndlessRecyclerViewScrollListener;
import it.denning.model.Bank;
import it.denning.model.Contact;
import it.denning.model.DocumentModel;
import it.denning.model.LegalFirm;
import it.denning.model.MatterModel;
import it.denning.model.MatterProperty;
import it.denning.model.SearchKeyword;
import it.denning.model.SearchResultModel;
import it.denning.network.CompositeCompletion;
import it.denning.network.RetrofitHelper;
import it.denning.network.services.DenningService;
import it.denning.search.SearchActivity;
import it.denning.search.accounts.AccountsActivity;
import it.denning.search.bank.BankActivity;
import it.denning.search.contact.SearchContactActivity;
import it.denning.search.document.DocumentActivity;
import it.denning.search.legal_firm.LegalFirmActivity;
import it.denning.search.matter.MatterActivity;
import it.denning.search.property.PropertyActivity;
import it.denning.search.utils.ClearableAutoCompleteTextView;
import it.denning.search.utils.OnItemClickListener;
import it.denning.ui.activities.base.BaseActivity;
import it.denning.ui.activities.base.BaseLoggableActivity;
import it.denning.ui.activities.call.CallActivity;
import it.denning.utils.KeyboardUtils;
import it.denning.utils.MediaUtils;
import it.denning.utils.ToastUtils;

/**
 * Created by denningit on 22/04/2017.
 */

public class DenningFileActivity extends BaseLoggableActivity implements OnItemClickListener{
    @BindView(R.id.search_type)
    ImageButton searchTypeBtn;

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
    private Single<JsonElement> mSingle;
    Integer isAutoComplete = 1;
    Integer page = 1;
    Integer currentSearch;
    String  currentCode;
    String  documentTitle = "Contact Folder";

    DenningFileAdapter denningFileAdapter;
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
        return R.layout.activity_file_attach;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initFields();

        determineServiceType();

        setSupportActionBar(toolbar);

        initActionBar();

        setupAutoComplete();

        setupSearchView();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mCompositeDisposable != null) {
            mCompositeDisposable.clear();
        }
    }

    @OnClick(R.id.search_type)
    void tapSearchBtnType() {
        if (DISharedPreferences.getInstance(this).getUserType().equals(DISharedPreferences.STAFF_USER)) {
            changeSearchType();
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

        currentKeyword = getIntent().getStringExtra("keyword");
    }

    private void changeSearchType() {
        searchView.setHint(DISharedPreferences.CurrentSearchType);
        searchView.setText("");
        currentKeyword = "";
        denningFileAdapter.clear();
        isGeneralSearh = !isGeneralSearh;
    }

    private void determineServiceType() {
        if (DISharedPreferences.getInstance(this).getUserType().equals(DISharedPreferences.STAFF_USER)) {
            mSearchService = new RetrofitHelper(this).getPrivateService();
            isGeneralSearh = true;
        } else {
            mSearchService = new RetrofitHelper(this).getPublicService();
            isGeneralSearh = false;
        }
    }

    private void setupAutoComplete() {
        searchView.setShowAlways(true);
        searchView.setHint(DISharedPreferences.CurrentSearchType);
        searchView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    KeyboardUtils.hideKeyboard(DenningFileActivity.this);
                    currentKeyword = v.getText().toString();
                    isAutoComplete = 1;
                    denningFileAdapter.clear();
                    page = 1;
                    getSearchResult();
                }
                return false;
            }
        });

        searchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!editable.toString().isEmpty()) {
                    getSearchKeywords(editable.toString().trim());
                }
            }
        });

        searchView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                currentKeyword = (String) parent.getItemAtPosition(position);
                KeyboardUtils.hideKeyboard(DenningFileActivity.this);
                searchView.dismissDropDown();
                isAutoComplete = 0;
                denningFileAdapter.clear();
                page = 1;
                getSearchResult();
            }
        });

        searchView.setText(currentKeyword);
    }

    private void setupSearchView() {
        searchList.setHasFixedSize(true);
        searchLayoutManager = new LinearLayoutManager(this, OrientationHelper.VERTICAL, false);
        searchLayoutManager.setItemPrefetchEnabled(false);
        searchList.setLayoutManager(searchLayoutManager);
        searchList.setItemAnimator(new DefaultItemAnimator());
        denningFileAdapter = new DenningFileAdapter(new ArrayList<SearchResultModel>(),this);
        searchList.setAdapter(denningFileAdapter);
        searchList.setItemViewCacheSize(0);
        denningFileAdapter.setClickListener(this);
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
        if (!isGeneralSearh) {
            DISharedPreferences.CurrentSearchType = DISharedPreferences.PUBLIC_SEARCH;
            DISharedPreferences.CurrentCategory = -1;
            searchView.setHint("Public Search");
        } else {
            DISharedPreferences.CurrentSearchType = DISharedPreferences.GENERAL_SEARCH;
            DISharedPreferences.CurrentCategory = 0;
            searchView.setHint("General Search");
        }

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
                }
                searchFromFilter(checkedIndex);
            }
        });
    }

    @OnClick(R.id.transit_btn)
    void gotoTransitFolder() {

    }

    private void determineKeywordType(String keyword) {
        if (isGeneralSearh) {
            mSingle =  mSearchService.queryGeneralSearchKeywords(keyword);
        } else {
            mSingle = mSearchService.queryPublicSearchKeywords(keyword);
        }
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
            case DIConstants.CONTACT_TYPE:
                mSingle = mSearchService.getContact(currentCode);
                break;

            case DIConstants.MATTER_TYPE:
                mSingle = mSearchService.getMatter(currentCode);
                break;
            case DIConstants.DOCUMENT_TYPE:
                documentTitle = "File Folder";
                mSingle = mSearchService.getDocument(currentCode);
                break;
            case DIConstants.DOCUMENT_FOR_CONTACT_TYPE:
                documentTitle = "Contact Folder";
                mSingle = mSearchService.getDocumentFromContact(currentCode);
                break;
        }
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
       denningFileAdapter.tab = tab;
       denningFileAdapter.filterList();
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
        denningFileAdapter.swapItems(Arrays.asList(searchArray));

        setupEndlessScroll();
        hideHorizontalProgress();
    }

    @Override
    public void onClick(View view, int position) {
        final SearchResultModel searchResultModel = denningFileAdapter.getModelArrayList().get(position);
        currentCode = searchResultModel.key;
        mCompositeDisposable.clear();
        Integer type = DIHelper.determinSearchType(searchResultModel.form);
        switch (type) {
            case DIConstants.MATTER_TYPE:
                documentTitle = "File Folder";
                mSingle = mSearchService.getDocument(currentCode);
                break;
            case DIConstants.CONTACT_TYPE:
                documentTitle = "Contact Folder";
                mSingle = mSearchService.getDocumentFromContact(currentCode);
                break;
            default:
                break;
        }

        excecuteCompositeDisposable(new CompositeCompletion() {
            @Override
            public void parseResponse(JsonElement jsonElement) {
                DISharedPreferences.getInstance().documentModel = new Gson().fromJson(jsonElement, DocumentModel.class);
                _gotoDocumentActivity();
            }
        });
    }

    private void excecuteCompositeDisposable(final CompositeCompletion completion) {
        mCompositeDisposable.add(mSingle
                .subscribeOn(Schedulers.io())
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
                        hideProgress();
                        hideHorizontalProgress();
                        ErrorUtils.showError(DenningFileActivity.this, e.getMessage());
                    }
                })
        );
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

    private void _gotoDocumentActivity() {
        DISharedPreferences.isDenningFile = true;
        Intent intent = new Intent(this, DocumentActivity.class);
        intent.putExtra("title", title);
        startActivityForResult(intent, MediaUtils.DENNING_FILE_REQUEST_CODE);
        setResult(Activity.RESULT_OK, new Intent());
    }
}

