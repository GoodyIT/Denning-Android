package it.denning.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.webkit.MimeTypeMap;

import com.quickblox.core.helper.MimeUtils;
import com.quickblox.q_municate_db.models.Attachment;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import it.denning.App;
import it.denning.R;

public class StringUtils {

    public static String createHumanNameFromSystemPermission(String permission){
        String permissionName = permission.replace("android.permission.", "");
        String[] words = permissionName.split("_", 0);
        String newPermissionName = "";
        for(String word : words){
            newPermissionName+= word.substring(0,1) + word.substring(1).toLowerCase() + " ";
        }

        return newPermissionName;
    }

    public static String createCompositeString(ArrayList<String> permissions){
        StringBuilder stringBuilder = new StringBuilder();

        for (String string : permissions){
            stringBuilder.append(createHumanNameFromSystemPermission(string));
            if (permissions.indexOf(string) == permissions.size() -2){
                stringBuilder.append(" and ");
            } else if (permissions.indexOf(string) == permissions.size() -1){
                stringBuilder.append("");
            } else {
                stringBuilder.append(", ");
            }
        }

        return stringBuilder.toString();
    }

    public static String getAttachmentNameByType(Context context, Attachment.Type type) {
        String attachmentName = "";

        switch (type) {
            case IMAGE:
                attachmentName = context.getString(R.string.dialog_attach_image);
                break;
            case AUDIO:
                attachmentName = context.getString(R.string.dialog_attach_audio);
                break;
            case VIDEO:
                attachmentName = context.getString(R.string.dialog_attach_video);
                break;
            case LOCATION:
                attachmentName = context.getString(R.string.dialog_location);
                break;
            //will be extend for new attachment types
        }

        return attachmentName;
    }

    public static Attachment.Type getAttachmentTypeByFile(File file) {
        return getAttachmentTypeByFileName(file.getName());
    }

    public static Attachment.Type getAttachmentTypeByFileName(String fileName){
        Attachment.Type attachmentType;
        String extension = fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length()).toLowerCase();
        String mimeType = MimeUtils.guessMimeTypeFromExtension(extension);
        if (mimeType == null){
            attachmentType = Attachment.Type.OTHER;
        } else if (mimeType.startsWith("image")){
            attachmentType = Attachment.Type.IMAGE;
        } else if (mimeType.startsWith("audio")){
            attachmentType = Attachment.Type.AUDIO;
        } else if (mimeType.startsWith("video")){
            attachmentType = Attachment.Type.VIDEO;
        } else if (mimeType.startsWith("text")) {
            attachmentType = Attachment.Type.DOC;
        } else if (mimeType.startsWith("application/msword") || mimeType.startsWith("application/vnd.openxmlformats") || mimeType.startsWith("application/pdf")) {
            attachmentType = Attachment.Type.FILE;
        } else {
            attachmentType = Attachment.Type.OTHER;
        }

        return attachmentType;
    }

    public static boolean isImageFile(File file) {
        return Attachment.Type.IMAGE.equals(StringUtils.getAttachmentTypeByFile(file));
    }

    public static String getMimeType(Uri uri) {
        String mimeType;
        if (uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
            ContentResolver cr = App.getInstance().getContentResolver();
            mimeType = cr.getType(uri);
        } else {
            String fileExtension = MimeTypeMap.getFileExtensionFromUrl(uri
                    .toString());
            mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(
                    fileExtension.toLowerCase());
        }
        return mimeType;
    }

    public static String getMimeType(String fileUrl)
    {
        String type = null;
        URL u = null;
        try {
            u = new URL(fileUrl);
            URLConnection  uc = u.openConnection();
            type = uc.getContentType();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return type;
    }

    public static int getAttachImage(String contentType) {
        int resId = -1;
        if (contentType == null) {
            resId =  R.drawable.icon_attach_file;
        } else if (contentType.startsWith("application/pdf")) {
            resId =  R.drawable.icon_attach_pdf;
        } else if (contentType.startsWith("application/msword") || contentType.startsWith("application/vnd.openxmlformats") ) {
            resId =  R.drawable.icon_attach_word;
        } else {
          resId =  R.drawable.icon_attach_file;
        }

        return resId;
    }

}