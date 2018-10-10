package it.denning.model;

import java.io.Serializable;
import java.util.List;

public class MenuModel implements Serializable {
    public String android_icon;
    public String ios_icon;
    public List<MenuModel> items;
    public String openForm;
    public String title;
}
