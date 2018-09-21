package it.denning.navigation.add.contact;

import android.content.Context;
import android.support.v4.util.SimpleArrayMap;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.hbb20.CountryCodePicker;
import com.quickblox.q_municate_db.utils.ErrorUtils;

import org.zakariya.stickyheaders.SectioningAdapter;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import it.denning.R;
import it.denning.general.DIAlert;
import it.denning.general.DIConstants;
import it.denning.general.DIGeneralInterface;
import it.denning.general.DIHelper;
import it.denning.model.AddSectionItemModel;
import it.denning.model.CodeDescription;
import it.denning.model.Contact;
import it.denning.model.LabelValueDetail;
import it.denning.navigation.add.utils.basesectionadapter.BaseSectionAdapter;
import it.denning.network.CompositeCompletion;
import it.denning.network.ErrorHandler;
import it.denning.network.NetworkManager;
import it.denning.search.utils.OnSectionItemClickListener;
import it.denning.search.utils.myfloatingedittext.MyFloatingEditText;
import it.denning.utils.KeyboardUtils;
import android.os.Handler;

/**
 * Created by denningit on 2018-01-19.
 */

public class AddContactAdapter extends BaseSectionAdapter {
    private Contact contact;
    private SimpleArrayMap<Integer, CountryCodePicker> codePickers = new SimpleArrayMap<>();

    // Sections
    public int PERSONAL_INFO = 0;
    public int CONTACT_INFO =1;
    public int OTHER_INFO = 2;
    public int COMPANY_INFO = 3;
    public int INVITATION = 4;

    // Personal Info
    public int ID_TYPE = 0;
    public int ID_NO = 1;
    public int OLD_IC = 2;
    public int NAME = 3;
    public int TITLE = 4;

    // Contact Info
    public int ADDRESS1 = 0;
    public int ADDRESS2 = 1;
    public int ADDRESS3 = 2;
    public int POSTCODE = 3;
    public int TOWN = 4;
    public int STATE = 5;
    public int COUNTRY = 6;
    public int PHONE_MOBILE = 7;
    public int PHONE_HOME = 8;
    public int PHONE_OFFICE = 9;
    public int FAX = 10;
    public int EMAIL = 11;
    public int WEBSITE = 12;
    public int CONTACT_PERSON = 13;

    // Other Info
    public int CITIZENSHIP = 0;
    public int DATE_OF_BIRTH = 1;
    public int OCCUPATION = 2;
    public int TAX_FILE_NO = 3;
    public int IRD_BRANCH = 4;

    // Company Info
    public int REGISTERED_OFFICE = 0;

    // INVITATION
    public int INVITATION_TO_DENNING = 0;

    private boolean onBind;

    public AddContactAdapter(Context context, OnSectionItemClickListener itemClickListener, Contact contact) {
        super(context, itemClickListener);
        this.contact = contact;
        titles = Arrays.asList(new String[]{"Personal Info", "Contact Info", "Other Info", "Company Info", "Invitation"});
        buildModel();

    }

    public Contact getContact() {
        return contact;
    }

