package it.denning.search.filenote;

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
import it.denning.general.OnBottomReachedListener;
import it.denning.model.FileNote;
import it.denning.search.utils.OnItemClickListener;

/**
 * Created by denningit on 2017-12-23.
 */

public class FileNoteAdapter extends RecyclerView.Adapter<FileNoteAdapter.FileNoteViewHolder>{
    private final List<FileNote> modelList;
    private final OnItemClickListener clickListener;
    OnBottomReachedListener onBottomReachedListener;

    public static class FileNoteViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.name_textview)
        TextView name;

        @BindView(R.id.content_textview)
        TextView content;

        @BindView(R.id.date_textView)
        TextView date;

        @BindView(R.id.cardview)
        CardView cardView;

        public FileNoteViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public FileNoteAdapter(List<FileNote> modelList, OnItemClickListener clickListener) {
        this.modelList = modelList;
        this.clickListener = clickListener;
    }

    //    public void setOnBottomReachedListener(OnBottomReachedListener onBottomReachedListener){
    //
    //        this.onBottomReachedListener = onBottomReachedListener;
    //    }

    @Override
    public FileNoteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.cardview_file_note, parent, false);
        return new FileNoteViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final FileNoteViewHolder holder, int position) {
        final FileNote fileNote = modelList.get(position);
        holder.name.setText(fileNote.clsMeetBy.strInitials);
        holder.content.setText(fileNote.strNote);
        holder.date.setText(DIHelper.getOnlyDateFromDateTime(fileNote.dtDate));
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickListener.onClick(view, holder.getLayoutPosition());
            }
        });

//        if (position > 0 && position == modelList.size() - 1){
//
//            onBottomReachedListener.onBottomReached(position);
//        }
    }

    @Override
    public int getItemCount() {
        return modelList.size();
    }

    public List<FileNote> getModelList() {
        return modelList;
    }

    public void clear() {
        modelList.clear();
        notifyDataSetChanged();
    }

    public void updateAdapter(List<FileNote> items) {
        modelList.addAll(items);
        notifyDataSetChanged();
    }
}
