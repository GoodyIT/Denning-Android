package it.denning.search.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.zakariya.stickyheaders.SectioningAdapter;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import it.denning.R;
import it.denning.general.DIConstants;
import it.denning.general.DIHelper;
import it.denning.model.SearchResultModel;

/**
 * Created by com on 12/4/2017.
 */

public class SearchAdapter extends SectioningAdapter {
    private final List<SearchResultModel> modelArrayList;
    private final Context mContext;
    private OnItemClickListener clickListener;
    OnFileFolderClickListener fileFolderClickListener;
    OnAccountsClickListener accountsClickListener;
    OnFileNoteClickListener fileNoteClickListener;
    OnPaymentRecordClickListener paymentRecordClickListener;
    OnTemplateClickListener templateClickListener;
    OnRelatedMatterClickListener matterClickListener;
    OnUploadClickListener uploadClickListener;

    public SearchAdapter(List<SearchResultModel> modelArrayList, Context mContext) {
        this.modelArrayList = modelArrayList;
        this.mContext = mContext;
    }

    public List<SearchResultModel> getModelArrayList() {
        return modelArrayList;
    }

    public void setClickListener(OnItemClickListener clickListener, OnFileFolderClickListener fileFolderClickListener, OnAccountsClickListener accountsClickListener, OnFileNoteClickListener fileNoteClickListener, OnPaymentRecordClickListener paymentRecordClickListener, OnTemplateClickListener templateClickListener, OnRelatedMatterClickListener matterClickListener, OnUploadClickListener uploadClickListener) {
        this.clickListener = clickListener;
        this.accountsClickListener = accountsClickListener;
        this.fileFolderClickListener = fileFolderClickListener;
        this.fileNoteClickListener = fileNoteClickListener;
        this.paymentRecordClickListener = paymentRecordClickListener;
        this.templateClickListener = templateClickListener;
        this.matterClickListener = matterClickListener;
        this.uploadClickListener = uploadClickListener;
    }

    public class ContactTypeViewHolder extends SectioningAdapter.ItemViewHolder{

        @BindView(R.id.search_cardview)
        CardView cardView;
        @BindView(R.id.search_title)
        TextView title;
        @BindView(R.id.search_description) TextView description;
        @BindView(R.id.search_openMatterBtn)
        Button openMatterBtn;
        @BindView(R.id.search_contactFolderBtn)
        Button contactFolderBtn;
        @BindView(R.id.search_uploadBtn)
        Button uploadBtn;

        public ContactTypeViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public class GeneralTypeViewHolder extends SectioningAdapter.ItemViewHolder{

        @BindView(R.id.search_cardview)
        CardView cardView;
        @BindView(R.id.search_title)
        TextView title;
        @BindView(R.id.search_description) TextView description;
        @BindView(R.id.search_openMatterBtn)
        Button openMatterBtn;

        public GeneralTypeViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public class MatterTypeViewHolder extends SectioningAdapter.ItemViewHolder {

        @BindView(R.id.search_matter_cardview) CardView cardView;
        @BindView(R.id.search_matter_title) TextView title;
        @BindView(R.id.search_matter_description) TextView description;
        @BindView(R.id.search_matter_accountsBtn)
        Button openAccountsBtn;
        @BindView(R.id.search_matter_uploadBtn)
        Button uploadBtn;
        @BindView(R.id.search_matter_openFolderBtn) Button openFolderBtn;
        @BindView(R.id.search_matter_filenoteBtn) Button fileNoteBtn;
        @BindView(R.id.search_matter_paymentRecordBtn) Button paymentRecordBtn;
        @BindView(R.id.search_matter_templateBtn) Button templateBtn;

        public MatterTypeViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }
    }

    public class DocumentTypeViewHolder extends SectioningAdapter.ItemViewHolder{

        @BindView(R.id.search_document_cardview) CardView cardView;
        @BindView(R.id.search_document_title) TextView title;
        @BindView(R.id.search_document_description) TextView description;
        @BindView(R.id.search_document_openMatterBtn)
        Button openMatterBtn;
        @BindView(R.id.search_document_openfolderBtn) Button openFolderBtn;

        public DocumentTypeViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }
    }

    public class HeaderViewHolder extends SectioningAdapter.HeaderViewHolder {
        @BindView(R.id.first_textview)
        TextView firstTitle;

        public HeaderViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    @Override
    public int getNumberOfSections() {
        return modelArrayList.size();
    }

