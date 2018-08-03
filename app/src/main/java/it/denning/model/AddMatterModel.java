package it.denning.model;

import java.util.ArrayList;
import java.util.List;

import it.denning.general.DIConstants;

/**
 * Created by denningit on 2018-01-16.
 */

public class AddMatterModel {
    public List<AddSectionItemModel> items = new ArrayList<>();

    public static AddMatterModel build() {
        AddMatterModel model = new AddMatterModel();

        model.items.add(new AddSectionItemModel(DIConstants.matter_information_labels, DIConstants.matter_information_details));

        return model;
    }
}
