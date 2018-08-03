package it.denning.model;

import java.util.ArrayList;
import java.util.List;

import it.denning.R;
import it.denning.general.DIConstants;

/**
 * Created by denningit on 2018-01-15.
 */

public class AddModel {
    public List<AddSectionModel> items = new ArrayList<>();


    public static AddModel build() {
        AddModel model = new AddModel();

        model.items.add(new AddSectionModel(DIConstants.first_add_labels, DIConstants.first_add_images));
        model.items.add(new AddSectionModel(DIConstants.second_add_labels, DIConstants.second_add_images));
        model.items.add(new AddSectionModel(DIConstants.third_add_labels, DIConstants.third_add_images));

        return model;
    }
}
