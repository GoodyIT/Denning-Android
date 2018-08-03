package it.denning.navigation.add.utils.basesectionadapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.CardView;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.gson.JsonObject;

import org.zakariya.stickyheaders.SectioningAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;

import butterknife.BindView;
import butterknife.ButterKnife;
import it.denning.R;
import it.denning.general.DIConstants;
import it.denning.model.AddingModel;
import it.denning.model.CodeDescription;
import it.denning.model.LabelValueDetail;
import it.denning.navigation.add.quotation.AddQuotationAdapter;
import it.denning.navigation.dashboard.section1.staffleave.leavependingapproval.LeavePendingAppAdapter;
import it.denning.search.utils.OnSectionItemClickListener;
import it.denning.search.utils.myfloatingedittext.MyFloatingEditText;
import it.denning.utils.KeyboardUtils;

import static it.denning.general.DIConstants.DISABLE_TYPE;
import static it.denning.general.DIConstants.GENERAL_TYPE;
import static it.denning.general.DIConstants.INPUT_TYPE;
import static it.denning.general.DIConstants.ONE_BUTTON_TYPE;
import static it.denning.general.DIConstants.SELECTION_TYPE;
import static it.denning.general.DIConstants.TWO_BUTTON_TYPE;
import static it.denning.general.DIConstants.TWO_COLUMN_DETAIL_TYPE;
import static it.denning.general.DIConstants.TWO_COLUMN_LEFT_DETAIL_RIGHT_INPUT_TYPE;
import static it.denning.general.DIConstants.TWO_COLUMN_LEFT_INPUT_RIGHT_DETAIL_TYPE;
import static it.denning.general.DIConstants.TWO_COLUMN_TYPE;

/**
 * Created by denningit on 2018-02-05.
 */

public class BaseSectionAdapter extends SectioningAdapter {
    final protected Context context;
    final protected OnSectionItemClickListener itemClickListener;

    protected int START_DATE_TIME = 4;
    protected int END_DATE_TIME = 5;
    protected int NEXT_START_DATE_TIME = 0;
    protected int NEXT_END_DATE_TIME = 1;
    protected AddingModel model;
    protected List<String> titles = new ArrayList<>();

    public BaseSectionAdapter(Context context, OnSectionItemClickListener itemClickListener) {
        this.context = context;
        this.itemClickListener = itemClickListener;
        model = new AddingModel();
    }

    protected JsonObject buildSaveParam() {
        return null;
    }

    protected JsonObject buildUpdateParam() {
        return null;
    }

    public class HeaderViewHolder extends SectioningAdapter.HeaderViewHolder {
        @BindView(R.id.first_textview)
        public TextView firstTitle;
        @BindView(R.id.link_button)
        public Button linkBtn;

        public HeaderViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public class GeneralTypeViewHolder extends ItemViewHolder {
        @BindView(R.id.add_detail_edittext)
        MyFloatingEditText editText;
        @BindView(R.id.add_detail_btn)
        ImageButton detailBtn;
        @BindView(R.id.add_cardview)
        CardView cardView;

        public GeneralTypeViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public class SelectionTypeViewHolder extends ItemViewHolder {
        @BindView(R.id.add_detail_edittext)
        MyFloatingEditText editText;
        @BindView(R.id.add_detail_btn)
        ImageButton detailBtn;
        @BindView(R.id.add_cardview)
        CardView cardView;

        public SelectionTypeViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public class InputTypeViewHolder extends SectioningAdapter.ItemViewHolder {
        @BindView(R.id.add_detail_edittext)
        MyFloatingEditText editText;
        @BindView(R.id.add_cardview)
        CardView cardView;
        public MyCustomEditTextListener myCustomEditTextListener;
        public InputTypeViewHolder(View itemView, MyCustomEditTextListener myCustomEditTextListener) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            this.myCustomEditTextListener = myCustomEditTextListener;
            editText.setInputType(InputType.TYPE_CLASS_TEXT);
            editText.setOnFocusChangeListener(myCustomEditTextListener);
        }
    }

    public class DisableTypeViewHolder extends SectioningAdapter.ItemViewHolder {
        @BindView(R.id.add_detail_edittext)
        MyFloatingEditText editText;
        @BindView(R.id.add_cardview)
        CardView cardView;
        public DisableTypeViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        }
    }

