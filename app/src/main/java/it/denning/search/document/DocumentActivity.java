package it.denning.search.document;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.SearchView;

import com.quickblox.q_municate_db.utils.ErrorUtils;

import org.zakariya.stickyheaders.StickyHeaderLayoutManager;

import java.io.File;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import it.denning.R;
import it.denning.general.DIConstants;
import it.denning.general.DIFileManager;
import it.denning.general.DISharedPreferences;
import it.denning.model.DocumentModel;
import it.denning.network.services.DownloadService;
import it.denning.network.utils.Download;
import it.denning.network.utils.DownloadCompleteInterface;
import it.denning.network.utils.ProgressInterface;
import it.denning.search.utils.OnFileClickListener;
import it.denning.search.utils.generallist.GeneralListActivity;
import it.denning.ui.activities.base.BaseActivity;
import it.denning.ui.activities.base.MyBaseActivity;
import it.denning.ui.activities.base.MySearchBaseActivity;
import it.denning.utils.KeyboardUtils;
import it.denning.utils.MediaUtils;

/**
 * Created by denningit on 28/04/2017.
 */

public class DocumentActivity extends MySearchBaseActivity implements OnFileClickListener, ProgressInterface, DownloadCompleteInterface {
    private String url;
    private String title;
    private DocumentAdapter documentAdapter;
    private DocumentModel documentModel;

    public static void start(Context context, String title) {
        Intent intent = new Intent(context, DocumentActivity.class);
        intent.putExtra("title", title);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initFields();
        initActionBar();

        setupSearchView();
    }

    private void initFields() {
        title = getIntent().getStringExtra("title");
        toolbarTitle.setText(title);
        documentModel = DISharedPreferences.getInstance().documentModel;

        if (documentModel != null) {
            setupRecyclerView(documentModel);
        }
    }

    private void setupRecyclerView(DocumentModel documentModel) {
        documentAdapter = new DocumentAdapter(documentModel);
        recyclerView.addItemDecoration(new it.denning.general.DividerItemDecoration(ContextCompat.getDrawable(this, R.drawable.item_decorator)));
        recyclerView.setLayoutManager(new StickyHeaderLayoutManager());
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(documentAdapter);
        documentAdapter.setItemClickListener(this);
    }

    private void setupSearchView() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                KeyboardUtils.hideKeyboard(DocumentActivity.this);
                searchData(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchData(newText);
                return false;
            }
        });

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                KeyboardUtils.hideKeyboard(DocumentActivity.this);
                return true;
            }
        });
    }

    private void searchData(String query) {
        DocumentModel newDocument = documentModel.searchQuery(query);
        documentAdapter.replaceData(newDocument);
    }

    @Override
    public void onClick(View view, String fileUrl) {
        url = fileUrl;
        downloadFile();
    }

    public void downloadFile(){

        if(checkPermission()){
            startDownload();
        } else {
            requestPermission();
        }
    }

    private void startDownload(){
      DownloadService downloadService =  new DownloadService(this, url, this, this);
      downloadService.initDownload();
      showActionBarProgress();
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

                    startDownload();
                } else {
                    showSnackbar(R.string.permission_denied, Snackbar.LENGTH_LONG);
                }
                break;
        }
    }

    @Override
    public void onProgress(final Download download) {
        if (download.getProgress() == 100) {
            hideActionBarProgress();
        }
    }

    @Override
    public void onComplete(final File file) {
        if (DISharedPreferences.isDenningFile) {
            DISharedPreferences.file = file;
            DISharedPreferences.isDenningFile = false;
            setResult(Activity.RESULT_OK, new Intent());
            finish();
        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        hideActionBarProgress();
                        new DIFileManager(DocumentActivity.this).openFile(file);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    @Override
    public void onFailure(String error) {
        ErrorUtils.showError(this, error);
        hideActionBarProgress();
    }

    @Override
    protected void onBack() {
        if (DISharedPreferences.isDenningFile) {
            DISharedPreferences.isDenningFile = false;
        }
        super.onBack();
    }
}
