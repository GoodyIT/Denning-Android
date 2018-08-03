package it.denning.navigation.add.leaveapplication.leaverecord;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.quickblox.q_municate_db.utils.ErrorUtils;

import java.util.ArrayList;
import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import it.denning.App;
import it.denning.R;
import it.denning.general.DIConstants;
import it.denning.general.EndlessRecyclerViewScrollListener;
import it.denning.model.LeaveRecordModel;
import it.denning.navigation.add.leaveapplication.LeaveApplicationActivity;
import it.denning.navigation.dashboard.section1.staffleave.leaverecords.LeaveRecordsActivity;
import it.denning.network.CompositeCompletion;
import it.denning.network.ErrorHandler;
import it.denning.network.NetworkManager;
import it.denning.search.template.TemplateAdapter;

/**
 * Created by denningit on 2018-01-26.
 */

public class LeaveRecordFragment extends Fragment {

    @BindView(R.id.recycler_list)
    protected RecyclerView recyclerView;

    private LeaveRecordAdapter adapter;
    private LinearLayoutManager linearLayoutManager;
    private Integer page = 1;
    private String code;

    public static LeaveRecordFragment newInstance(String code) {
        LeaveRecordFragment fragment = new LeaveRecordFragment();
        Bundle bundle = new Bundle();
        bundle.putString("code", code);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        setupRecyclerView();
        setupEndlessScroll();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadLeaveRecords();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        code = getArguments().getString("code");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recyclerview, container, false);
        return view;
    }

    private void setupRecyclerView() {
        adapter = new LeaveRecordAdapter(new ArrayList<LeaveRecordModel>());
        linearLayoutManager = new LinearLayoutManager(getActivity(), OrientationHelper.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addItemDecoration(new it.denning.general.DividerItemDecoration(ContextCompat.getDrawable(App.getInstance(), R.drawable.item_decorator)));

        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
    }

    private void setupEndlessScroll() {
        EndlessRecyclerViewScrollListener scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                loadLeaveRecords();
            }
        };
        recyclerView.clearOnScrollListeners();
        recyclerView.addOnScrollListener(scrollListener);
    }

    private void loadLeaveRecords() {
        String url = DIConstants.LEAVE_RECORD_GET_URL + code + "&page=" + page;
        if (getActivity() instanceof LeaveApplicationActivity) {
            ((LeaveApplicationActivity)getActivity()).showActionBarProgress();
        } else {
            ((LeaveRecordsActivity)getActivity()).showActionBarProgress();
        }
        NetworkManager.getInstance().sendPrivateGetRequest(url, new CompositeCompletion() {
            @Override
            public void parseResponse(JsonElement jsonElement) {
                manageSuccess(jsonElement);
            }
        }, new ErrorHandler() {
            @Override
            public void handleError(String error) {
                manageError(error);
            }
        });
    }

    private void manageSuccess(JsonElement jsonElement) {
        if (getActivity() instanceof LeaveApplicationActivity) {
            ((LeaveApplicationActivity)getActivity()).hideActionBarProgress();
        } else {
            ((LeaveRecordsActivity)getActivity()).hideActionBarProgress();
        }
        LeaveRecordModel[] models = new Gson().fromJson(jsonElement, LeaveRecordModel[].class);
        if (models.length > 0) {
            page++;
        }

        adapter.addItems(Arrays.asList(models));
    }

    private void manageError(String error) {
        if (getActivity() instanceof LeaveApplicationActivity) {
            ((LeaveApplicationActivity)getActivity()).hideActionBarProgress();
        } else {
            ((LeaveRecordsActivity)getActivity()).hideActionBarProgress();
        }
        ErrorUtils.showError(getActivity(), error);
    }
}
