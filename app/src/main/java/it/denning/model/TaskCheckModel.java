package it.denning.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hothongmee on 09/09/2017.
 */

public class TaskCheckModel {
    public String clerkName;
    public String startDate;
    public String endDate;
    public String fileName;
    public String fileNo;
    public String taskName;

    public static TaskCheckModel getTaskCheckFromResponse(JSONObject response) {
        TaskCheckModel taskCheckModel = new TaskCheckModel();

        try {
            taskCheckModel.fileNo = response.getString("fileNo");
            taskCheckModel.clerkName = response.getString("clerkName");
            taskCheckModel.startDate = response.getString("startDate");
            taskCheckModel.endDate = response.getString("endDate");
            taskCheckModel.fileName = response.getString("fileName");
            taskCheckModel.taskName = response.getString("taskName");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return taskCheckModel;
    }

    public static ArrayList<TaskCheckModel> getTaskCheckArrayFromResponse(JSONArray response) {
        ArrayList<TaskCheckModel> taskCheckModelList = new ArrayList<>();

        for (int i = 0; i < response.length(); i++) {
            try {
                taskCheckModelList.add(TaskCheckModel.getTaskCheckFromResponse(response.getJSONObject(i)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return  taskCheckModelList;
    }
}
