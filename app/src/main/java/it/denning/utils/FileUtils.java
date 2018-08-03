package it.denning.utils;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.MimeTypeMap;

import com.quickblox.auth.session.QBSession;
import com.quickblox.auth.session.QBSessionManager;
import com.quickblox.chat.model.QBAttachment;
import com.quickblox.content.QBContent;
import com.quickblox.content.model.QBFile;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.QBProgressCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.q_municate_core.utils.ConstsCore;
import com.quickblox.q_municate_db.utils.ErrorUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import it.denning.App;
import it.denning.BuildConfig;
import it.denning.general.MySimpleCallback;


public class FileUtils {

    private static final String TAG = FileUtils.class.getSimpleName();
    private static final String folderName = "/Q-municate";
    private static final String fileType = ".jpg";
    public static final String AUTHORITY = BuildConfig.APPLICATION_ID + ".provider";

    private File filesFolder;

    public FileUtils() {
        initFilesFolder();
    }

    private void initFilesFolder() {
        filesFolder = new File(Environment.getExternalStorageDirectory() + folderName);
        if (!filesFolder.exists()) {
            filesFolder.mkdirs();
        }
    }

    public void checkExistsFile(String fileUrlString, Bitmap bitmap) {
        Log.d(TAG, "+++ fileUrlString = " + fileUrlString);
        if (!TextUtils.isEmpty(fileUrlString)) {
            File file = createFileIfNotExist(fileUrlString);
            if (!file.exists()) {
                saveFile(file, bitmap);
            }
        }
    }

    public static void saveAttachmentToFile(final QBAttachment attachment, final MySimpleCallback callback) {
        String fileId = attachment.getId();
        File parentDir = StorageUtil.getAppExternalDataDirectoryFile();

        final File resultFile = new File(parentDir, fileId);
        if (resultFile.exists()) {
            attachment.setUrl(resultFile.getAbsolutePath());
            callback.next(resultFile.getAbsolutePath());
        } else {

            QBContent.getFile(new QBFile(QBFile.getPrivateUrlForUID(attachment.getId()))).performAsync(new QBEntityCallback<QBFile>() {
                @Override
                public void onSuccess(QBFile qbFile, Bundle bundle) {
                    attachment.setUrl(qbFile.getPublicUrl());
                    callback.next(qbFile.getPublicUrl());
                }

                @Override
                public void onError(QBResponseException e) {

                }
            });
        }
    }

    private static void _returnSavedAttachmentFile(InputStream inputStream, FileOutputStream outputStream) {

        BufferedInputStream bis = new BufferedInputStream(inputStream);

        BufferedOutputStream bos = new BufferedOutputStream(outputStream);

        byte[] buf = new byte[2048];
        int length;
        try {
            while ((length = bis.read(buf)) > 0) {
                bos.write(buf, 0, length);
            }
            bis.close();
            bos.close();
        } catch (IOException e) {
            ErrorUtils.showError(App.getInstance(), e.getMessage());
        }


    }

    public File createFileIfNotExist(String fileUrlString) {
        Uri fileUri = Uri.parse(fileUrlString);
        String fileName = fileUri.getLastPathSegment() + fileType;
        return new File(filesFolder, fileName);
    }

    private void saveFile(File file, Bitmap bitmap) {
        // starting new Async Task
        new SavingFileTask().execute(file, bitmap);
    }

    private class SavingFileTask extends AsyncTask<Object, String, Object> {

        @Override
        protected Object doInBackground(Object... objects) {
            File file = (File) objects[0];
            Bitmap bitmap = (Bitmap) objects[1];

            FileOutputStream fileOutputStream;

            try {
                fileOutputStream = new FileOutputStream(file);

                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, ConstsCore.ZERO_INT_VALUE, byteArrayOutputStream);
                byte[] byteArray = byteArrayOutputStream.toByteArray();

                fileOutputStream.write(byteArray);

                fileOutputStream.flush();
                fileOutputStream.close();

                byteArrayOutputStream.flush();
                byteArrayOutputStream.close();

            } catch (FileNotFoundException e) {
                ErrorUtils.logError(e);
            } catch (IOException e) {
                ErrorUtils.logError(e);
            }

            return null;
        }
    }
}