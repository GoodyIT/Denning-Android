package it.denning.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by denningit on 20/04/2017.
 */

public class DocumentModel implements Serializable {
    public String date;
    public String name;
    public List<DocumentModel> folders;
    public List<FileModel> documents, originals;

    public int getDocumentsSize() {
        if (documents == null) {
            return 0;
        }
        return documents.size();
    }

    public int getFolderSize(int index) {
        if (folders == null) {
            return 0;
        }
        List<FileModel> _documents = folders.get(index).documents;
        if (_documents == null) {
            return 0;
        }
        return _documents.size();
    }

    public String getName() {
        if (name == null) {
            return "";
        }
        return name;
    }

    public DocumentModel searchQuery(String query) {
        DocumentModel newDocumentModel = new DocumentModel();

        newDocumentModel.name = name;
        newDocumentModel.date = date;
        newDocumentModel.folders = new ArrayList<>();
        for (DocumentModel model : folders) {
            DocumentModel folderDoc = new DocumentModel();
            folderDoc.name = model.name;
            folderDoc.date = model.date;
            folderDoc.documents = new ArrayList<>();
            for (FileModel file : model.documents) {
                if (file.name.toLowerCase().contains(query.toLowerCase())) {
                    folderDoc.documents.add(file);
                }
            }
            newDocumentModel.folders.add(folderDoc);
        }

        newDocumentModel.documents = new ArrayList<>();
        if (documents != null) {
            for (FileModel file : documents) {
                if (file.name.toLowerCase().contains(query.toLowerCase())) {
                    newDocumentModel.documents.add(file);
                }
            }
        }

        return newDocumentModel;
    }
}
