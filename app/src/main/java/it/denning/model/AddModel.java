package it.denning.model;

import android.view.Menu;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import it.denning.App;
import it.denning.R;
import it.denning.general.DIConstants;

/**
 * Created by denningit on 2018-01-15.
 */

public class AddModel {
    public List<AddSectionModel> items = new ArrayList<>();


    public static AddModel build(MenuModel[] models) {
        AddModel model = new AddModel();

        for (MenuModel menuModel : models) {
            ArrayList<String> addLabels = new ArrayList<>();
            ArrayList<Integer> addImages = new ArrayList<>();
            ArrayList<String> addOpenForms = new ArrayList<>();
            if (menuModel != null && menuModel.items != null) {
                for (MenuModel item : menuModel.items) {
                    addLabels.add(item.title);
                    addImages.add(App.getInstance().getResources().getIdentifier(item.android_icon, "drawable", App.getInstance().getPackageName()));
                    addOpenForms.add(item.openForm);
                }
                model.items.add(new AddSectionModel(addLabels, addImages, addOpenForms));
            }
        }

//        model.items.add(new AddSectionModel(DIConstants.second_add_labels, DIConstants.second_add_images));
//        model.items.add(new AddSectionModel(DIConstants.third_add_labels, DIConstants.third_add_images));
//        model.items.add(new AddSectionModel(DIConstants.forth_add_labels, DIConstants.forth_add_images));

        return model;
    }
}
