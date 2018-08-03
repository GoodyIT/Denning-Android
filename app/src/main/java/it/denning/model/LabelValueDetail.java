package it.denning.model;

import android.text.InputType;

import it.denning.general.DIConstants;

/**
 * Created by denningit on 2018-01-16.
 */

public class LabelValueDetail {
    public String label = "";
    public String value = "";
    public String otherValue1 = "";
    public String fieldName = "";
    public String formula = "";
    public String code = "";
    public boolean hasDetail = true;
    public boolean isInput = true;
    public boolean isInputEnabled = true;
    public boolean isAdd = false;
    public boolean isOneRowAdd = false;
    public boolean isOneRowDetail = false;
    public boolean is3Rows = false;
    public int childCnt = 0;
    public int inputType = InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES;
    public int viewType = DIConstants.GENERAL_TYPE;

    public LabelValueDetail leftView;
    public LabelValueDetail rightView;

    public boolean isEmptyCode() {
        if (code != null && code.trim().length() != 0) {
            return false;
        }
        return true;
    }

    public LabelValueDetail() {
    }

    public LabelValueDetail(String label, String value, String code, boolean hasDetail, boolean isInput, int inputType) {
        this.label = label;
        this.value = value;
        this.code = code;
        this.hasDetail = hasDetail;
        this.isInput = isInput;
        this.isAdd = false;
        this.childCnt = 0;
        this.inputType = inputType;
    }

    public LabelValueDetail(String label, String value, String code, int viewType) {
        this.label = label;
        this.value = value;
        this.code = code;
        this.viewType = viewType;
    }

    public LabelValueDetail(String label, String value, int viewType) {
        this.label = label;
        this.value = value;
        this.code = code;
        this.viewType = viewType;
    }

    public LabelValueDetail(String label, String value, String code, boolean hasDetail) {
        this.label = label;
        this.value = value;
        this.code = code;
        this.hasDetail = hasDetail;
        this.isInput = !hasDetail;
        this.isAdd = false;
        this.childCnt = 0;
    }

    public LabelValueDetail(String label, String value, boolean hasDetail) {
        this.label = label;
        this.value = value;
        this.hasDetail = hasDetail;
        this.code = "";
        this.isInput = !hasDetail;
        this.isAdd = false;
        this.childCnt = 0;
        if (!hasDetail) {
            this.viewType = DIConstants.GENERAL_TYPE;
        } else {
            this.viewType = DIConstants.INPUT_TYPE;
        }
    }

    public LabelValueDetail(String value, String code) {
        this.code = code;
        this.value = value;
    }
}