    @Override
    public int getNumberOfItemsInSection(int sectionIndex) {
        return 1;
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
    public int getSectionItemUserType(int sectionIndex, int itemIndex)
    {
        return DIHelper.determinSearchType(modelArrayList.get(sectionIndex).form);
    }

    @Override
    public SectioningAdapter.ItemViewHolder onCreateItemViewHolder(ViewGroup parent, int itemUserType) {
        View view;
        SectioningAdapter.ItemViewHolder itemViewHolder = null;
        switch (itemUserType) {
            case DIConstants.CONTACT_TYPE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_search_contact, parent, false);
                itemViewHolder = new ContactTypeViewHolder(view);
                break;
            case DIConstants.BANK_TYPE:
            case DIConstants.GOVERNMENT_LAND_OFFICES:
            case DIConstants.GOVERNMENT_PTG_OFFICES:
            case DIConstants.LEGAL_FIRM:
            case DIConstants.PROPERTY_TYPE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_search_contact, parent, false);
                itemViewHolder =  new  ContactTypeViewHolder(view);
                break;

            case DIConstants.MATTER_TYPE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_search_matter, parent, false);
                itemViewHolder =  new MatterTypeViewHolder(view);
                break;

            case DIConstants.DOCUMENT_TYPE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_search_document, parent, false);
                itemViewHolder = new DocumentTypeViewHolder(view);
                break;

