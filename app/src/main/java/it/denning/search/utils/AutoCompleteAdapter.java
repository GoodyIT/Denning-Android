package it.denning.search.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import okhttp3.OkHttpClient;

/**
 * Created by denningit on 23/04/2017.
 */

public class AutoCompleteAdapter extends ArrayAdapter<String> implements Filterable {
    private final OkHttpClient client = new OkHttpClient();
    private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
    private Context mContext;
    private List<String> originalList;
    private List<String> suggestions = new ArrayList<>();
    private LayoutInflater mInflater;

    public AutoCompleteAdapter(Context context, int textViewResourceId, List<String> originalList){
        super(context, textViewResourceId);
        mInflater = LayoutInflater.from(context);
        this.mContext = context;
        this.originalList = originalList;
    }

    public void updateData(List<String> array) {
        originalList = array;
        notifyDataSetChanged();
    }

    @Override
    public int getCount(){
        return suggestions.size();
    }

    @Override
    public String getItem(int index){
        return suggestions.get(index);
    }

    @Override
    public Filter getFilter(){

        Filter myFilter = new Filter(){

            @Override
            protected FilterResults performFiltering(CharSequence constraint){
                FilterResults filterResults = new FilterResults();
                if(constraint != null) {
                    suggestions.clear();
                    // Check if the Original List and Constraint aren't null.
                    if (constraint == null || constraint.length() == 0) {
                        suggestions.addAll(originalList);
                    }
                    if (originalList != null && constraint != null) {
                        for (int i = 0; i < originalList.size(); i++) {
                            // Compare item in original list if it contains constraints.
                            if (originalList.get(i).toLowerCase().contains(constraint.toString().toLowerCase().trim())) {
                                // If TRUE add item in Suggestions.
                                suggestions.add(originalList.get(i));
                            }
                        }
                    }
                    // Now assign the values and count to the FilterResults object
                    filterResults.values = suggestions;
                    filterResults.count = suggestions.size();
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence contraint, FilterResults results) {
                if(results != null && results.count > 0) {
                    notifyDataSetChanged();
                }
                else {
                    notifyDataSetInvalidated();
                }
            }
        };

        return myFilter;

    }
}
