package it.denning.search.template;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.SearchView;
import android.widget.TextView;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.BootstrapText;
import com.gigamole.navigationtabstrip.NavigationTabStrip;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.quickblox.q_municate_db.utils.ErrorUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import it.denning.App;
import it.denning.R;
import it.denning.general.DIAlert;
import it.denning.general.DIConstants;
import it.denning.general.DIFileManager;
import it.denning.general.DIHelper;
import it.denning.general.DISharedPreferences;
import it.denning.general.EndlessRecyclerViewScrollListener;
import it.denning.model.CodeDescription;
import it.denning.model.Template;
import it.denning.navigation.home.calendar.CalendarActivity;
import it.denning.network.CompositeCompletion;
import it.denning.network.ErrorHandler;
import it.denning.network.NetworkManager;
import it.denning.network.RetrofitHelper;
import it.denning.network.utils.Download;
import it.denning.search.document.DocumentActivity;
import it.denning.search.utils.OnItemClickListener;
import it.denning.search.utils.bootstrap.CustomBootstrapStyle;
import it.denning.search.utils.desc.GeneralDescActivity;
import it.denning.search.utils.generallist.GeneralListActivity;
import it.denning.ui.activities.base.BaseActivity;
import it.denning.utils.KeyboardUtils;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Created by denningit on 2017-12-24.
 */

public class TemplateActivity extends BaseActivity implements OnItemClickListener {
    private static final int templateRequestCode = 1;
    private static final int typeRequestCode = 2;

    @BindView(R.id.toolbar_title)
    protected TextView toolbarTitle;

    @BindView(R.id.toolbar_sub_title)
    TextView toolbarSubTitle;

    @BindView(R.id.template_top)
    NavigationTabStrip topFilter;

    @BindView(R.id.category_btn)
    BootstrapButton categoryBtn;

    @BindView(R.id.type_btn)
    BootstrapButton typeBtn;

    @BindView(R.id.searchview)
    SearchView searchView;

    @BindView(R.id.recycler_list)
    protected RecyclerView recyclerView;

    @OnClick(R.id.back_btn)
    void onBack() {
        KeyboardUtils.hideKeyboard(this);
        finish();
    }

    private LinearLayoutManager linearLayoutManager;
    private TemplateAdapter adapter;
    private List<Template> modelList = new ArrayList<>();
    private String name, code, title;
    private String curCategory = "", curType = "", userType = "all";
    private static String _query = "";
    private int page = 1;
    private String[] topFilters = {"all", "online", "user"};
    Template template;

    public static void start(Context context, String code, String title) {
        Intent intent = new Intent(context, TemplateActivity.class);
        intent.putExtra("code", code);
        intent.putExtra("title", title);
        context.startActivity(intent);
    }

    @Override
    protected int getContentResId() {
        return R.layout.activity_template;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initFields();
        initActionBar();

    setupRecyclerView();

        setupSearchView();

        setupFilters();
    }

    private void initFields() {
        toolbarTitle.setText(getString(R.string.template_title));
        code = getIntent().getStringExtra("code");
        title = getIntent().getStringExtra("title");
        name = DIHelper.separateNameIntoTwo(title)[1];
        toolbarSubTitle.setText(name + "  " + code);


        categoryBtn.setBootstrapBrand(new CustomBootstrapStyle(this));
        typeBtn.setBootstrapBrand(new CustomBootstrapStyle(this));
    }

