package it.denning.search.document;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import org.zakariya.stickyheaders.SectioningAdapter;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import it.denning.R;
import it.denning.general.DIConstants;
import it.denning.general.DIHelper;
import it.denning.model.DocumentModel;
import it.denning.model.FileModel;
import it.denning.search.contact.SearchContactAdapter;
import it.denning.search.utils.OnFileClickListener;
import it.denning.search.utils.OnItemClickListener;

/**
 * Created by denningit on 28/04/2017.
 */

public class DocumentAdapter extends SectioningAdapter {
    DocumentModel mDocumentModel;
    OnFileClickListener itemClickListener;

    DocumentAdapter(DocumentModel documentModel) {
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

    public class HeaderTypeViewHolder extends SectioningAdapter.ItemViewHolder  {
        @BindView(R.id.search_header_name)
        TextView headerName;
        @BindView(R.id.search_header_IDNo) TextView headerIDNo;

        public HeaderTypeViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public class FileTypeViewHolder extends SectioningAdapter.ItemViewHolder  {
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
        return 2 + mDocumentModel.folders.size();
    }

    @Override
    public int getNumberOfItemsInSection(int sectionIndex) {
        int count;
        if (sectionIndex == 0) {
            count = 1;
        } else if (sectionIndex == 1) {
            count = mDocumentModel.getDocumentsSize();
        } else {
            count = mDocumentModel.getFolderSize(sectionIndex-2);
        }

        return count;
    }

    @Override
    public boolean doesSectionHaveHeader(int sectionIndex) {
        if (sectionIndex == 0) {
            return false;
        }
        return true;
    }

    @Override
    public boolean doesSectionHaveFooter(int sectionIndex) {
        return false;
    }

    @Override
    public int getSectionItemUserType(int sectionIndex, int itemIndex)
    {
        int type = 0;
        switch (sectionIndex) {
            case 0:
                type = DIConstants.HEADER_TYPE;
                break;
            default:
                type = DIConstants.FILE_TYPE;
                break;
        }
        return type;
    }

    @Override
    public SectioningAdapter.ItemViewHolder onCreateItemViewHolder(ViewGroup parent, int itemUserType) {
        SectioningAdapter.ItemViewHolder itemViewHolder = null;
        switch (itemUserType) {
            case DIConstants.HEADER_TYPE:
                View itemView = LayoutInflater.
                        from(parent.getContext()).
                        inflate(R.layout.cardview_search_header, parent, false);

                return new HeaderTypeViewHolder(itemView);
            case DIConstants.FILE_TYPE:
                itemView = LayoutInflater.
                        from(parent.getContext()).
                        inflate(R.layout.cardview_search_file, parent, false);
                return new FileTypeViewHolder(itemView);
        }
        return null;
    }

    @Override
    public HeaderViewHolder onCreateHeaderViewHolder(ViewGroup parent, int headerType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.cardview_search_section_header, parent, false);
        return new HeaderViewHolder(v);
    }

    private void displayHeader(HeaderTypeViewHolder holder) {
        String[] nameAndNo = DIHelper.separateNameIntoTwo(mDocumentModel.name);
        holder.headerName.setText(nameAndNo[0]);
        holder.headerIDNo.setText(nameAndNo[1]);
    }

    private void displayFiles(FileTypeViewHolder holder, int itemIndex) {
        final FileModel fileModel = mDocumentModel.documents.get(itemIndex);
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

    private void displayFolders(FileTypeViewHolder holder, int sectionIndex, int itemIndex) {
        final FileModel fileModel = mDocumentModel.folders.get(sectionIndex-2).documents.get(itemIndex);
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
    public void onBindItemViewHolder(SectioningAdapter.ItemViewHolder viewHolder, int sectionIndex, int itemIndex, int itemType) {
        if (sectionIndex == 0) {
            displayHeader((HeaderTypeViewHolder)viewHolder);
        } else if(sectionIndex == 1) {
            displayFiles((FileTypeViewHolder)viewHolder, itemIndex);
        } else {
            displayFolders((FileTypeViewHolder)viewHolder, sectionIndex, itemIndex);
        }
    }

    @Override
    public void onBindHeaderViewHolder(SectioningAdapter.HeaderViewHolder viewHolder, int sectionIndex, int headerType) {
        HeaderViewHolder headerViewHolder = (HeaderViewHolder) viewHolder;
        String headerName = "";
        if (sectionIndex == 1) {
            headerName = "Files";
        } else if (sectionIndex >= 2) {
            headerName = mDocumentModel.folders.get(sectionIndex-2).getName();
        }

        headerViewHolder.firstTitle.setText(headerName);
    }

    @Override
    public GhostHeaderViewHolder onCreateGhostHeaderViewHolder(ViewGroup parent) {
        final View ghostView = new View(parent.getContext());
        ghostView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        return new GhostHeaderViewHolder(ghostView);
    }

    public void replaceData(DocumentModel documentModel) {
        this.mDocumentModel = documentModel;
        notifyAllSectionsDataSetChanged();
    }
}
