package it.denning.search.contact;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.view.View;

import com.google.gson.Gson;
import com.google.gson.JsonElement;

import org.zakariya.stickyheaders.StickyHeaderLayoutManager;

import it.denning.R;
import it.denning.general.DIConstants;
import it.denning.general.DISharedPreferences;
import it.denning.model.Contact;
import it.denning.model.MatterModel;
import it.denning.navigation.add.contact.AddContactActivity;
import it.denning.network.CompositeCompletion;
import it.denning.network.NetworkManager;
import it.denning.search.matter.MatterActivity;
import it.denning.search.utils.OnClickListenerWithCode;
import it.denning.ui.activities.base.MyBaseActivity;

/**
 * Created by denningit on 26/04/2017.
 */

public class SearchContactActivity extends MyBaseActivity implements OnClickListenerWithCode {
    private Boolean isRelatedMatter;
    Contact contact;

    public static void start(Context context, String matter) {
        Intent i = new Intent(context, SearchContactActivity.class);
        i.putExtra("matter", matter);
        context.startActivity(i);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initFields();
        initActionBar();
    }

    private void initFields() {
        contact = DISharedPreferences.contact;

        isRelatedMatter = getIntent().getStringExtra("matter").equals("matter");
        if (isRelatedMatter) {
            toolbarTitle.setText(R.string.related_matters_title);
        } else {
            toolbarTitle.setText(R.string.contact_title);
        }

        if (contact != null) {
            setupRecyclerView(contact);
        }
    }

    private void setupRecyclerView(Contact contact) {
        SearchContactAdapter searchContactAdapter = new SearchContactAdapter(contact, this, this, isRelatedMatter);
        recyclerView.setLayoutManager(new StickyHeaderLayoutManager());
        recyclerView.addItemDecoration(new it.denning.general.DividerItemDecoration(ContextCompat.getDrawable(this, R.drawable.item_decorator)));

        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(searchContactAdapter);
    }

    public void editContact() {
        DISharedPreferences.contact = contact;
        AddContactActivity.start(this);
    }

    @Override
    public void onClick(View view, String code) {
        String url = DIConstants.MATTER_GET_URL + code;
        NetworkManager.getInstance().sendPrivateGetRequestWithoutError(url, new CompositeCompletion() {
            @Override
            public void parseResponse(JsonElement jsonElement) {
                MatterModel relatedMatter = new Gson().fromJson(jsonElement, MatterModel.class);
                MatterActivity.start(SearchContactActivity.this, relatedMatter);
            }
        }, SearchContactActivity.this);
    }
}
