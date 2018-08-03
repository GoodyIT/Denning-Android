package it.denning.navigation.add.utils.simplespinerdialog;

import android.app.Activity;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.quickblox.q_municate_db.utils.ErrorUtils;

import java.util.ArrayList;
import java.util.List;

import it.denning.R;
import it.denning.network.CompositeCompletion;
import it.denning.network.ErrorHandler;
import it.denning.network.NetworkManager;
import it.denning.search.utils.OnSpinerItemClick;

/**
 * Created by denningit on 2018-01-19.
 */


public class SpinnerDialog {
    List<String> items = new ArrayList<>();
    String key;
    String _url;
    Activity context;
    String closeTitle="Close";
    int dTitle;
    OnSpinerItemClick onSpinerItemClick;
    AlertDialog alertDialog;
    ArrayAdapter<String> adapter;
    JsonObject[] jsonObjects;
    int pos;
    int style;
    int page = 1;


    public SpinnerDialog(Activity activity, String _url, String key, int dialogTitle) {
        this._url = _url;
        this.key = key;
        this.context = activity;
        this.dTitle = dialogTitle;
    }

    public SpinnerDialog(Activity activity, String _url, String key, int dialogTitle, String closeTitle) {
        this._url = _url;
        this.key = key;
        this.context = activity;
        this.dTitle = dialogTitle;
        this.closeTitle=closeTitle;
    }

    public SpinnerDialog(Activity activity, String _url, String key, int dialogTitle, int style) {
        this._url = _url;
        this.key = key;
        this.context = activity;
        this.dTitle = dialogTitle;
        this.style = style;
    }

    public SpinnerDialog(Activity activity, String _url, String key, int dialogTitle, int style,String closeTitle) {
        this._url = _url;
        this.key = key;
        this.context = activity;
        this.dTitle = dialogTitle;
        this.style = style;
        this.closeTitle=closeTitle;
    }

    public void bindOnSpinerListener(OnSpinerItemClick onSpinerItemClick1) {
        this.onSpinerItemClick = onSpinerItemClick1;
    }

    public void showSpinerDialog() {
        AlertDialog.Builder adb = new AlertDialog.Builder(context);
        View v = context.getLayoutInflater().inflate(R.layout.dialog_layout, null);
        TextView rippleViewClose = (TextView) v.findViewById(R.id.close);
        TextView title = (TextView) v.findViewById(R.id.spinerTitle);
        rippleViewClose.setText(closeTitle);
        title.setText(dTitle);
        final ListView listView = (ListView) v.findViewById(R.id.list);
        final EditText searchBox = (EditText) v.findViewById(R.id.searchBox);
        adapter = new ArrayAdapter<String>(context, R.layout.items_view, items);
        listView.setAdapter(adapter);
        adb.setView(v);
        alertDialog = adb.create();
        alertDialog.getWindow().getAttributes().windowAnimations = style;//R.style.DialogAnimations_SmileWindow;
        alertDialog.setCancelable(false);

        loadData("");

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                TextView t = view.findViewById(R.id.text1);
                pos = -1;
                for (int j = 0; j < items.size(); j++) {
                    if (t.getText().toString().equalsIgnoreCase(items.get(j).toString())) {
                        pos = j;
                    }
                }
                onSpinerItemClick.onClick(t.getText().toString(), pos);
                JsonObject newObj = new JsonObject();
                newObj.addProperty("key", t.getText().toString());
                if (pos != -1) {
                    newObj = jsonObjects[pos];
                }
                onSpinerItemClick.onClick(newObj);
                alertDialog.dismiss();
            }
        });

        searchBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                page = 1;
                loadData(searchBox.getText().toString());
            }
        });

        rippleViewClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }

    private void manageResponse(JsonElement jsonElement) {
        jsonObjects = new Gson().fromJson(jsonElement, JsonObject[].class);
        if (jsonObjects.length > 0) {
            page++;
        }
        items = new ArrayList<>();
        for (JsonObject jsonObject : jsonObjects) {
            items.add(jsonObject.get(key).getAsString());
        }

        adapter.clear();
        adapter.addAll(items);
    }

    private void manageError(String error) {
        ErrorUtils.showError(context, error);
    }

    public void loadData(String filter) {
        String url = _url + filter + "&page=" + page;

        NetworkManager.getInstance().sendPrivateGetRequest(url, new CompositeCompletion() {
            @Override
            public void parseResponse(JsonElement jsonElement) {
                manageResponse(jsonElement);
            }
        }, new ErrorHandler() {
            @Override
            public void handleError(String error) {
                manageError(error);
            }
        });
    }
}