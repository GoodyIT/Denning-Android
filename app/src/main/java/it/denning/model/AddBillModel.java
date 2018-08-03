package it.denning.model;

import java.util.ArrayList;
import java.util.List;

import it.denning.general.DIConstants;

/**
 * Created by denningit on 2018-01-16.
 */

public class AddBillModel {
    public List<AddSectionItemModel> items = new ArrayList<>();

    public static AddBillModel build() {
        AddBillModel model = new AddBillModel();

        model.items.add(new AddSectionItemModel(DIConstants.bill_detail_labels, DIConstants.bill_detail_has_details));
        model.items.add(new AddSectionItemModel(DIConstants.bill_analysis_labels, DIConstants.bill_analysis_has_details));

        return model;
    }
}
