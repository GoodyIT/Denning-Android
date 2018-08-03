package it.denning.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by hothongmee on 09/11/2017.
 */

public class AttendanceDetailModel {
    public String timeIn;
    public String timeOut;
    public String hours;

    public static AttendanceDetailModel getAttendanceDetailFromResponse(JSONObject jsonObject) throws JSONException {
        AttendanceDetailModel model = new AttendanceDetailModel();

        model.timeIn = jsonObject.getString("timeIn");
        model.timeOut = jsonObject.getString("timeOut");
        model.hours = jsonObject.getString("hours");
        return model;
    }

    public static ArrayList<AttendanceDetailModel> getAttendanceDetailArrayFromResponse(JSONArray jsonArray){
        ArrayList<AttendanceDetailModel> arrayList = new ArrayList<>();

        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                arrayList.add(AttendanceDetailModel.getAttendanceDetailFromResponse(jsonArray.getJSONObject(i)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return arrayList;
    }
}
