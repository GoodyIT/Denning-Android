package it.denning.search.paymentrecord;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.quickblox.q_municate_db.utils.ErrorUtils;

import org.zakariya.stickyheaders.StickyHeaderLayoutManager;

import it.denning.R;
import it.denning.general.DIConstants;
import it.denning.model.PaymentRecord;
import it.denning.network.CompositeCompletion;
import it.denning.network.ErrorHandler;
import it.denning.network.NetworkManager;
import it.denning.ui.activities.base.MyBaseActivity;

/**
 * Created by denningit on 2017-12-24.
 */

public class PaymentRecordActivity extends MyBaseActivity {

    private String fileNo;

    public static void start(Context context, String fileNo) {
        Intent i = new Intent(context, PaymentRecordActivity.class);
        i.putExtra("fileNo", fileNo);
        context.startActivity(i);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initFields();
        initActionBar();

        loadPaymentRecord();
    }

    private void initFields() {
        toolbarTitle.setText(getString(R.string.payment_record_title));
        fileNo = getIntent().getStringExtra("fileNo");
    }

    private void setupRecyclerView(PaymentRecord paymentRecord) {
        PaymentRecordAdapter searchContactAdapter = new PaymentRecordAdapter(paymentRecord);
        recyclerView.setLayoutManager(new StickyHeaderLayoutManager());
        recyclerView.addItemDecoration(new it.denning.general.DividerItemDecoration(ContextCompat.getDrawable(this, R.drawable.item_decorator)));

        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(searchContactAdapter);
    }

    private void loadPaymentRecord() {
        showActionBarProgress();
        String url = DIConstants.PAYMENT_RECORD + fileNo;

        NetworkManager.getInstance().sendPrivateGetRequest(url, new CompositeCompletion() {
            @Override
            public void parseResponse(JsonElement jsonElement) {
                hideActionBarProgress();
                PaymentRecord paymentRecord = new Gson().fromJson(jsonElement.getAsJsonObject(), PaymentRecord.class);
                setupRecyclerView(paymentRecord);
            }
        }, new ErrorHandler() {
            @Override
            public void handleError(String error) {
                hideActionBarProgress();
                ErrorUtils.showError(PaymentRecordActivity.this, error);
            }
        });
    }
}
