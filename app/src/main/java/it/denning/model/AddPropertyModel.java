package it.denning.model;

import java.util.ArrayList;
import java.util.List;

import it.denning.general.DIConstants;

/**
 * Created by denningit on 2018-01-16.
 */

public class AddPropertyModel {
    public List<AddSectionItemModel> items = new ArrayList<>();

    public static AddPropertyModel build() {
        AddPropertyModel model = new AddPropertyModel();

        model.items.add(new AddSectionItemModel(DIConstants.contact_personal_info_labels, DIConstants.contact_personal_info_details));
        model.items.add(new AddSectionItemModel(DIConstants.contact_contact_info_labels, DIConstants.contact_contact_info_details));
        model.items.add(new AddSectionItemModel(DIConstants.contact_other_info_labels, DIConstants.contact_other_info_details));
        model.items.add(new AddSectionItemModel(DIConstants.contact_company_info_labels, DIConstants.contact_company_info_details));
        model.items.add(new AddSectionItemModel(DIConstants.contact_invitation_labels, DIConstants.contact_invitation_details));

        return model;
    }
}
