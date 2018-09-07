package it.denning.general;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.text.format.Formatter;
import android.util.Base64;
import android.util.TypedValue;

import com.quickblox.auth.session.QBSession;
import com.quickblox.chat.model.QBChatDialog;
import com.quickblox.chat.model.QBDialogCustomData;
import com.quickblox.chat.model.QBDialogType;
import com.quickblox.q_municate_core.models.AppSession;
import com.quickblox.q_municate_user_service.QMUserService;
import com.quickblox.q_municate_user_service.model.QMUser;
import com.wdullaer.materialdatetimepicker.date.MonthAdapter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import it.denning.App;
import it.denning.MainActivity;
import it.denning.model.ChatFirmModel;

import static android.content.Context.WIFI_SERVICE;

/**
 * Created by denningit on 18/04/2017.
 */

public class DIHelper {
    public static Bitmap base64ToBitmap(String b64) {
        byte[] imageAsBytes = Base64.decode(b64.getBytes(), Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);
    }

    public static String bitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream .toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    public static String today() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        SimpleDateFormat mdformat = new SimpleDateFormat("dd MMM yyyy ");
        return mdformat.format(calendar.getTime());
    }

    public static String todayWithTime() {
        SimpleDateFormat mdformat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        return mdformat.format(new Date());
    }

    public static String todayF() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        SimpleDateFormat mdformat = new SimpleDateFormat("yyyy-MM-dd");
        return mdformat.format(calendar.getTime());
    }

    public static String sevenDaysAfter(String today) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date testDate = null;
        try {
            testDate = sdf.parse(today);
        }catch(Exception ex){
            ex.printStackTrace();
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(testDate);
        calendar.add(Calendar.DAY_OF_MONTH, 6);

        return sdf.format(calendar.getTime());
    }

    public static String toCustomDate(String date) {
        if (date.trim().length() == 0) {
            return date;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("dd MM yyyy");
        Date testDate = null;
        try {
            testDate = sdf.parse(date);
        }catch(Exception ex){
            ex.printStackTrace();
        }

        SimpleDateFormat newFormat = new SimpleDateFormat("dd MMM yyy");
        return  newFormat.format(testDate);
    }

    public static String toMySQLDateFormat(String date) {
        if (date.trim().length() == 0) {
            return date;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy");
        Date testDate = null;
        try {
            testDate = sdf.parse(date);
        }catch(Exception ex){
            ex.printStackTrace();
        }

        SimpleDateFormat newFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        return  newFormat.format(testDate);
    }

    public static String toMySQLDateFormat2(String date) {
        if (date.trim().length() == 0) {
            return date;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy hh:mm");
        Date testDate = null;
        try {
            testDate = sdf.parse(date);
        }catch(Exception ex){
            ex.printStackTrace();
        }

        SimpleDateFormat newFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        return  newFormat.format(testDate);
    }

    public static String toMySQLDateFormat(Calendar cal) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        return sdf.format(cal.getTime());
    }

    public static String toSimpleDateFormat(Calendar cal) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy");
        return sdf.format(cal.getTime());
    }

    public static String convertToSimpleDateFormat(String date) {
        if (date.trim().length() == 0) {
            return date;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        Date testDate = null;
        try {
            testDate = sdf.parse(date);
        }catch(Exception ex){
            ex.printStackTrace();
        }

        SimpleDateFormat newFormat = new SimpleDateFormat("d MMM yyy");
        return  newFormat.format(testDate);
    }

    public static String getOnlyDateFromDateTime(String dateTime) {
        if (dateTime == null || dateTime.trim().length() == 0) {
            return dateTime;
        }
        if (dateTime != null && dateTime.trim().length() == 0) return "";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        Date testDate = null;
        try {
            testDate = sdf.parse(dateTime);
        }catch(Exception ex){
            ex.printStackTrace();
        }

        SimpleDateFormat newFormat = new SimpleDateFormat("dd MMM yyyy");
        return newFormat.format(testDate);
    }

    public static String getLeaveAppDate(String dateTime) {
        if (dateTime == null || dateTime.trim().length() == 0) return "";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        Date testDate = null;
        try {
            testDate = sdf.parse(dateTime);
        }catch(Exception ex){
            ex.printStackTrace();
        }

        SimpleDateFormat newFormat = new SimpleDateFormat("dd MMM yyyy hh:mm");
        return newFormat.format(testDate);
    }

    public static String getTime(String date) {
        if (date == null || date.trim().length() == 0) {
            return date;
        }
        String newFormattedDate = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        Date testDate = null;
        try {
            testDate = sdf.parse(date);
        }catch(Exception ex){
            ex.printStackTrace();
        }

        SimpleDateFormat newFormat = new SimpleDateFormat("hh:mm");
        newFormattedDate = newFormat.format(testDate);
        return newFormattedDate.equals("00:00") ? "09:00" : newFormattedDate;
    }

    public static String getYear(Date date) {
        if (date == null) return "";
        return (String) DateFormat.format("yyyy", date);
    }

    public static String getMonth(Date date) {
        if (date == null) return "";
        return (String) DateFormat.format("MM",   date);
    }

    public static String getDate(Date date) {
        SimpleDateFormat newFormat = new SimpleDateFormat("yyyy-MM-dd");
        return newFormat.format(date);
    }

    public static String getDay(String date) {
        if (date == null || date.trim().length() == 0) return "";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        Date testDate = null;
        try {
            testDate = sdf.parse(date);
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return (String) DateFormat.format("EEEE", testDate);
    }

    public static String getDate(Calendar cal) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy hh:mm");
        return sdf.format(cal.getTime());
    }

    public static String getBirthday(MonthAdapter.CalendarDay selectedDay) {
        Calendar calendar= Calendar.getInstance();
        calendar.set(selectedDay.getYear(), selectedDay.getMonth(), selectedDay.getDay());
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy");
        return sdf.format(calendar.getTime());
    }

    public static String getTime(int hourOfDay, int minute) {
        Calendar calendar= Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        calendar.set(Calendar.MINUTE, minute);
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm");
        return sdf.format(calendar.getTime());
    }

    public static String getIPWAN() {
        {
            try
            {
                //Enumerate all the network interfaces
                for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();)
                {
                    NetworkInterface intf = en.nextElement();
                    // Make a loop on the number of IP addresses related to each Network Interface
                    for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();)
                    {
                        InetAddress inetAddress = enumIpAddr.nextElement();
                        //Check if the IP address is not a loopback address, in that case it is
                        //the IP address of your mobile device
                        if (!inetAddress.isLoopbackAddress())
                            return inetAddress.getHostAddress();
                    }
                }
            }
            catch (SocketException e)
            {
                e.printStackTrace();
            }
            return null;
        }
    }

    public static String getIPLAN() {

        return "test LAN";
    }

    public static String getOS() {
        return "Android";
    }

    public static String getDevice() {
        return Build.DEVICE;
    }

    public static String getDeviceName() {
        return Build.USER;
    }

    public static String getMAC(Context context) {
//        return Build.SERIAL;
//        WifiManager manager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
//        WifiInfo info = manager.getConnectionInfo();
//        return info.getMacAddress();
        return Settings.Secure.getString(App.getInstance().getContentResolver(),
                Settings.Secure.ANDROID_ID);
//        return UUID.randomUUID().toString();
    }

    // Safely parse the response - avoid NULL
    public static String getSafeString(String response) {
        if (response.equals("null")) {
            return "";
        }

        return response;
    }

    public static int determinSearchType(String formName) {
        int searchType = DIConstants.CONTACT_TYPE;

        if (formName.equals("200customer") ) // Contact
        {
            searchType = DIConstants.CONTACT_TYPE;
        } else if (formName.equals("500file")){ // Related Matter
            searchType = DIConstants.MATTER_TYPE;
        } else if ( formName.equals("800property")){ // MatterProperty
            searchType = DIConstants.PROPERTY_TYPE;
        } else if (formName.equals("400bankbranch")){ // Bank
            searchType = DIConstants.BANK_TYPE;
        } else if (formName.equals("310landoffice")  || formName.equals("310landregdist") ){ // Government Office
            searchType = DIConstants.GOVERNMENT_LAND_OFFICES;
        } else if (formName.equals("320PTG") ){ // Government Office
            searchType = DIConstants.GOVERNMENT_PTG_OFFICES;
        } else if ( formName.equals("300lawyer") ){ // Legal firm
            searchType = DIConstants.LEGAL_FIRM;
        } else if (formName.equals("950docfile") || formName.equals("900book") ){ // Document
            searchType = DIConstants.DOCUMENT_TYPE;
        } else {
            searchType = DIConstants.CONTACT_TYPE;
        }

        return searchType;
    }

    public static String[] removeFileNoFromMatterTitle(String title) {
        if (title.matches("\\w*") || title.length() < 9) {
            return new String[]{"", ""};
        }
        return DIHelper.separateNameIntoTwo(title.split(":")[1]);
    }

    public static boolean isDocFile(String ext) {
        String[] docExts = {".docx", ".doc", ".rtf", ".xls", "xlsx", "csv", ".txt"};
        if (Arrays.asList(docExts).contains(ext.toLowerCase())) {
            return true;
        }
        return false;
    }

    public static boolean isPDFFile(String ext) {
        String[] pdfExts = {".pdf"};
        if (Arrays.asList(pdfExts).contains(ext.toLowerCase())) {
            return true;
        }
        return false;
    }

    public static boolean isWebFile(String ext) {
        String[] webExts = {".url"};
        if (Arrays.asList(webExts).contains(ext.toLowerCase())) {
            return true;
        }
        return false;
    }

    public static boolean isImageFile(String ext) {
        String[] imageExts = {".png", ".tif", ".bmp", ".jpg", ".jpeg", ".gif"};
        if (Arrays.asList(imageExts).contains(ext.toLowerCase())) {
            return true;
        }
        return false;
    }

    public static String getFileName(String url) {
        int lastIndex = url.lastIndexOf('/');
        String fileName = url.substring(lastIndex+1);
        int idxForExt = fileName.lastIndexOf('.');
        String ext;
        String _fileName = "";
        if (idxForExt == -1) {
            _fileName = fileName;
            ext = "pdf";
        } else {
            fileName.substring(0, idxForExt);
            ext = fileName.substring(idxForExt+1);
        }
        return  _fileName + System.currentTimeMillis() + '.'  + ext;
    }

    public static String[] separateNameIntoTwo(String name) {
        String[] array = name.split("\\(");
        if (array.length > 1) {
            array[1] = array[1].substring(0, array[1].length()-1);
        } else {
            array = new String[]{array[0], ""};
        }
        return array;
    }

    public static void openFile(Context context, File url) throws IOException {
        // Create URI
        File file=url;
        Uri uri = Uri.fromFile(file);

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

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public static String toNumber(String value) {
        return value.matches("\\w*") ? "0" : value;
    }

    public static String getTag(QBChatDialog chatDialog) {
        QBDialogCustomData data = chatDialog.getCustomData();
        if (data == null) {
            return DIConstants.kChatColleaguesTag;
        }

        String tag = (String) data.get("tag");
        return tag == null ? DIConstants.kChatColleaguesTag : tag.toLowerCase();
    }

    public static String getPosition(QBChatDialog chatDialog) {
        if (QBDialogType.PRIVATE.equals(chatDialog.getType())) {
            for (QMUser user :  QMUserService.getInstance().getUserCache().getUsersByIDs(chatDialog.getOccupants())) {
                if (user.getEmail().equals(AppSession.getSession().getUser().getEmail())) {
                    return user.getTwitterDigitsId();
                }
            }
        }

        QBDialogCustomData data = chatDialog.getCustomData();
        if (data == null) {
            return "";
        }

        String position = (String) data.get("position");
        return position == null ? "" : position;
    }

    public static ArrayList<ChatFirmModel> filterMeOut(ArrayList<ChatFirmModel> contacts) {
        ArrayList<ChatFirmModel> newContacts = new ArrayList<>();

        for (ChatFirmModel chatFirmModel : contacts) {
            List<QMUser> userArrayList = chatFirmModel.chatUsers;
            ChatFirmModel newChatFirmModel = new ChatFirmModel();
            List<QMUser> newUserList = new ArrayList<>();
            boolean isExisting = false;
            for (QMUser user : userArrayList) {
                if (!user.getEmail().equals(AppSession.getSession().getUser().getEmail())) {
                    newUserList.add(user);
                }
            }

            newChatFirmModel.firmCode = chatFirmModel.firmCode;
            newChatFirmModel.firmName = chatFirmModel.firmName;
            newChatFirmModel.chatUsers = newUserList;
            newContacts.add(newChatFirmModel);
        }

        return newContacts;
    }

    public static String addThousandsSeparator(String value) {
        value = value.replace(",", "");
        DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);
        formatter.applyPattern("#,###,###,###,###.00");
        return formatter.format(Double.valueOf(value));
    }

    public static String UriToBase64(Uri uri) {
        InputStream iStream = null;
        try {
            iStream = App.getInstance().getContentResolver().openInputStream(uri);
            return getBytes(iStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }

        return Base64.encodeToString(byteBuffer.toByteArray(), Base64.DEFAULT);
    }

    public static String toSafeStr(String value) {
        return value == null ? "" : value;
    }

    public static String generateFileName() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        SimpleDateFormat mdformat = new SimpleDateFormat("hhmmss");
        String today = mdformat.format(calendar.getTime());
        return "IMG_" + today + ".jpg";
    }

    public static String getUserPosition(String email) {
        return "";
    }
}
