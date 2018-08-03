package it.denning.search.utils;

import android.support.v7.util.DiffUtil;

import java.util.List;

import it.denning.model.SearchResultModel;

/**
 * Created by denningit on 24/04/2017.
 */

public class SearchResultDiffCallback extends DiffUtil.Callback {
    private List<SearchResultModel> mOldList;
    private List<SearchResultModel> mNewList;

    public SearchResultDiffCallback(List<SearchResultModel> mNewList, List<SearchResultModel> mOldList) {
        this.mNewList = mNewList;
        this.mOldList = mOldList;
    }

    @Override
    public int getOldListSize() {
        return this.mOldList.size();
    }

    @Override
    public int getNewListSize() {
        return this.mNewList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return false;
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return false;
    }
}
