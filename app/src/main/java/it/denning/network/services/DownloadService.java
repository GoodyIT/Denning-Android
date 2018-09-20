package it.denning.network.services;


import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import io.reactivex.disposables.CompositeDisposable;
import it.denning.general.DIFileManager;
import it.denning.general.DIHelper;
import it.denning.network.RetrofitHelper;
import it.denning.network.utils.Download;
import it.denning.network.utils.DownloadCompleteInterface;
import it.denning.network.utils.ProgressInterface;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DownloadService  {
    private   String url;
    private  String fileName;
    private  Context mContext;
    private  ProgressInterface progressInterface;
    private  DownloadCompleteInterface downloadCompleteInterface;

    private int totalFileSize;
    private File outputFile;

    public DownloadService() {

    }

    public DownloadService(Context mContext, String url, ProgressInterface progressInterface, DownloadCompleteInterface downloadCompleteInterface) {
        this.url = url;
        this.fileName = "";
        this.mContext = mContext;
        this.progressInterface = progressInterface;
        this.downloadCompleteInterface = downloadCompleteInterface;
    }

    public DownloadService(Context mContext, String url, String fileName, ProgressInterface progressInterface, DownloadCompleteInterface downloadCompleteInterface) {
        this.url = url;
        this.fileName = fileName;
        this.mContext = mContext;
        this.progressInterface = progressInterface;
        this.downloadCompleteInterface = downloadCompleteInterface;
    }

        public void initDownload(){

        DenningService mService = new RetrofitHelper(mContext).getPrivateService();
        CompositeDisposable mCompositeDisposable = new CompositeDisposable();
        mService.downloadFile(url).enqueue(new Callback<ResponseBody>() {
           public static final String TAG = "";

           @Override
            public void onResponse(Call<ResponseBody> call, final Response<ResponseBody> response) {
                Log.d(TAG, response.message());
                if(!response.isSuccessful()){
                    Log.e(TAG, "Something's gone wrong");
                    // TODO: show error message
                    downloadCompleteInterface.onFailure(response.message());
                    return;
                }
               new AsyncTask<Void, Long, Void>() {
                   @Override
                   protected Void doInBackground(Void... voids) {
                       try {
                           downloadFile(response.body());
                       } catch (IOException e) {
                           e.printStackTrace();
                       }
                       return null;
                   }
               }.execute();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e(TAG, t.getMessage());
                // TODO: show error message
            }
        });
    }

    private void downloadFile(ResponseBody body) throws IOException {

        int count;
        byte data[] = new byte[1024 * 4];
        long fileSize = body.contentLength();
        InputStream bis = new BufferedInputStream(body.byteStream(), 1024 * 8);
        File outputDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + DIFileManager.folderName);
        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }
        if (fileName.isEmpty()) {
            outputFile = new File(outputDir, DIHelper.getFileName(url));
        } else {
            outputFile = new File(outputDir, fileName);
        }

        OutputStream output = new FileOutputStream(outputFile);
        long total = 0;
        long startTime = System.currentTimeMillis();
        int timeCount = 1;
        while ((count = bis.read(data)) != -1) {

            total += count;
            totalFileSize = (int) (fileSize / (Math.pow(1024, 2)));
            double current = Math.round(total / (Math.pow(1024, 2)));

            int progress = (int) ((total * 100) / fileSize);

            long currentTime = System.currentTimeMillis() - startTime;

            Download download = new Download();
            download.setTotalFileSize(totalFileSize);

            if (currentTime > 1000 * timeCount) {

                download.setCurrentFileSize((int) current);
                download.setProgress(progress);
//                downloadProgress(download);
                timeCount++;
            }

            output.write(data, 0, count);
        }

        output.flush();
        output.close();
        bis.close();
        onDownloadComplete();
    }

    private void downloadProgress(Download download) {
        progressInterface.onProgress(download);
    }

    private void onDownloadComplete(){
        downloadCompleteInterface.onComplete(outputFile);
    }

}