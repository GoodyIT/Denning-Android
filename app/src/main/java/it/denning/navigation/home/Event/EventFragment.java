package it.denning.navigation.home.Event;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import it.denning.R;
import it.denning.general.DIConstants;
import it.denning.general.DIHelper;
import it.denning.general.DividerItemDecoration;
import it.denning.general.RecyclerItemListener;
import it.denning.model.Event;
import it.denning.model.News;
import it.denning.navigation.home.news.NewsAdapter;

/**
 * Created by denningit on 18/04/2017.
 */

public class EventFragment extends Fragment {
    private RecyclerView recList;
    private List<Event> eventList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //returning our layout file
        //change R.layout.yourlayoutfilename for each of your fragments
        return inflater.inflate(R.layout.fragment_event, container, false);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //you can set the title for your toolbar here for different fragments different titles
        getActivity().setTitle("Event");

        parseEventListFromBundle();

        displayHeaderView();

        displayNews(view);
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void parseEventListFromBundle() {
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            String jsonString = (String)bundle.getSerializable(DIConstants.FRAGMENT_BUNDLE_KEY);
//            try {
////                eventList = Event.getEventArrayFromResponse(new JSONArray(jsonString));
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
        }
    }

    private void displayHeaderView() {
    }

    private void displayNews(View view) {
        recList = (RecyclerView) view.findViewById(R.id.event_cardview);
        recList.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recList.setLayoutManager(llm);
        recList.setItemAnimator(new DefaultItemAnimator());
        recList.addItemDecoration(new DividerItemDecoration(ContextCompat.getDrawable(getActivity().getApplicationContext(), R.drawable.item_decorator)));

        recList.addOnItemTouchListener(new RecyclerItemListener(getActivity().getApplicationContext(), recList,
                new RecyclerItemListener.RecyclerTouchListener() {
                    public void onClickItem(View v, int position) {

                    }

                    public void onLongClickItem(View v, int position) {
                        System.out.println("On Long Click Item interface");
                    }
                }));

        List<Event> newEventList = new ArrayList<>();
        for (int i = 0; i < eventList.size(); i++) {
            if (i == 0) continue;;
            newEventList.add(eventList.get(i));
        }
        EventAdapter ca = new EventAdapter(newEventList);
        recList.setAdapter(ca);
    }
}
