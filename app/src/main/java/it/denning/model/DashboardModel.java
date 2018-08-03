package it.denning.model;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by hothongmee on 07/09/2017.
 */

public class DashboardModel implements Serializable {
    public S1 s1;
    public S2 s2;
    public S3 s3;
    public S1 s4;
    public String todayDate;

    public static DashboardModel getDashboardFromResponse(JSONObject jsonObject) {
        DashboardModel dashboard = new DashboardModel();
        try {
            dashboard.todayDate = jsonObject.getString("todayDate");
            dashboard.s1 = S1.getS1FromResponse(jsonObject.getJSONObject("s1"));
            dashboard.s2 = S2.getS2FromResponse(jsonObject.getJSONObject("s2"));
            dashboard.s3 = S3.getS3FromResponse(jsonObject.getJSONObject("s3"));
            dashboard.s4 = S1.getS1FromResponse(jsonObject.getJSONObject("s4"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return dashboard;
    }
}
