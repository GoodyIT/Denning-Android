package it.denning.navigation.home.upload;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.quickblox.q_municate_db.models.Attachment;
import com.quickblox.q_municate_db.utils.ErrorUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import it.denning.R;
import it.denning.general.DIAlert;
import it.denning.general.DIConstants;
import it.denning.general.DIHelper;
import it.denning.general.DISharedPreferences;
import it.denning.model.DocumentModel;
import it.denning.network.CompositeCompletion;
import it.denning.network.ErrorHandler;
import it.denning.network.NetworkManager;
import it.denning.search.utils.ClearableAutoCompleteTextView;
import it.denning.tasks.GetFilepathFromUriTask;
import it.denning.ui.activities.base.BaseLoggableActivity;
import it.denning.ui.activities.others.PreviewImageActivity;
import it.denning.utils.DialogsUtils;
import it.denning.utils.KeyboardUtils;
import it.denning.utils.MediaUtils;
import it.denning.utils.ValidationUtils;
import it.denning.utils.helpers.MediaPickHelper;
import it.denning.utils.helpers.SystemPermissionHelper;
import it.denning.utils.listeners.OnMediaPickedListener;

/**
 * Created by denningit on 2017-12-10.
 */

public class UploadActivity extends BaseLoggableActivity implements OnMediaPickedListener {
    DocumentModel documentModel;

    @BindView(R.id.toolbar_title)
    protected TextView toolbarTitle;

    @BindView(R.id.rename_autocomplete)
    protected ClearableAutoCompleteTextView renameAutocomplete;

    @BindView(R.id.remarks_textview)
    protected TextView remarks;

    @BindView(R.id.preview_imageview)
    protected ImageView imageView;

    @BindView(R.id.right_btn)
    protected Button rightBtn;

    @Nullable
    @BindView(R.id.upload_to_label_layout)
    RelativeLayout uploadToLabelLayout;

    @Nullable
    @BindView(R.id.upload_to_layout)
    RelativeLayout uploadToLayout;

    @Nullable
    @BindView(R.id.mid_separator_layout)
    LinearLayout separatorLayout;

    @Nullable
    @BindView(R.id.upload_to_textview)
    TextView uploadToLabel;

    private MediaPickHelper mediaPickHelper;
    private String fileUrl;
    private String key, fileNo1, url;
    private int title;
    private SystemPermissionHelper systemPermissionHelper = new SystemPermissionHelper(this);
    protected Handler handler = new Handler();

    @OnClick(R.id.back_btn)
    void onBack() {
        KeyboardUtils.hideKeyboard(this);
        finish();
    }

    public static void start(Context context, String key, int title) {
        start(context, key, title, "", "");
    }

    public static void start(Context context, String key, int title, String url, String defaultFileName) {
        Intent intent = new Intent(context, UploadActivity.class);
        intent.putExtra("key", key);
        intent.putExtra("title", title);
        intent.putExtra("url", url);
        intent.putExtra("defaultFileName", defaultFileName);
        context.startActivity(intent);
    }

    @Override
    protected int getContentResId() {
        return R.layout.activity_business_upload;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initFields();
        initActionBar();

        setupAutoComplete();
    }

    @OnClick(R.id.upload_file_layout)
    void selectFileFromGallery() {
        canPerformLogout.set(false);
        if (systemPermissionHelper.isAllPermissionsGrantedForSaveFileImage()) {
            MediaUtils.startMediaPicker(UploadActivity.this);
        } else {
            systemPermissionHelper.requestPermissionsForSaveFile();
        }
    }

    @OnClick(R.id.scan_layout)
    void scanFileFromCamera() {
        canPerformLogout.set(false);
        if (systemPermissionHelper.isCameraPermissionGranted()) {
            MediaUtils.startCameraPhotoForResult(this);
        } else {
            systemPermissionHelper.requestPermissionsTakePhoto();
        }
    }

