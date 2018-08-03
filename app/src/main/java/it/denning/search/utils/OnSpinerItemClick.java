package it.denning.search.utils;

import com.google.gson.JsonObject;

/**
 * Created by denningit on 2018-01-19.
 */

public interface OnSpinerItemClick {
    public void onClick(String item, int position);
    public void onClick(JsonObject object);
}