    public class TwoButtonViewHolder extends SectioningAdapter.ItemViewHolder {
        @BindView(R.id.add_leftbtn)
        Button leftBtn;
        @BindView(R.id.add_rightbtn)
        Button rightBtn;

        public TwoButtonViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public class TwoColumnViewHolder extends SectioningAdapter.ItemViewHolder {
        @BindView(R.id.left_edittext)
        MyFloatingEditText leftEditText;
        @BindView(R.id.right_edittext)
        MyFloatingEditText rightEditText;

        public TwoColumnViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public class LeftDetailRightInputViewHolder extends SectioningAdapter.ItemViewHolder {
        @BindView(R.id.left_edittext)
        MyFloatingEditText leftEditText;
        @BindView(R.id.right_edittext)
        MyFloatingEditText rightEditText;
        public MyCustomEditTextListener myCustomEditTextListener;
        public LeftDetailRightInputViewHolder(View itemView, MyCustomEditTextListener myCustomEditTextListener) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            this.myCustomEditTextListener = myCustomEditTextListener;
            rightEditText.setOnFocusChangeListener(myCustomEditTextListener);
        }
    }

    public class LeftInputRightDetailViewHolder extends SectioningAdapter.ItemViewHolder {
        @BindView(R.id.left_edittext)
        MyFloatingEditText leftEditText;
        @BindView(R.id.right_edittext)
        MyFloatingEditText rightEditText;
        public MyCustomEditTextListener myCustomEditTextListener;
        public LeftInputRightDetailViewHolder(View itemView, MyCustomEditTextListener myCustomEditTextListener) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            this.myCustomEditTextListener = myCustomEditTextListener;
            leftEditText.setOnFocusChangeListener(myCustomEditTextListener);
        }
    }

