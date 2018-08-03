package it.denning.search.document;

import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Document;
import org.zakariya.stickyheaders.SectioningAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;
import it.denning.R;
import it.denning.general.DIConstants;
import it.denning.general.DIHelper;
import it.denning.model.DocumentModel;
import it.denning.model.FileModel;
import it.denning.search.utils.OnFileClickListener;

/**
 * Created by denningit on 28/04/2017.
 */

public class PersonalDocumentAdapter extends SectioningAdapter {
    DocumentModel mDocumentModel;
    OnFileClickListener itemClickListener;

    PersonalDocumentAdapter(DocumentModel documentModel) {
        this.mDocumentModel = documentModel;
    }

    public void setItemClickListener(OnFileClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public class HeaderViewHolder extends SectioningAdapter.HeaderViewHolder {
        @BindView(R.id.first_textview)
        TextView firstTitle;

        public HeaderViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public class FileTypeViewHolder extends ItemViewHolder  {
        @BindView(R.id.search_cardview)
        CardView cardView;
        @BindView(R.id.search_file_name)
        TextView fileName;
        @BindView(R.id.search_file_time) TextView fileTime;
        @BindView(R.id.search_file_image)
        ImageView fileImage;
        public FileTypeViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    @Override
    public int getNumberOfSections() {
        return mDocumentModel.folders.size();
    }

    @Override
    public int getNumberOfItemsInSection(int sectionIndex) {
        int count = mDocumentModel.getFolderSize(sectionIndex);
        return count;
    }

    @Override
    public boolean doesSectionHaveHeader(int sectionIndex) {
        return true;
    }

    @Override
    public boolean doesSectionHaveFooter(int sectionIndex) {
        return false;
    }

    @Override
    public ItemViewHolder onCreateItemViewHolder(ViewGroup parent, int itemUserType) {
        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.cardview_search_file, parent, false);
        return new FileTypeViewHolder(itemView);
    }

    @Override
    public HeaderViewHolder onCreateHeaderViewHolder(ViewGroup parent, int headerType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.cardview_search_section_header, parent, false);
        return new HeaderViewHolder(v);
    }

    private void displayFolders(FileTypeViewHolder holder, int sectionIndex, int itemIndex) {
        final FileModel fileModel = mDocumentModel.folders.get(sectionIndex).documents.get(itemIndex);
        holder.fileName.setText(fileModel.name);
        holder.fileTime.setText(fileModel.date);
        setImageOnFile(holder.fileImage, fileModel.ext);
        final String url = fileModel.URL;
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.onClick(v, url);
            }
        });
    }

    private void setImageOnFile(ImageView fileImage, String ext) {
        if (DIHelper.isDocFile(ext)) {
            fileImage.setImageResource(R.mipmap.ic_doc);
        } else if (DIHelper.isPDFFile(ext)) {
            fileImage.setImageResource(R.mipmap.ic_pdf);
        } else if (DIHelper.isImageFile(ext)) {
            fileImage.setImageResource(R.mipmap.ic_png);
        } else if (DIHelper.isWebFile(ext)) {
            fileImage.setImageResource(R.mipmap.ic_web);
        }
    }

    @Override
    public void onBindItemViewHolder(ItemViewHolder viewHolder, int sectionIndex, int itemIndex, int itemType) {
        displayFolders((FileTypeViewHolder)viewHolder, sectionIndex, itemIndex);
    }

    @Override
    public void onBindHeaderViewHolder(SectioningAdapter.HeaderViewHolder viewHolder, int sectionIndex, int headerType) {
        HeaderViewHolder headerViewHolder = (HeaderViewHolder) viewHolder;
        String headerName = "Files";

        headerViewHolder.firstTitle.setText(headerName);
    }

    public void replaceData(DocumentModel documentModel) {
        this.mDocumentModel = documentModel;
        notifyAllSectionsDataSetChanged();
    }
}
