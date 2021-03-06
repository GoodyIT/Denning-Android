package it.denning.navigation.add.matter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.chauthai.swipereveallayout.SwipeRevealLayout;
import com.chauthai.swipereveallayout.ViewBinderHelper;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import it.denning.model.*;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

import org.zakariya.stickyheaders.SectioningAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import it.denning.R;
import it.denning.general.DIAlert;
import it.denning.general.DIConstants;
import it.denning.general.DIHelper;
import it.denning.general.MyCallbackInterface;
import it.denning.navigation.add.utils.basesectionadapter.BaseSectionAdapter;
import it.denning.network.NetworkManager;
import it.denning.search.utils.OnSectionItemClickListener;

/**
 * Created by denningit on 2018-01-19.
 */

public class AddMatterAdapter extends BaseSectionAdapter {
    private boolean isUpdateMode = false;
    MatterModel matter;

    // Sections
    public int MATTER_INFO = 0;
    public int REMARKS = 1;
    public int CASE_DETAILS = 2;
    public int PROPERTIES = 3;
    public int PARTYGROUP = 4;
    public int SOLICITORS = 5;
    public int BANKS = 6;
    public int IMPORTANT_RM = 7;
    public int IMPORTANT_DATES = 8;
    public int TEXT_GROUP = 9;

    // Matter information
    public int FILE_NO = 0;
    public int REF2 =1;
    public int PRIMARY_CLIENT = 2;
    public int FILE_STATUS = 3;
    public int PARTNER_IN_CHARGE = 4;
    public int LA_IN_CHARGE = 5;
    public int CLERK_IN_CHARGE = 6;
    public int TEAM = 7;
    public int MATTER = 8;
    public int BRANCH = 9;
    public int FILE_LOCATION = 10;
    public int POCKET_LOCATION = 11;
    public int STORAGE_LOCATION = 12;
    public int UPDATE_BUTTON = 13;

    // Remarks
    public int NOTES = 0;

    // Case details
    public int CASE_TYPE = 0;
    public int TYPE_NO = 1;
    public int COURT = 2;
    public int PLACE = 3;
    public int JUDGE = 4;
    public int SAR = 5;

    public AddMatterAdapter(Context context, OnSectionItemClickListener itemClickListener, boolean isUpdateMode) {
        super(context, itemClickListener);
        this.isUpdateMode = isUpdateMode;
        matter = new MatterModel();
        updateTitles();
        buildModel();
    }

    private void buildModel() {
        AddSectionItemModel sectionItemModel = new AddSectionItemModel();
        LabelValueDetail labelValueDetail = new LabelValueDetail("File No (Auto assigned)", "", DIConstants.GENERAL_TYPE);
        labelValueDetail.hasDetail = false;
        sectionItemModel.items.add(labelValueDetail);

        labelValueDetail = new LabelValueDetail("Ref 2 (if any)", "", DIConstants.INPUT_TYPE);
        sectionItemModel.items.add(labelValueDetail);

        labelValueDetail = new LabelValueDetail("Primary Client", "", DIConstants.GENERAL_TYPE);
        sectionItemModel.items.add(labelValueDetail);

        labelValueDetail = new LabelValueDetail("File Status", "Active", "1", true, true, DIConstants.GENERAL_TYPE);
        sectionItemModel.items.add(labelValueDetail);

        labelValueDetail = new LabelValueDetail("Partner-in-Charge", "", DIConstants.GENERAL_TYPE);
        sectionItemModel.items.add(labelValueDetail);

        labelValueDetail = new LabelValueDetail("LA-in-Charge", "", DIConstants.GENERAL_TYPE);
        sectionItemModel.items.add(labelValueDetail);

        labelValueDetail = new LabelValueDetail("Clerk-in-Charge", "", DIConstants.GENERAL_TYPE);
        sectionItemModel.items.add(labelValueDetail);

        labelValueDetail = new LabelValueDetail("Team", "", DIConstants.GENERAL_TYPE);
        sectionItemModel.items.add(labelValueDetail);

        labelValueDetail = new LabelValueDetail("Matter", "", DIConstants.GENERAL_TYPE);
        sectionItemModel.items.add(labelValueDetail);

        labelValueDetail = new LabelValueDetail("Branch", "", DIConstants.GENERAL_TYPE);
        sectionItemModel.items.add(labelValueDetail);

        labelValueDetail = new LabelValueDetail("File Location", "", DIConstants.INPUT_TYPE);
        sectionItemModel.items.add(labelValueDetail);

        labelValueDetail = new LabelValueDetail("Pocket Location", "", DIConstants.INPUT_TYPE);
        sectionItemModel.items.add(labelValueDetail);

        labelValueDetail = new LabelValueDetail("Storage Location", "", DIConstants.INPUT_TYPE);
        sectionItemModel.items.add(labelValueDetail);

        labelValueDetail = new LabelValueDetail("Save", "", DIConstants.ONE_BUTTON_TYPE);
        sectionItemModel.items.add(labelValueDetail);

        model.items.add(sectionItemModel);

        notifyAllSectionsDataSetChanged();
    }

    private void updateTitles() {
        titles = new ArrayList<>();
        if (isUpdateMode) {
            String[] __titles = {"Matter Information", "Remarks", "Case Details", "Properties", "Parties", "Solicitors", "Banks", "Important RM", "Important Dates", "Text Group"};
            titles.addAll(Arrays.asList(__titles));
        } else {
            String[] __titles = {"Matter Information"};
            titles.addAll(Arrays.asList(__titles));
        }
    }

