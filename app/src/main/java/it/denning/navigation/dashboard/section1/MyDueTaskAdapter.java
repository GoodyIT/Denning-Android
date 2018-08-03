package it.denning.navigation.dashboard.section1;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import it.denning.R;
import it.denning.general.DIHelper;
import it.denning.model.TaskCheckModel;
import it.denning.search.utils.OnItemClickListener;

/**
 * Created by hothongmee on 09/09/2017.
 */

public class MyDueTaskAdapter extends RecyclerView.Adapter {
    private final List<TaskCheckModel> taskCheckModelList;
    private final Context mContext;
    private final OnItemClickListener clickListener;

    public MyDueTaskAdapter(List<TaskCheckModel> taskCheckModelList, Context mContext, OnItemClickListener clickListener) {
        this.taskCheckModelList = taskCheckModelList;
        this.mContext = mContext;
        this.clickListener = clickListener;
    }

    public class TaskViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        @BindView(R.id.clerk_value)
        TextView clerkValue;
        @BindView(R.id.fileno_value)
        TextView fileNoValue;
        @BindView(R.id.start_value)
        TextView startValue;
        @BindView(R.id.task_value)
        TextView taskVaule;
        @BindView(R.id.filename_value)
        TextView fileNameValue;
        @BindView(R.id.due_value)
        TextView dueValue;
        @BindView(R.id.dashboard_cardview)
        CardView cardView;

        public TaskViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            cardView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            clickListener.onClick(itemView, getLayoutPosition());
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_myduetask, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        TaskCheckModel model = taskCheckModelList.get(position);
        TaskViewHolder viewHolder = (TaskViewHolder) holder;
        viewHolder.fileNameValue.setText(model.fileName);
        viewHolder.clerkValue.setText(model.clerkName);
        viewHolder.fileNoValue.setText(model.fileNo);
        viewHolder.taskVaule.setText(model.taskName);
        viewHolder.startValue.setText(DIHelper.getOnlyDateFromDateTime(model.startDate));
        viewHolder.dueValue.setText(DIHelper.getOnlyDateFromDateTime(model.endDate));
    }

    public void swapItems(List<TaskCheckModel> newSearchResultList) {
//        final DueTaskDiffCallback diffCallback = new DueTaskDiffCallback(taskCheckModelList, newSearchResultList);
//        final DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffCallback);

        this.taskCheckModelList.clear();
        this.taskCheckModelList.addAll(newSearchResultList);

//        diffResult.dispatchUpdatesTo(this); // calls adapter's notify methods after diff is computed
        notifyDataSetChanged();
    }

    public void clear() {
        this.taskCheckModelList.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return taskCheckModelList.size();
    }
}
