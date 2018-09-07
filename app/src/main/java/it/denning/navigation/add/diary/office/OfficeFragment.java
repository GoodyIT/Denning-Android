package it.denning.navigation.add.diary.office;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.zakariya.stickyheaders.StickyHeaderLayoutManager;

import butterknife.ButterKnife;
import it.denning.App;
import it.denning.R;
import it.denning.model.EditCourtModel;
import it.denning.model.OfficeDiaryModel;
import it.denning.navigation.add.diary.BaseDiaryFragment;
import it.denning.navigation.add.diary.court.CourtDiaryAdapter;
import it.denning.navigation.add.diary.court.CourtFragment;

/**
 * Created by denningit on 2018-02-03.
 */

public class OfficeFragment extends BaseDiaryFragment {
    OfficeDiaryModel officeDiaryModel;

    private static OfficeFragment fragment = null;

    public static OfficeFragment newInstance(OfficeDiaryModel officeDiaryModel) {
        if (fragment == null) {
            fragment = new OfficeFragment();
        }

        Bundle bundle = new Bundle();
        bundle.putSerializable("model", officeDiaryModel);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        initFields();

        setupRecyclerView();
    }

    private void initFields() {
        officeDiaryModel = (OfficeDiaryModel) getArguments().getSerializable("model");;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recyclerview, container, false);
        return view;
    }

    private void setupRecyclerView() {
        adapter = new OfficeDiaryAdapter(getActivity(), officeDiaryModel, this);
        recyclerView.setLayoutManager(new StickyHeaderLayoutManager());
        recyclerView.addItemDecoration(new it.denning.general.DividerItemDecoration(ContextCompat.getDrawable(App.getInstance(), R.drawable.item_decorator)));

        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
    }
}
