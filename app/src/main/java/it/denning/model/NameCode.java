package it.denning.model;

import java.io.Serializable;

/**
 * Created by denningit on 2017-12-02.
 */

public class NameCode implements Serializable{
    public String name;
    public String code;

    public NameCode(String name, String code) {
        this.name = name;
        this.code = code;
    }

    public NameCode() {

    }
}
