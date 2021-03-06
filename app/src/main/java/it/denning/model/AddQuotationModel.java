package it.denning.model;

import java.util.ArrayList;
import java.util.List;

import it.denning.general.DIConstants;

/**
 * Created by denningit on 2018-01-16.
 */

public class AddQuotationModel {
    public List<AddSectionItemModel> items = new ArrayList<>();

    public static AddQuotationModel build() {
        AddQuotationModel model = new AddQuotationModel();

        model.items.add(new AddSectionItemModel(DIConstants.quotation_detail_labels, DIConstants.quotation_detail_has_details));
        model.items.add(new AddSectionItemModel(DIConstants.quotation_analysis_labels, DIConstants.quotation_analysis_has_details));

        return model;
    }
}
