package it.denning.navigation.dashboard.section4.feestransfer.transferred;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.SearchView;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.quickblox.q_municate_db.utils.ErrorUtils;

import java.util.ArrayList;
import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import it.denning.App;
import it.denning.R;
import it.denning.general.EndlessRecyclerViewScrollListener;
import it.denning.model.FeesTransferModel;
import it.denning.navigation.dashboard.section4.feestransfer.FeesTransferActivity;
import it.denning.navigation.dashboard.section4.feestransferreddetail.FeesTransferDetailActivity;
import it.denning.network.CompositeCompletion;
import it.denning.network.ErrorHandler;
import it.denning.network.NetworkManager;
import it.denning.search.utils.OnItemClickListener;
import it.denning.utils.KeyboardUtils;

/**
 * Created by denningit on 2018-01-26.
 */

public class TransferredFragment extends Fragment implements OnItemClickListener{

    @BindView(R.id.recycler_list)
    protected RecyclerView recyclerView;
    @BindView(R.id.dashboard_search)
    SearchView searchView;

    private TransferredAdapter adapter;
    private LinearLayoutManager linearLayoutManager;
    private Integer page = 1;
    private String url, filter = "";

    public static TransferredFragment newInstance(String url) {
        TransferredFragment fragment = new TransferredFragment();
        Bundle bundle = new Bundle();
        bundle.putString("url", url);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        url = getArguments().getString("url");
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        setupRecyclerView();
        setupEndlessScroll();
        setupSearchView();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadTransferred();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recyclerview_search, container, false);
        return view;
    }

    private void setupRecyclerView() {
        adapter = new TransferredAdapter(new ArrayList<FeesTransferModel>(), this);
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
                loadTransferred();
            }
        };
        recyclerView.clearOnScrollListeners();
        recyclerView.addOnScrollListener(scrollListener);
    }

    void setupSearchView() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchQuery(query);
                KeyboardUtils.hideKeyboard(getActivity());
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchQuery(newText);
                return false;
            }
        });
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                KeyboardUtils.hideKeyboard(getActivity());
                searchQuery("");
                return false;
            }
        });
    }

    void searchQuery(String query) {
        filter = query;
        page = 1;
        adapter.clear();
        loadTransferred();
    }

    private void loadTransferred() {
        String _url = url + "batch?search=" + filter + "&page=" + page;
        ((FeesTransferActivity)getActivity()).showActionBarProgress();
        NetworkManager.getInstance().sendPrivateGetRequest(_url, new CompositeCompletion() {
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
        ((FeesTransferActivity)getActivity()).hideActionBarProgress();
        FeesTransferModel[] models = new Gson().fromJson(jsonElement, FeesTransferModel[].class);
        if (models.length > 0) {
            page++;
        }

        adapter.addItems(Arrays.asList(models));
    }

    private void manageError(String error) {
        ((FeesTransferActivity)getActivity()).hideActionBarProgress();
        ErrorUtils.showError(getActivity(), error);
    }

    @Override
    public void onClick(View view, int position) {
        FeesTransferModel feesTransferModel = adapter.getModels().get(position);
        FeesTransferDetailActivity.start(getContext(), feesTransferModel);
    }
}