    public void buildModel() {
        AddSectionItemModel sectionItemModel = new AddSectionItemModel();

        LabelValueDetail labelValueDetail = new LabelValueDetail("ID Type *", "", DIConstants.GENERAL_TYPE);
        if (contact != null) {
            labelValueDetail.value = contact.getIdTypeDesc();
            labelValueDetail.code = contact.getIdTypeCode();
        }
        sectionItemModel.items.add(labelValueDetail);

        labelValueDetail = new LabelValueDetail("ID No *", "", DIConstants.INPUT_TYPE);
        if (contact != null) {
            labelValueDetail.value = contact.IDNo;
        }
        sectionItemModel.items.add(labelValueDetail);

        labelValueDetail = new LabelValueDetail("Old IC", "", DIConstants.DISABLE_TYPE);
        if (contact != null) {
            labelValueDetail.value = contact.KPLama;
        }
        sectionItemModel.items.add(labelValueDetail);

        labelValueDetail = new LabelValueDetail("Name *", "", DIConstants.INPUT_TYPE);
        if (contact != null) {
            labelValueDetail.value = contact.name;
        }
        sectionItemModel.items.add(labelValueDetail);

        labelValueDetail = new LabelValueDetail("Title", "", DIConstants.GENERAL_TYPE);
        if (contact != null) {
            labelValueDetail.value = contact.title;
        }
        sectionItemModel.items.add(labelValueDetail);
        model.items.add(sectionItemModel);

        // Contact Info
        sectionItemModel = new AddSectionItemModel();
        labelValueDetail = new LabelValueDetail("Address 1", "", DIConstants.INPUT_TYPE);
        if (contact != null) {
            labelValueDetail.value = contact.getAddress1();
        }
        sectionItemModel.items.add(labelValueDetail);

        labelValueDetail = new LabelValueDetail("Address 2", "", DIConstants.INPUT_TYPE);
        if (contact != null) {
            labelValueDetail.value = contact.getAddress2();
        }
        sectionItemModel.items.add(labelValueDetail);

        labelValueDetail = new LabelValueDetail("Address 3", "", DIConstants.INPUT_TYPE);
        if (contact != null) {
            labelValueDetail.value = contact.getAddress3();
        }
        sectionItemModel.items.add(labelValueDetail);

        labelValueDetail = new LabelValueDetail("Postcode", "", DIConstants.GENERAL_TYPE);
        if (contact != null) {
            labelValueDetail.value = contact.getPostCode();
        }
        sectionItemModel.items.add(labelValueDetail);

        labelValueDetail = new LabelValueDetail("Town", "", DIConstants.GENERAL_TYPE);
        if (contact != null) {
            labelValueDetail.value = contact.getTown();
        }
        sectionItemModel.items.add(labelValueDetail);

        labelValueDetail = new LabelValueDetail("State", "", DIConstants.GENERAL_TYPE);
        if (contact != null) {
            labelValueDetail.value = contact.getState();
        }
        sectionItemModel.items.add(labelValueDetail);

        labelValueDetail = new LabelValueDetail("Country", "", DIConstants.GENERAL_TYPE);
        if (contact != null) {
            labelValueDetail.value = contact.getCountry();
        }
        sectionItemModel.items.add(labelValueDetail);

        labelValueDetail = new LabelValueDetail("Phone (Mobile)", "", DIConstants.PHONE_TYPE);
        if (contact != null) {
            labelValueDetail.value = contact.mobilePhone;
        }
        sectionItemModel.items.add(labelValueDetail);

        labelValueDetail = new LabelValueDetail("Phone (Home)", "", DIConstants.PHONE_TYPE);
        if (contact != null) {
            labelValueDetail.value = contact.homePhone;
        }
        sectionItemModel.items.add(labelValueDetail);

        labelValueDetail = new LabelValueDetail("Phone (Office)", "", DIConstants.PHONE_TYPE);
        if (contact != null) {
            labelValueDetail.value = contact.officePhone;
        }
        sectionItemModel.items.add(labelValueDetail);

        labelValueDetail = new LabelValueDetail("Fax", "", DIConstants.PHONE_TYPE);
        if (contact != null) {
            labelValueDetail.value = contact.fax;
        }
        sectionItemModel.items.add(labelValueDetail);

        labelValueDetail = new LabelValueDetail("Email", "", DIConstants.INPUT_TYPE);
        labelValueDetail.inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS;
        if (contact != null) {
            labelValueDetail.value = contact.email;
        }
        sectionItemModel.items.add(labelValueDetail);

        labelValueDetail = new LabelValueDetail("Website", "", DIConstants.LEFT_BUTTON_TYPE);
        labelValueDetail.leftView = new LabelValueDetail("HTTP://", "", false);
        labelValueDetail.inputType = InputType.TYPE_TEXT_VARIATION_WEB_EDIT_TEXT;
        if (contact != null) {
            labelValueDetail.leftView.value = contact.website;
        }
        sectionItemModel.items.add(labelValueDetail);

        labelValueDetail = new LabelValueDetail("Contact Person", "", DIConstants.INPUT_TYPE);
        if (contact != null) {
            labelValueDetail.value = contact.contactPerson;
        }
        sectionItemModel.items.add(labelValueDetail);
        model.items.add(sectionItemModel);

        // Other Info
        sectionItemModel = new AddSectionItemModel();
        labelValueDetail = new LabelValueDetail("Citizenship", "", DIConstants.GENERAL_TYPE);
        if (contact != null) {
            labelValueDetail.value = contact.citizenship;
        }
        sectionItemModel.items.add(labelValueDetail);

        labelValueDetail = new LabelValueDetail("Date of Birth", "", DIConstants.GENERAL_TYPE);
        if (contact != null) {
            labelValueDetail.value = DIHelper.convertToSimpleDateFormat(contact.dateOfBirth);
        }
        sectionItemModel.items.add(labelValueDetail);

        labelValueDetail = new LabelValueDetail("Occupation", "", DIConstants.GENERAL_TYPE);
        if (contact != null) {
            labelValueDetail.code = contact.getOccupationCode();
            labelValueDetail.value = contact.getOccupationDesc();
        }
        sectionItemModel.items.add(labelValueDetail);

        labelValueDetail = new LabelValueDetail("Tax File No.", "", DIConstants.INPUT_TYPE);
        if (contact != null) {
            labelValueDetail.value = contact.tax;
        }
        sectionItemModel.items.add(labelValueDetail);

        labelValueDetail = new LabelValueDetail("IRD Branch", "", DIConstants.GENERAL_TYPE);
        if (contact != null) {
            labelValueDetail.code = contact.getIRDBranchCode();
            labelValueDetail.value = contact.getIRDBranchDesc();
        }
        sectionItemModel.items.add(labelValueDetail);
        model.items.add(sectionItemModel);

        // Company Info
        sectionItemModel = new AddSectionItemModel();
        labelValueDetail = new LabelValueDetail("Registered Office", "", DIConstants.INPUT_TYPE);
        if (contact != null) {
            labelValueDetail.value = contact.registeredOffice;
        }
        sectionItemModel.items.add(labelValueDetail);
        model.items.add(sectionItemModel);

        // Invitation
        sectionItemModel = new AddSectionItemModel();
        labelValueDetail = new LabelValueDetail("Invite to Denning", "", DIConstants.RIGHT_BUTTON_TYPE);
        if (contact != null) {
            labelValueDetail.value = contact.InviteToDenning;
        }
        sectionItemModel.items.add(labelValueDetail);
        model.items.add(sectionItemModel);

        if (contact != null) {
            applyRuleToIDType(contact.getIdTypeCode());
//            enableOldICInput(false);
        } else {
            contact = new Contact();
        }

        notifyAllSectionsDataSetChanged();
    }

