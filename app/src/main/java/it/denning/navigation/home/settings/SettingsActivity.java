package it.denning.navigation.home.settings;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.quickblox.auth.session.QBSettings;
import com.quickblox.q_municate_core.models.AppSession;
import com.quickblox.q_municate_core.models.LoginType;
import com.quickblox.q_municate_core.models.UserCustomData;
import com.quickblox.q_municate_core.service.QBServiceConsts;
import com.quickblox.q_municate_core.utils.UserFriendUtils;
import com.quickblox.q_municate_core.utils.Utils;
import com.quickblox.q_municate_db.models.Attachment;
import com.quickblox.q_municate_db.utils.ErrorUtils;
import com.quickblox.q_municate_user_service.model.QMUser;
import com.quickblox.users.model.QBUser;
import com.soundcloud.android.crop.Crop;

import java.io.File;

import butterknife.BindView;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import it.denning.R;
import it.denning.general.DIConstants;
import it.denning.general.DISharedPreferences;
import it.denning.ui.activities.base.BaseLoggableActivity;
import it.denning.ui.activities.changepassword.ChangePasswordActivity;
import it.denning.ui.activities.feedback.FeedbackActivity;
import it.denning.ui.activities.invitefriends.InviteFriendsActivity;
import it.denning.ui.activities.profile.MyProfileActivity;
import it.denning.ui.fragments.dialogs.base.TwoButtonsDialogFragment;
import it.denning.ui.views.roundedimageview.RoundedImageView;
import it.denning.utils.MediaUtils;
import it.denning.utils.ToastUtils;
import it.denning.utils.ValidationUtils;
import it.denning.utils.helpers.FacebookHelper;
import it.denning.utils.helpers.FirebaseAuthHelper;
import it.denning.utils.helpers.MediaPickHelper;
import it.denning.utils.helpers.ServiceManager;
import it.denning.utils.image.ImageLoaderUtils;
import it.denning.utils.listeners.OnMediaPickedListener;
import rx.Observable;
import rx.Subscriber;

public class SettingsActivity extends BaseLoggableActivity implements OnMediaPickedListener {

    public static final int REQUEST_CODE_LOGOUT = 300;

    @BindView(R.id.avatar_imageview)
    RoundedImageView avatarImageView;

    @BindView(R.id.nickname_textview)
    TextView nickName;
    @BindView(R.id.full_name_edittext)
    TextView fullName;

    @BindView(R.id.phone_textview)
    TextView phone;


    @BindView(R.id.email_textview)
    TextView email;

    @BindView(R.id.push_notification_switch)
    SwitchCompat pushNotificationSwitch;

    @BindView(R.id.change_password_view)
    RelativeLayout changePasswordView;

    private QMUser user;
    private MediaPickHelper mediaPickHelper;
    private UserCustomData userCustomData;
    private Uri imageUri;
    private boolean isNeedUpdateImage;

