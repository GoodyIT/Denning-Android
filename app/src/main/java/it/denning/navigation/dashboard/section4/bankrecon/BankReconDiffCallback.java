package it.denning.navigation.dashboard.section4.bankrecon;

import android.support.annotation.Nullable;
import android.support.v7.util.DiffUtil.Callback;

import java.util.List;

import it.denning.model.BankReconModel;
import it.denning.model.SearchResultModel;

/**
 * Created by hothongmee on 09/09/2017.
 */

public class BankReconDiffCallback extends Callback {
    private final List<BankReconModel> mOldList;
    private final List<BankReconModel> mNewList;

    public BankReconDiffCallback(List<BankReconModel> mOldList, List<BankReconModel> mNewList) {
        this.mOldList = mOldList;
        this.mNewList = mNewList;
    }

    @Override
    public int getOldListSize() {
        return mOldList.size();
    }

    @Override
    public int getNewListSize() {
        return mNewList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return false;
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return false;
    }

    @Nullable
    @Override
    public Object getChangePayload(int oldItemPosition, int newItemPosition) {
        // Implement method if you're going to use ItemAnimator
        return super.getChangePayload(oldItemPosition, newItemPosition);
    }
}
