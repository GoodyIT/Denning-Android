package it.denning.model;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;

import okio.BufferedSource;

/**
 * Created by hothongmee on 06/11/2017.
 */

public class ThreeItemModel implements Serializable {
    public String iStyle;
    public List<GraphModel> graphs;
    public List<ItemModel> items;
    public List<VisibleModel> main;

    public static ThreeItemModel getThreeItemFromResponse(JSONObject jsonObject) {
        ThreeItemModel model = new ThreeItemModel();

        try {
            model.iStyle = jsonObject.getString("iStyle");
            model.graphs = GraphModel.getGraphArrayFromResponse(jsonObject.getJSONArray("graphs"));
            model.items = ItemModel.getItemModelArrayFromResponse(jsonObject.getJSONArray("items"));
            model.main = VisibleModel.getVisibleModelArrayFromResponse(jsonObject.getJSONArray("main"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return model;
    }
}