    public void applyRuleToIDType(String code) {
        if (code.equals("5") || code.equals("8") || code.equals("11")) {
            model.items.get(OTHER_INFO).items.get(CITIZENSHIP).label = "Country of Incorporation";
        }

        if (code.equals("1") || code.equals("3")) {
            enableOldICInput(true);
        } else {
            enableOldICInput(false);
        }

        if (code.equals("1") || code.equals("2")) {
            model.items.get(PERSONAL_INFO).items.get(ID_NO).inputType = InputType.TYPE_CLASS_NUMBER;
        } else {
            model.items.get(PERSONAL_INFO).items.get(ID_NO).inputType = InputType.TYPE_CLASS_TEXT;
        }

        notifySectionItemChanged(PERSONAL_INFO, ID_NO);
        notifySectionItemChanged(PERSONAL_INFO, OLD_IC);
        notifySectionItemChanged(OTHER_INFO, CITIZENSHIP);
    }

    private String splitPhone(String phone) {
        if (phone.trim().length() == 0) {
            return phone;
        }

        phone = phone.replace("-", "").replace(")", "").replace("(", "");

        return "+" + phone;
    }

    public String getFileNo() {
        return model.items.get(0).items.get(0).value;
    }

    public class LeftButtonViewholder extends ItemViewHolder {
        @BindView(R.id.add_detail_edittext)
        MyFloatingEditText editText;
        @BindView(R.id.left_textview)
        TextView leftBtn;
        public MyCustomEditTextListener myCustomEditTextListener;

