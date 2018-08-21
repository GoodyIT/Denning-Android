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
}
