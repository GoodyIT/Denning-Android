package it.denning.model;

import android.text.InputType;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by denningit on 2018-01-16.
 */

public class AddSectionItemModel {
    public List<LabelValueDetail> items = new ArrayList<>();

    public AddSectionItemModel() {

    }

    public AddSectionItemModel(String[] labels, String[] values, boolean[] details) {
        for (int i = 0; i < labels.length; i++) {
            LabelValueDetail model = new LabelValueDetail();
            model.value = values[i];
            model.label = labels[i];
            model.hasDetail = details[i];
            model.isInput = !model.hasDetail;
            model.inputType = InputType.TYPE_CLASS_TEXT;
        }
    }

    public AddSectionItemModel(String[] labels, boolean[] details) {
        for (int i = 0; i < labels.length; i++) {
            LabelValueDetail model = new LabelValueDetail();
            model.label = labels[i];
            model.hasDetail = details[i];
            model.value = "";
            model.code = "";
            model.isInput = !model.hasDetail;
            model.inputType = InputType.TYPE_CLASS_TEXT;
            items.add(model);
        }
    }
}