        public LeftButtonViewholder(View itemView, MyCustomEditTextListener myCustomEditTextListener) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            this.myCustomEditTextListener = myCustomEditTextListener;
            editText.setOnFocusChangeListener(myCustomEditTextListener);
        }
    }

    public class RightButtonViewholder extends ItemViewHolder {
        @BindView(R.id.textview)
        TextView editText;
        @BindView(R.id.right_checkbox)
        CheckBox rightCheckbox;

        public RightButtonViewholder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public class PhoneViewHolder extends ItemViewHolder {
        @BindView(R.id.editText_carrierNumber)
        EditText editTextCarrierNumber;
        @BindView(R.id.ccp)
        CountryCodePicker ccp;
        public MyPhoneInputListener myCustomEditTextListener;

        public PhoneViewHolder(View itemView, MyPhoneInputListener myCustomEditTextListener) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            ccp.registerCarrierNumberEditText(editTextCarrierNumber);
            ccp.setNumberAutoFormattingEnabled(false);
            this.myCustomEditTextListener = myCustomEditTextListener;
            editTextCarrierNumber.setOnFocusChangeListener(myCustomEditTextListener);
        }
    }

    @Override
    public ItemViewHolder onCreateItemViewHolder(ViewGroup parent, int itemUserType) {
        ItemViewHolder itemViewHolder = null;
        View itemView = null;
        if (itemUserType == DIConstants.RIGHT_BUTTON_TYPE) {
            itemView = LayoutInflater.
                    from(parent.getContext()).
                    inflate(R.layout.cardview_right_button, parent, false);
            itemViewHolder = new RightButtonViewholder(itemView);
        } else if (itemUserType == DIConstants.PHONE_TYPE) {
            itemView = LayoutInflater.
                    from(parent.getContext()).
                    inflate(R.layout.cardview_phone_number, parent, false);
            itemViewHolder = new PhoneViewHolder(itemView, new MyPhoneInputListener());
        } else if (itemUserType == DIConstants.LEFT_BUTTON_TYPE) {
            itemView = LayoutInflater.
                    from(parent.getContext()).
                    inflate(R.layout.cardview_left_button, parent, false);
            itemViewHolder = new LeftButtonViewholder(itemView, new MyCustomEditTextListener());
        }  else {
            itemViewHolder = super.onCreateItemViewHolder(parent, itemUserType);
        }

        return itemViewHolder;
    }

    @Override
    public void onBindItemViewHolder(ItemViewHolder viewHolder, int sectionIndex, int itemIndex, int itemType) {
        if (itemType == DIConstants.LEFT_BUTTON_TYPE) {
            displayLeft((LeftButtonViewholder)viewHolder, sectionIndex, itemIndex);
        } else if (itemType == DIConstants.PHONE_TYPE) {
            displayPhone((PhoneViewHolder) viewHolder, sectionIndex, itemIndex);
        } else if (itemType == DIConstants.RIGHT_BUTTON_TYPE) {
            displayRight((RightButtonViewholder)viewHolder, sectionIndex, itemIndex);
        } else {
            super.onBindItemViewHolder(viewHolder, sectionIndex, itemIndex, itemType);
        }
    }

    @Override
    public void onBindHeaderViewHolder(SectioningAdapter.HeaderViewHolder viewHolder, final int sectionIndex, final int headerType) {
        HeaderViewHolder headerViewHolder = (HeaderViewHolder) viewHolder;
        headerViewHolder.firstTitle.setText(titles.get(sectionIndex));
        if (sectionIndex == COMPANY_INFO) {
            headerViewHolder.linkBtn.setVisibility(View.VISIBLE);
            headerViewHolder.linkBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String formattedAddress = model.items.get(CONTACT_INFO).items.get(ADDRESS1).value + " " +
                            model.items.get(CONTACT_INFO).items.get(ADDRESS1).value + " " +
                            model.items.get(CONTACT_INFO).items.get(ADDRESS1).value + " " +
                            model.items.get(CONTACT_INFO).items.get(POSTCODE).value + " " +
                            model.items.get(CONTACT_INFO).items.get(TOWN).value + ", " +
                            model.items.get(CONTACT_INFO).items.get(STATE).value + ", " +
                            model.items.get(CONTACT_INFO).items.get(COUNTRY).value;
                    updateDataAndRefresh(formattedAddress, COMPANY_INFO, 0);
                }
            });
        } else {
            headerViewHolder.linkBtn.setVisibility(View.INVISIBLE);
        }
    }

    private void displayLeft(final LeftButtonViewholder viewHolder, final int sectionIndex, final int itemIndex) {
        viewHolder.myCustomEditTextListener.updatePosition(sectionIndex, itemIndex);
        final LabelValueDetail labelValueDetail = model.items.get(sectionIndex).items.get(itemIndex);
        viewHolder.leftBtn.setText(labelValueDetail.leftView.label);
        viewHolder.editText.setText(labelValueDetail.value);
        viewHolder.editText.setHint(labelValueDetail.label);
        viewHolder.editText.setFloatingLabelText(labelValueDetail.label);
    }

    private void displayRight(final RightButtonViewholder viewHolder, final int sectionIndex, final int itemIndex) {
        final LabelValueDetail labelValueDetail = model.items.get(sectionIndex).items.get(itemIndex);
        viewHolder.editText.setText(labelValueDetail.label);
        if (labelValueDetail.value == null) {
            viewHolder.rightCheckbox.setChecked(false);
        } else {
            viewHolder.rightCheckbox.setChecked(true);
        }

        viewHolder.rightCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String value = "";
                if (isChecked) {
                    value = "1";
                } else {
                    value = "0";
                }
                updateData(value, sectionIndex, itemIndex);
            }
        });
    }

    private void displayPhone(final PhoneViewHolder viewHolder, final int sectionIndex, final int itemIndex) {
        viewHolder.myCustomEditTextListener.updatePosition(viewHolder.ccp, sectionIndex, itemIndex);
        final LabelValueDetail labelValueDetail = model.items.get(sectionIndex).items.get(itemIndex);
        viewHolder.editTextCarrierNumber.setHint(labelValueDetail.label);
        String phone = splitPhone(labelValueDetail.value);
        if (phone.trim().length() != 0) {
            viewHolder.ccp.setFullNumber(phone);
        }
    }

    @Override
    public void updateDataFromInput(String input, int sectionIndex, int itemIndex) {
        input = input.trim();
        if (input.trim().length() == 0) {
            return;
        }

        input = input.substring(0, 1).toUpperCase() + input.substring(1);

        if (sectionIndex == COMPANY_INFO) {
            if (itemIndex == ADDRESS1 || itemIndex == ADDRESS2 || itemIndex == ADDRESS3) {
                input = input.trim() + ",";
            }
        }

        if (sectionIndex == PERSONAL_INFO) {
            if (itemIndex == ID_NO) {
                String IDType = model.items.get(PERSONAL_INFO).items.get(ID_TYPE).code;
                if (IDType.equals("1") || IDType.equals("2")) {
                    input = input.replace("-", "");
                    if (input.length() > 12) {
                        DIAlert.showSimpleAlert(context, R.string.alert_ID_wrong);
                        return;
                    }

                    if (input.length() > 6) {
                        String birth = input.substring(0, 6);
                        String year = birth.substring(0, 2);
                        if (Integer.parseInt(year) < 50) {
                            year = "20" + year;
                        } else {
                            year = "19" + year;
                        }
                        String month = birth.substring(2, 4);
                        if (Integer.parseInt(month) - 1 < 0) {
                            DIAlert.showSimpleAlert(context, R.string.alert_valid_ID);
                            return;
                        }
                        String day = birth.substring(4, 6);
                        if (Integer.parseInt(month) > 12 || Integer.parseInt(day) > 31) {
                            DIAlert.showSimpleAlert(context, R.string.alert_valid_ID);
                            return;
                        }
                        final Calendar cal = Calendar.getInstance();
                        cal.set(Integer.parseInt(year), Integer.parseInt(month)-1, Integer.parseInt(day));
                        new Handler().post(new Runnable() {
                            @Override
                            public void run () {
                                updateDataAndRefresh(DIHelper.toSimpleDateFormat(cal), OTHER_INFO, DATE_OF_BIRTH);
                            }
                        });
                        input = new StringBuilder(input).insert(6, '-').toString();
                    }

                    if (input.length() > 9) {
                        input = new StringBuilder(input).insert(9, '-').toString();
                    }
                }

                if (!input.equals(contact.IDNo)) {
                    checkIDValidation(input, itemIndex, R.string.alert_ID_duplicate, new DIGeneralInterface() {
                        @Override
                        public void completionBlock() {
                            ((AddContactActivity) context).isIDDuplicated = false;
                        }
                    }, new DIGeneralInterface() {
                        @Override
                        public void completionBlock() {
                            ((AddContactActivity) context).isIDDuplicated = true;
                        }
                    });
                }
            } else  if (itemIndex == OLD_IC) {
                if (!input.equals(contact.KPLama)) {
                    checkIDValidation(input, itemIndex, R.string.alert_ID_duplicate, new DIGeneralInterface() {
                        @Override
                        public void completionBlock() {
                            ((AddContactActivity) context).isOldIDDuplicated = false;
                        }
                    }, new DIGeneralInterface() {
                        @Override
                        public void completionBlock() {
                            ((AddContactActivity) context).isOldIDDuplicated = true;
                        }
                    });
                }
            } else if (itemIndex == NAME) {
//                checkIDValidation(input, itemIndex, R.string.alert_Name_duplicate, new DIGeneralInterface() {
//                    @Override
//                    public void completionBlock() {
//                        ((AddContactActivity) context).isNameDuplicated = false;
//                    }
//                }, new DIGeneralInterface() {
//                    @Override
//                    public void completionBlock() {
//                        ((AddContactActivity) context).isNameDuplicated = true;
//                    }
//                });
            } else {
                input = input.substring(0, 1).toUpperCase() + input.substring(1);
            }
        } else {
            input = input.substring(0, 1).toUpperCase() + input.substring(1);
        }

        if (sectionIndex == CONTACT_INFO && itemIndex >= ADDRESS1 && itemIndex <= ADDRESS3) {
            if (input.charAt(input.length()-1) == ',') {
                return;
            }
            input += ",";
        }

        model.items.get(sectionIndex).items.get(itemIndex).value = input;
    }

    private void checkIDValidation(final String ID, final int itemIndex, final int resID, final DIGeneralInterface successCallback, final DIGeneralInterface errorCallback) {
        ((AddContactActivity)context).showActionBarProgress();
        ((AddContactActivity)context).isDuplicationChecking = true;
        String url = DIConstants.CONTACT_ID_NAME_DUPLICATE + ID;
        NetworkManager.getInstance().sendPrivateGetRequest(url, new CompositeCompletion() {
            @Override
            public void parseResponse(JsonElement jsonElement) {
                ((AddContactActivity)context).hideActionBarProgress();
                ((AddContactActivity)context).isDuplicationChecking = false;
                if (jsonElement.getAsJsonArray().size() > 0) {
                    ErrorUtils.showError(context, resID);
                    errorCallback.completionBlock();
                } else {
                    successCallback.completionBlock();
                }
            }
        }, new ErrorHandler() {
            @Override
            public void handleError(String error) {
                ((AddContactActivity)context).hideActionBarProgress();
                ((AddContactActivity)context).isDuplicationChecking = false;
                ErrorUtils.showError(context, error);
            }
        });
    }

    public void enableOldICInput(boolean isEnabled) {
        if (!isEnabled) {
            model.items.get(PERSONAL_INFO).items.get(OLD_IC).viewType = DIConstants.DISABLE_TYPE;
            model.items.get(PERSONAL_INFO).items.get(OLD_IC).value = "";
        } else {
            model.items.get(PERSONAL_INFO).items.get(OLD_IC).viewType = DIConstants.INPUT_TYPE;
        }
        notifySectionItemChanged(PERSONAL_INFO, OLD_IC);
    }

    public void updateIDType(CodeDescription codeDescription, int sectionIndex, int itemIndex) {
        applyRuleToIDType(codeDescription.code);

        notifySectionItemChanged(PERSONAL_INFO, ID_NO);
        notifySectionItemChanged(OTHER_INFO, CITIZENSHIP);

        updateCodeDescData(codeDescription, sectionIndex, itemIndex);
    }

    public void updatePostCode(JsonObject object) {
        updateDataAndRefresh(object.get("postcode").getAsString(), CONTACT_INFO, POSTCODE);
        updateDataAndRefresh(object.get("city").getAsString(), CONTACT_INFO, TOWN);
        updateDataAndRefresh(object.get("state").getAsString(), CONTACT_INFO, STATE);
        updateDataAndRefresh(object.get("country").getAsString(), CONTACT_INFO, COUNTRY);
    }

    public boolean isValidProceed() {
        if (model.items.get(PERSONAL_INFO).items.get(ID_TYPE).code.trim().length() == 0) {
            DIAlert.showSimpleAlert(context, R.string.alert_ID_type_must);
            return false;
        }

        if ((getCode(PERSONAL_INFO, ID_TYPE).equals("1") || getCode(PERSONAL_INFO, ID_TYPE).equals("3")) && model.items.get(PERSONAL_INFO).items.get(OLD_IC).value.trim().length() == 0) {
            DIAlert.showSimpleAlert(context, R.string.alert_old_ID_must);
            return false;
        }

        if (model.items.get(PERSONAL_INFO).items.get(NAME).value.trim().length() == 0) {
            DIAlert.showSimpleAlert(context, R.string.alert_name_must);
            return false;
        }

        if (model.items.get(PERSONAL_INFO).items.get(ID_NO).value.trim().length() == 0) {
            DIAlert.showSimpleAlert(context, R.string.alert_ID_no_must);
            return false;
        }

        return true;
    }

    private String removeSpecial(String val) {
        if (val == null || val.trim().length() == 0) {
            return "";
        }
        return val.replace("-", "").replace(")", "").replace("(", "").replace("+", "");
    }

    private boolean isSpecialEquals(String val1, String val2) {
        return removeSpecial(val1).equals(removeSpecial(val2));
    }

    @Override
    public JsonObject buildSaveParam() {
        JsonObject params = new JsonObject();

        List<LabelValueDetail> personalItems = model.items.get(PERSONAL_INFO).items;

        if (!isSpecialEquals(personalItems.get(ID_NO).value, contact.IDNo)) {
            params.addProperty("IDNo", personalItems.get(ID_NO).value);
        }

        if (!isSpecialEquals(personalItems.get(OLD_IC).value, contact.KPLama)) {
            params.addProperty("KPLama", personalItems.get(OLD_IC).value);
        }

        if ( !isSpecialEquals(personalItems.get(ID_TYPE).code,contact.getIdTypeCode())) {
            JsonObject IDType = new JsonObject();
            IDType.addProperty("code", personalItems.get(ID_TYPE).code);
            params.add("idType", IDType);
        }

        if ( !isSpecialEquals(personalItems.get(NAME).value,contact.name)) {
            params.addProperty("name", personalItems.get(NAME).value);
        }

        if ( !isSpecialEquals(personalItems.get(TITLE).value,contact.title)) {
            params.addProperty("title", personalItems.get(TITLE).value);
        }

        List<LabelValueDetail> contactItems = model.items.get(CONTACT_INFO).items;
        JsonObject address = new JsonObject();
        if ( !isSpecialEquals(contactItems.get(TOWN).value,contact.getTown())) {
            address.addProperty("city", contactItems.get(TOWN).value);
        }
        if ( !isSpecialEquals(contactItems.get(STATE).value,contact.getState())) {
            address.addProperty("state", contactItems.get(STATE).value);
        }
        if ( !isSpecialEquals(contactItems.get(COUNTRY).value,contact.getCountry())) {
            address.addProperty("country", contactItems.get(COUNTRY).value);
        }
        if ( !isSpecialEquals(contactItems.get(POSTCODE).value,contact.getPostCode())) {
            address.addProperty("postcode", contactItems.get(POSTCODE).value);
        }
        if ( !isSpecialEquals(contactItems.get(ADDRESS1).value,contact.getAddress1())) {
            address.addProperty("line1", contactItems.get(ADDRESS1).value);
        }
        if ( !isSpecialEquals(contactItems.get(ADDRESS2).value,contact.getAddress2())) {
            address.addProperty("line2", contactItems.get(ADDRESS2).value);
        }
        if ( !isSpecialEquals(contactItems.get(ADDRESS3).value,contact.getAddress3())) {
            address.addProperty("line3", contactItems.get(ADDRESS3).value);
        }
        if (address.size() > 0) {
            params.add("address", address);
        }

        if ( !isSpecialEquals(contactItems.get(EMAIL).value,contact.email)) {
            params.addProperty("emailAddress", contactItems.get(EMAIL).value);
        }
        // Phones
        if ( !isSpecialEquals(contactItems.get(PHONE_MOBILE).value, contact.mobilePhone)) {
            params.addProperty("phoneMobile", contactItems.get(PHONE_MOBILE).value);
        }

        if ( !isSpecialEquals(contactItems.get(PHONE_HOME).value, contact.homePhone)) {
            params.addProperty("phoneHome", contactItems.get(PHONE_HOME).value);
        }

        if ( !isSpecialEquals(contactItems.get(PHONE_OFFICE).value, contact.officePhone)) {
            params.addProperty("phoneOffice", contactItems.get(PHONE_OFFICE).value);
        }

        if ( !isSpecialEquals(contactItems.get(FAX).value, contact.officePhone)) {
            params.addProperty("phoneFax", contactItems.get(FAX).value);
        }

        if ( !isSpecialEquals(contactItems.get(WEBSITE).value,contact.website)) {
            params.addProperty("webSite", contactItems.get(WEBSITE).value);
        }
        if (  !isSpecialEquals(contactItems.get(CONTACT_PERSON).value,contact.contactPerson)) {
            params.addProperty("contactPerson", contactItems.get(CONTACT_PERSON).value);
        }

        List<LabelValueDetail> otherItems = model.items.get(OTHER_INFO).items;
        if ( !isSpecialEquals(otherItems.get(CITIZENSHIP).value,contact.citizenship)) {
            params.addProperty("citizenship", otherItems.get(CITIZENSHIP).value);
        }

        if (!isSpecialEquals(DIHelper.toMySQLDateFormat(otherItems.get(DATE_OF_BIRTH).value),contact.dateOfBirth)) {
            params.addProperty("dateBirth", DIHelper.toMySQLDateFormat(otherItems.get(DATE_OF_BIRTH).value));
        }

        if ( !isSpecialEquals(otherItems.get(OCCUPATION).code,contact.getOccupationCode())) {
            JsonObject occupation = new JsonObject();
            occupation.addProperty("code", otherItems.get(OCCUPATION).code);
            params.add("occupation", occupation);
        }

        if ( !isSpecialEquals(otherItems.get(TAX_FILE_NO).value,contact.tax)) {
            params.addProperty("taxFileNo", otherItems.get(TAX_FILE_NO).value);
        }

        if ( !isSpecialEquals(otherItems.get(IRD_BRANCH).code,contact.getIRDBranchCode())) {
            JsonObject irdBranch = new JsonObject();
            irdBranch.addProperty("code", otherItems.get(IRD_BRANCH).code);
            params.add("irdBranch", irdBranch);
        }

        List<LabelValueDetail> companyItems = model.items.get(COMPANY_INFO).items;
        if ( !isSpecialEquals(companyItems.get(REGISTERED_OFFICE).value,contact.registeredOffice)) {
            params.addProperty("registeredOffice", companyItems.get(REGISTERED_OFFICE).value);
        }

        List<LabelValueDetail> inviteItems = model.items.get(INVITATION).items;
        if (!isSpecialEquals(inviteItems.get(INVITATION_TO_DENNING).value,contact.InviteToDenning)) {
            params.addProperty("inviteToDenning", inviteItems.get(INVITATION_TO_DENNING).value);
        }
        return params;
    }

    @Override
    public JsonObject buildUpdateParam() {
        JsonObject param = new JsonObject();
        param.addProperty("code", contact.code);

        return NetworkManager.mergeJSONObjects(buildSaveParam(), param);
    }

    private class MyCustomEditTextListener implements View.OnFocusChangeListener {
        private int sectionIndex, itemIndex;

        public void updatePosition(int sectionIndex, int itemIndex) {
            this.sectionIndex = sectionIndex;
            this.itemIndex = itemIndex;
        }

        @Override
        public void onFocusChange(View v, final boolean hasFocus) {
            updateDataFromInput(((EditText)v).getText().toString(), sectionIndex, itemIndex);
            KeyboardUtils.hideKeyboard(v);

            android.os.Handler mHandler = ((AddContactActivity)context).getWindow().getDecorView().getHandler();
            mHandler.post(new Runnable() {
                public void run(){
                    //change adapter contents
                    if (!hasFocus) {
                        notifySectionItemChanged(sectionIndex, itemIndex);
                    }
                }
            });
        }
    }

    private class MyPhoneInputListener implements View.OnFocusChangeListener {
        private int sectionIndex, itemIndex;
        private CountryCodePicker ccp;

        public void updatePosition(CountryCodePicker ccp,  int sectionIndex, int itemIndex) {
            this.sectionIndex = sectionIndex;
            this.itemIndex = itemIndex;
            this.ccp = ccp;
        }

        @Override
        public void onFocusChange(View v, final boolean hasFocus) {
            model.items.get(sectionIndex).items.get(itemIndex).value = ccp.getFullNumber();
            KeyboardUtils.hideKeyboard(v);
        }
    }
}