    private void updateMatterInfoSection(MatterCodeModel matter) {
        model.items.get(MATTER_INFO).items.get(PARTNER_IN_CHARGE).label = matter.strLabelPartner;
        model.items.get(MATTER_INFO).items.get(LA_IN_CHARGE).label = matter.strLabelLegalAssistant;
        model.items.get(MATTER_INFO).items.get(CLERK_IN_CHARGE).label = matter.strLabelClerk;
        model.items.get(MATTER_INFO).items.get(TEAM).label = matter.strLabelTeam;
    }

    public MatterModel getModel() {
        return matter;
    }

    public void adjustModelForUpdate(MatterModel matter) {
        this.matter = matter;
        isUpdateMode = true;
        updateTitles();

        updateMatterInfoSection(matter.matter);

        updateData(matter.systemNo, MATTER_INFO, FILE_NO);
        updateData("Update", MATTER_INFO, UPDATE_BUTTON);
        updateData(matter.getManualNo(), MATTER_INFO, REF2);
        updateCodeDescDataWithoutRefresh(new CodeDescription(matter.getPrimaryClientCode(), matter.getPrimaryClientName()), MATTER_INFO, PRIMARY_CLIENT);
        updateCodeDescDataWithoutRefresh(matter.getFileStatus(), MATTER_INFO, FILE_STATUS);
        updateCodeDescDataWithoutRefresh(new CodeDescription(matter.getPartnerCode(), matter.getPartnerName()), MATTER_INFO, PARTNER_IN_CHARGE);
        updateCodeDescDataWithoutRefresh(new CodeDescription(matter.getLegalAssistantCode(), matter.getLegalAssistantName()), MATTER_INFO, LA_IN_CHARGE);
        updateCodeDescDataWithoutRefresh(new CodeDescription(matter.getClerkCode(), matter.getClerkName()), MATTER_INFO, CLERK_IN_CHARGE);
        updateData(matter.team, MATTER_INFO, TEAM);
        updateCodeDescDataWithoutRefresh(new CodeDescription(matter.getMatterFormCode(), matter.getMatterDesc()), MATTER_INFO, MATTER);
        updateCodeDescDataWithoutRefresh(new CodeDescription(matter.getBranchCode(), matter.getBranchName()), MATTER_INFO, BRANCH);
        updateData(matter.locationBox, MATTER_INFO, FILE_LOCATION);
        updateData(matter.locationPocket, MATTER_INFO, POCKET_LOCATION);
        updateData(matter.locationPhysical, MATTER_INFO, STORAGE_LOCATION);
        updateLabel("Update", MATTER_INFO, UPDATE_BUTTON);

        // Notes
        AddSectionItemModel sectionItemModel = new AddSectionItemModel();
        sectionItemModel.items.add(new LabelValueDetail("Notes", matter.remarks, DIConstants.INPUT_TYPE));
        model.items.add(sectionItemModel);

        // Case Details
        sectionItemModel = new AddSectionItemModel();
        sectionItemModel.items.add(new LabelValueDetail("Case Type", matter.getCourtCaseNo(), DIConstants.GENERAL_TYPE));
        sectionItemModel.items.add(new LabelValueDetail("Case", matter.getCourtTypeNo(), DIConstants.INPUT_TYPE));
        sectionItemModel.items.add(new LabelValueDetail("Court", matter.getCourtPlace(), DIConstants.GENERAL_TYPE));
        sectionItemModel.items.add(new LabelValueDetail("Place", matter.getCourt(), DIConstants.GENERAL_TYPE));
        sectionItemModel.items.add(new LabelValueDetail("Judge", matter.getCourtJudge(), DIConstants.GENERAL_TYPE));
        sectionItemModel.items.add(new LabelValueDetail("SAR", matter.getCourtSAR(), DIConstants.GENERAL_TYPE));

        model.items.add(sectionItemModel);

        // Properties
        sectionItemModel = new AddSectionItemModel();
        LabelValueDetail labelValueDetail = new LabelValueDetail("Add Property", "", DIConstants.ONE_ROW_ADD_TYPE);
        labelValueDetail.isOneRowAdd = true;
        sectionItemModel.items.add(labelValueDetail);
        int idx = 1;
        for (MatterProperty property : matter.propertyGroup) {
            labelValueDetail = new LabelValueDetail("Property " + idx, property.fullTitle, DIConstants.THREE_TYPE);
            labelValueDetail.otherValue1 = property.address;
            labelValueDetail.code = property.code;
            labelValueDetail.is3Rows = true;
            sectionItemModel.items.add(labelValueDetail);
            idx++;
        }
        model.items.add(sectionItemModel);

        // Parties Group
        sectionItemModel = new AddSectionItemModel();
        for (PartyGroup partyGroup : matter.partyGroup) {
            labelValueDetail = new LabelValueDetail(partyGroup.partyName, "", DIConstants.ONE_ROW_ADD_TYPE);
            labelValueDetail.isOneRowAdd = true;
            labelValueDetail.childCnt = partyGroup.party.size();
            sectionItemModel.items.add(labelValueDetail);
            for (StaffModel staffModel : partyGroup.party) {
                labelValueDetail = new LabelValueDetail("", staffModel.name, DIConstants.ONE_TYPE);
                labelValueDetail.code = staffModel.code;
                labelValueDetail.isOneRowDetail = true;
                sectionItemModel.items.add(labelValueDetail);
            }
        }
        model.items.add(sectionItemModel);

        // Solicitors
        sectionItemModel = new AddSectionItemModel();
        for (SolicitorGroup solicitorGroup : matter.solicitorsGroup) {
            labelValueDetail = new LabelValueDetail(solicitorGroup.getGroupName(), solicitorGroup.getName().toUpperCase(), DIConstants.GENERAL_ADD_TYPE);
            labelValueDetail.code = solicitorGroup.getCode();
            labelValueDetail.otherValue1 = solicitorGroup.reference;
            labelValueDetail.isAdd = true;
            sectionItemModel.items.add(labelValueDetail);
        }
        model.items.add(sectionItemModel);

        // Bank
        sectionItemModel = new AddSectionItemModel();
        for (BankGroup bankGroup : matter.bankGroup) {
            labelValueDetail = new LabelValueDetail(bankGroup.getGroupName(), bankGroup.getBankName(), DIConstants.GENERAL_ADD_TYPE);
            labelValueDetail.code = bankGroup.getBankCode();
            labelValueDetail.isAdd = true;
            sectionItemModel.items.add(labelValueDetail);
        }
        model.items.add(sectionItemModel);

        // Important RM
        sectionItemModel = new AddSectionItemModel();
        for (FormulaModel formulaModel : matter.RMGroup) {
            labelValueDetail = new LabelValueDetail(formulaModel.label, formulaModel.value, DIConstants.INPUT_TYPE);
            labelValueDetail.formula = formulaModel.formula;
            labelValueDetail.fieldName = formulaModel.fieldName;
            labelValueDetail.inputType = InputType.TYPE_CLASS_NUMBER;
            labelValueDetail.viewType = formulaModel.formula.isEmpty() ? DIConstants.INPUT_TYPE : DIConstants.GENERAL_TYPE;
            labelValueDetail.hasDetail = false;
            sectionItemModel.items.add(labelValueDetail);
        }
        model.items.add(sectionItemModel);

        // Important Date
        sectionItemModel = new AddSectionItemModel();
        for (FormulaModel formulaModel : matter.dateGroup) {
            labelValueDetail = new LabelValueDetail(formulaModel.label, DIHelper.getOnlyDateFromDateTime(formulaModel.value), DIConstants.SELECTION_TYPE);
            sectionItemModel.items.add(labelValueDetail);
        }
        model.items.add(sectionItemModel);

        // Text Group
        if (matter.textGroup.size() > 0) {
            sectionItemModel = new AddSectionItemModel();
            for (LabelValue formulaModel : matter.textGroup) {
                labelValueDetail = new LabelValueDetail(formulaModel.label, formulaModel.value, DIConstants.INPUT_TYPE);
                labelValueDetail.isInput = false;
                sectionItemModel.items.add(labelValueDetail);
            }
            model.items.add(sectionItemModel);
        }

        notifyAllSectionsDataSetChanged();
    }

