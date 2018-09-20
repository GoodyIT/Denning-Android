package it.denning.general;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.content.FileProvider;

import java.io.File;
import java.io.IOException;

import it.denning.App;
import it.denning.BuildConfig;
import it.denning.R;
import it.denning.utils.FileUtils;
import it.denning.utils.MediaUtils;

/**
 * Created by denningit on 2017-12-07.
 */

public class DIFileManager {
    public static String folderName = "/DenningIT/";
    private final Context mContext;


    public DIFileManager(Context mContext) {
        this.mContext = mContext;
    }

    public void openFile(String fileUrl) {
        if (fileUrl == null) {
            return;
        }
        try {
            openFile(new File(fileUrl));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void openFile(File url) throws IOException {
//        Uri uri = MediaUtils.getValidUri(url, mContext);
        Uri uri = Uri.fromFile(url);
        if (Build.VERSION.SDK_INT >= 24) {
            uri = FileProvider.getUriForFile(App.getInstance(), BuildConfig.APPLICATION_ID + ".provider", url);
        }

        Intent intent = new Intent(Intent.ACTION_VIEW);
        // Check what kind of file you are trying to open, by comparing the url with extensions.
        // When the if condition is matched, plugin sets the correct intent (mime) type,
        // so Android knew what application to use to open the file
        if (url.toString().contains(".doc") || url.toString().contains(".docx")) {
            // Word document
            intent.setDataAndType(uri, "application/msword");
        } else if(url.toString().contains(".pdf")) {
            // PDF file
            intent.setDataAndType(uri, "application/pdf");
        } else if(url.toString().contains(".ppt") || url.toString().contains(".pptx")) {
            // Powerpoint file
            intent.setDataAndType(uri, "application/vnd.ms-powerpoint");
        } else if(url.toString().contains(".xls") || url.toString().contains(".xlsx")) {
            // Excel file
            intent.setDataAndType(uri, "application/vnd.ms-excel");
        } else if(url.toString().contains(".zip") || url.toString().contains(".rar")) {
            // WAV audio file
            intent.setDataAndType(uri, "application/x-wav");
        } else if(url.toString().contains(".rtf")) {
            // RTF file
            intent.setDataAndType(uri, "application/rtf");
        } else if(url.toString().contains(".wav") || url.toString().contains(".mp3")) {
            // WAV audio file
            intent.setDataAndType(uri, "audio/x-wav");
        } else if(url.toString().contains(".gif")) {
            // GIF file
            intent.setDataAndType(uri, "image/gif");
        } else if(url.toString().contains(".jpg") || url.toString().contains(".jpeg") || url.toString().contains(".png")) {
            // JPG file
            intent.setDataAndType(uri, "image/jpeg");
        } else if(url.toString().contains(".txt")) {
            // Text file
            intent.setDataAndType(uri, "text/plain");
        } else if(url.toString().contains(".3gp") || url.toString().contains(".mpg") || url.toString().contains(".mpeg") || url.toString().contains(".mpe") || url.toString().contains(".mp4") || url.toString().contains(".avi")) {
            // Video files
            intent.setDataAndType(uri, "video/*");
        } else {
            //if you want you can also define the intent type for any other file

            //additionally use else clause below, to manage other unknown extensions
            //in this case, Android will show all applications installed on the device
            //so you can choose which application to use
            intent.setDataAndType(uri, "*/*");
        }

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        if (intent.resolveActivity(mContext.getPackageManager()) != null) {
            mContext.startActivity(intent);
        } else {
            String message = "Unable to open file To " + url.getName() + " You don't have an app installed on this device to open this type of file.";
            DIAlert.showSimpleAlert(mContext, R.string.alert_open_file_title, message);
        }
    }


    public static Boolean isFileExisting(String url) {
        File outputDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + DIFileManager.folderName);
        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }
        File outputFile = new File(outputDir, DIHelper.getFileName(url));
        return outputFile.exists();
    }
}
