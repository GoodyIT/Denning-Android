package it.denning.navigation.add.diary;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.gson.JsonElement;

import butterknife.BindView;
import butterknife.OnClick;
import info.hoang8f.android.segmented.SegmentedGroup;
import it.denning.R;
import it.denning.general.DIAlert;
import it.denning.general.DIConstants;
import it.denning.general.MyCallbackInterface;
import it.denning.general.SaveUpdateListener;
import it.denning.model.EditCourtModel;
import it.denning.model.OfficeDiaryModel;
import it.denning.navigation.add.diary.court.CourtFragment;
import it.denning.navigation.add.diary.office.OfficeDiaryAdapter;
import it.denning.navigation.add.diary.office.OfficeFragment;
import it.denning.navigation.add.diary.personal.PersonalFragment;
import it.denning.network.NetworkManager;
import it.denning.ui.activities.base.BaseActivity;
import it.denning.utils.KeyboardUtils;
import it.denning.utils.listeners.UserStatusChangingListener;

/**
 * Created by denningit on 2017-12-24.
 */

public class DiaryActivity extends BaseActivity implements RadioGroup.OnCheckedChangeListener {

    @BindView(R.id.toolbar_title)
    protected TextView toolbarTitle;

    @BindView(R.id.diary_segmented)
    SegmentedGroup topFilter;

    public int resTitle;
    private EditCourtModel courtModel;
    private OfficeDiaryModel officeDiaryModel;
    private boolean isSaved = false;
    private int curCheckId;

    @OnClick(R.id.back_btn)
    void onBack() {
        KeyboardUtils.hideKeyboard(this);
        finish();
    }

    public static void start(Context context, int title, EditCourtModel model) {
        Intent intent = new Intent(context, DiaryActivity.class);
        intent.putExtra("title", title);
        intent.putExtra("model", model);
        context.startActivity(intent);
    }

    public static void start(Context context, int title, OfficeDiaryModel model) {
        Intent intent = new Intent(context, DiaryActivity.class);
        intent.putExtra("title", title);
        intent.putExtra("model", model);
        context.startActivity(intent);
    }

    public static void start(Context context, int title) {
        start(context, title, (EditCourtModel) null);
    }

    @Override
    protected int getContentResId() {
        return R.layout.activity_add_diary;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initFields();
        initActionBar();

        setupFragment();
        setupFilter();

    }

    private void initFields() {
        resTitle = getIntent().getIntExtra("title", -1);
        updateTitle(resTitle);
        toolbarTitle.setText(resTitle);
    }

    public void updateTitle(int resTitle) {

    }

    private void setupFilter() {
        if (courtModel != null || officeDiaryModel != null) {
            return;
        }
        topFilter.setOnCheckedChangeListener(this);
    }

    private void setupFragment() {
        int checkId = R.id.court;
        BaseDiaryFragment fragment = null;
        switch (resTitle) {
            case R.string.add_court_diary_title:
            case R.string.update_court_diary_title:
                checkId = R.id.court;
                courtModel = (EditCourtModel) getIntent().getSerializableExtra("model");
                fragment = CourtFragment.newInstance(courtModel);
                break;
            case R.string.update_office_diary_title:
            case R.string.add_office_diary_title:
                checkId = R.id.office;
                officeDiaryModel = (OfficeDiaryModel) getIntent().getSerializableExtra("model");
                fragment = OfficeFragment.newInstance(officeDiaryModel);
                break;
            case R.string.update_personal_diary_title:
            case R.string.add_personal_diary_title:
                checkId = R.id.personal;
                officeDiaryModel = (OfficeDiaryModel) getIntent().getSerializableExtra("model");
                fragment = PersonalFragment.newInstance(officeDiaryModel);
                break;
        }
        topFilter.check(checkId);
        curCheckId = checkId;
        changeFragment(fragment);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        BaseDiaryFragment fragment = null;
        curCheckId = checkedId;
        switch (checkedId) {
            case R.id.court:
                fragment = CourtFragment.newInstance(courtModel);
                resTitle = R.string.add_court_diary_title;
                break;
            case R.id.office:
                fragment = OfficeFragment.newInstance(officeDiaryModel);
                resTitle = R.string.add_office_diary_title;
                break;
            case R.id.personal:
                fragment = PersonalFragment.newInstance(officeDiaryModel);
                resTitle = R.string.add_personal_diary_title;
                break;
        }

        updateTitle(resTitle);
        changeFragment(fragment);
    }

    private void changeFragment(BaseDiaryFragment fragment) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.framelayout, fragment);
        ft.commit();
    }

    private void saveDiary() {
        if (isSaved) {
            return;
        }

        DIAlert.showSimpleAlertWithCompletion(this, R.string.alert_save_diary, new MyCallbackInterface() {
            @Override
            public void nextFunction() {
                _save();
            }

            @Override
            public void nextFunction(JsonElement jsonElement) {
            }
        });
    }

    private String selectUrl() {
        String url = "";
        switch (curCheckId) {
            case R.id.court:
                url = DIConstants.COURT_SAVE_UPATE_URL;
                break;
            case R.id.office:
                url = DIConstants.OFFICE_DIARY_SAVE_URL;
                break;
            case R.id.personal:
                url = DIConstants.PERSONAL_DIARY_SAVE_URL;
                break;
        }
        return url;
    }

    private void _save() {
        ((BaseDiaryFragment)getSupportFragmentManager().getFragments().get(0)).save(selectUrl());
    }

    private void updateDiary() {
        if (R.id.court != curCheckId) {
            return;
        }

        DIAlert.showSimpleAlertWithCompletion(this, R.string.alert_update_diary, new MyCallbackInterface() {
            @Override
            public void nextFunction() {
                _update();
            }

            @Override
            public void nextFunction(JsonElement jsonElement) {
            }
        });
    }

    private void _update() {
        ((BaseDiaryFragment)getSupportFragmentManager().getFragments().get(0)).update(selectUrl());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        if (courtModel == null) {
            getMenuInflater().inflate(R.menu.save, menu);
        } else {
            getMenuInflater().inflate(R.menu.update, menu);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_save) {
            saveDiary();
            return true;
        } else {
            updateDiary();
        }

        return super.onOptionsItemSelected(item);
    }
}