    public class TwoColumnDetailViewHolder extends SectioningAdapter.ItemViewHolder {
        @BindView(R.id.left_edittext)
        MyFloatingEditText leftEditText;
        @BindView(R.id.right_edittext)
        MyFloatingEditText rightEditText;
        public TwoColumnDetailViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public class OneButtonViewHolder extends SectioningAdapter.ItemViewHolder {
        @BindView(R.id.last_btn)
        Button button;

        public OneButtonViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    @Override
    public int getNumberOfSections() {

        return model.items.size();
    }

    @Override
    public int getNumberOfItemsInSection(int sectionIndex) {
        return model.items.get(sectionIndex).items.size();
    }

    @Override
    public boolean doesSectionHaveHeader(int sectionIndex) {
        if (titles.get(sectionIndex).trim().length() == 0) {
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
        return model.items.get(sectionIndex).items.get(itemIndex).viewType;
    }

    @Override
    public ItemViewHolder onCreateItemViewHolder(ViewGroup parent, int itemUserType) {
        ItemViewHolder itemViewHolder = null;
        View itemView = null;
        if (itemUserType == INPUT_TYPE) {
            itemView = LayoutInflater.
                    from(parent.getContext()).
                    inflate(R.layout.cardview_input, parent, false);
            itemViewHolder =  new InputTypeViewHolder(itemView, new MyCustomEditTextListener());
        } else if (itemUserType == DISABLE_TYPE) {
            itemView = LayoutInflater.
                    from(parent.getContext()).
                    inflate(R.layout.cardview_disable_input, parent, false);
            itemViewHolder =  new DisableTypeViewHolder(itemView);
        }  else if (itemUserType == GENERAL_TYPE) {
            itemView = LayoutInflater.
                    from(parent.getContext()).
                    inflate(R.layout.cardview_add_detail, parent, false);
            itemViewHolder = new GeneralTypeViewHolder(itemView);
        } else if (itemUserType == DIConstants.TWO_COLUMN_TYPE) {
            itemView = LayoutInflater.
                    from(parent.getContext()).
                    inflate(R.layout.cardview_two_column_input, parent, false);
            itemViewHolder = new TwoColumnViewHolder(itemView);
        } else if (itemUserType == TWO_COLUMN_LEFT_DETAIL_RIGHT_INPUT_TYPE){
            itemView = LayoutInflater.
                    from(parent.getContext()).
                    inflate(R.layout.cardview_left_detail_right_input, parent, false);
            itemViewHolder = new LeftDetailRightInputViewHolder(itemView, new MyCustomEditTextListener());
        } else if (itemUserType == TWO_COLUMN_DETAIL_TYPE) {
            itemView = LayoutInflater.
                    from(parent.getContext()).
                    inflate(R.layout.cardview_two_column_detail, parent, false);
            itemViewHolder = new TwoColumnDetailViewHolder(itemView);
        } else if (itemUserType == TWO_COLUMN_LEFT_INPUT_RIGHT_DETAIL_TYPE) {
            itemView = LayoutInflater.
                    from(parent.getContext()).
                    inflate(R.layout.cardview_left_input_right_detail, parent, false);
            itemViewHolder = new LeftInputRightDetailViewHolder(itemView, new MyCustomEditTextListener());
        } else if (itemUserType == ONE_BUTTON_TYPE) {
            itemView = LayoutInflater.
                    from(parent.getContext()).
                    inflate(R.layout.cardview_add_last_onebutton, parent, false);

            itemViewHolder =  new OneButtonViewHolder(itemView);
        } else if (itemUserType == TWO_BUTTON_TYPE) {
            itemView = LayoutInflater.
                    from(parent.getContext()).
                    inflate(R.layout.cardview_twobutton, parent, false);

            itemViewHolder = new TwoButtonViewHolder(itemView);
        } else if (itemUserType == SELECTION_TYPE) {
            itemView = LayoutInflater.
                    from(parent.getContext()).
                    inflate(R.layout.cardview_add_detail, parent, false);

            itemViewHolder = new SelectionTypeViewHolder(itemView);
        }

        return itemViewHolder;
    }

    @Override
    public void onBindItemViewHolder(ItemViewHolder viewHolder, int sectionIndex, int itemIndex, int itemType) {
        if (itemType == INPUT_TYPE) {
            displayInput((InputTypeViewHolder)viewHolder, sectionIndex, itemIndex);
        } else if (itemType == DISABLE_TYPE) {
            displayDisableInput((DisableTypeViewHolder) viewHolder, sectionIndex, itemIndex);
        } else if (itemType == GENERAL_TYPE) {
            displayGeneral((GeneralTypeViewHolder)viewHolder, sectionIndex, itemIndex);
        } else if (itemType == SELECTION_TYPE) {
            displaySelection((SelectionTypeViewHolder) viewHolder, sectionIndex, itemIndex);
        } else if (itemType == TWO_COLUMN_TYPE){
            displayTwoColumnInput((TwoColumnViewHolder)viewHolder, sectionIndex, itemIndex);
        } else if (itemType == TWO_COLUMN_LEFT_DETAIL_RIGHT_INPUT_TYPE) {
            displayLeftDetailRightInput((LeftDetailRightInputViewHolder)viewHolder, sectionIndex, itemIndex);
        } else if (itemType == TWO_COLUMN_LEFT_INPUT_RIGHT_DETAIL_TYPE) {
            displayLeftInputRightDetail((LeftInputRightDetailViewHolder)viewHolder, sectionIndex, itemIndex);
        } else if (itemType == TWO_COLUMN_DETAIL_TYPE) {
            displayTwoColumnDetail((TwoColumnDetailViewHolder)viewHolder, sectionIndex, itemIndex);
        } else if (itemType == ONE_BUTTON_TYPE) {
            displayOneButton((OneButtonViewHolder) viewHolder, sectionIndex, itemIndex);
        } else if (itemType == TWO_BUTTON_TYPE) {
            displayTwoButton((TwoButtonViewHolder) viewHolder, sectionIndex, itemIndex);
        }
    }

    @Override
    public HeaderViewHolder onCreateHeaderViewHolder(ViewGroup parent, int headerType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.cardview_search_section_header_link, parent, false);
        return new HeaderViewHolder(v);
    }

    @Override
    public void onBindHeaderViewHolder(SectioningAdapter.HeaderViewHolder viewHolder, int sectionIndex, int headerType) {
        HeaderViewHolder headerViewHolder = (HeaderViewHolder) viewHolder;
        headerViewHolder.firstTitle.setText(titles.get(sectionIndex));
    }

    private void displayOneButton(OneButtonViewHolder viewHolder, final int sectionIndex, final int itemIndex) {
        final LabelValueDetail labelValueDetail = model.items.get(sectionIndex).items.get(itemIndex);

        viewHolder.button.setText(labelValueDetail.label);
        viewHolder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.onClick(v, sectionIndex, itemIndex, labelValueDetail.label);
            }
        });
    }

