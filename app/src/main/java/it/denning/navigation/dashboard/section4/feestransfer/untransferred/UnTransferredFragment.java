package it.denning.navigation.dashboard.section4.feestransfer.untransferred;

import android.os.Bundle;
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
import butterknife.BindView;
import butterknife.ButterKnife;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.quickblox.q_municate_db.utils.ErrorUtils;
import it.denning.App;
import it.denning.R;
import it.denning.general.EndlessRecyclerViewScrollListener;
import it.denning.model.FeesUnTransferModel;
import it.denning.navigation.dashboard.section4.feestransfer.FeesTransferActivity;
import it.denning.network.CompositeCompletion;
import it.denning.network.ErrorHandler;
import it.denning.network.NetworkManager;
import it.denning.utils.KeyboardUtils;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by denningit on 2018-01-26.
 */

public class UnTransferredFragment extends Fragment {

    @BindView(R.id.recycler_list)
    protected RecyclerView recyclerView;
    @BindView(R.id.dashboard_search)
    SearchView searchView;

    private UnTransferredAdapter adapter;
    private LinearLayoutManager linearLayoutManager;
    private Integer page = 1;
    private String url, filter = "";

    public static UnTransferredFragment newInstance(String url) {
        UnTransferredFragment fragment = new UnTransferredFragment();
        Bundle bundle = new Bundle();
        bundle.putString("url", url);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadUnTransferred();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        url = getArguments().getString("url");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recyclerview_search, container, false);
        ButterKnife.bind(this, view);

        setupRecyclerView();
        setupEndlessScroll();
        setupSearchView();
        return view;
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
             //   searchQuery(newText);
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
        loadUnTransferred();
    }

    private void setupRecyclerView() {
        adapter = new UnTransferredAdapter(new ArrayList<FeesUnTransferModel>());
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
                loadUnTransferred();
            }
        };
        recyclerView.clearOnScrollListeners();
        recyclerView.addOnScrollListener(scrollListener);
    }

    private void loadUnTransferred() {
        String _url = url + "new?search=" + filter + "&page=" + page;
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
        FeesUnTransferModel[] models = new Gson().fromJson(jsonElement, FeesUnTransferModel[].class);
        if (models.length > 0) {
            page++;
        }

        adapter.addItems(Arrays.asList(models));
    }

    private void manageError(String error) {
        ((FeesTransferActivity)getActivity()).hideActionBarProgress();
        ErrorUtils.showError(getActivity(), error);
    }
}
