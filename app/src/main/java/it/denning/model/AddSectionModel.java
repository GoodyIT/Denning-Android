package it.denning.model;

import java.util.ArrayList;
import java.util.List;

import it.denning.R;

/**
 * Created by denningit on 2018-01-15.
 */

public class AddSectionModel {
    public List<LabelIconModel> items = new ArrayList<>();

    public AddSectionModel(String[] labels, int[] images) {
        for (int i = 0; i < labels.length; i++) {
            LabelIconModel labelIconModel = new LabelIconModel();
            labelIconModel.image = images[i];
            labelIconModel.label = labels[i];
            items.add(labelIconModel);
        }
    }
}
