package it.denning.auth.branch;

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
import it.denning.MainActivity;
import it.denning.R;
import it.denning.auth.FirmPasswordConfirmActivity;
import it.denning.general.DISharedPreferences;
import it.denning.model.DocumentModel;
import it.denning.model.FirmModel;
import it.denning.model.FirmURLModel;
import it.denning.navigation.home.upload.UploadActivity;
import it.denning.network.CompositeCompletion;
import it.denning.network.ErrorHandler;
import it.denning.network.NetworkManager;
import it.denning.search.document.PersonalDocumentActivity;
import it.denning.search.utils.OnItemClickListener;
import it.denning.utils.KeyboardUtils;
import okhttp3.OkHttpClient;

/**
 * Created by denningit on 20/04/2017.
 */

public class FirmBranchActivity extends AppCompatActivity implements OnItemClickListener{


    private List<FirmModel> firmURLModelList;
    private final OkHttpClient client = new OkHttpClient();

    protected static View.OnClickListener onClickListener;

    @BindView(R.id.branch_layout)
    RelativeLayout branchLayout;

    @BindView(R.id.branch_recyclerview)
     RecyclerView recList;

    @OnClick(R.id.back_btn)
    void onBack() {
        KeyboardUtils.hideKeyboard(this);
        finish();
    }

    public static void start(Context context) {
        Intent intent = new Intent(context, FirmBranchActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_branch);
        ButterKnife.bind(this);

        initFields();
        displayBranches();
    }

    private void initFields() {
        if(DISharedPreferences.getInstance(this).getUserType().equals(DISharedPreferences.STAFF_USER)) {
            firmURLModelList = DISharedPreferences.denningArray;
        } else {
            firmURLModelList = DISharedPreferences.personalArray;
        }

        onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                proceedBranch((Integer) v.getTag());
            }
        };
    }

    private void proceedBranch(Integer tag) {
        FirmModel firmModel = firmURLModelList.get(tag);

        if (DISharedPreferences.documentView.equals("upload") || DISharedPreferences.documentView.equals("shared")) {
            DISharedPreferences.tempServerAPI = firmModel.APIServer;
            DISharedPreferences.tempTheCode = firmModel.theCode;
            clientLogin(firmModel);
        } else {
            staffSignIn(firmModel);
        }
    }

    private void staffSignIn(final FirmModel firmModel) {
        DISharedPreferences.getInstance().saveServerAPI(firmModel);
        NetworkManager.getInstance().staffSignIn(new CompositeCompletion() {
            @Override
            public void parseResponse(JsonElement jsonElement) {
                FirmURLModel firmURLModel = new Gson().fromJson(jsonElement.getAsJsonObject(), FirmURLModel.class);
                if (firmURLModel.statusCode == 200) {
                    DISharedPreferences.getInstance().saveSessionID(firmURLModel.sessionID);
                    DISharedPreferences.getInstance().saveServerAPI(firmModel);
                    MainActivity.start(FirmBranchActivity.this);
                    finish();
                } else {
                    ErrorUtils.showError(FirmBranchActivity.this, getApplicationContext().getResources().getString(R.string.alert_no_access_to_firm));
                }
            }
        }, new ErrorHandler() {
            @Override
            public void handleError(String error) {
                ErrorUtils.showError(FirmBranchActivity.this, error);
            }
        });
    }

    private void clientLogin(final FirmModel firmURLModel) {

        NetworkManager.getInstance().clientSignIn(new CompositeCompletion() {
            @Override
            public void parseResponse(JsonElement jsonElement) {
                manageResponse(jsonElement.getAsJsonObject(), firmURLModel);
            }
        }, new ErrorHandler() {
            @Override
            public void handleError(String error) {
                ErrorUtils.showError(FirmBranchActivity.this, error);
            }
        });
    }

    private DocumentModel saveSessionAndGetFolder(JsonObject response) {
        DISharedPreferences.getInstance(this).saveSessionID(response.get("sessionID").getAsString());
        DISharedPreferences.getInstance(this).saveTheCode(response.get("theCode").getAsString());
        return new Gson().fromJson(response, DocumentModel.class);
    }

    private void manageResponse(JsonObject response, FirmModel firmURLModel) {
        DocumentModel documentModel = saveSessionAndGetFolder(response);
        if (response.get("statusCode").getAsString().equals("250")) {
            FirmPasswordConfirmActivity.start(this, firmURLModel.LawFirm.address.city, firmURLModel.LawFirm.name);
        } else {
            if (DISharedPreferences.documentView.equals("upload")) {
                finish();
                UploadActivity.start(
                        FirmBranchActivity.this, response.get("theCode").getAsString(), R.string.client_upload_title);
            } else {
                if (documentModel.folders == null || documentModel.folders.size() == 0) {
                    ErrorUtils.showError(this, "There is no shared folder for you");
                } else {
                    DISharedPreferences.documentModel = documentModel;
                    PersonalDocumentActivity.start(this, "Documents");
                }
            }
        }
    }

    private void displayBranches() {
        recList.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recList.setLayoutManager(llm);
        recList.setItemAnimator(new DefaultItemAnimator());

        BranchAdapter ca = new BranchAdapter(firmURLModelList);
        recList.setAdapter(ca);
    }

    @Override
    public void onClick(View view, int position) {

    }
}
