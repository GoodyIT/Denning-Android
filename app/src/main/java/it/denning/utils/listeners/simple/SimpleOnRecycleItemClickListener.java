package it.denning.utils.listeners.simple;

import android.view.View;

import it.denning.utils.listeners.OnRecycleItemClickListener;

public class SimpleOnRecycleItemClickListener<T> implements OnRecycleItemClickListener<T> {

    @Override
    public void onItemClicked(View view, T entity, int position) {
    }

    @Override
    public void onItemLongClicked(View view, T entity, int position) {
    }
}