package it.denning.model;

/**
 * Created by denningit on 2017-12-01.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SearchKeyword {

    @SerializedName("keyword")
    @Expose
    private String keyword;
    @SerializedName("score")
    @Expose
    private String score;

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

}