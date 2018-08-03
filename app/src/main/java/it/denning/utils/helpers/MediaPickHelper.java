package it.denning.utils.helpers;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

import it.denning.ui.fragments.mediapicker.MediaPickHelperFragment;
import it.denning.ui.fragments.mediapicker.MediaSourcePickDialogFragment;

public class MediaPickHelper {

    public void pickAnMedia(Fragment fragment, int requestCode) {
        MediaPickHelperFragment mediaPickHelperFragment = MediaPickHelperFragment
                .start(fragment, requestCode);
        showMediaSourcePickerDialog(fragment.getChildFragmentManager(), mediaPickHelperFragment);
    }

    public void pickAnMedia(FragmentActivity activity, int requestCode) {
        MediaPickHelperFragment mediaPickHelperFragment = MediaPickHelperFragment
                .start(activity, requestCode);
        showMediaSourcePickerDialog(activity.getSupportFragmentManager(), mediaPickHelperFragment);
    }

    private void showMediaSourcePickerDialog(FragmentManager fm, MediaPickHelperFragment fragment) {
        MediaSourcePickDialogFragment.show(fm,fragment);
    }
}