    @OnClick(R.id.preview_imageview)
    void previewImage() {
        if (fileUrl.trim().length() == 0) {
            return;
        }
        PreviewImageActivity.start(this, fileUrl);
    }

    @OnClick(R.id.right_btn)
    void uploadFile() {
        if (fileUrl.trim().length() == 0) {
            DIAlert.showSimpleAlert(this, R.string.alert_file_not_load);
            return;
        }

        if (renameAutocomplete.getText().toString().trim().length() == 0) {
            DIAlert.showSimpleAlert(this, R.string.alert_filename_not_input);
            return;
        }

        showProgress();

        String fileData = DIHelper.bitmapToBase64(MediaUtils.getBitmapFromFile(fileUrl));
        int fileLength = fileData.length();
        String fileName = "IMG_" + Uri.parse(fileUrl).getLastPathSegment();

        JsonObject document = new JsonObject();
        document.addProperty("FileName", fileName);
        document.addProperty("MimeType", "jpg");
        document.addProperty("dateCreate", DIHelper.todayWithTime());
        document.addProperty("dateModify", DIHelper.todayWithTime());
        document.addProperty("fileLength", fileLength);
        document.addProperty("remarks", remarks.getText().toString());
        document.addProperty("base64", fileData);
        JsonArray documents = new JsonArray();
        documents.add(document);

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("fileNo1", fileNo1);
        jsonObject.add("documents", documents);

        String uploadUrl = DISharedPreferences.getInstance().getServerAPI();
        if (DISharedPreferences.documentView.equals("upload")) {
            uploadUrl = DISharedPreferences.tempServerAPI;
        }
        if (url.isEmpty()) {
            uploadUrl += DIConstants.MATTER_CLIENT_FILEFOLDER;
        } else {
            uploadUrl += url;
        }

        NetworkManager.getInstance().sendPrivatePostRequest(uploadUrl, jsonObject, new CompositeCompletion() {
            @Override
            public void parseResponse(JsonElement jsonElement) {
                hideProgress();
                ErrorUtils.showError(getApplicationContext(), R.string.success_upload);
            }
        }, new ErrorHandler() {
            @Override
            public void handleError(String error) {
                hideProgress();
                ErrorUtils.showError(getApplicationContext(), error);
            }
        });
    }

    private void initFields() {
        title = getIntent().getIntExtra("title", -1);
        toolbarTitle.setText(title);

        rightBtn.setVisibility(View.VISIBLE);
        rightBtn.setEnabled(false);
        mediaPickHelper = new MediaPickHelper();
        key = getIntent().getStringExtra("key");
        url = getIntent().getStringExtra("url");

        changeUIAndInfo();
    }

    private void changeUIAndInfo() {
        if (title == R.string.client_upload_title) {
            uploadToLayout.setVisibility(View.GONE);
            uploadToLabelLayout.setVisibility(View.GONE);
            separatorLayout.setVisibility(View.GONE);
            fileNo1 = DISharedPreferences.tempTheCode;
            if (key != null) {
                fileNo1 = key;
            }
        } else {
            uploadToLabel.setText(key.split(":")[1]);
            fileNo1 = DIHelper.separateNameIntoTwo(key.split(":")[1])[0];
        }
    }