    public String getFileNo() {
        return model.items.get(MATTER_INFO).items.get(FILE_NO).value;
    }

    public class OneRowAddViewHolder extends SectioningAdapter.ItemViewHolder {
        @BindView(R.id.textview)
        TextView textView;
        @BindView(R.id.right_addbtn)
        ImageButton addBtn;
        @BindView(R.id.add_cardview)
        CardView cardView;

        public OneRowAddViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            textView.setText("Add");
        }
    }

    public class GeneralAddViewHolder extends SectioningAdapter.ItemViewHolder {
        @BindView(R.id.textview1)
        TextView textView1;
        @BindView(R.id.textview2)
        TextView textView2;
        @BindView(R.id.textview3)
        TextView textView3;
        @BindView(R.id.right_addbtn)
        ImageButton addBtn;
        @BindView(R.id.add_cardview)
        CardView cardView;
        @BindView(R.id.one_label_swipelayout)
        SwipeRevealLayout swipeLayout;
        @BindView(R.id.delete_btn)
        ImageView deleteBtn;

        public GeneralAddViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public class OneLabelTypeViewHolder extends SectioningAdapter.ItemViewHolder {
        @BindView(R.id.search_cardview)
        CardView cardView;
        @BindView(R.id.search_general_description) TextView description;
        @BindView(R.id.search_last_rightBtn)
        ImageButton rightButton;
        @BindView(R.id.one_label_swipelayout)
        SwipeRevealLayout swipeLayout;
        @BindView(R.id.delete_btn)
        ImageView deleteBtn;

        public OneLabelTypeViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public class ThreeTypeViewHolder extends SectioningAdapter.ItemViewHolder{
        @BindView(R.id.search_cardview)
        CardView cardView;
        @BindView(R.id.search_first) TextView textView1;
        @BindView(R.id.search_second) TextView textView2;
        @BindView(R.id.search_third) TextView textView3;
        @BindView(R.id.search_last_rightBtn)
        ImageView rightButton;
        @BindView(R.id.one_label_swipelayout)
        SwipeRevealLayout swipeLayout;
        @BindView(R.id.delete_btn)
        ImageView deleteBtn;

        public ThreeTypeViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    @Override
    public ItemViewHolder onCreateItemViewHolder(ViewGroup parent, int itemUserType) {
        ItemViewHolder itemViewHolder = null;
        View itemView = null;
        if (itemUserType == DIConstants.ONE_ROW_ADD_TYPE) {
            itemView = LayoutInflater.
                    from(parent.getContext()).
                    inflate(R.layout.cardview_one_row_add, parent, false);
            itemViewHolder = new OneRowAddViewHolder(itemView);
        } else if (itemUserType == DIConstants.GENERAL_ADD_TYPE) {
            itemView = LayoutInflater.
                    from(parent.getContext()).
                    inflate(R.layout.cardview_general_add, parent, false);
            itemViewHolder = new GeneralAddViewHolder(itemView);
        } else if (itemUserType == DIConstants.THREE_TYPE) {
            itemView = LayoutInflater.
                    from(parent.getContext()).
                    inflate(R.layout.cardview_search_threefield, parent, false);
            itemViewHolder = new ThreeTypeViewHolder(itemView);
        } else if (itemUserType == DIConstants.ONE_TYPE) {
            itemView = LayoutInflater.
                    from(parent.getContext()).
                    inflate(R.layout.cardview_search_one_label, parent, false);

            itemViewHolder =  new OneLabelTypeViewHolder(itemView);
        } else {
           return super.onCreateItemViewHolder(parent, itemUserType);
        }

        return itemViewHolder;
    }

    @Override
    public void onBindItemViewHolder(ItemViewHolder viewHolder, int sectionIndex, int itemIndex, int itemType) {
        if (itemType == DIConstants.ONE_ROW_ADD_TYPE) {
            displayOneRowAdd((OneRowAddViewHolder)viewHolder, sectionIndex, itemIndex);
        } else if (itemType == DIConstants.GENERAL_ADD_TYPE){
            displayGeneralAdd((GeneralAddViewHolder) viewHolder, sectionIndex, itemIndex);
        } else if (itemType == DIConstants.THREE_TYPE){
            displayThreeType((ThreeTypeViewHolder) viewHolder, sectionIndex, itemIndex);
        } else if (itemType == DIConstants.ONE_TYPE) {
            displayOneType((OneLabelTypeViewHolder)viewHolder, sectionIndex, itemIndex);
        } else {
            super.onBindItemViewHolder(viewHolder, sectionIndex, itemIndex, itemType);
        }
    }

    private void displayOneRowAdd(final OneRowAddViewHolder viewHolder, final int sectionIndex, final int itemIndex) {
        String clickName = "Add Property";
        if (sectionIndex == PARTYGROUP) {
            clickName = "Add Party";
        }
        final String _clickName = clickName;
        LabelValueDetail labelValueDetail = model.items.get(sectionIndex).items.get(itemIndex);
        viewHolder.textView.setText(labelValueDetail.label);
        viewHolder.addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.onClick(v, sectionIndex, itemIndex, _clickName);
            }
        });

        viewHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.onClick(v, sectionIndex, itemIndex, _clickName);
            }
        });
    }

    private void displayGeneralAdd(final GeneralAddViewHolder viewHolder, final int sectionIndex, final int itemIndex) {
        LabelValueDetail labelValueDetail = model.items.get(sectionIndex).items.get(itemIndex);
        viewHolder.textView1.setText(labelValueDetail.label);
        viewHolder.textView2.setText(labelValueDetail.value);
        viewHolder.textView3.setText(labelValueDetail.otherValue1);

        String clickName = "Solicitor";
        if (sectionIndex == BANKS) {
            clickName = "Bank";
        }
        final String _clickName = clickName;
        viewHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.onClick(v, sectionIndex, itemIndex, _clickName);
            }
        });
        viewHolder.addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.onClick(v, sectionIndex, itemIndex, _clickName);
            }
        });

        viewBinderHelper.bind(viewHolder.swipeLayout, String.valueOf(itemIndex));
        viewHolder.deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DIAlert.showConfirm(context, R.string.warning_title, R.string.dlg_confirm_delete, new MyCallbackInterface() {
                    @Override
                    public void nextFunction() {
                        deleteItem(sectionIndex, itemIndex);
                    }

                    @Override
                    public void nextFunction(JsonElement jsonElement) {

                    }
                });
                viewHolder.swipeLayout.close(true);
            }
        });
    }

    private void displayThreeType(final ThreeTypeViewHolder viewHolder, final int sectionIndex,final int itemIndex) {
        LabelValueDetail labelValueDetail = model.items.get(sectionIndex).items.get(itemIndex);
        viewHolder.textView1.setText(labelValueDetail.label);
        viewHolder.textView2.setText(labelValueDetail.value);
        viewHolder.textView3.setText(labelValueDetail.otherValue1);
        viewHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.onClick(v, sectionIndex, itemIndex, "Load Property");
            }
        });
        viewBinderHelper.bind(viewHolder.swipeLayout, String.valueOf(itemIndex));
        viewHolder.deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DIAlert.showConfirm(context, R.string.warning_title, R.string.dlg_confirm_delete, new MyCallbackInterface() {
                    @Override
                    public void nextFunction() {
                        removeItem(sectionIndex, itemIndex);
                    }

                    @Override
                    public void nextFunction(JsonElement jsonElement) {

                    }
                });
                viewHolder.swipeLayout.close(true);
            }
        });
    }

    private void displayOneType(final OneLabelTypeViewHolder viewHolder, final int sectionIndex, final int itemIndex) {
        LabelValueDetail labelValueDetail = model.items.get(sectionIndex).items.get(itemIndex);
        viewHolder.description.setText(labelValueDetail.value);


        viewBinderHelper.setOpenOnlyOne(true);

        viewHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.onClick(v, sectionIndex, itemIndex, "Load Party");
            }
        });
        viewBinderHelper.bind(viewHolder.swipeLayout, String.valueOf(itemIndex));
        viewHolder.deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DIAlert.showConfirm(context, R.string.warning_title, R.string.dlg_confirm_delete, new MyCallbackInterface() {
                    @Override
                    public void nextFunction() {
                        removeItem(sectionIndex, itemIndex);
                    }

                    @Override
                    public void nextFunction(JsonElement jsonElement) {

                    }
                });
                viewHolder.swipeLayout.close(true);
            }
        });
    }

    @Override
    public void updateDataFromInput(String input, int sectionIndex, int itemIndex) {
        input = input.trim();
        if (input.trim().length() == 0) {
            return;
        }

        String[] inputArray = input.split(" ");
        String newInput = "";
        for(int i = 0; i < inputArray.length; i++) {
            newInput += inputArray[i].substring(0, 1).toUpperCase() + inputArray[i].substring(1) + " ";
        }

        input = newInput;

        if (sectionIndex == IMPORTANT_RM) {
            input = DIHelper.addThousandsSeparator(input);
        }

        model.items.get(sectionIndex).items.get(itemIndex).value = input;

        if (sectionIndex == IMPORTANT_RM) {
            int position = 0;
            for (LabelValueDetail labelValueDetail : model.items.get(sectionIndex).items) {
                if (labelValueDetail.formula.trim().length() != 0) {
                    calculateFormula(labelValueDetail, sectionIndex, position);
                }
                position++;
            }
        }
    }

    private void calculateFormula(LabelValueDetail labelValueDetail, final int sectionIndex, final int position) {
        String formula = labelValueDetail.formula;
        Matcher m = Pattern.compile("\\[\\$\\d+\\]")
                .matcher(formula);
        while (m.find()) {
            String fieldName = m.group();
            fieldName = fieldName.substring(1, fieldName.length()-1);
            formula = replaceFormulaWithValue(formula, fieldName, sectionIndex, m.group());
        }
        Expression expression = new ExpressionBuilder(formula).build();
        String value = DIHelper.addThousandsSeparator(expression.evaluate()+"");
        model.items.get(sectionIndex).items.get(position).value = value;

        android.os.Handler handler = new android.os.Handler();
        handler.postDelayed(new Runnable() {
            public void run(){
                //change adapter contents
                notifySectionItemChanged(sectionIndex, position);
            }
        }, 300);
    }

    private String replaceFormulaWithValue(String formula, String fieldName, int sectionIndex, String matching) {
        String newFormula = "";

        for(LabelValueDetail labelValueDetail : model.items.get(sectionIndex).items) {
            if (labelValueDetail.fieldName.equals(fieldName)) {
                String value = labelValueDetail.value;
                value = value.isEmpty() ?  "0" : value.replace(",", "");
                newFormula = formula.replace(matching, value);
                break;
            }
        }

        return newFormula;
    }

    private int getPartyIndex(int itemIndex) {
        int cnt = 0;
       for (int i = 0; i <= itemIndex; i++) {
           if (model.items.get(PARTYGROUP).items.get(i).isOneRowAdd) {
               cnt += model.items.get(PARTYGROUP).items.get(i).childCnt + 1;
           }
       }
       return cnt;
    }

    public void updateParty(LabelValueDetail labelValueDetail, int sectionIndex, int itemIndex) {
        labelValueDetail.isOneRowDetail = true;
        int index = getPartyIndex(itemIndex);
        if (model.items.get(PARTYGROUP).items.get(itemIndex).isOneRowAdd) {
            model.items.get(sectionIndex).items.add(index, labelValueDetail);
            model.items.get(sectionIndex).items.get(itemIndex).childCnt++;
        } else {
            model.items.get(sectionIndex).items.remove(itemIndex);
            model.items.get(sectionIndex).items.get(itemIndex).childCnt--;
        }
        notifySectionDataSetChanged(sectionIndex);
    }

    public void updateProperty(LabelValueDetail labelValueDetail, int sectionIndex, int itemIndex) {
        labelValueDetail.is3Rows = true;
        labelValueDetail.viewType = DIConstants.THREE_TYPE;
        labelValueDetail.label = "Property " + (model.items.get(sectionIndex).items.size());
        if (itemIndex == 0) {
            model.items.get(sectionIndex).items.add(labelValueDetail);
        } else {
            model.items.get(sectionIndex).items.remove(itemIndex);
        }
        notifySectionItemChanged(sectionIndex, itemIndex);
    }

    public void updateBankOrSolicitor(LabelValueDetail labelValueDetail, int sectionIndex, int itemIndex) {
        LabelValueDetail origin = model.items.get(sectionIndex).items.get(itemIndex);
        origin.code = labelValueDetail.code;
        origin.value = labelValueDetail.value;
        model.items.get(sectionIndex).items.set(itemIndex, origin);
        notifySectionItemChanged(sectionIndex, itemIndex);
    }

    protected boolean isValidProceed() {
        clearFocus();

        if (getCode(MATTER_INFO, PRIMARY_CLIENT).trim().length() == 0) {
            DIAlert.showSimpleAlert(context, R.string.alert_primary_client);
            return false;
        }

        if (getCode(MATTER_INFO, PARTNER_IN_CHARGE).trim().length() == 0) {
            DIAlert.showSimpleAlert(context, R.string.alert_partner);
            return false;
        }

        if (getCode(MATTER_INFO, CLERK_IN_CHARGE).trim().length() == 0) {
            DIAlert.showSimpleAlert(context, R.string.alert_clerk);
            return false;
        }

        if (getCode(MATTER_INFO, MATTER).trim().length() == 0) {
            DIAlert.showSimpleAlert(context, R.string.alert_matter);
            return false;
        }
        return true;
    }

    public JsonObject _buildSaveParams() {
        clearFocus();

        JsonObject params = new JsonObject();

        List<LabelValueDetail> matterInfo = model.items.get(MATTER_INFO).items;

        if (!matterInfo.get(BRANCH).getCode().equals(matter.getBranchCode())) {
            JsonObject branch = new JsonObject();
            branch.addProperty("code", matterInfo.get(BRANCH).getCode());
            params.add("branch", branch);
        }

        if (!matterInfo.get(REF2).value.isEmpty() && !matterInfo.get(REF2).value.equals(matter.getManualNo())) {
            params.addProperty("manualNo", matterInfo.get(REF2).value);
        }

        if (!matterInfo.get(PRIMARY_CLIENT).getCode().isEmpty() && !matterInfo.get(PRIMARY_CLIENT).getCode().equals(matter.getPrimaryClientCode())) {
            JsonObject primary = new JsonObject();
            primary.addProperty("code", matterInfo.get(PRIMARY_CLIENT).getCode());
            params.add("primaryClient", primary);
        }

        if (!matterInfo.get(MATTER).getCode().isEmpty() && !matterInfo.get(MATTER).getCode().equals(matter.matter.code)) {
            JsonObject model = new JsonObject();
            model.addProperty("code", matterInfo.get(MATTER).getCode());
            params.add("matter", model);
        }

        if (!matterInfo.get(PARTNER_IN_CHARGE).getCode().isEmpty() && !matterInfo.get(PARTNER_IN_CHARGE).getCode().equals(matter.getPartnerCode())) {
            JsonObject model = new JsonObject();
            model.addProperty("code", matterInfo.get(PARTNER_IN_CHARGE).getCode());
            params.add("partner", model);
        }

        if (!matterInfo.get(LA_IN_CHARGE).getCode().isEmpty() && !matterInfo.get(LA_IN_CHARGE).getCode().equals(matter.getLACode())) {
            JsonObject model = new JsonObject();
            model.addProperty("code", matterInfo.get(LA_IN_CHARGE).getCode());
            params.add("legalAssistant", model);
        }

        if (!matterInfo.get(CLERK_IN_CHARGE).getCode().isEmpty() && !matterInfo.get(CLERK_IN_CHARGE).getCode().equals(matter.getClerkCode())) {
            JsonObject model = new JsonObject();
            model.addProperty("code", matterInfo.get(CLERK_IN_CHARGE).getCode());
            params.add("clerk", model);
        }

        if (!matterInfo.get(TEAM).value.isEmpty() && !matterInfo.get(TEAM).value.equals(matter.team)) {
            params.addProperty("team", matterInfo.get(TEAM).value);
        }

        if (!matterInfo.get(FILE_STATUS).getCode().isEmpty() && !matterInfo.get(FILE_STATUS).getCode().equals(matter.getFileStatusCode())) {
            JsonObject model = new JsonObject();
            model.addProperty("code", matterInfo.get(FILE_STATUS).getCode());
            params.add("fileStatus", model);
        }

        if (!matterInfo.get(FILE_LOCATION).value.isEmpty() && !matterInfo.get(FILE_LOCATION).value.equals(matter.locationBox)) {
            params.addProperty("locationBox", matterInfo.get(FILE_LOCATION).value);
        }

        if (!matterInfo.get(POCKET_LOCATION).value.isEmpty() && !matterInfo.get(POCKET_LOCATION).value.equals(matter.locationPocket)) {
            params.addProperty("locationPocket", matterInfo.get(POCKET_LOCATION).value);
        }

        if (!matterInfo.get(STORAGE_LOCATION).value.isEmpty() && !matterInfo.get(STORAGE_LOCATION).value.equals(matter.locationPhysical)) {
            params.addProperty("locationPhysical", matterInfo.get(STORAGE_LOCATION).value);
        }

        return params;
    }

    @Override
    protected JsonObject buildSaveParam() {
        JsonObject params = new JsonObject();

        params.addProperty("dateOpen", DIHelper.todayWithTime());

        return NetworkManager.mergeJSONObjects(params, _buildSaveParams());
    }

    @Override
    protected JsonObject buildUpdateParam() {
        JsonObject params = new JsonObject();
        params.addProperty("systemNo", matter.systemNo);

        List<LabelValueDetail> matterInfo = model.items.get(MATTER_INFO).items;
        JsonObject branch = new JsonObject();
        branch.addProperty("code", matterInfo.get(BRANCH).getCode());
        params.add("branch", branch);

        if (!matterInfo.get(REF2).value.equals(matter.getManualNo())) {
            params.addProperty("manualNo", matterInfo.get(REF2).value);
        }

        if (!matterInfo.get(PRIMARY_CLIENT).getCode().equals(matter.primaryClient.getCode())) {
            JsonObject primary = new JsonObject();
            primary.addProperty("code", matterInfo.get(PRIMARY_CLIENT).getCode());
            params.add("primaryClient", primary);
        }

        if ( !matterInfo.get(MATTER).getCode().equals(matter.matter.getCode())) {
            JsonObject model = new JsonObject();
            model.addProperty("code", matterInfo.get(MATTER).getCode());
            params.add("matter", model);
        }

        if ( !matterInfo.get(PARTNER_IN_CHARGE).getCode().equals(matter.partner.getCode())) {
            JsonObject model = new JsonObject();
            model.addProperty("code", matterInfo.get(PARTNER_IN_CHARGE).getCode());
            params.add("partner", model);
        }

        if ( !matterInfo.get(LA_IN_CHARGE).getCode().equals(matter.getLACode())) {
            JsonObject model = new JsonObject();
            model.addProperty("code", matterInfo.get(LA_IN_CHARGE).getCode());
            params.add("legalAssistant", model);
        }

        if ( !matterInfo.get(CLERK_IN_CHARGE).getCode().equals(matter.getClerkCode())) {
            JsonObject model = new JsonObject();
            model.addProperty("code", matterInfo.get(CLERK_IN_CHARGE).getCode());
            params.add("clerk", model);
        }

        if (!matterInfo.get(TEAM).value.isEmpty() && !matterInfo.get(TEAM).value.equals(matter.team)) {
            params.addProperty("team", matterInfo.get(TEAM).value);
        }

        if ( !matterInfo.get(FILE_STATUS).getCode().equals(matter.getFileStatusCode())) {
            JsonObject model = new JsonObject();
            model.addProperty("code", matterInfo.get(FILE_STATUS).getCode());
            params.add("fileStatus", model);
        }

        if (!matterInfo.get(FILE_LOCATION).value.equals(matter.locationBox)) {
            params.addProperty("locationBox", matterInfo.get(FILE_LOCATION).value);
        }

        if ( !matterInfo.get(POCKET_LOCATION).value.equals(matter.locationPocket)) {
            params.addProperty("locationPocket", matterInfo.get(POCKET_LOCATION).value);
        }

        if (!matterInfo.get(STORAGE_LOCATION).value.equals(matter.locationPhysical)) {
            params.addProperty("locationPhysical", matterInfo.get(STORAGE_LOCATION).value);
        }

        // Remarks
        List<LabelValueDetail> remarks = model.items.get(REMARKS).items;
        if ( !remarks.get(NOTES).value.equals(matter.remarks)) {
            params.addProperty("remarks", remarks.get(NOTES).value);
        }

        // Case Details
        if (matter.court != null) {
            List<LabelValueDetail> court = model.items.get(CASE_DETAILS).items;
            JsonObject courtParam = new JsonObject();
            if ( !court.get(CASE_TYPE).value.equals(matter.getCourtCaseNo())) {
                courtParam.addProperty("CaseNo", court.get(CASE_TYPE).value);
            }
            if ( !court.get(COURT).value.equals(matter.getCourt())) {
                courtParam.addProperty("Court", court.get(COURT).value);
            }
            if (  !court.get(JUDGE).value.equals(matter.getCourtJudge())) {
                courtParam.addProperty("Judge", court.get(JUDGE).value);
            }
            if ( !court.get(SAR).value.equals(matter.getCourtSAR())) {
                courtParam.addProperty("SAR", court.get(SAR).value);
            }
            if ( !court.get(PLACE).value.equals(matter.getCourtPlace())) {
                courtParam.addProperty("Place", court.get(PLACE).value);
            }
            if ( !court.get(TYPE_NO).value.equals(matter.getCourtTypeNo())) {
                courtParam.addProperty("TypeCase", court.get(TYPE_NO).value);
            }
            params.add("courtInfo", courtParam);
        }

        // Properties
        List<LabelValueDetail> property = model.items.get(PROPERTIES).items;
        JsonArray propertyList = new JsonArray();
        if (matter.propertyGroup.size() >= property.size()-1) {
            for (int i = 1; i < property.size(); i++) {
                if (!property.get(i).code.equals(matter.propertyGroup.get(i-1).getCode())) {
                    JsonObject code = new JsonObject();
                    code.addProperty("code", property.get(i).getCode());
                    propertyList.add(code);
                }
            }
        } else {
            for (int i = 1; i < property.size(); i++) {
                JsonObject code = new JsonObject();
                code.addProperty("code", property.get(i).getCode());
                propertyList.add(code);
            }
        }

        if ((propertyList.size() == 0 && property.size() == 1 && matter.propertyGroup.size() != 0) || propertyList.size() != 0) {
            params.add("propertyGroup", propertyList);
        }

        // PartyGroup
        List<LabelValueDetail> partyGroup = model.items.get(PARTYGROUP).items;
        boolean hasValue = false;
        JsonArray partyArray = new JsonArray();
        for (int i = 0; i < partyGroup.size(); i++) {
            List<LabelValueDetail> codeList = new ArrayList<>();
            if (partyGroup.get(i).isOneRowAdd) {
                for (int j = 0; j < partyGroup.get(i).childCnt; j++) {
                    codeList.add(partyGroup.get(i+1+j));
                }
                JsonObject vendor = buildPartyParam(partyGroup.get(i).label, codeList);
                if (vendor != null) {
                    hasValue = true;
                    partyArray.add(vendor);
                }
            }
        }

        if (hasValue) {
            params.add("partyGroup", partyArray);
        }

        // Solicitor Group
        List<LabelValueDetail> solicitors = model.items.get(SOLICITORS).items;
        JsonArray jsonArray = new JsonArray();
        for (int i = 0; i < solicitors.size(); i++) {
            if (!solicitors.get(i).getCode().equals(matter.solicitorsGroup.get(i).getCode())) {
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("groupName", solicitors.get(i).label);
                jsonObject.addProperty("reference", solicitors.get(i).otherValue1);
                JsonObject code = new JsonObject();
                code.addProperty("code", solicitors.get(i).getCode());
                jsonObject.add("solicitor", code);
                jsonArray.add(jsonObject);
            }
        }

        if ((jsonArray.size() == 0 && solicitors.size() == 0 && matter.solicitorsGroup.size() != 0) || jsonArray.size() != 0) {
            params.add("solicitorsGroup", jsonArray);
        }

        // Bank group
        List<LabelValueDetail> banks = model.items.get(BANKS).items;
        JsonArray bankArray = new JsonArray();
        for (int i = 0; i < banks.size(); i++) {
            if (!banks.get(i).getCode().equals(matter.bankGroup.get(i).getBankCode())) {
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("groupName", banks.get(i).label);
                JsonObject code = new JsonObject();
                code.addProperty("code", banks.get(i).getCode());
                jsonObject.add("bank", code);
                bankArray.add(jsonObject);
            }
        }

        if ((bankArray.size() == 0 && banks.size() == 0 && matter.bankGroup.size() != 0) || bankArray.size() != 0) {
            params.add("bankGroup", bankArray);
        }

        // Important RMGroup
        List<LabelValueDetail> importantRM = model.items.get(IMPORTANT_RM).items;
        JsonArray RMArray = new JsonArray();
        for (int i = 0; i < importantRM.size(); i++) {
            if (!importantRM.get(i).value.equals(matter.RMGroup.get(i).value)) {
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("fieldName", importantRM.get(i).label);
                jsonObject.addProperty("value", importantRM.get(i).value);
                RMArray.add(jsonObject);
            }
        }
        params.add("RMGroup", RMArray);

        // DateGroup
        List<LabelValueDetail> dateGroup = model.items.get(IMPORTANT_DATES).items;
        JsonArray dateArray = new JsonArray();
        for (int i = 0; i < dateGroup.size(); i++) {
            String  value = DIHelper.toMySQLDateFormat(dateGroup.get(i).value);
            if (!value.equals(matter.dateGroup.get(i).value)) {
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("fieldName", dateGroup.get(i).label);
                jsonObject.addProperty("value", value);
                dateArray.add(jsonObject);
            }
        }
        params.add("dateGroup", dateArray);

        // Text Group
        if (matter.textGroup.size() > 0) {
            List<LabelValueDetail> textGroup = model.items.get(TEXT_GROUP).items;
            JsonArray textArray = new JsonArray();
            for (int i = 0; i < textGroup.size(); i++) {
                if (!textGroup.get(i).value.equals(matter.textGroup.get(i).value)) {
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("fieldName", textGroup.get(i).label);
                    jsonObject.addProperty("value", textGroup.get(i).value);
                    textArray.add(jsonObject);
                }
            }
            params.add("textGroup", textArray);
        }

        return NetworkManager.mergeJSONObjects(_buildSaveParams(), params);
    }

    private JsonObject buildPartyParam(String partryGroupName, List<LabelValueDetail> codeList) {
        List<StaffModel> target = new ArrayList<>();
        JsonArray partyParam = new JsonArray();
        for (int i = 0; i < matter.partyGroup.size(); i++) {
            PartyGroup partyGroup = matter.partyGroup.get(i);
            if (partyGroup.partyName.equals(partryGroupName)) {
                target.addAll(partyGroup.party);
                if (partyGroup.party.size() >= codeList.size()) {
                    for (int j = 0; j < codeList.size(); j++) {
                        if (!codeList.get(j).getCode().equals(partyGroup.party.get(j).getCode())) {
                            JsonObject code = new JsonObject();
                            code.addProperty("code", codeList.get(i).getCode());
                            partyParam.add(code);
                        }
                    }
                } else {
                    for (int j = 0; j < codeList.size(); j++) {
                        JsonObject code = new JsonObject();
                        code.addProperty("code", codeList.get(i).getCode());
                        partyParam.add(code);
                    }
                }
            }
        }

        JsonObject jsonObject = null;
        if ((partyParam.size() == 0 && target.size() != 0 && codeList.size() == 0 ) || partyParam.size() != 0) {
            jsonObject = new JsonObject();
            jsonObject.addProperty("PartyName", partryGroupName);
            jsonObject.add("party", partyParam);
        }

        return jsonObject;
    }
}