    private void displayTwoButton(TwoButtonViewHolder viewHolder, final int sectionIndex, final int itemIndex) {
        final LabelValueDetail labelValueDetail = model.items.get(sectionIndex).items.get(itemIndex);

        viewHolder.leftBtn.setText(labelValueDetail.leftView.label);
        viewHolder.leftBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.onClick(v, sectionIndex, itemIndex, labelValueDetail.leftView.label);
            }
        });

        viewHolder.rightBtn.setText(labelValueDetail.rightView.label);
        viewHolder.rightBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.onClick(v, sectionIndex, itemIndex, labelValueDetail.rightView.label);
            }
        });
    }

    protected void displayLeftDetailRightInput(final LeftDetailRightInputViewHolder viewHolder, final int sectionIndex, final int itemIndex) {
        viewHolder.myCustomEditTextListener.updatePosition(sectionIndex, itemIndex);
        final LabelValueDetail labelValueDetail = model.items.get(sectionIndex).items.get(itemIndex);
        viewHolder.leftEditText.setText(labelValueDetail.leftView.value);
        viewHolder.leftEditText.setHint(labelValueDetail.leftView.label);
        viewHolder.leftEditText.setFloatingLabelText(labelValueDetail.leftView.label);
        viewHolder.leftEditText.setFocusable(false);

        // Specially enable/disable fields
        viewHolder.leftEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.onClick(v, sectionIndex, itemIndex, labelValueDetail.leftView.label);
            }
        });

        viewHolder.rightEditText.setText(labelValueDetail.rightView.value);
        viewHolder.rightEditText.setHint(labelValueDetail.rightView.label);
        viewHolder.rightEditText.setFloatingLabelText(labelValueDetail.rightView.label);
        viewHolder.rightEditText.setInputType(labelValueDetail.rightView.inputType);
    }

    protected void displayLeftInputRightDetail(final LeftInputRightDetailViewHolder viewHolder, final int sectionIndex, final int itemIndex) {
        viewHolder.myCustomEditTextListener.updatePosition(sectionIndex, itemIndex);
        final LabelValueDetail labelValueDetail = model.items.get(sectionIndex).items.get(itemIndex);
        viewHolder.leftEditText.setText(labelValueDetail.leftView.value);
        viewHolder.leftEditText.setHint(labelValueDetail.leftView.label);
        viewHolder.leftEditText.setFloatingLabelText(labelValueDetail.leftView.label);
        viewHolder.leftEditText.setInputType(labelValueDetail.leftView.inputType);

        viewHolder.rightEditText.setText(labelValueDetail.rightView.value);
        viewHolder.rightEditText.setHint(labelValueDetail.rightView.label);
        viewHolder.rightEditText.setFloatingLabelText(labelValueDetail.rightView.label);
        viewHolder.rightEditText.setFocusable(false);
        // Specially enable/disable fields
        viewHolder.rightEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.onClick(v, sectionIndex, itemIndex, labelValueDetail.rightView.label);
            }
        });
    }

    private void displayTwoColumnDetail(final TwoColumnDetailViewHolder viewHolder, final int sectionIndex, final int itemIndex) {
        final LabelValueDetail labelValueDetail = model.items.get(sectionIndex).items.get(itemIndex);
        viewHolder.leftEditText.setText(labelValueDetail.leftView.value);
        viewHolder.leftEditText.setHint(labelValueDetail.leftView.label);
        viewHolder.leftEditText.setFloatingLabelText(labelValueDetail.leftView.label);
        viewHolder.leftEditText.setFocusable(false);

        // Specially enable/disable fields
        viewHolder.leftEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.onClick(v, sectionIndex, itemIndex, labelValueDetail.leftView.label);
            }
        });

        viewHolder.rightEditText.setText(labelValueDetail.rightView.value);
        viewHolder.rightEditText.setHint(labelValueDetail.rightView.label);
        viewHolder.rightEditText.setFloatingLabelText(labelValueDetail.rightView.label);
        viewHolder.rightEditText.setFocusable(false);

        // Specially enable/disable fields
        viewHolder.rightEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.onClick(v, sectionIndex, itemIndex, labelValueDetail.rightView.label);
            }
        });
    }

    protected void displayTwoColumnInput(final TwoColumnViewHolder viewHolder, final int sectionIndex, final int itemIndex) {
        final LabelValueDetail labelValueDetail = model.items.get(sectionIndex).items.get(itemIndex);
        viewHolder.leftEditText.setText(labelValueDetail.leftView.value);
        viewHolder.leftEditText.setHint(labelValueDetail.leftView.label);
        viewHolder.leftEditText.setFloatingLabelText(labelValueDetail.leftView.label);
        viewHolder.leftEditText.setFocusable(false);
        viewHolder.leftEditText.setInputType(labelValueDetail.leftView.inputType);

        // Specially enable/disable fields
        viewHolder.leftEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.onClick(v, sectionIndex, itemIndex, labelValueDetail.leftView.label);
            }
        });

        viewHolder.rightEditText.setText(labelValueDetail.rightView.value);
        viewHolder.rightEditText.setHint(labelValueDetail.rightView.label);
        viewHolder.rightEditText.setFloatingLabelText(labelValueDetail.rightView.label);
        viewHolder.rightEditText.setInputType(labelValueDetail.rightView.inputType);
        viewHolder.rightEditText.setFocusable(false);

        // Specially enable/disable fields
        viewHolder.rightEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.onClick(v, sectionIndex, itemIndex, labelValueDetail.rightView.label);
            }
        });
    }

    protected void displayInput(final InputTypeViewHolder viewHolder, final int sectionIndex, int itemIndex) {
        viewHolder.myCustomEditTextListener.updatePosition(sectionIndex, itemIndex);
        final LabelValueDetail labelValueDetail = model.items.get(sectionIndex).items.get(itemIndex);
        Log.d("Log inputtype", labelValueDetail.label + " -- " + labelValueDetail.value);
        viewHolder.editText.setText(labelValueDetail.value);
        viewHolder.editText.setHint(labelValueDetail.label);
        viewHolder.editText.setFloatingLabelText(labelValueDetail.label);
        viewHolder.editText.setInputType(labelValueDetail.inputType);
    }

    protected void displayDisableInput(final DisableTypeViewHolder viewHolder, final int sectionIndex, int itemIndex) {
        final LabelValueDetail labelValueDetail = model.items.get(sectionIndex).items.get(itemIndex);
        Log.d("Log inputtype", labelValueDetail.label + " -- " + labelValueDetail.value);
        viewHolder.editText.setText(labelValueDetail.value);
        viewHolder.editText.setHint(labelValueDetail.label);
        viewHolder.editText.setFloatingLabelText(labelValueDetail.label);
        viewHolder.editText.setInputType(labelValueDetail.inputType);
        viewHolder.editText.setFocusable(false);
    }

    protected void displayGeneral(final GeneralTypeViewHolder viewHolder, final int sectionIndex, final int itemIndex) {

        final LabelValueDetail labelValueDetail = model.items.get(sectionIndex).items.get(itemIndex);
        viewHolder.editText.setText(labelValueDetail.value);
        viewHolder.editText.setHint(labelValueDetail.label);
        viewHolder.editText.setFloatingLabelText(labelValueDetail.label);
        viewHolder.editText.setFocusable(false);
        if (labelValueDetail.hasDetail) {
            viewHolder.detailBtn.setVisibility(View.VISIBLE);
            // Specially enable/disable fields
            viewHolder.editText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemClickListener.onClick(v, sectionIndex, itemIndex, labelValueDetail.label);
                }
            });
        } else {
            viewHolder.detailBtn.setVisibility(View.INVISIBLE);
        }
    }

    protected void displaySelection(final SelectionTypeViewHolder viewHolder, final int sectionIndex, final int itemIndex) {

        final LabelValueDetail labelValueDetail = model.items.get(sectionIndex).items.get(itemIndex);
        viewHolder.editText.setText(labelValueDetail.value);
        viewHolder.editText.setHint(labelValueDetail.label);
        viewHolder.editText.setFloatingLabelText(labelValueDetail.label);
        viewHolder.editText.setFocusable(false);

        viewHolder.detailBtn.setVisibility(View.INVISIBLE);

        // Specially enable/disable fields
        viewHolder.editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.onClick(v, sectionIndex, itemIndex, "Selection");
            }
        });
    }

    public int getViewType(int sectionIndex, int itemIndex) {
        return model.items.get(sectionIndex).items.get(itemIndex).viewType;
    }

    public String getValue(int sectionIndex, int itemIndex) {
        return model.items.get(sectionIndex).items.get(itemIndex).value;
    }

    public String getCode(int sectionIndex, int itemIndex) {
        return model.items.get(sectionIndex).items.get(itemIndex).code;
    }

    public String getLeftValue(int sectionIndex, int itemIndex) {
        return model.items.get(sectionIndex).items.get(itemIndex).leftView.value;
    }

    public String getRightValue(int sectionIndex, int itemIndex) {
        return model.items.get(sectionIndex).items.get(itemIndex).rightView.value;
    }

    public void updateDataFromInput(String input, int sectionIndex, int itemIndex) {
        input = input.trim();
        if (input.trim().length() == 0) {
            return;
        }
        input = input.substring(0, 1).toUpperCase() + input.substring(1);
        model.items.get(sectionIndex).items.get(itemIndex).value = input;
    }

    public void updateLeftRightInput(String input, int sectionIndex, int itemIndex, int twoColumn) {
    }

    public void updateDataAndRefresh(String data, int sectionIndex, int itemIndex) {
        updateData(data, sectionIndex, itemIndex);
        notifySectionItemChanged(sectionIndex, itemIndex);
    }

    public void updateData(String date, int sectionIndex, int itemIndex) {
        model.items.get(sectionIndex).items.get(itemIndex).value = date;
    }

    public void updateLabel(String label, int sectionIndex, int itemIndex) {
        model.items.get(sectionIndex).items.get(itemIndex).label = label;
        notifySectionItemChanged(sectionIndex, itemIndex);
    }

    public void updateCodeDescData(CodeDescription codeDescription, int sectionIndex, int itemIndex) {
        updateCodeDescDataWithoutRefresh(codeDescription, sectionIndex, itemIndex);
        notifySectionItemChanged(sectionIndex, itemIndex);
    }

    public void updateCodeDescDataWithoutRefresh(CodeDescription codeDescription, int sectionIndex, int itemIndex) {
        model.items.get(sectionIndex).items.get(itemIndex).value = codeDescription.description;
        model.items.get(sectionIndex).items.get(itemIndex).code = codeDescription.code;
    }

    public void updateItem(LabelValueDetail labelValueDetail, int sectionIndex, int itemIndex) {
        model.items.get(sectionIndex).items.set(itemIndex, labelValueDetail);
        notifySectionItemChanged(sectionIndex, itemIndex);
    }

    protected class MyCustomEditTextListener implements View.OnFocusChangeListener {
        protected int sectionIndex, itemIndex;

        public void updatePosition(int sectionIndex, int itemIndex) {
            this.sectionIndex = sectionIndex;
            this.itemIndex = itemIndex;
        }

        @Override
        public void onFocusChange(View v, final boolean hasFocus) {
            updateDataFromInput(((EditText)v).getText().toString(), sectionIndex, itemIndex);
            KeyboardUtils.hideKeyboard(v);

            android.os.Handler handler = new android.os.Handler();
            handler.postDelayed(new Runnable() {
                public void run(){
                    //change adapter contents
                    if (!hasFocus) {
                        notifySectionItemChanged(sectionIndex, itemIndex);
                    }
                }
            }, 300);
        }
    }
}
