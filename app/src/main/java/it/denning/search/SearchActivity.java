package it.denning.search;

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
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ProgressBar;
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
import io.reactivex.disposables.CompositeDisposable;
import it.denning.R;
import it.denning.general.DIAlert;
import it.denning.general.DIConstants;
import it.denning.general.DIHelper;
import it.denning.general.DISharedPreferences;
import it.denning.general.EndlessRecyclerViewScrollListener;
import it.denning.model.Accounts;
import it.denning.model.Bank;
import it.denning.model.Contact;
import it.denning.model.DocumentModel;
import it.denning.model.LegalFirm;
import it.denning.model.MatterModel;
import it.denning.model.Property;
import it.denning.model.SearchKeyword;
import it.denning.model.SearchResultModel;
import it.denning.navigation.add.property.AddPropertyActivity;
import it.denning.navigation.home.upload.UploadActivity;
import it.denning.network.CompositeCompletion;
import it.denning.network.RetrofitHelper;
import it.denning.network.services.DenningService;
import it.denning.search.accounts.AccountsActivity;
import it.denning.search.bank.BankActivity;
import it.denning.search.contact.SearchContactActivity;
import it.denning.search.document.DocumentActivity;
import it.denning.search.filenote.FileNoteActivity;
import it.denning.search.legal_firm.LegalFirmActivity;
import it.denning.search.matter.MatterActivity;
import it.denning.search.paymentrecord.PaymentRecordActivity;
import it.denning.search.template.TemplateActivity;
import it.denning.search.utils.ClearableAutoCompleteTextView;
import it.denning.search.utils.OnAccountsClickListener;
import it.denning.search.utils.OnFileFolderClickListener;
import it.denning.search.utils.OnFileNoteClickListener;
import it.denning.search.utils.OnItemClickListener;
import it.denning.search.utils.OnPaymentRecordClickListener;
import it.denning.search.utils.OnRelatedMatterClickListener;
import it.denning.search.utils.OnTemplateClickListener;
import it.denning.search.utils.OnUploadClickListener;
import it.denning.search.utils.SearchAdapter;
import it.denning.ui.activities.base.BaseActivity;
import it.denning.utils.KeyboardUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by denningit on 22/04/2017.
 */

