package it.denning.search.property;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import butterknife.BindView;
import butterknife.ButterKnife;
import it.denning.R;
import it.denning.model.MatterProperty;
import it.denning.model.Property;

/**
 * Created by denningit on 27/04/2017.
 */

public class PropertyActivity extends AppCompatActivity {
    @BindView(R.id.search_property_list)
    RecyclerView propertyList;

    public static void start(Context context) {
        Intent intent = new Intent(context, PropertyActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_property);
        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        MatterProperty matterProperty = (MatterProperty) intent.getSerializableExtra("matterProperty");

        if (matterProperty != null) {
            displaySearchResult(matterProperty);
        }
    }

    private void displaySearchResult(MatterProperty matterProperty) {
        PropertyAdapter propertyAdapter = new PropertyAdapter(matterProperty, this);
        LinearLayoutManager propertyLayoutManager = new LinearLayoutManager(this, OrientationHelper.VERTICAL, false);

        propertyList.setLayoutManager(propertyLayoutManager);
        propertyList.setHasFixedSize(true);
        propertyList.setItemAnimator(new DefaultItemAnimator());
        try {
            propertyList.setAdapter(propertyAdapter);
        } catch (Exception e) {
            Log.i("", e.getLocalizedMessage());
        }

    }
}
