package it.denning.search.utils;

import android.view.View;

/**
 * Created by denningit on 27/04/2017.
 */

public interface OnUploadClickListener {
    public void onUploadClick(View view, String code, int title, String url, String defaultFileName);
}
