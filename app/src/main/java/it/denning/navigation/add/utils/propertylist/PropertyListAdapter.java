package it.denning.navigation.add.utils.propertylist;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import it.denning.R;
import it.denning.model.FullPropertyModel;
import it.denning.model.MatterCodeModel;
import it.denning.search.utils.OnItemClickListener;

/**
 * Created by denningit on 2018-02-01.
 */

public class PropertyListAdapter extends RecyclerView.Adapter<PropertyListAdapter.ViewHolder> {
    private static final String TAG = "PropertyListAdapter";

    private List<FullPropertyModel> mDataSet;
    private OnItemClickListener itemClickListener;

    // BEGIN_INCLUDE(recyclerViewSampleViewHolder)
    /**
     * Provide a reference to the type of views that you are using (custom ViewHolder)
     */
    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.title)
        TextView title;
        @BindView(R.id.project_name)
        TextView projectName;
        @BindView(R.id.address)
        TextView address;

        @BindView(R.id.parcel_no)
        TextView parcelNo;

        @BindView(R.id.condo_name)
        TextView condoName;

        public ViewHolder(View v) {
            super(v);
            // Define click listener for the ViewHolder's View.
            ButterKnife.bind(this, v);
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemClickListener.onClick(v, getAdapterPosition());
                }
            });
        }

    }
    // END_INCLUDE(recyclerViewSampleViewHolder)

    /**
     * Initialize the dataset of the Adapter.
     *
     * @param dataSet String[] containing the data to populate views to be used by RecyclerView.
     */
    public PropertyListAdapter(List<FullPropertyModel> dataSet) {
        mDataSet = dataSet;
    }

    public List<FullPropertyModel> getModels() {
        return mDataSet;
    }

    public void setItemClickListener(OnItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    // BEGIN_INCLUDE(recyclerViewOnCreateViewHolder)
    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view.
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.cardview_property, viewGroup, false);

        return new ViewHolder(v);
    }
    // END_INCLUDE(recyclerViewOnCreateViewHolder)

    // BEGIN_INCLUDE(recyclerViewOnBindViewHolder)
    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        Log.d(TAG, "Element " + position + " set.");

        // Get element from your dataset at this position and replace the contents of the view
        // with that element
        FullPropertyModel model = mDataSet.get(position);
        viewHolder.title.setText(model.fullTitle);
        viewHolder.projectName.setText(model.projectName);
        viewHolder.address.setText(model.getFullAddress());
        viewHolder.parcelNo.setText(model.getParcelType() + " " + model.getParcelValue());
        viewHolder.condoName.setText(model.spaCondoName);
    }
    // END_INCLUDE(recyclerViewOnBindViewHolder)

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataSet.size();
    }

    public void clear() {
        this.mDataSet.clear();
        notifyDataSetChanged();
    }

    public void swapItems(List<FullPropertyModel> newModels) {
        this.mDataSet.addAll(newModels);
        notifyDataSetChanged();
    }
}
