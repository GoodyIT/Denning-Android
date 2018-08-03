package it.denning.ui.fragments.base;

import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;

public abstract class BaseLoaderFragment<T> extends BaseFragment implements LoaderManager.LoaderCallbacks<T> {

    public static final int PICK_DIALOG = 100;
    public static final int CREATE_DIALOG = 200;

    protected Loader<T> loader;

    protected void initDataLoader(int id) {
        getLoaderManager().initLoader(id, null, this);
    }

    protected abstract Loader<T> createDataLoader();

    @Override
    public Loader<T> onCreateLoader(int id, Bundle args) {
        loader = createDataLoader();
        return loader;
    }

    @Override
    public void onLoaderReset(Loader<T> loader) {
        // nothing by default.
    }

    protected void onChangedData() {
        loader.onContentChanged();
    }
}