package it.denning.search.filenote;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.rockerhieu.rvadapter.endless.EndlessRecyclerViewAdapter;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import it.denning.R;
import it.denning.general.DIConstants;
import it.denning.general.DIHelper;
import it.denning.general.DISharedPreferences;
import it.denning.general.EndlessRecyclerViewScrollListener;
import it.denning.model.Event;
import it.denning.model.FileNote;
import it.denning.model.FileNoteSub;
import it.denning.navigation.home.calendar.CalendarActivity;
import it.denning.navigation.home.calendar.CalendarAdapter;
import it.denning.network.CompositeCompletion;
import it.denning.network.ErrorHandler;
import it.denning.network.NetworkManager;
import it.denning.search.utils.OnClickListenerWithCode;
import it.denning.search.utils.OnItemClickListener;
import it.denning.ui.activities.base.BaseActivity;
import it.denning.utils.KeyboardUtils;

/**
 * Created by denningit on 2017-12-23.
 */

public class FileNoteActivity extends BaseActivity implements OnItemClickListener {
    @BindView(R.id.toolbar_title)
    protected TextView toolbarTitle;
    @BindView(R.id.toolbar_sub_title)
    TextView toolbarSubTitle;

    @OnClick(R.id.back_btn)
    void onBack() {
        KeyboardUtils.hideKeyboard(this);
        finish();
    }

    @BindView(R.id.recycler_list)
    protected RecyclerView recyclerView;

    private FileNoteAdapter fileNoteAdapter;
    private LinearLayoutManager linearLayoutManager;

    private String name, code, title;
    private int page = 1;

    public static void start(Context context, String code, String title) {
        Intent intent = new Intent(context, FileNoteActivity.class);
        intent.putExtra("code", code);
        intent.putExtra("title", title);
        context.startActivity(intent);
    }

    @Override
    protected int getContentResId() {
        return R.layout.activity_filenote;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initFields();
        initActionBar();

        setupRecyclerView();

        updateHeaderInfo();

        loadFileNotes();

        setupEndlessScroll();
    }

    private void initFields() {
        toolbarTitle.setText(getString(R.string.file_note_title));

        code = getIntent().getStringExtra("code");
        title = getIntent().getStringExtra("title");
    }

    private void updateHeaderInfo() {
        name = DIHelper.separateNameIntoTwo(title)[1];
        toolbarSubTitle.setText(name + "  " + code);
    }

    private void setupRecyclerView() {
        fileNoteAdapter = new FileNoteAdapter(new ArrayList<FileNote>(), this);
        linearLayoutManager = new LinearLayoutManager(this, OrientationHelper.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addItemDecoration(new it.denning.general.DividerItemDecoration(ContextCompat.getDrawable(this, R.drawable.item_decorator)));

        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(fileNoteAdapter);
    }

    private void setupEndlessScroll() {
        EndlessRecyclerViewScrollListener scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                loadFileNotes();
            }
        };
        recyclerView.clearOnScrollListeners();
        recyclerView.addOnScrollListener(scrollListener);
    }

    private void manageResponse(JsonArray jsonArray) {
        hideActionBarProgress();

        final FileNote[] fileNotes = new Gson().fromJson(jsonArray, FileNote[].class);
        if (fileNotes.length > 0) {
            page++;
        }
        new Handler().post(new Runnable() {
              @Override
              public void run() {
                  fileNoteAdapter.updateAdapter(Arrays.asList(fileNotes));
              }
          });
    }

    private void loadFileNotes() {
        String url = DIConstants.FILE_NOTE_URL + "?fileNo=" + code + "&page=" + page;
        showActionBarProgress();
        NetworkManager.getInstance().sendPrivateGetRequest(url, new CompositeCompletion() {
            @Override
            public void parseResponse(JsonElement jsonElement) {
                manageResponse(jsonElement.getAsJsonArray());
            }
        }, new ErrorHandler() {
            @Override
            public void handleError(String error) {
                hideActionBarProgress();
            }
        });
    }

    @OnClick(R.id.bottom_btn)
    void newFileNote() {
        DISharedPreferences.fileNote = null;
        NewFileNoteActivity.start(this, name, code);
    }

    @Override
    public void onClick(View view, int position) {
        DISharedPreferences.fileNote = fileNoteAdapter.getModelList().get(position);
        NewFileNoteActivity.start(this, name, code);
    }
}