    public static void startForResult(Context context) {
        Intent intent = new Intent(context, SettingsActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected int getContentResId() {
        return R.layout.activity_settings;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initFields();
        setUpActionBarWithUpButton();

        addActions();

        initCustomData();
        updateOldData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUIData();
    }

    private void updateUIData() {
        user = UserFriendUtils.createLocalUser(AppSession.getSession().getUser());
        fillUI();
    }

    private void initCustomData() {
        userCustomData = Utils.customDataToObject(user.getCustomData());
        if (userCustomData == null) {
            userCustomData = new UserCustomData();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        removeActions();
    }

    @OnClick(R.id.avatar_imageview)
    void editProfile() {
//        MyProfileActivity.start(this);
        mediaPickHelper.pickAnMedia(this, MediaUtils.IMAGE_REQUEST_CODE);
    }

    @Override
    public void onMediaPicked(int requestCode, Attachment.Type attachmentType, Object attachment) {
        if (Attachment.Type.IMAGE.equals(attachmentType)) {
            canPerformLogout.set(true);
            startCropActivity(MediaUtils.getValidUri((File) attachment, this));
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Crop.REQUEST_CROP) {
            handleCrop(resultCode, data);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void handleCrop(int resultCode, Intent result) {
        if (resultCode == RESULT_OK) {
            isNeedUpdateImage = true;
            avatarImageView.setImageURI(imageUri);
            saveChanges();
        } else if (resultCode == Crop.RESULT_ERROR) {
            ToastUtils.longToast(Crop.getError(result).getMessage());
        }
        canPerformLogout.set(true);
    }


    private void startCropActivity(Uri originalUri) {
        String extensionOriginalUri = originalUri.getPath().substring(originalUri.getPath().lastIndexOf(".")).toLowerCase();

        canPerformLogout.set(false);
        imageUri = MediaUtils.getValidUri(new File(getCacheDir(), extensionOriginalUri), this);
        Crop.of(originalUri, imageUri).asSquare().start(this);
    }

    @Override
    public void onMediaPickError(int requestCode, Exception e) {
        canPerformLogout.set(true);
        ErrorUtils.showError(this, e);
    }

    @Override
    public void onMediaPickClosed(int requestCode) {
        canPerformLogout.set(true);
    }

    @OnCheckedChanged(R.id.push_notification_switch)
    void enablePushNotification(boolean enable) {
        QBSettings.getInstance().setEnablePushNotification(enable);
    }

    @OnClick(R.id.change_password_button)
    void changePassword() {
        UpdatePassword.start(this);
    }

    @OnClick(R.id.nick_name)
    void changeNickName() {

    }

    @OnClick(R.id.logout_button)
    void logout() {
        setResult(RESULT_OK);
        finish();

//        if (checkNetworkAvailableWithError()) {
//            TwoButtonsDialogFragment
//                    .show(getSupportFragmentManager(), R.string.dlg_logout, R.string.dlg_confirm,
//                            new MaterialDialog.ButtonCallback() {
//                                @Override
//                                public void onPositive(MaterialDialog dialog) {
//                                    super.onPositive(dialog);
//                                    showProgress();
//
//                                    ServiceManager.getInstance().logout(new Subscriber<Void>() {
//                                        @Override
//                                        public void onCompleted() {
//
//                                        }
//
//                                        @Override
//                                        public void onError(Throwable e) {
//                                            ErrorUtils.showError(SettingsActivity.this, e);
//                                            hideProgress();
//                                        }
//
//                                        @Override
//                                        public void onNext(Void aVoid) {
//                                            setResult(RESULT_OK);
//                                            hideProgress();
//                                            finish();
//                                        }
//                                    });
//                                }
//                            });
//        }

    }

    private void initFields() {
        title = getString(R.string.settings_title);
        user = UserFriendUtils.createLocalUser(AppSession.getSession().getUser());
        mediaPickHelper = new MediaPickHelper();
//        fullName.setText("Nick Name");
    }

    private void fillUI() {
        pushNotificationSwitch.setChecked(QBSettings.getInstance().isEnablePushNotification());

        email.setText(DISharedPreferences.getInstance().getEmail());
        nickName.setText(DISharedPreferences.getInstance().getUsername());
        phone.setText(DISharedPreferences.getInstance().getPhoneNumber());

        showUserAvatar();
    }

    private void initCurrentData() {
        initCustomData();
    }

    private void updateOldData() {
        isNeedUpdateImage = false;
    }

    private void resetUserData() {
        isNeedUpdateImage = false;
        initCurrentData();
    }

    private void showUserAvatar() {
        ImageLoader.getInstance().displayImage(
                user.getAvatar(),
                avatarImageView,
                ImageLoaderUtils.UIL_USER_AVATAR_DISPLAY_OPTIONS);
    }

    private void addActions() {
        addAction(QBServiceConsts.LOGOUT_FAIL_ACTION, failAction);

        updateBroadcastActionList();
    }

    private void removeActions() {
        removeAction(QBServiceConsts.LOGOUT_FAIL_ACTION);

        updateBroadcastActionList();
    }

    private QBUser createUserForUpdating() {
        QBUser newUser = new QBUser();
        newUser.setId(user.getId());
        newUser.setPassword(user.getPassword());
        newUser.setOldPassword(user.getOldPassword());
        newUser.setFullName(user.getFullName());
        newUser.setFacebookId(user.getFacebookId());
        newUser.setTwitterId(user.getTwitterId());
        newUser.setTwitterDigitsId(user.getTwitterDigitsId());
        newUser.setCustomData(Utils.customDataToString(userCustomData));
        return newUser;
    }

    private void saveChanges() {
        showProgress();

        QBUser newUser = createUserForUpdating();
        File file = null;
        if (isNeedUpdateImage && imageUri != null) {
            file = MediaUtils.getCreatedFileFromUri(imageUri);
        }

        Observable<QMUser> qmUserObservable;

        if (file != null) {
            qmUserObservable = ServiceManager.getInstance().updateUser(newUser, file);
        } else {
            qmUserObservable = ServiceManager.getInstance().updateUser(newUser);
        }

        qmUserObservable.subscribe(new Subscriber<QMUser>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                hideProgress();

                if (e != null) {
                    ToastUtils.longToast(e.getMessage());
                }

                resetUserData();
            }

            @Override
            public void onNext(QMUser qmUser) {
                hideProgress();
                AppSession.getSession().updateUser(qmUser);
                updateOldData();
                user = qmUser;
                showUserAvatar();
            }
        });
    }
}