    private void setupRecyclerView() {
        adapter = new TemplateAdapter(modelList, this);
        linearLayoutManager = new LinearLayoutManager(this, OrientationHelper.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addItemDecoration(new it.denning.general.DividerItemDecoration(ContextCompat.getDrawable(this, R.drawable.item_decorator)));

        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
    }

    private void setupEndlessScroll() {
        EndlessRecyclerViewScrollListener scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                loadTemplate();
            }
        };
        recyclerView.clearOnScrollListeners();
        recyclerView.addOnScrollListener(scrollListener);
    }

    private void setupSearchView() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                KeyboardUtils.hideKeyboard(TemplateActivity.this);
                _loadTemplate(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                _loadTemplate(newText);
                return false;
            }
        });

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                KeyboardUtils.hideKeyboard(TemplateActivity.this);
                _loadTemplate("");
                return false;
            }
        });
    }

    private void setupFilters() {
        topFilter.setTabIndex(0);
        topFilter.setOnTabStripSelectedIndexListener(new NavigationTabStrip.OnTabStripSelectedIndexListener() {
            @Override
            public void onStartTabSelected(String title, int index) {
                userType = title.toLowerCase();
                _loadTemplate("");
            }

            @Override
            public void onEndTabSelected(String title, int index) {

            }
        });
    }

    private void _loadTemplate(String query) {
        TemplateActivity._query = query;
        page = 1;
        adapter.clear();
        loadTemplate();
    }

    private void loadTemplate() {
        showActionBarProgress();
        String url = "v1/Table/cboTemplate?fileno=" + code +"&Online=" +
                userType +"&category="+
                curCategory +"&Type="+ curType +"&page="+ page +"&search=" + _query;
        NetworkManager.getInstance().sendPrivateGetRequest(url, new CompositeCompletion() {
            @Override
            public void parseResponse(JsonElement jsonElement) {
                manageResponse(jsonElement.getAsJsonArray());
            }
        }, new ErrorHandler() {
            @Override
            public void handleError(String error) {
                hideActionBarProgress();
                ErrorUtils.showError(TemplateActivity.this, error);
            }
        });
    }

    private void manageResponse(JsonArray jsonArray) {
        hideActionBarProgress();
        Template[] templates = new Gson().fromJson(jsonArray, Template[].class);
        if (templates.length > 0) {
            page++;
        }
        adapter.swapItems(Arrays.asList(templates));

        setupEndlessScroll();
    }

    @OnClick(R.id.category_btn)
    void selectCategory() {
        Intent i = new Intent(this, GeneralDescActivity.class);
        i.putExtra("title", R.string.category);
        i.putExtra("url", DIConstants.SEARCH_TEMPLATE_CATEGORY );
        startActivityForResult(i, templateRequestCode);
    }

    @OnClick(R.id.type_btn)
    void selectType() {
        if (curCategory.trim().length() == 0) {
            DIAlert.showSimpleAlert(this, R.string.alert_category_not_selected);
            return;
        }

        Intent i = new Intent(this, GeneralListActivity.class);
        i.putExtra("title", R.string.type_title);
        i.putExtra("url", DIConstants.SEARCH_TEMPLATE_TYPE + curCategory + "&search=");
        i.putExtra("code", "strTypeCode");
        i.putExtra("value", "strTypeName");
        startActivityForResult(i, typeRequestCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == templateRequestCode) {
            if(resultCode == Activity.RESULT_OK){
                String value = data.getStringExtra("desc");
                curCategory = value;
                categoryBtn.setBootstrapText(new BootstrapText.Builder(this).addText(value).addFontAwesomeIcon("fa-chevron-down").build());
            } else if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        } else if (requestCode == typeRequestCode) {
            if(resultCode == Activity.RESULT_OK){
                CodeDescription model= (CodeDescription) data.getSerializableExtra("model");
                curType = model.code;
                typeBtn.setBootstrapText(new BootstrapText.Builder(this).addText(model.description).addFontAwesomeIcon("fa-chevron-down").build());
                loadTemplate();
            } else if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }

    @Override
    public void onClick(View view, int position) {
        template = adapter.getModel().get(position);
        if(checkPermission()){
            downloadTemplate();
        } else {
            requestPermission();
        }
    }

    private boolean checkPermission(){
        int result = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED){

            return true;

        } else {

            return false;
        }
    }

    private void requestPermission(){
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},DIConstants.PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case DIConstants.PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    downloadTemplate();
                } else {
                    showSnackbar(R.string.permission_denied, Snackbar.LENGTH_LONG);
                }
                break;
        }
    }

    private void downloadTemplate() {
        showActionBarProgress();

        final String url = DISharedPreferences.getInstance().getServerAPI() + template.generateAPI;

        final String fileName = (template.getJsonGenerateBody().get("strDocumentName").getAsString() + template.getJsonGenerateBody().get("eOutput").getAsString()).replaceAll("[^a-zA-Z0-9\\.\\-]", "_");

        // Json data mime type string value.
        String jsonMimeType = "application/json; charset=utf-8";

        // Get json string media type.
        MediaType jsonContentType = MediaType.parse(jsonMimeType);

        // Create json string request body
        RequestBody jsonRequestBody = RequestBody.create(jsonContentType, template.generateBody);

        Request.Builder builder = new Request.Builder();
        // Set url.
        builder = builder.url(url);
        // Post request body.
        builder = builder.post(jsonRequestBody);
        // Create request object.
        Request request = builder.build();
        // Get okhttp3.Call object.
        Call call = RetrofitHelper.getInstance(App.getInstance()).createPrivateClient().newCall(request);

        // Execute the call asynchronously.
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("Template", e.getMessage());
                ErrorUtils.showError(TemplateActivity.this, e.getMessage());
                hideActionBarProgress();
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                // Download file local file path.
                new AsyncTask<Void, Long, Void>() {
                    @Override
                    protected Void doInBackground(Void... voids) {
                        try {
                            downloadFile(fileName, url, response.body());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        return null;
                    }
                }.execute();
            }
        });
    }

    private void downloadFile(String fileName, String url, ResponseBody body) throws IOException {
        File outputDir = new File(String.valueOf(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)) + DIFileManager.folderName);
        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }

        File outputFile = new File(outputDir, fileName);

        int count;
        byte data[] = new byte[1024 * 4];
        long fileSize = body.contentLength();
        int totalFileSize = 0;
        InputStream bis = new BufferedInputStream(body.byteStream(), 1024 * 8);
        OutputStream output = new FileOutputStream(outputFile);
        long total = 0;
        long startTime = System.currentTimeMillis();
        int timeCount = 1;
        while ((count = bis.read(data)) != -1) {

            total += count;
            totalFileSize = (int) (fileSize / (Math.pow(1024, 2)));
            double current = Math.round(total / (Math.pow(1024, 2)));

            int progress = (int) ((total * 100) / fileSize);

            long currentTime = System.currentTimeMillis() - startTime;

            Download download = new Download();
            download.setTotalFileSize(totalFileSize);

            if (currentTime > 1000 * timeCount) {

                download.setCurrentFileSize((int) current);
                download.setProgress(progress);
                timeCount++;
            }

            output.write(data, 0, count);
        }

        output.flush();
        output.close();
        bis.close();

        displayTemplate(outputFile);
    }

    private void displayTemplate(final File file) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    hideActionBarProgress();
                    new DIFileManager(TemplateActivity.this).openFile(file);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
