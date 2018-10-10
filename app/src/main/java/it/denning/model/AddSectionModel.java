package it.denning.model;

import java.util.ArrayList;
import java.util.List;

import it.denning.R;

/**
 * Created by denningit on 2018-01-15.
 */

public class AddSectionModel {
    public List<LabelIconOpenFormModel> items = new ArrayList<>();

    public AddSectionModel(ArrayList<String> labels, ArrayList<Integer> images, ArrayList<String> openForms) {
        for (int i = 0; i < labels.size(); i++) {
            LabelIconOpenFormModel labelIconModel = new LabelIconOpenFormModel();
            labelIconModel.image = images.get(i);
            labelIconModel.label = labels.get(i);
            labelIconModel.openForm = openForms.get(i);
            items.add(labelIconModel);
        }
    }
}
