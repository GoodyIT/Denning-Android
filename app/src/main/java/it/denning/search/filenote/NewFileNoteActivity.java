package it.denning.search.filenote;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.quickblox.q_municate_db.utils.ErrorUtils;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.OnClick;
import it.denning.R;
import it.denning.general.DIConstants;
import it.denning.general.DIHelper;
import it.denning.general.DISharedPreferences;
import it.denning.model.FileNote;
import it.denning.network.CompositeCompletion;
import it.denning.network.ErrorHandler;
import it.denning.network.NetworkManager;
import it.denning.ui.activities.base.BaseActivity;
import it.denning.utils.KeyboardUtils;

/**
 * Created by denningit on 2017-12-23.
 */

public class NewFileNoteActivity extends BaseActivity {
    @BindView(R.id.toolbar_title)
    protected TextView toolbarTitle;
    @BindView(R.id.toolbar_sub_title)
    TextView toolbarSubTitle;

    @BindView(R.id.content_textview)
    EditText content;

    @BindView(R.id.date_btn)
    Button dateBtn;

    @BindView(R.id.update_btn)
    Button updateBtn;

    @OnClick(R.id.back_btn)
    void onBack() {
        KeyboardUtils.hideKeyboard(this);
        finish();
    }

    private String fileName, fileNo;
    private FileNote fileNote;
    private MaterialDialog dialog;

    public static void start(Context context, String fileName, String fileNo) {
        Intent intent = new Intent(context, NewFileNoteActivity.class);
        intent.putExtra("fileNo", fileNo);
        intent.putExtra("fileName", fileName);
        context.startActivity(intent);
    }

    @Override
    protected int getContentResId() {
        return R.layout.activity_new_filenote;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initFields();
        initActionBar();

        displayFileNote();
    }

    private void initFields() {
        toolbarTitle.setText(getString(R.string.file_note_title));

        fileName = getIntent().getStringExtra("fileName");
        fileNo = getIntent().getStringExtra("fileNo");

        toolbarSubTitle.setText(fileName + "  " + fileNo);

        dateBtn.setText(DIHelper.today());

        fileNote = DISharedPreferences.fileNote;
    }

    private void displayFileNote() {
        if (fileNote != null) {
            dateBtn.setText(DIHelper.getOnlyDateFromDateTime(fileNote.dtDate));
            content.setText(fileNote.strNote);
            updateBtn.setText("Update");
        } else {
            updateBtn.setText("Save");
        }
    }

    @OnClick(R.id.date_btn)
    void changeDate() {
        Calendar now = Calendar.getInstance();
        new DatePickerDialog(
                NewFileNoteActivity.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                        String date = datePicker.getDayOfMonth() + " " + datePicker.getMonth()+1 + " " + datePicker.getYear();
                        dateBtn.setText(DIHelper.toCustomDate(date));
                    }
                },
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        ).show();
    }

    private void saveFileNote(String url, JsonObject param) {
        NetworkManager.getInstance().sendPrivatePostRequest(url, param, new CompositeCompletion() {
            @Override
            public void parseResponse(JsonElement jsonElement) {
                dialog.dismiss();
                ErrorUtils.showError(NewFileNoteActivity.this, "Success");
            }
        }, new ErrorHandler() {
            @Override
            public void handleError(String error) {
                dialog.dismiss();
                ErrorUtils.showError(NewFileNoteActivity.this, error);
            }
        });
    }

    private void updateFileNote(String url, JsonObject param) {
        param.addProperty("code", fileNote.code);
        NetworkManager.getInstance().sendPrivatePutRequest(url, param, new CompositeCompletion() {
            @Override
            public void parseResponse(JsonElement jsonElement) {
                dialog.dismiss();
                ErrorUtils.showError(NewFileNoteActivity.this, "Success");
            }
        }, new ErrorHandler() {
            @Override
            public void handleError(String error) {
                dialog.dismiss();
                ErrorUtils.showError(NewFileNoteActivity.this, error);
            }
        });
    }

    @OnClick(R.id.update_btn)
    void updateNote() {
        String url = DISharedPreferences.getInstance().getServerAPI() + DIConstants.FILE_NOTE_URL;
        JsonObject param = new JsonObject();
        param.addProperty("dtDate", DIHelper.toMySQLDateFormat(dateBtn.getText().toString()));
        param.addProperty("strFileNo", fileNo);
        param.addProperty("strNote", content.getText().toString());

        dialog = new MaterialDialog.Builder(this)
                .content(R.string.please_wait)
                .progress(true, 0)
                .show();

        if (fileNote != null) { // Update
            updateFileNote(url, param);
        } else { // Save
            saveFileNote(url, param);
        }
    }
}