    private void setupAutoComplete() {
        renameAutocomplete.setShowAlways(true);
        renameAutocomplete.setHint("File Name");
        renameAutocomplete.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    KeyboardUtils.hideKeyboard(UploadActivity.this);
                    renameAutocomplete.setText(v.getText().toString());
                }
                return false;
            }
        });

        renameAutocomplete.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().trim().length() != 0) {
                    displayRenameSuggestions(editable.toString().trim());
                }
            }
        });

        renameAutocomplete.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                renameAutocomplete.setText(parent.getItemAtPosition(position).toString());
                KeyboardUtils.hideKeyboard(UploadActivity.this);
                renameAutocomplete.dismissDropDown();

            }
        });
    }

    private void displayItems(JsonArray jsonArray) {
        JsonObject[] jsonObjects = new Gson().fromJson(jsonArray, JsonObject[].class);

        List<String> keywordList = new ArrayList<>();
        for (JsonObject object : jsonObjects) {
            keywordList.add(object.get("strSuggestedFilename").getAsString());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, keywordList);
        renameAutocomplete.setAdapter(adapter);
        if (keywordList.size() > 0) {
            renameAutocomplete.showDropDownIfFocused();
        }
    }

    private void displayRenameSuggestions(String string) {
        String url = DIConstants.FILE_NAME_AUTOCOMPLETE_URL + string;
        NetworkManager.getInstance().sendPrivateGetRequestWithoutError(url, new CompositeCompletion() {
            @Override
            public void parseResponse(JsonElement jsonElement) {
                displayItems(jsonElement.getAsJsonArray());
            }
        }, this);
    }

    @Override
    public void onMediaPicked(int requestCode, Attachment.Type type, Object attachment) {
        canPerformLogout.set(true);
        if(ValidationUtils.validateAttachment(getSupportFragmentManager(), getResources().getStringArray(R.array.supported_attachment_types), type, attachment)){
            fileUrl = attachment.toString();
            Glide.with(this).load(fileUrl).into(imageView);
            renameAutocomplete.setText(DIHelper.generateFileName());
            rightBtn.setEnabled(true);
        }
    }

    @Override
    public void onMediaPickClosed(int requestCode) {
        canPerformLogout.set(true);
    }

    @Override
    public void onMediaPickError(int requestCode, Exception e) {
        canPerformLogout.set(true);
        ErrorUtils.logError(e);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode == MediaUtils.CAMERA_PHOTO_REQUEST_CODE || requestCode == MediaUtils.CAMERA_VIDEO_REQUEST_CODE) && (data == null || data.getData() == null)) {
            // Hacky way to get EXTRA_OUTPUT param to work.
            // When setting EXTRA_OUTPUT param in the camera intent there is a chance that data will return as null
            // So we just pass temporary camera file as a data, because RESULT_OK means that photo was written in the file.
            data = new Intent();
            data.setData(MediaUtils.getValidUri(MediaUtils.getLastUsedCameraFile(), this));
        }

        new GetFilepathFromUriTask(getSupportFragmentManager(), this,
                requestCode).execute(data);
    }

    @Override
    public void onRequestPermissionsResult(final int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        //postDelayed() is temp fix before fixing this bug https://code.google.com/p/android/issues/detail?id=190966
        //on Android 7+ can use without delay
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                switch (requestCode) {
                    case (SystemPermissionHelper.PERMISSIONS_FOR_TAKE_PHOTO_REQUEST):
                        if (systemPermissionHelper.isCameraPermissionGranted()) {
                            MediaUtils.startCameraPhotoForResult(UploadActivity.this);
                        } else {
                            showPermissionSettingsDialog(R.string.dlg_permission_camera);
                        }
                        break;

                    case (SystemPermissionHelper.PERMISSIONS_FOR_SAVE_FILE_REQUEST):
                        if (systemPermissionHelper.isAllPermissionsGrantedForSaveFile()) {
                            MediaUtils.startMediaPicker(UploadActivity.this);
                        } else {
                            showPermissionSettingsDialog(R.string.dlg_permission_storage);
                        }
                        break;

                }

                getSupportFragmentManager().popBackStack();
            }
        }, 300);
    }

    private void showPermissionSettingsDialog(int permissionNameId) {
        DialogsUtils.showOpenAppSettingsDialog(
                getSupportFragmentManager(),
                getString(R.string.dlg_need_permission, getString(R.string.app_name), getString(permissionNameId)),
                new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        super.onPositive(dialog);
                    }

                    @Override
                    public void onNegative(MaterialDialog dialog) {
                        super.onNegative(dialog);
                        SystemPermissionHelper.openSystemSettings(getApplicationContext());
                    }
                });
    }
}
