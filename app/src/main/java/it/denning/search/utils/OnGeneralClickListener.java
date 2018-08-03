package it.denning.search.utils;

import android.view.View;

import com.google.gson.JsonObject;

/**
 * Created by denningit on 2017-12-27.
 */

public interface OnGeneralClickListener {
    void onClick(View view, JsonObject model);
}
