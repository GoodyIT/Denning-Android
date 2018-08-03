package it.denning.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by denningit on 20/04/2017.
 */

public class FileModel implements Serializable {
    public String URL;
    public String date;
    public String ext;
    public String name;
    public String size;
    public String type;

    public static FileModel getFileFromResponse(JSONObject response) {
        FileModel fileModel = new FileModel();
        try {
            fileModel.URL = response.getString("URL");
            fileModel.date = response.getString("date");
            fileModel.ext= response.getString("ext");
            fileModel.name = response.getString("name");
            fileModel.size = response.getString("size");
            fileModel.type = response.getString("type");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return fileModel;
    }

    public static List<FileModel> getFileArrayFromResponse(JSONArray response) {
        List<FileModel> fileModelList = new ArrayList<>();
        for (int i = 0; i < response.length(); i++) {
            try {
                JSONObject obj = response.getJSONObject(i);
                fileModelList.add(FileModel.getFileFromResponse(obj));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return fileModelList;
    }
}