public class SearchActivity extends BaseActivity implements OnItemClickListener,
                                                        OnFileFolderClickListener,
                                                        OnAccountsClickListener,
                                                        OnFileNoteClickListener,
                                                        OnPaymentRecordClickListener,
                                                        OnTemplateClickListener,
                                                        OnRelatedMatterClickListener,
                                                        OnUploadClickListener {
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

    @BindView(R.id.search_tabbar)
    TabLayout filterTabbar;

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
    String  documentTitle = "Contact Folder";

    SearchAdapter searchAdapter;
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
        return R.layout.activity_search;
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
    }

    private void changeSearchType() {
        searchView.setHint(DISharedPreferences.CurrentSearchType);
        searchView.setText("");
        currentKeyword = "";
        searchAdapter.clear();
        isGeneralSearh = !isGeneralSearh;

        changeActionBar();
    }

    private void determineServiceType() {
        if (DISharedPreferences.getInstance(this).getUserType().equals(DISharedPreferences.STAFF_USER)) {
            isGeneralSearh = true;
        } else {
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
                    KeyboardUtils.hideKeyboard(SearchActivity.this);
                    currentKeyword = v.getText().toString();
                    isAutoComplete = 1;
                    searchAdapter.clear();
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
                if (editable.toString().trim().length() != 0) {
                    getSearchKeywords(editable.toString().trim());
                }
            }
        });

        searchView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                currentKeyword = (String) parent.getItemAtPosition(position);
                KeyboardUtils.hideKeyboard(SearchActivity.this);
                searchView.dismissDropDown();
                isAutoComplete = 0;
                searchAdapter.clear();
                page = 1;
                getSearchResult();
            }
        });
    }

    private void setupSearchView() {
        searchList.setHasFixedSize(true);
        searchLayoutManager = new LinearLayoutManager(this, OrientationHelper.VERTICAL, false);
        searchLayoutManager.setItemPrefetchEnabled(false);
        searchList.setLayoutManager(searchLayoutManager);
        searchList.setItemAnimator(new DefaultItemAnimator());
        searchAdapter = new SearchAdapter(new ArrayList<SearchResultModel>(),this);
        searchList.setAdapter(searchAdapter);
        searchList.setItemViewCacheSize(0);
        searchAdapter.setClickListener(this, this, this, this, this, this, this, this);
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

    @SuppressWarnings("deprecation")
    private void setupFilter(String[] filterArray) {
        for (int i = 0; i < filterArray.length; i++) {
            filterTabbar.addTab(filterTabbar.newTab().setText(filterArray[i]));
            filterTabbar.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    searchFromFilter(tab);
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {
                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {
                }
            });
        }
    }

    private void changeActionBar() {
        filterTabbar.removeAllTabs();

        initActionBar();
    }

    @SuppressWarnings("deprecation")
    public void initActionBar() {
        String[] filterArray;
        if (!isGeneralSearh) {
            filterArray = DIConstants.PublicSearchFilter;
            DISharedPreferences.CurrentSearchType = DISharedPreferences.PUBLIC_SEARCH;
            DISharedPreferences.CurrentCategory = -1;
            searchView.setHint("Public Search");
            mSearchService = new RetrofitHelper(this).getPublicService();
        } else {
            filterArray = DIConstants.GeneralSearchFilter;
            DISharedPreferences.CurrentSearchType = DISharedPreferences.GENERAL_SEARCH;
            DISharedPreferences.CurrentCategory = 0;
            searchView.setHint("General Search");
            mSearchService = new RetrofitHelper(this).getPrivateService();
        }

        setupFilter(filterArray);
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
            case DIConstants.BANK_TYPE:
                mSingle = mSearchService.getBank(currentCode);
                break;
            case DIConstants.GOVERNMENT_LAND_OFFICES:
                if (isGeneralSearh) {
                    mSingle = mSearchService.getGovOffice(currentCode, "LandOffice");
                } else  {
                    mSingle = mSearchService.getPublicGovOffice(currentCode, "LandOffice");
                }
                break;
            case DIConstants.GOVERNMENT_PTG_OFFICES:
                if (isGeneralSearh) {
                    mSingle = mSearchService.getGovOffice(currentCode, "PTG");
                } else {
                    mSingle = mSearchService.getPublicGovOffice(currentCode, "PTG");
                }
                break;
            case DIConstants.LEGAL_FIRM:
                if (isGeneralSearh) {
                    mSingle = mSearchService.getLegalFirm(currentCode);
                } else {
                    mSingle = mSearchService.getPublicLegalFirm(currentCode);
                }
                break;
            case DIConstants.PROPERTY_TYPE:
                mSingle = mSearchService.getProperty(currentCode);
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
            case DIConstants.DOCUMENT_FOR_PROPERTY_TYPE:
                documentTitle = "Contact Folder";
                mSingle = mSearchService.getDocumentFromProperty(currentCode);
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

    private void searchFromFilter(TabLayout.Tab tab) {
        if (isGeneralSearh) {
            DISharedPreferences.CurrentCategory = DIConstants.GeneralSearchCategory[tab.getPosition()];
        } else {
            DISharedPreferences.CurrentCategory = DIConstants.PublicSearchCategory[tab.getPosition()];
        }

        page = 1;
        searchAdapter.clear();
        getSearchResult();

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
        searchAdapter.swapItems(Arrays.asList(searchArray));

        setupEndlessScroll();
        hideHorizontalProgress();
    }

    @Override
    public void onClick(View view, int position) {
        final SearchResultModel searchResultModel = searchAdapter.getModelArrayList().get(position);
        currentCode = searchResultModel.key;
        mCompositeDisposable.clear();
        Integer type = DIHelper.determinSearchType(searchResultModel.form);
        switch (type) {
            case DIConstants.CONTACT_TYPE:
                gotoContactActivity("");
                break;
            case DIConstants.BANK_TYPE:
                gotoBankActivity();
                break;
            case DIConstants.GOVERNMENT_LAND_OFFICES:
                currentSearch =  DIConstants.GOVERNMENT_LAND_OFFICES;
                gotoGovernmentOfficeActivity();
                break;
            case DIConstants.GOVERNMENT_PTG_OFFICES:
                currentSearch =  DIConstants.GOVERNMENT_PTG_OFFICES;
                gotoGovernmentOfficeActivity();
                break;
            case DIConstants.LEGAL_FIRM:
                gotoLegalFirmActivity();
                break;
            case DIConstants.PROPERTY_TYPE:
                gotoPropertyActivity();
                break;
            case DIConstants.MATTER_TYPE:
                gotoMatterActivity();
                break;
            case DIConstants.DOCUMENT_TYPE:
                gotoDocumentActivity();
                break;
            default:
                break;
        }
    }

    private void gotoAccounts() {
        hideProgress();
        AccountsActivity.start(SearchActivity.this);
    }

    @Override
    public void onAccountsClick(View view, String code) {
        mSingle = mSearchService.getLedger(code);
        showProgress();
        excecuteCompositeDisposable(new CompositeCompletion() {
            @Override
            public void parseResponse(JsonElement jsonElement) {
                DISharedPreferences.accounts = new Gson().fromJson(jsonElement, Accounts.class);
                gotoAccounts();
            }
        });
    }

    @Override
    public void onFileNoteClick(View view, String code, String title) {
        FileNoteActivity.start(this, code, title);
    }

    @Override
    public void onFileFolderClick(View view, String code, String type) {
        currentCode = code;
        documentTitle = type;
        if (documentTitle.equals("File Folder")) {
            currentSearch = DIConstants.DOCUMENT_TYPE;
        } else if (documentTitle.equals("Contact Folder")) {
            currentSearch = DIConstants.DOCUMENT_FOR_CONTACT_TYPE;
        } else {
            currentSearch = DIConstants.DOCUMENT_FOR_PROPERTY_TYPE;
        }

        gotoDocumentActivity();
    }

    @Override
    public void onPaymentRecordClick(View view, String code) {
        PaymentRecordActivity.start(this, code);
    }

    @Override
    public void onTemplateClick(View view, String code, String tittle) {
        TemplateActivity.start(this, code, tittle);
    }

    private void excecuteCompositeDisposable(final CompositeCompletion completion) {
//        mCompositeDisposable.add(mSingle
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .map(new Function<JsonElement, JsonElement>() {
//                    @Override
//                    public JsonElement apply(JsonElement jsonElement) throws Exception {
//                        return jsonElement;
//                    }
//                })
//                .subscribeWith(new DisposableSingleObserver<JsonElement>() {
//                    @Override
//                    public void onSuccess(JsonElement jsonElement) {
//                        completion.parseResponse(jsonElement);
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        hideProgress();
//                        hideHorizontalProgress();
//                        ErrorUtils.showError(SearchActivity.this, e.getMessage());
//                    }
//                })
//        );
        mSingle.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                if (!response.isSuccessful()) {
                    if (response.code() == 408){
                        ErrorUtils.showError(SearchActivity.this,"Session expired. Please log in again.");
//                        DIAlert.showSimpleAlertAndGotoLogin(SearchActivity.this, R.string.warning_title, R.string.alert_session_expired);
                    } else {
                        ErrorUtils.showError(SearchActivity.this, response.message());
                    }
                } else {
                    completion.parseResponse(response.body());
                }
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable e) {
                hideProgress();
                hideHorizontalProgress();
                ErrorUtils.showError(SearchActivity.this, e.getMessage());
            }
        });
    }

    private void gotoContactActivity(final String gotoMatter) {
        currentSearch =  DIConstants.CONTACT_TYPE;
        determineSearchType();
        excecuteCompositeDisposable(new CompositeCompletion() {
            @Override
            public void parseResponse(JsonElement jsonElement) {
                DISharedPreferences.contact = new Gson().fromJson(jsonElement, Contact.class);
                SearchContactActivity.start(SearchActivity.this, gotoMatter);
            }
        });
    }

    private void _gotoBank(JsonElement jsonElement, int title) {
        DISharedPreferences.bank = new Gson().fromJson(jsonElement, Bank.class);
        BankActivity.start(getApplicationContext(), title);
    }

    private void gotoBankActivity() {
        currentSearch =  DIConstants.BANK_TYPE;
        determineSearchType();

        excecuteCompositeDisposable(new CompositeCompletion() {
            @Override
            public void parseResponse(JsonElement jsonElement) {
                _gotoBank(jsonElement, R.string.bank_title);
            }
        });
    }

    private void gotoGovernmentOfficeActivity() {
        determineSearchType();

        excecuteCompositeDisposable(new CompositeCompletion() {
            @Override
            public void parseResponse(JsonElement jsonElement) {
                _gotoBank(jsonElement, R.string.gov_offices_title);
            }
        });
    }

    private void gotoMatterActivity() {
        currentSearch =  DIConstants.MATTER_TYPE;
        determineSearchType();

        excecuteCompositeDisposable(new CompositeCompletion() {
            @Override
            public void parseResponse(JsonElement jsonElement) {
                MatterModel relatedMatter = new Gson().fromJson(jsonElement, MatterModel.class);
                MatterActivity.start(SearchActivity.this, relatedMatter);
            }
        });
    }

    private void gotoLegalFirmActivity() {
        currentSearch =  DIConstants.LEGAL_FIRM;
        determineSearchType();

        excecuteCompositeDisposable(new CompositeCompletion() {
            @Override
            public void parseResponse(JsonElement jsonElement) {
                LegalFirm legalFirm = new Gson().fromJson(jsonElement, LegalFirm.class);
                Intent i = new Intent(SearchActivity.this, LegalFirmActivity.class);
                i.putExtra("legalFirm", legalFirm);
                startActivity(i);
            }
        });
    }

    private void gotoPropertyActivity() {
        currentSearch =  DIConstants.PROPERTY_TYPE;
        determineSearchType();

        excecuteCompositeDisposable(new CompositeCompletion() {
            @Override
            public void parseResponse(JsonElement jsonElement) {
                Property property = new Gson().fromJson(jsonElement, Property.class);
                Intent i = new Intent(SearchActivity.this, AddPropertyActivity.class);
                i.putExtra("model", property);
                startActivity(i);
            }
        });
    }

    private void _gotoDocumentActivity() {
        DocumentActivity.start(this, documentTitle);
    }

    private void gotoDocumentActivity() {
        determineSearchType();
        excecuteCompositeDisposable(new CompositeCompletion() {
            @Override
            public void parseResponse(JsonElement jsonElement) {
                DISharedPreferences.getInstance().documentModel = new Gson().fromJson(jsonElement, DocumentModel.class);
                _gotoDocumentActivity();
            }
        });
    }

    @Override
    public void onMatterClick(View view, String code) {
        currentCode = code;
        mCompositeDisposable.clear();
        gotoContactActivity("matter");
    }

    @Override
    public void onUploadClick(View view, String code, int title, String url, String defaultFileName) {
        UploadActivity.start(this, code, title, url, defaultFileName);
    }
}

