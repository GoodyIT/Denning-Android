package it.denning.network.utils;

import java.io.File;

/**
 * Created by denningit on 2017-12-08.
 */

public interface DownloadCompleteInterface {
    void onComplete(File file);
    void onFailure(String error);
}
