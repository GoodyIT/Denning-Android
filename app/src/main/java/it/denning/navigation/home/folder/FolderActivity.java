package it.denning.navigation.home.folder;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.quickblox.q_municate_db.utils.ErrorUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import it.denning.R;
import it.denning.auth.branch.BranchAdapter;
import it.denning.general.DISharedPreferences;
import it.denning.model.DocumentModel;
import it.denning.model.FirmModel;
import it.denning.model.FirmURLModel;
import it.denning.navigation.home.upload.UploadActivity;
import it.denning.network.CompositeCompletion;
import it.denning.network.ErrorHandler;
import it.denning.network.NetworkManager;
import it.denning.search.utils.OnItemClickListener;
import it.denning.ui.activities.base.MyBaseActivity;
import it.denning.utils.KeyboardUtils;
import okhttp3.OkHttpClient;

/**
 * Created by denningit on 20/04/2017.
 */

public class FolderActivity extends MyBaseActivity{

    private List<DocumentModel> folders;

    protected static View.OnClickListener onClickListener;

    public static void start(Context context) {
        Intent intent = new Intent(context, FolderActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initFields();

        setupRecyclerView();
    }

    private void initFields() {
        folders = DISharedPreferences.folders;

        onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoDocument((Integer) v.getTag());
            }
        };
    }

    private void setupRecyclerView() {
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        FolderAdapter ca = new FolderAdapter(folders);
        recyclerView.setAdapter(ca);
    }

    private void gotoDocument(int position) {

    }
}