            default:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_search_result, parent, false);
                itemViewHolder = new GeneralTypeViewHolder(view);
                break;
        }
        return itemViewHolder;
    }

    @Override
    public HeaderViewHolder onCreateHeaderViewHolder(ViewGroup parent, int headerType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.cardview_search_section_header, parent, false);
        return new HeaderViewHolder(v);
    }

    @Override
    public void onBindItemViewHolder(SectioningAdapter.ItemViewHolder viewHolder, int sectionIndex, int itemIndex, int itemType) {
        SearchResultModel searchResultModel = this.modelArrayList.get(sectionIndex);
        switch (itemType) {
            case DIConstants.CONTACT_TYPE:
                displayContactSearchResult(viewHolder, searchResultModel, sectionIndex);
                break;
            case DIConstants.BANK_TYPE:
            case DIConstants.GOVERNMENT_LAND_OFFICES:
            case DIConstants.GOVERNMENT_PTG_OFFICES:
            case DIConstants.LEGAL_FIRM:
            case DIConstants.PROPERTY_TYPE:
                displayGeneralSearchResult(viewHolder, searchResultModel, sectionIndex);
                break;
            case DIConstants.DOCUMENT_TYPE:
                displayDocumentSearchResult(viewHolder, searchResultModel, sectionIndex);
                break;
            case DIConstants.MATTER_TYPE:
                displayMatterSearchResult(viewHolder, searchResultModel, sectionIndex);
                break;

            default:
                break;
        }
    }

    @Override
    public GhostHeaderViewHolder onCreateGhostHeaderViewHolder(ViewGroup parent) {
        final View ghostView = new View(parent.getContext());
        ghostView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        return new GhostHeaderViewHolder(ghostView);
    }

    private void displayGeneralSearchResult(SectioningAdapter.ItemViewHolder viewHolder, final SearchResultModel searchResultModel, final int sectionIndex) {
        ContactTypeViewHolder generalTypeViewHolder = (ContactTypeViewHolder) viewHolder;
        generalTypeViewHolder.title.setText(searchResultModel.title);
        generalTypeViewHolder.description.setText(getFormatedDesc(searchResultModel.json));

        generalTypeViewHolder.openMatterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                matterClickListener.onMatterClick(v, searchResultModel.key);
            }
        });

        generalTypeViewHolder.contactFolderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fileFolderClickListener.onFileFolderClick(v, searchResultModel.key, "Property Folder");
            }
        });

        generalTypeViewHolder.uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadClickListener.onUploadClick(v, searchResultModel.key, R.string.client_upload_title, DIConstants.PROPERTY_FILE_FOLDER_URL, searchResultModel.title);
            }
        });

        // implement button click
        generalTypeViewHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickListener.onClick(view, sectionIndex);
            }
        });
    }

    private String getFormatedDesc(String json) {
        JsonArray jsonArray = new Gson().fromJson(json, JsonArray.class);
        String str = "";
        for (JsonElement obj : jsonArray) {
            if (str != "") {
                str += "\n";
            }
            str += obj.getAsJsonObject().get("label").getAsString() + ": " + obj.getAsJsonObject().get("value").getAsString();
        }
        return str;
    }

    private void displayContactSearchResult(SectioningAdapter.ItemViewHolder viewHolder, final SearchResultModel searchResultModel, final int sectionIndex) {
        ContactTypeViewHolder generalTypeViewHolder = (ContactTypeViewHolder) viewHolder;
        generalTypeViewHolder.title.setText(searchResultModel.title);
        generalTypeViewHolder.description.setText(getFormatedDesc(searchResultModel.json));

        generalTypeViewHolder.openMatterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                matterClickListener.onMatterClick(v, searchResultModel.key);
            }
        });

        generalTypeViewHolder.contactFolderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fileFolderClickListener.onFileFolderClick(v, searchResultModel.key, "Contact Folder");
            }
        });

        generalTypeViewHolder.uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadClickListener.onUploadClick(v, searchResultModel.key, R.string.client_upload_title, DIConstants.MATTER_CLIENT_FILEFOLDER, "");
            }
        });

        // implement button click
        generalTypeViewHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickListener.onClick(view, sectionIndex);
            }
        });
    }

    private void displayDocumentSearchResult(SectioningAdapter.ItemViewHolder viewHolder, SearchResultModel searchResultModel, final int sectionIndex) {
        DocumentTypeViewHolder documentTypeViewHolder = (DocumentTypeViewHolder) viewHolder;
        documentTypeViewHolder.title.setText(searchResultModel.title);
        documentTypeViewHolder.description.setText(searchResultModel.description);

        // implement button click
        documentTypeViewHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickListener.onClick(view, sectionIndex);
            }
        });
    }

    private void displayMatterSearchResult(SectioningAdapter.ItemViewHolder viewHolder, final SearchResultModel searchResultModel, final int sectionIndex) {
        MatterTypeViewHolder matterTypeViewHolder = (MatterTypeViewHolder) viewHolder;
        matterTypeViewHolder.title.setText(searchResultModel.title);
        matterTypeViewHolder.description.setText(searchResultModel.description);
        matterTypeViewHolder.openFolderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fileFolderClickListener.onFileFolderClick(v, searchResultModel.key, "File Folder");
            }
        });

        matterTypeViewHolder.openAccountsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                accountsClickListener.onAccountsClick(v, searchResultModel.key);
            }
        });

        matterTypeViewHolder.fileNoteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fileNoteClickListener.onFileNoteClick(v, searchResultModel.key, searchResultModel.title);
            }
        });

        matterTypeViewHolder.paymentRecordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                paymentRecordClickListener.onPaymentRecordClick(v, searchResultModel.key);
            }
        });

        matterTypeViewHolder.templateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                templateClickListener.onTemplateClick(view, searchResultModel.key, searchResultModel.title);
            }
        });

        matterTypeViewHolder.uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadClickListener.onUploadClick(v, searchResultModel.title,  R.string.business_upload_title, DIConstants.MATTER_STAFF_FILEFOLDER, "");
            }
        });

        matterTypeViewHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickListener.onClick(view, sectionIndex);
            }
        });
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindHeaderViewHolder(SectioningAdapter.HeaderViewHolder viewHolder, int sectionIndex, int headerType) {
        HeaderViewHolder headerViewHolder = (HeaderViewHolder) viewHolder;
        SearchResultModel searchResultModel = this.modelArrayList.get(sectionIndex);
        Integer type = DIHelper.determinSearchType(searchResultModel.form);
        String headerName = "";
        switch (type) {
            case DIConstants.CONTACT_TYPE:
                headerName = "Contact";
                break;
            case DIConstants.BANK_TYPE:
                headerName = "Bank";
                break;
            case DIConstants.GOVERNMENT_LAND_OFFICES:
                headerName = "Government Land Offices";
                break;
            case DIConstants.GOVERNMENT_PTG_OFFICES:
                headerName = "Government PTG Offices";
                break;
            case DIConstants.LEGAL_FIRM:
                headerName = "Legal Firm";
                break;
            case DIConstants.PROPERTY_TYPE:
                headerName = "Property";
                break;
            case DIConstants.DOCUMENT_TYPE:
                headerName = "Document";
                break;
            case DIConstants.MATTER_TYPE:
                headerName = "Matter";
                break;

            default:
                headerName = "";
                break;
        }
        headerViewHolder.firstTitle.setText(headerName);
    }

    public void swapItems(List<SearchResultModel> newSearchResultList) {
        this.modelArrayList.addAll(newSearchResultList);
        notifyAllSectionsDataSetChanged();
    }

    public void clear() {
        this.modelArrayList.clear();
        notifyAllSectionsDataSetChanged();
    }
}
