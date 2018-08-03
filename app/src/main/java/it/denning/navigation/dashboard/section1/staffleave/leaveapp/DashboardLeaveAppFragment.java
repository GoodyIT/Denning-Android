package it.denning.navigation.dashboard.section1.staffleave.leaveapp;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
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
import it.denning.model.NameCode;
import it.denning.model.StaffLeaveModel;
import it.denning.navigation.dashboard.section1.staffleave.StaffLeaveActivity;
import it.denning.navigation.dashboard.section1.staffleave.leavependingapproval.LeavePendingApprovalActivity;
import it.denning.network.CompositeCompletion;
import it.denning.network.ErrorHandler;
import it.denning.network.NetworkManager;
import it.denning.search.utils.OnItemClickListener;

/**
 * Created by denningit on 2018-01-26.
 */

public class DashboardLeaveAppFragment extends Fragment implements OnItemClickListener{

    @BindView(R.id.recycler_list)
    protected RecyclerView recyclerView;

    private DashboardLeaveAppAdapter adapter;
    private LinearLayoutManager linearLayoutManager;
    private Integer page = 1;

    public static DashboardLeaveAppFragment newInstance() {
        DashboardLeaveAppFragment fragment = new DashboardLeaveAppFragment();
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
        loadLeaveApps();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recyclerview, container, false);
        return view;
    }

    private void setupRecyclerView() {
        adapter = new DashboardLeaveAppAdapter(new ArrayList<LeaveRecordModel>(), this);
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
                loadLeaveApps();
            }
        };
        recyclerView.clearOnScrollListeners();
        recyclerView.addOnScrollListener(scrollListener);
    }

    private void loadLeaveApps() {
        String url = ((StaffLeaveActivity)getActivity()).baseUrl + "pending&page=" + page;
        ((StaffLeaveActivity)getActivity()).showActionBarProgress();
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
        ((StaffLeaveActivity)getActivity()).hideActionBarProgress();
        LeaveRecordModel[] models = new Gson().fromJson(jsonElement, LeaveRecordModel[].class);
        if (models.length > 0) {
            page++;
        }

        adapter.addItems(Arrays.asList(models));
    }

    private void manageError(String error) {
        ((StaffLeaveActivity)getActivity()).hideActionBarProgress();
        ErrorUtils.showError(getActivity(), error);
    }

    @Override
    public void onClick(View view, int position) {
        final LeaveRecordModel model = adapter.getModels().get(position);
        String url = DIConstants.LEAVE_RECORD_URL + model.getCode();
        ((StaffLeaveActivity)getActivity()).showProgress();
        NetworkManager.getInstance().sendPrivateGetRequest(url, new CompositeCompletion() {
            @Override
            public void parseResponse(JsonElement jsonElement) {
                manageSuccessResponse(jsonElement, model);
            }
        }, new ErrorHandler() {
            @Override
            public void handleError(String error) {
                ((StaffLeaveActivity)getActivity()).hideProgress();
                ErrorUtils.showError(getActivity(), error);
            }
        });
    }

    public void manageSuccessResponse(JsonElement jsonElement, LeaveRecordModel model) {
        ((StaffLeaveActivity)getActivity()).hideProgress();
        StaffLeaveModel staffLeaveModel = new Gson().fromJson(jsonElement, StaffLeaveModel.class);
        NameCode nameCode = new NameCode(model.getClsStaff().strName, model.getClsStaff().code);
        String url = ((StaffLeaveActivity)getActivity()).baseUrl + "pending";
        LeavePendingApprovalActivity.start(getContext(), nameCode, staffLeaveModel, true, url);
        getActivity().finish();
    }

}
