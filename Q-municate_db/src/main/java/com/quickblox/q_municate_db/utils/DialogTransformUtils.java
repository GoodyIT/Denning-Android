package com.quickblox.q_municate_db.utils;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import com.quickblox.chat.model.QBChatDialog;
import com.quickblox.chat.model.QBDialogCustomData;
import com.quickblox.chat.model.QBDialogType;
import com.quickblox.core.helper.CollectionsUtil;
import com.quickblox.q_municate_db.managers.DataManager;
import com.quickblox.q_municate_db.models.Dialog;
import com.quickblox.q_municate_db.models.DialogOccupant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class DialogTransformUtils {

    public static QBChatDialog createQBDialogFromLocalDialog(DataManager dataManager, Dialog dialog) {
        List<DialogOccupant> dialogOccupantsList = dataManager.getDialogOccupantDataManager()
                .getDialogOccupantsListByDialogId(dialog.getDialogId());
        QBChatDialog qbDialog = createQBDialogFromLocalDialog(dialog, dialogOccupantsList);
        return qbDialog;
    }

    private static QBChatDialog createQBDialogFromLocalDialog(Dialog dialog, List<DialogOccupant> dialogOccupantsList) {
        QBChatDialog qbDialog = new QBChatDialog();
        qbDialog.setDialogId(dialog.getDialogId());
        qbDialog.setRoomJid(dialog.getRoomJid());
        qbDialog.setPhoto(dialog.getPhoto());
        qbDialog.setName(dialog.getTitle());
        if (dialog.getCustomData() != null) {
            qbDialog.setCustomData(chatCustomDataToObject(dialog.getCustomData()));
        }
        qbDialog.setOccupantsIds(createOccupantsIdsFromDialogOccupantsList(dialogOccupantsList));
        qbDialog.setType(
                Dialog.Type.PRIVATE.equals(dialog.getType()) ? QBDialogType.PRIVATE : QBDialogType.GROUP);

        qbDialog.setUpdatedAt(new Date(dialog.getModifiedDateLocal()));
        return qbDialog;
    }

    public static ArrayList<Integer> createOccupantsIdsFromDialogOccupantsList(
            List<DialogOccupant> dialogOccupantsList) {
        ArrayList<Integer> occupantsIdsList = new ArrayList<>(dialogOccupantsList.size());
        for (DialogOccupant dialogOccupant : dialogOccupantsList) {
            occupantsIdsList.add(dialogOccupant.getUser().getId());
        }
        return occupantsIdsList;
    }

    public static Dialog createLocalDialog(QBChatDialog qbDialog) {
        Dialog dialog = new Dialog();
        dialog.setDialogId(qbDialog.getDialogId());
        dialog.setRoomJid(qbDialog.getRoomJid());
        dialog.setTitle(qbDialog.getName());
        dialog.setPhoto(qbDialog.getPhoto());
        if (qbDialog.getCustomData() != null) {
            dialog.setCustomData(dialogCustomDataToString(qbDialog.getCustomData()));
        }
        if (qbDialog.getUpdatedAt() != null) {
            dialog.setUpdatedAt(qbDialog.getUpdatedAt().getTime()/1000);
        }
        dialog.setModifiedDateLocal(qbDialog.getLastMessageDateSent());

        if (QBDialogType.PRIVATE.equals(qbDialog.getType())) {
            dialog.setType(Dialog.Type.PRIVATE);
        } else if (QBDialogType.GROUP.equals(qbDialog.getType())) {
            dialog.setType(Dialog.Type.GROUP);
        }

        return dialog;
    }

    public static List<Dialog> getListLocalDialogsFromQBDialogs(Collection<QBChatDialog> chatDialogs){
        List<Dialog> dialogsList = new ArrayList<>(chatDialogs.size());
        for (QBChatDialog chatDialog : chatDialogs){
            dialogsList.add(DialogTransformUtils.createLocalDialog(chatDialog));
        }

        return dialogsList;
    }

    public static List<QBChatDialog> getListQBDialogsFromLocalDialogs(Collection<Dialog> dialogsList) {
        List<QBChatDialog> chatDialogList = new ArrayList<>();

        if (!CollectionsUtil.isEmpty(dialogsList)) {
            for (Dialog dialog : dialogsList) {
                chatDialogList.add(DialogTransformUtils.createQBDialogFromLocalDialog(DataManager.getInstance(), dialog));
            }
        }

        return chatDialogList;
    }

    public static String dialogCustomDataToString(QBDialogCustomData dialogCustomData) {
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("className", dialogCustomData.getClassName());
            jsonObject.put("fields", toJSON(dialogCustomData.getFields()));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject.toString();
    }

    public static Object toJSON(Object object) throws JSONException {
        if (object instanceof Map) {
            JSONObject json = new JSONObject();
            Map map = (Map) object;
            for (Object key : map.keySet()) {
                json.put(key.toString(), toJSON(map.get(key)));
            }
            return json;
        } else if (object instanceof Iterable) {
            JSONArray json = new JSONArray();
            for (Object value : ((Iterable)object)) {
                json.put(value);
            }
            return json;
        } else {
            return object;
        }
    }

    public static Map<String, Object> toMap(JSONObject object) throws JSONException {
        Map<String, Object> map = new HashMap();
        Iterator keys = object.keys();
        while (keys.hasNext()) {
            String key = (String) keys.next();
            map.put(key, fromJson(object.get(key)));
        }
        return map;
    }

    public static List toList(JSONArray array) throws JSONException {
        List list = new ArrayList();
        for (int i = 0; i < array.length(); i++) {
            list.add(fromJson(array.get(i)));
        }
        return list;
    }

    private static Object fromJson(Object json) throws JSONException {
        if (json == JSONObject.NULL) {
            return null;
        } else if (json instanceof JSONObject) {
            return toMap((JSONObject) json);
        } else if (json instanceof JSONArray) {
            return toList((JSONArray) json);
        } else {
            return json;
        }
    }

    public static QBDialogCustomData chatCustomDataToObject(String chatCustomDataString) {
        if (TextUtils.isEmpty(chatCustomDataString)) {
            return new QBDialogCustomData();
        }

        QBDialogCustomData dialogCustomData = null;
        Gson gson = new Gson();
        try {
            dialogCustomData = gson.fromJson(chatCustomDataString, QBDialogCustomData.class);

            JSONObject jsonObject = new JSONObject(chatCustomDataString);
            Object fields = toMap(jsonObject.getJSONObject("fields"));
            dialogCustomData.setClassName(jsonObject.getString("className"));
            dialogCustomData.setFields((HashMap<String, Object>) fields);
        } catch (JSONException e) {
            ErrorUtils.logError(e);
        }

        return dialogCustomData;
    }
}
