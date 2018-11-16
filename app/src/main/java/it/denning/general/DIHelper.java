package it.denning.general;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;
import android.text.format.DateFormat;
import android.text.format.Formatter;
import android.util.Base64;

import com.quickblox.chat.model.QBChatDialog;
import com.quickblox.chat.model.QBDialogCustomData;
import com.quickblox.chat.model.QBDialogType;
import com.quickblox.q_municate_core.models.AppSession;
import com.quickblox.q_municate_core.utils.UserFriendUtils;
import com.quickblox.q_municate_user_service.QMUserService;
import com.quickblox.q_municate_user_service.model.QMUser;
import com.quickblox.users.model.QBUser;
import com.wdullaer.materialdatetimepicker.date.MonthAdapter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

import it.denning.App;
import it.denning.R;
import it.denning.model.ChatFirmModel;

import static android.content.Context.WIFI_SERVICE;
import static java.util.Calendar.DATE;
import static java.util.Calendar.MONTH;
import static java.util.Calendar.YEAR;

/**
 * Created by denningit on 18/04/2017.
 */

public class DIHelper {
    private static DIHelper instance;
    private static Context activity;

    public static DIHelper getInstance(Context activity) {
        if (instance == null) {
            instance = new DIHelper();
        }

        instance.activity = activity;
        return instance;
    }

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
        mdformat.setTimeZone(TimeZone.getDefault());
        return mdformat.format(calendar.getTime());
    }

    public static String todayWithTime() {
        SimpleDateFormat mdformat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        mdformat.setTimeZone(TimeZone.getDefault());
        return mdformat.format(new Date());
    }

    public static String todayF() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        SimpleDateFormat mdformat = new SimpleDateFormat("yyyy-MM-dd");
        mdformat.setTimeZone(TimeZone.getDefault());
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
        if (date == null || date.trim().length() == 0) {
            return "";
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
        if (date == null || date.trim().length() == 0) {
            return "";
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
        if (date == null || date.trim().length() == 0) {
            return "";
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
        if (date == null || date.trim().length() == 0) {
            return "";
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        Date testDate = null;
        try {
            testDate = sdf.parse(date);
        }catch(Exception ex){
            ex.printStackTrace();
        }

        SimpleDateFormat newFormat = new SimpleDateFormat("dd MMM yyy");
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

    public static String getBirthday(int year, int month, int day) {
        Calendar calendar= Calendar.getInstance();
        calendar.set(year, month, day);
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

    public static Calendar getCalendar(Date date) {
        Calendar cal = Calendar.getInstance(Locale.US);
        cal.setTime(date);
        return cal;
    }

    public static float calcDateDiff(String _startDate, String _endDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy");
        Date startDate = null;
        try {
            startDate = sdf.parse(_startDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Date endDate = null;
        try {
            endDate = sdf.parse(_endDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        //milliseconds
        float difference = Math.abs(endDate.getTime() - startDate.getTime());
        float differenceyears = difference / 31540000.0f / 1000.0f;

       return  Float.valueOf(String.format("%.3f", differenceyears));
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
//        String ip = "";
//        try {
//           ip = Inet4Address.getLocalHost().getHostAddress().toString();
//        } catch (UnknownHostException e) {
//            e.printStackTrace();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        return DISharedPreferences.getInstance().getIp();
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

    public static String getNotNull(Object object) {
        return object != null ? object.toString() : "";
    }

    public static List<String> getNotNullArray(Object object) {
        if (object instanceof ArrayList) {
            return (List<String>) object;
        }
        return object != null ? new LinkedList<String>(Arrays.asList(object.toString())) : new ArrayList<String>();
    }

    public static int determinSearchType(String formName) {
        int searchType;

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
        return getChatCustomData(chatDialog, "tag");
    }

    public static String getChatCustomData(QBChatDialog chatDialog, String key) {
        QBDialogCustomData data = chatDialog.getCustomData();
        if (data == null) {
            return DIConstants.kChatColleaguesTag;
        }

        return data.get(key) == null ? DIConstants.kChatColleaguesTag : data.get(key).toString().toLowerCase();
    }

    public static List<String> getChatCustomDataArray(QBChatDialog chatDialog, String key) {
        QBDialogCustomData data = chatDialog.getCustomData();
        if (data == null) {
            return new ArrayList<>();
        }

        return getNotNullArray(data.get(key));
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

        return data.get("position") == null ? "" : data.get("position").toString();
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
        try {
            Float test = Float.parseFloat(value);
        } catch (Exception e) {
            return value;
        }

        DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);
        formatter.applyPattern("#,###,###,###,###.00");
        String result = formatter.format(Double.valueOf(value));

        return result.equals(".00") ? "0.00" : result.equals("-.00") ? "-.00": result;
    }

    public static float toFloat(String value) {
        float result = 0.0f;
        if (!value.isEmpty()) {
          result =  Float.valueOf(value.replace(",", ""));
        }
        return result;
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

    public static String getCurrentUserRole(QBUser user, QBChatDialog chatDialog) {
        String role;

        List<String> adminRole = getChatCustomDataArray(chatDialog, DIConstants.kRoleAdminTag);
        List<String> readerRole = getChatCustomDataArray(chatDialog, DIConstants.kRoleReaderTag);
        List<String> normalRole = getChatCustomDataArray(chatDialog, DIConstants.kRoleStaffTag);

        if (DISharedPreferences.getInstance().checkDenningUser(user.getEmail())) {
            role = DIConstants.kRoleDenningTag;
        } else if (adminRole.contains(user.getId())) {
            role = DIConstants.kRoleAdminTag;
        } else if (readerRole.contains(user.getId())) {
            role = DIConstants.kRoleReaderTag;
        } else if (normalRole.contains(user.getId())) {
            role = DIConstants.kRoleStaffTag;
        } else {
            role = DIConstants.kRoleClientTag;
        }

        return role;
    }

    public static String joinString(List<String> array) {
        String result = "";
        for (String val : array) {
            result += val + ",";
        }

        return result;
    }

    public static Boolean isChatExpired(QBChatDialog dialog) {
        if (DISharedPreferences.getInstance().isExpired()) {
            DIAlert.showSimpleAlert(activity, R.string.access_restricted, R.string.chat_access_restricted_text);
            return false;
        }

        return true;
    }

    public static Boolean isAllowToSendForDialog(QBChatDialog dialog) {
        String role = getCurrentUserRole(AppSession.getSession().getUser(), dialog);
        if (role.equals(DIConstants.kRoleReaderTag)) {
            // reader only can read dialog
            return false;
        }

        if (!role.equals(DIConstants.kChatClientsTag)) {
            if (!getTag(dialog).equals(DIConstants.kChatDenningTag)) {
                return isChatExpired(dialog);
            }
        }

        return true;
    }

    public static Boolean canChangeGroupInfoforDialog(QBChatDialog dialog) {
        if (!isAllowToSendForDialog(dialog)) {
            return false;
        }

        Boolean isPossible = false;
        String role = getCurrentUserRole(AppSession.getSession().getUser(), dialog);
        String tag = getTag(dialog);
        Boolean isDenningUser = DISharedPreferences.getInstance().isDenningUser();

        if (tag.equals(DIConstants.kChatDenningTag)) {
            if (isDenningUser) {
                isPossible = true;
            } else if (role.equals(DIConstants.kRoleAdminTag)) {
                isPossible = true;
            } else if (role.equals(DIConstants.kRoleStaffTag)) {
                isPossible = true;
            } else if (role.equals(DIConstants.kRoleReaderTag)) {
                isPossible = false;
            } else {
                isPossible = false;
            }
        } else {
            if (isDenningUser) {
                isPossible = true;
            } else if (role.equals(DIConstants.kRoleAdminTag)) {
                isPossible = true;
            } else if (role.equals(DIConstants.kRoleStaffTag)) {
                isPossible = true;
            } else if (role.equals(DIConstants.kRoleReaderTag)) {
                isPossible = false;
            } else {
                isPossible = false;
            }
        }

        if (!isPossible) {
            DIAlert.showSimpleAlert(activity, R.string.access_restricted, R.string.chat_access_restricted_text);
        }
        return isPossible;
    }

    public static Boolean canChangeGroupNameforDialog(QBChatDialog dialog) {
        if (!isAllowToSendForDialog(dialog)) {
            return false;
        }

        Boolean isPossible = false;
        String role = getCurrentUserRole(AppSession.getSession().getUser(), dialog);
        String tag = getTag(dialog);
        Boolean isDenningUser = DISharedPreferences.getInstance().isDenningUser();

        if (tag.equals(DIConstants.kChatDenningTag)) {
            if (isDenningUser) {
                isPossible = true;
            } else if (role.equals(DIConstants.kRoleAdminTag)) {
                isPossible = false;
            } else if (role.equals(DIConstants.kRoleStaffTag)) {
                isPossible = false;
            } else if (role.equals(DIConstants.kRoleReaderTag)) {
                isPossible = false;
            } else {
                isPossible = false;
            }
        } else {
            if (isDenningUser) {
                isPossible = true;
            } else if (role.equals(DIConstants.kRoleAdminTag)) {
                isPossible = true;
            } else if (role.equals(DIConstants.kRoleStaffTag)) {
                isPossible = false;
            } else if (role.equals(DIConstants.kRoleReaderTag)) {
                isPossible = false;
            } else {
                isPossible = false;
            }
        }

        if (!isPossible) {
            DIAlert.showSimpleAlert(activity, R.string.access_restricted, R.string.chat_access_restricted_text);
        }

        return isPossible;
    }

    public static Boolean canChangeGroupTagforDialog(QBChatDialog dialog) {
        if (!isAllowToSendForDialog(dialog)) {
            return false;
        }

        Boolean isPossible = false;
        String role = getCurrentUserRole(AppSession.getSession().getUser(), dialog);
        String tag = getTag(dialog);
        Boolean isDenningUser = DISharedPreferences.getInstance().isDenningUser();

        if (tag.equals(DIConstants.kChatDenningTag)) {
            if (isDenningUser) {
                isPossible = true;
            } else if (role.equals(DIConstants.kRoleAdminTag)) {
                isPossible = false;
            } else if (role.equals(DIConstants.kRoleStaffTag)) {
                isPossible = false;
            } else if (role.equals(DIConstants.kRoleReaderTag)) {
                isPossible = false;
            } else {
                isPossible = false;
            }
        } else {
            if (isDenningUser) {
                isPossible = true;
            } else if (role.equals(DIConstants.kRoleAdminTag)) {
                isPossible = true;
            } else if (role.equals(DIConstants.kRoleStaffTag)) {
                isPossible = false;
            } else if (role.equals(DIConstants.kRoleReaderTag)) {
                isPossible = false;
            } else {
                isPossible = false;
            }
        }

        if (!isPossible) {
//            DIAlert.showSimpleAlert(App.getInstance(), R.string.access_restricted, R.string.chat_access_restricted_text);
        }

        return isPossible;
    }

    public static Boolean canChangeGroupRoleforDialog(QBChatDialog dialog, QBUser toUser) {
        if (!isAllowToSendForDialog(dialog)) {
            return false;
        }

        Boolean isPossible = false;
        QBUser user = AppSession.getSession().getUser();
        String role = getCurrentUserRole(AppSession.getSession().getUser(), dialog);
        String roleofToUser = getCurrentUserRole(toUser, dialog);
        String tag = getTag(dialog);
        Boolean isDenningUser = DISharedPreferences.getInstance().isDenningUser();

        if (user.getEmail().equals(toUser.getEmail())) {
            DIAlert.showSimpleAlert(activity, R.string.warning_title, R.string.alert_role_self_change);
            return false;
        }

        if (roleofToUser.equals(DIConstants.kRoleClientTag)) {
            DIAlert.showSimpleAlert(activity, R.string.warning_title, R.string.alert_role_client_change);
            return false;
        }

        if (tag.equals(DIConstants.kChatDenningTag)) {
            if (isDenningUser) {
                isPossible = true;
            } else if (role.equals(DIConstants.kRoleAdminTag)) {
                if (roleofToUser.equals(DIConstants.kRoleDenningTag)) {
                    DIAlert.showSimpleAlert(activity, R.string.warning_title, R.string.alert_role_denning_change);
                    return false;
                }
                isPossible = true;
            } else if (role.equals(DIConstants.kRoleStaffTag)) {
                isPossible = false;
            } else if (role.equals(DIConstants.kRoleReaderTag)) {
                isPossible = false;
            } else {
                isPossible = false;
            }
        } else {
            if (isDenningUser) {
                isPossible = true;
            } else if (role.equals(DIConstants.kRoleAdminTag)) {
                if (roleofToUser.equals(DIConstants.kRoleDenningTag)) {
                    DIAlert.showSimpleAlert(activity, R.string.warning_title, R.string.alert_role_denning_change);
                    return false;
                }
                isPossible = true;
            } else if (role.equals(DIConstants.kRoleStaffTag)) {
                isPossible = false;
            } else if (role.equals(DIConstants.kRoleReaderTag)) {
                isPossible = false;
            } else {
                isPossible = false;
            }
        }


        if (!isPossible) {
            DIAlert.showSimpleAlert(activity, R.string.access_restricted, R.string.chat_access_restricted_text);
        }

        return isPossible;
    }

    public static Boolean canMuteforDialog(QBChatDialog dialog) {
        if (!isAllowToSendForDialog(dialog)) {
            return false;
        }

        Boolean isPossible = false;
        String role = getCurrentUserRole(AppSession.getSession().getUser(), dialog);
        String tag = getTag(dialog);
        Boolean isDenningUser = DISharedPreferences.getInstance().isDenningUser();

        if (tag.equals(DIConstants.kChatDenningTag)) {
            if (isDenningUser) {
                isPossible = true;
            } else if (role.equals(DIConstants.kRoleAdminTag)) {
                isPossible = true;
            } else if (role.equals(DIConstants.kRoleStaffTag)) {
                isPossible = true;
            } else if (role.equals(DIConstants.kRoleReaderTag)) {
                isPossible = false;
            } else {
                isPossible = false;
            }
        } else {
            if (isDenningUser) {
                isPossible = true;
            } else if (role.equals(DIConstants.kRoleAdminTag)) {
                isPossible = true;
            } else if (role.equals(DIConstants.kRoleStaffTag)) {
                isPossible = true;
            } else if (role.equals(DIConstants.kRoleReaderTag)) {
                isPossible = false;
            } else {
                isPossible = false;
            }
        }

        if (!isPossible) {
            DIAlert.showSimpleAlert(App.getInstance(), R.string.access_restricted, R.string.chat_access_restricted_text);
        }

        return isPossible;
    }

    public static Boolean canAddMemberforDialog(QBChatDialog dialog) {
        if (!isAllowToSendForDialog(dialog)) {
            return false;
        }

        Boolean isPossible = false;
        String role = getCurrentUserRole(AppSession.getSession().getUser(), dialog);
        String tag = getTag(dialog);
        Boolean isDenningUser = DISharedPreferences.getInstance().isDenningUser();

        if (tag.equals(DIConstants.kChatDenningTag)) {
            if (isDenningUser) {
                isPossible = true;
            } else if (role.equals(DIConstants.kRoleAdminTag)) {
                isPossible = true;
            } else if (role.equals(DIConstants.kRoleStaffTag)) {
                isPossible = false;
            } else if (role.equals(DIConstants.kRoleReaderTag)) {
                isPossible = false;
            } else {
                isPossible = false;
            }
        } else {
            if (isDenningUser) {
                isPossible = true;
            } else if (role.equals(DIConstants.kRoleAdminTag)) {
                isPossible = true;
            } else if (role.equals(DIConstants.kRoleStaffTag)) {
                isPossible = false;
            } else if (role.equals(DIConstants.kRoleReaderTag)) {
                isPossible = false;
            } else {
                isPossible = false;
            }
        }

        if (!isPossible) {
            DIAlert.showSimpleAlert(App.getInstance(), R.string.access_restricted, R.string.chat_access_restricted_text);
        }

        return isPossible;
    }

    public static Boolean canRemoveMemberforDialog(QBChatDialog dialog) {
        if (!isAllowToSendForDialog(dialog)) {
            return false;
        }

        Boolean isPossible = false;
        String role = getCurrentUserRole(AppSession.getSession().getUser(), dialog);
        String tag = getTag(dialog);
        Boolean isDenningUser = DISharedPreferences.getInstance().isDenningUser();

        if (tag.equals(DIConstants.kChatDenningTag)) {
            if (isDenningUser) {
                isPossible = true;
            } else if (role.equals(DIConstants.kRoleAdminTag)) {
                isPossible = true;
            } else if (role.equals(DIConstants.kRoleStaffTag)) {
                isPossible = false;
            } else if (role.equals(DIConstants.kRoleReaderTag)) {
                isPossible = false;
            } else {
                isPossible = false;
            }
        } else {
            if (isDenningUser) {
                isPossible = true;
            } else if (role.equals(DIConstants.kRoleAdminTag)) {
                isPossible = true;
            } else if (role.equals(DIConstants.kRoleStaffTag)) {
                isPossible = false;
            } else if (role.equals(DIConstants.kRoleReaderTag)) {
                isPossible = false;
            } else {
                isPossible = false;
            }
        }

        if (!isPossible) {
            DIAlert.showSimpleAlert(App.getInstance(), R.string.access_restricted, R.string.chat_access_restricted_text);
        }

        return isPossible;
    }

    public static Boolean canLeaveChatforDialog(QBChatDialog dialog) {
        if (!isAllowToSendForDialog(dialog)) {
            return false;
        }

        Boolean isPossible = false;
        String role = getCurrentUserRole(AppSession.getSession().getUser(), dialog);
        String tag = getTag(dialog);
        Boolean isDenningUser = DISharedPreferences.getInstance().isDenningUser();

        if (tag.equals(DIConstants.kChatDenningTag)) {
            if (isDenningUser) {
                isPossible = true;
            } else if (role.equals(DIConstants.kRoleAdminTag)) {
                isPossible = true;
            } else if (role.equals(DIConstants.kRoleStaffTag)) {
                isPossible = false;
            } else if (role.equals(DIConstants.kRoleReaderTag)) {
                isPossible = false;
            } else {
                isPossible = false;
            }
        } else {
            if (isDenningUser) {
                isPossible = true;
            } else if (role.equals(DIConstants.kRoleAdminTag)) {
                isPossible = true;
            } else if (role.equals(DIConstants.kRoleStaffTag)) {
                isPossible = false;
            } else if (role.equals(DIConstants.kRoleReaderTag)) {
                isPossible = false;
            } else {
                isPossible = false;
            }
        }

        if (!isPossible) {
            DIAlert.showSimpleAlert(App.getInstance(), R.string.access_restricted, R.string.chat_access_restricted_text);
        }

        return isPossible;
    }

    public static Boolean canViewContactProfileforDialog(QBChatDialog dialog, QBUser toUser) {
        if (!isAllowToSendForDialog(dialog)) {
            return false;
        }

        Boolean isPossible = false;
        QBUser user = AppSession.getSession().getUser();
        String role = getCurrentUserRole(AppSession.getSession().getUser(), dialog);
        String roleofToUser = getCurrentUserRole(toUser, dialog);
        String tag = getTag(dialog);
        Boolean isDenningUser = DISharedPreferences.getInstance().isDenningUser();

        if (user.getEmail().equals(toUser.getEmail())) {
            DIAlert.showSimpleAlert(App.getInstance(), R.string.warning_title, R.string.alert_role_self_change);
            return false;
        }

        if (roleofToUser.equals(DIConstants.kRoleClientTag)) {
            DIAlert.showSimpleAlert(App.getInstance(), R.string.warning_title, R.string.alert_role_client_change);
            return false;
        }

        if (tag.equals(DIConstants.kChatDenningTag)) {
            if (isDenningUser) {
                isPossible = true;
            } else if (role.equals(DIConstants.kRoleAdminTag)) {
                if (roleofToUser.equals(DIConstants.kRoleDenningTag)) {
                    return false;
                }
                isPossible = true;
            } else if (role.equals(DIConstants.kRoleStaffTag)) {
                isPossible = true;
            } else if (role.equals(DIConstants.kRoleReaderTag)) {
                isPossible = false;
            } else {
                isPossible = false;
            }
        } else {
            if (isDenningUser) {
                isPossible = true;
            } else if (role.equals(DIConstants.kRoleAdminTag)) {
                if (roleofToUser.equals(DIConstants.kRoleDenningTag)) {
                    return false;
                }
                isPossible = true;
            } else if (role.equals(DIConstants.kRoleStaffTag)) {
                if (roleofToUser.equals(DIConstants.kRoleDenningTag)) {
                    return false;
                }
                isPossible = true;
            } else if (role.equals(DIConstants.kRoleReaderTag)) {
                isPossible = false;
            } else {
                isPossible = false;
            }
        }


        if (!isPossible) {
            DIAlert.showSimpleAlert(App.getInstance(), R.string.access_restricted, R.string.chat_access_restricted_text);
        }

        return isPossible;
    }

    public static Boolean canShowAllMembersStatusforDialog(QBChatDialog dialog) {
        if (!isAllowToSendForDialog(dialog)) {
            return false;
        }

        Boolean isPossible = false;
        String role = getCurrentUserRole(AppSession.getSession().getUser(), dialog);
        String tag = getTag(dialog);
        Boolean isDenningUser = DISharedPreferences.getInstance().isDenningUser();

        if (tag.equals(DIConstants.kChatDenningTag)) {
            if (isDenningUser) {
                isPossible = true;
            } else if (role.equals(DIConstants.kRoleAdminTag)) {
                isPossible = true;
            } else if (role.equals(DIConstants.kRoleStaffTag)) {
                isPossible = false;
            } else if (role.equals(DIConstants.kRoleReaderTag)) {
                isPossible = false;
            } else {
                isPossible = false;
            }
        } else {
            if (isDenningUser) {
                isPossible = true;
            } else if (role.equals(DIConstants.kRoleAdminTag)) {
                isPossible = true;
            } else if (role.equals(DIConstants.kRoleStaffTag)) {
                isPossible = false;
            } else if (role.equals(DIConstants.kRoleReaderTag)) {
                isPossible = false;
            } else {
                isPossible = false;
            }
        }

        if (!isPossible) {
            DIAlert.showSimpleAlert(App.getInstance(), R.string.access_restricted, R.string.chat_access_restricted_text);
        }

        return isPossible;
    }

    public static Boolean canSendMessageforDialog(QBChatDialog dialog) {
        if (!isAllowToSendForDialog(dialog)) {
            return false;
        }

        Boolean isPossible = false;
        String role = getCurrentUserRole(AppSession.getSession().getUser(), dialog);
        String tag = getTag(dialog);
        Boolean isDenningUser = DISharedPreferences.getInstance().isDenningUser();

        if (tag.equals(DIConstants.kChatDenningTag)) {
            if (isDenningUser) {
                isPossible = true;
            } else if (role.equals(DIConstants.kRoleAdminTag)) {
                isPossible = true;
            } else if (role.equals(DIConstants.kRoleStaffTag)) {
                isPossible = true;
            } else if (role.equals(DIConstants.kRoleReaderTag)) {
                isPossible = false;
            } else {
                isPossible = true;
            }
        } else {
            if (isDenningUser) {
                isPossible = true;
            } else if (role.equals(DIConstants.kRoleAdminTag)) {
                isPossible = true;
            } else if (role.equals(DIConstants.kRoleStaffTag)) {
                isPossible = true;
            } else if (role.equals(DIConstants.kRoleReaderTag)) {
                isPossible = false;
            } else {
                isPossible = true;
            }
        }

        if (!isPossible) {
            DIAlert.showSimpleAlert(App.getInstance(), R.string.access_restricted, R.string.chat_access_restricted_text);
        }

        return isPossible;
    }

    public static Boolean canCallVideoforDialog(QBChatDialog dialog) {
        if (!isAllowToSendForDialog(dialog)) {
            return false;
        }

        Boolean isPossible = false;
        String role = getCurrentUserRole(AppSession.getSession().getUser(), dialog);
        String tag = getTag(dialog);
        Boolean isDenningUser = DISharedPreferences.getInstance().isDenningUser();

        if (tag.equals(DIConstants.kChatDenningTag)) {
            if (isDenningUser) {
                isPossible = true;
            } else if (role.equals(DIConstants.kRoleAdminTag)) {
                isPossible = true;
            } else if (role.equals(DIConstants.kRoleStaffTag)) {
                isPossible = true;
            } else if (role.equals(DIConstants.kRoleReaderTag)) {
                isPossible = false;
            } else {
                isPossible = true;
            }
        } else {
            if (isDenningUser) {
                isPossible = true;
            } else if (role.equals(DIConstants.kRoleAdminTag)) {
                isPossible = true;
            } else if (role.equals(DIConstants.kRoleStaffTag)) {
                isPossible = true;
            } else if (role.equals(DIConstants.kRoleReaderTag)) {
                isPossible = false;
            } else {
                isPossible = true;
            }
        }

        if (!isPossible) {
            DIAlert.showSimpleAlert(App.getInstance(), R.string.access_restricted, R.string.chat_access_restricted_text);
        }

        return isPossible;
    }

    public static void filterGroupOccupants(ArrayList<QBUser> users, QBChatDialog dialog) {
        ArrayList<QBUser> newUsers = new ArrayList<>();
        ArrayList<Integer> ids = UserFriendUtils.getFriendIdsList(users);
    }

    public static Boolean isSuperUser(String email) {
        return email.equals("tmho@denning.com.m") || email.equals("jingpiow@denning.com.my");
    }


    // Calculate Legal Fee
    public static float[] calcLoanAndLegal(float priceValue) {
        float legalCost = 0;
        if (priceValue  >= 500000) {
            legalCost  += 500000* 0.01;
        } else {
            legalCost  += priceValue * 0.01;
            legalCost = Math.max(legalCost,500.0f);
        }
        priceValue  -= 500000;

        if (priceValue  > 0 && priceValue  < 500000){
            legalCost  += priceValue *0.008;
        } else if (priceValue  >= 500000) {
            legalCost  += 500000*0.008;
        }
        priceValue  -= 500000;

        if (priceValue  > 0 && priceValue  < 2000000){
            legalCost  += priceValue *0.007;
        } else if (priceValue  >= 2000000) {
            legalCost  += 2000000*0.007;
        }
        priceValue  -= 2000000;

        if (priceValue  > 0 && priceValue  < 2000000){
            legalCost  += priceValue *0.006;
        } else if (priceValue  >= 2000000) {
            legalCost  += 2000000*0.006;
        }
        priceValue  -= 2000000;

        if (priceValue  > 0 && priceValue  < 2500000){
            legalCost  += priceValue *0.005;
        } else if (priceValue  >= 2500000) {
            legalCost  += 2500000*0.005;
        }
        priceValue  -= 2500000;

        return new float[]{(priceValue), (legalCost)};
    }
}
