package it.denning.navigation.add;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.zakariya.stickyheaders.StickyHeaderLayoutManager;

import butterknife.BindView;
import butterknife.ButterKnife;
import it.denning.App;
import it.denning.MainActivity;
import it.denning.R;
import it.denning.general.MyNameCodeCallback;
import it.denning.model.NameCode;
import it.denning.navigation.add.bill.AddBillActivity;
import it.denning.navigation.add.contact.AddContactActivity;
import it.denning.navigation.add.diary.DiaryActivity;
import it.denning.navigation.add.leaveapplication.LeaveApplicationActivity;
import it.denning.navigation.add.matter.AddMatterActivity;
import it.denning.navigation.add.property.AddPropertyActivity;
import it.denning.navigation.add.quotation.AddQuotationActivity;
import it.denning.navigation.add.receipt.AddReceiptActivity;
import it.denning.network.NetworkManager;
import it.denning.search.utils.OnSectionItemClickListener;

/**
 * Created by denningit on 09/04/2017.
 */

public class Add extends Fragment implements OnSectionItemClickListener {
    @BindView(R.id.recycler_list)
    protected RecyclerView recyclerView;

    public static void start(Context context) {
        Intent i = new Intent(context, Add.class);
        context.startActivity(i);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //returning our layout file
        //change R.layout.yourlayoutfilename for each of your fragments
        ((MainActivity)getActivity()).hideDennigSupport();
        ((MainActivity)getActivity()).hideBottomBar();
        return inflater.inflate(R.layout.fragment_recyclerview, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        ((MainActivity)getActivity()).titleView.setText(R.string.add_title);

        setupRecyclerView();
    }

    private void setupRecyclerView() {
        AddAdapter addAdapter = new AddAdapter(this);
        recyclerView.setLayoutManager(new StickyHeaderLayoutManager());
        recyclerView.addItemDecoration(new it.denning.general.DividerItemDecoration(ContextCompat.getDrawable(App.getInstance(), R.drawable.item_decorator)));

        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(addAdapter);
    }

    @Override
    public void onClick(View view, int sectionIndex, int itemIndex, String name) {
        switch (sectionIndex) {
            case 0:
                switch (itemIndex) {
                    case 0: // Contact
                        AddContactActivity.start(getActivity(), null);
                        break;
                    case 1: // Property
                        AddPropertyActivity.start(getActivity(), null);
                        break;
                    case 2: // Matter
                        AddMatterActivity.start(getActivity(), null);
                        break;
                }
                break;
            case 1:
                switch (itemIndex) {
                    case 0: // Court Diary
                        DiaryActivity.start(getActivity(), R.string.add_court_diary_title);
                        break;
                    case 1: // Office Diary
                        DiaryActivity.start(getActivity(), R.string.add_office_diary_title);
                        break;
                    case 2: // Leave Application
                        NetworkManager.getInstance().getSubmittedBy(new MyNameCodeCallback() {
                            @Override
                            public void next(NameCode value) {
                                LeaveApplicationActivity.start(getActivity(), value);
                            }
                        });

                        break;
                }
                break;
            case 2:
                switch (itemIndex) {
                    case 0: // Quotation
                        AddQuotationActivity.start(getActivity());
                        break;
                    case 1: // Tax Invoice
                        AddBillActivity.start(getActivity(), true, null);
                        break;
                    case 2: // Receipt
                        AddReceiptActivity.start(getActivity(), null);
                        break;
                }
                break;
        }
    }


}
