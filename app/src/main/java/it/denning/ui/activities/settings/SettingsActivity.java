package it.denning.ui.activities.settings;

import android.content.Intent;
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
import com.quickblox.q_municate_core.service.QBServiceConsts;
import com.quickblox.q_municate_core.utils.UserFriendUtils;
import com.quickblox.q_municate_db.utils.ErrorUtils;
import com.quickblox.q_municate_user_service.model.QMUser;

import butterknife.BindView;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import it.denning.R;
import it.denning.ui.activities.base.BaseLoggableActivity;
import it.denning.ui.activities.changepassword.ChangePasswordActivity;
import it.denning.ui.activities.feedback.FeedbackActivity;
import it.denning.ui.activities.invitefriends.InviteFriendsActivity;
import it.denning.ui.activities.profile.MyProfileActivity;
import it.denning.ui.fragments.dialogs.base.TwoButtonsDialogFragment;
import it.denning.ui.views.roundedimageview.RoundedImageView;
import it.denning.utils.ToastUtils;
import it.denning.utils.helpers.FacebookHelper;
import it.denning.utils.helpers.FirebaseAuthHelper;
import it.denning.utils.helpers.ServiceManager;
import it.denning.utils.image.ImageLoaderUtils;
import rx.Subscriber;

public class SettingsActivity extends BaseLoggableActivity {

    public static final int REQUEST_CODE_LOGOUT = 300;

    @BindView(R.id.avatar_imageview)
    RoundedImageView avatarImageView;

    @BindView(R.id.full_name_edittext)
    TextView fullNameTextView;

    @BindView(R.id.push_notification_switch)
    SwitchCompat pushNotificationSwitch;

    @BindView(R.id.change_password_view)
    RelativeLayout changePasswordView;

    private QMUser user;
    private FacebookHelper facebookHelper;
    private FirebaseAuthHelper firebaseAuthHelper;

    public static void startForResult(Fragment fragment) {
        Intent intent = new Intent(fragment.getActivity(), SettingsActivity.class);
        fragment.getActivity().startActivityForResult(intent, REQUEST_CODE_LOGOUT);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        removeActions();
    }

    @OnCheckedChanged(R.id.push_notification_switch)
    void enablePushNotification(boolean enable) {
        QBSettings.getInstance().setEnablePushNotification(enable);
    }

    @OnClick(R.id.change_password_button)
    void changePassword() {
        ChangePasswordActivity.start(this);
    }

    @OnClick(R.id.logout_button)
    void logout() {
        if (checkNetworkAvailableWithError()) {
            TwoButtonsDialogFragment
                    .show(getSupportFragmentManager(), R.string.dlg_logout, R.string.dlg_confirm,
                            new MaterialDialog.ButtonCallback() {
                                @Override
                                public void onPositive(MaterialDialog dialog) {
                                    super.onPositive(dialog);
                                    showProgress();

                                    facebookHelper.logout();
                                    firebaseAuthHelper.logout();

                                    ServiceManager.getInstance().logout(new Subscriber<Void>() {
                                        @Override
                                        public void onCompleted() {

                                        }

                                        @Override
                                        public void onError(Throwable e) {
                                            ErrorUtils.showError(SettingsActivity.this, e);
                                            hideProgress();
                                        }

                                        @Override
                                        public void onNext(Void aVoid) {
                                            setResult(RESULT_OK);
                                            hideProgress();
                                            finish();
                                        }
                                    });
                                }
                            });
        }

    }


    private void initFields() {
        title = getString(R.string.settings_title);
        user = UserFriendUtils.createLocalUser(AppSession.getSession().getUser());
        facebookHelper = new FacebookHelper(this);
        firebaseAuthHelper = new FirebaseAuthHelper(SettingsActivity.this);
    }

    private void fillUI() {
        pushNotificationSwitch.setChecked(QBSettings.getInstance().isEnablePushNotification());
        changePasswordView.setVisibility(
                LoginType.EMAIL.equals(AppSession.getSession().getLoginType()) ? View.VISIBLE : View.GONE);
        fullNameTextView.setText(user.getFullName());

        showUserAvatar();
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

}