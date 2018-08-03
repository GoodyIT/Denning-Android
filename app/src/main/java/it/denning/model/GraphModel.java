package it.denning.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by hothongmee on 06/11/2017.
 */

public class GraphModel implements Serializable {
    public String graphID;
    public String graphName;
    public String xLegend;
    public String yLegend;
    public ArrayList<String> xValue = new ArrayList<>();
    public ArrayList<Float> yValue = new ArrayList<>();

    public static GraphModel getGraphModelFromResponse(JSONObject jsonObject) {
        GraphModel model = new GraphModel();

        try {
            model.graphID = jsonObject.getString("graphID");
            model.graphName = jsonObject.getString("graphName");
            model.xLegend = jsonObject.getString("xLegend");
            model.yLegend = jsonObject.getString("yLegend");
            JSONArray jsonArray = jsonObject.getJSONArray("xValue");
            for (int i=0;i<jsonArray.length();i++){
                model.xValue.add((String) jsonArray.get(i));
            }
            jsonArray = jsonObject.getJSONArray("yValue");
            for (int i=0;i<jsonArray.length();i++){
                model.yValue.add(Float.parseFloat(jsonArray.getString(i)));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return model;
    }

    public static ArrayList<GraphModel> getGraphArrayFromResponse(JSONArray jsonArray) {
        ArrayList<GraphModel> graphModelArrayList = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                graphModelArrayList.add(GraphModel.getGraphModelFromResponse(jsonArray.getJSONObject(i)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return graphModelArrayList;
    }
}
