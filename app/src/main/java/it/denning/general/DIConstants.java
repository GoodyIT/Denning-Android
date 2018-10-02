package it.denning.general;

import java.util.List;

import it.denning.R;

/**
 * Created by denningit on 18/04/2017.
 */

public class DIConstants {
    public static final String FRAGMENT_BUNDLE_KEY = "denning.it.android.fragment.key";
    public static final String ACTIVITY_BUNDLE_KEY = "denning.it.android.activity.key";

    public static final String kGoogleMapKey = "AIzaSyAxOQtqe1t0TkVgmYV1t7Y_JWFERGEpcuU";

    public static final String  GOOGLE_MAP_REVERSE_URL  = "maps/api/geocode/json?key=AIzaSyAxOQtqe1t0TkVgmYV1t7Y_JWFERGEpcuU&latlng=";

    public static final String kDIAgreementUrl = "http://denningsoft.dlinkddns.com/denningwcf/v1/table/eulaAPP";
    public static final String kDIAgreementGetUrl = "denningapi/v1/table/eulaAPP";

    public static final String HOME_ADS_GET_URL = "denningapi/v1/advertisement";

    public static final String LOGOUT_URL = "https://www.denningonline.com.my/denningapi/v1/logout";

    public static final String AUTH_SIGNIN_URL = "https://denningonline.com.my/denningapi/v1/app/signIn";
    public static final String AUTH_SIGNUP_URL = "https://www.denningonline.com.my/denningapi/v1/signUp";
    public static final String STAFF_SIGNIN_URL   = "v1/app/staffLogin";
    public static final String CLIENT_SIGNIN_URL   = "v1/app/clientLogin";
    public static final String CLIENT_FIRST_SIGNIN_URL  = "v1/app/clientLogin/first";
    public static final String SIGNUP_FIRM_LIST_URL = "denningapi/v1/Solicitor";

    public static final String AUTH_SMS_NEW = "https://www.denningonline.com.my/denningapi/v1/SMS/newDevice";
    public static final String AUTH_SMS_FORGET_PASSWORD = "https://www.denningonline.com.my/denningapi/v1/SMS/lostPassword";
    public static final String AUTH_SMS_REQUEST = "https://www.denningonline.com.my/denningapi/v1/SMS/request";

    public static final String AUTH_SET_NEW_PASSWORD = "https://www.denningonline.com.my/denningapi/v1/password/new";
    public static final String AUTH_FORGET_PASSWORD = "https://www.denningonline.com.my/denningapi/v1/password/forget";

    public static final String NEWS_LATEST_URL = "denningapi/v1/DenningNews";
    public static final String UPDATES_LATEST_URL = "denningapi/v1/DenningUpdate";
    public static final String EVENT_LATEST_URL = "v1/DenningCalendar";

    public static final String ATTENDANCE_GET_URL = "v1/app/StaffAttendance/101";
    public static final String ATTENDANCE_CLOCK_IN = "v1/app/StaffAttendance/CheckIn";
    public static final String ATTENDANCE_BREAK = "v1/app/StaffAttendance/Break";

    public static final String CALENDAR_MONTHLY_SUMMARY_URL =  "v1/DenningCalendar/MonthlySummary";
    // Search
    public static final String DENNING_KEYWORD_SEARCH_URL = "https://www.denningonline.com.my/denningapi/v1/publicSearch/keyword?search=";
    public static final String DENNING_SEARCH_URL = "https://www.denningonline.com.my/denningapi/v1/publicSearch?search=";
    public static final String DENNING_SEARCH_FILTER = "";

    public static final String MATTER_GET_URL  = "v1/app/matter/";
    public static final String CONTACT_GET_URL  = "v1/app/Contact/";
    public static final String SOLICITOR_GET_URL = "v1/app/Solicitor/";
    public static final String PROPERTY_GET_URL = "v1/app/Property/";
    public static final String BANK_GET_GET_URL = "v1/app/bank/branch/";

    public static final String FILE_NAME_AUTOCOMPLETE_URL = "v1/table/cboDocumentName?search=";
    public static final String MATTER_CLIENT_FILEFOLDER = "v1/app/userClientFolder";
    public static final String PROPERTY_FILE_FOLDER_URL = "v1/document/property/upload";
    public static final String FILE_NOTE_URL = "v1/table/Note";
    public static final String PAYMENT_RECORD = "v1/app/PaymentRecord/";
    public static final String SEARCH_TEMPLATE_CATEGORY = "v1/Table/cbotemplatecategory/only?search=";
    public static final String SEARCH_TEMPLATE_TYPE =  "v1/Table/cbotemplatecategory?filter=";

    public static final String[] PublicSearchFilter = {"All Public", "Public lawFirm", "Public Document", "Public GovOffices"};
    public static final int[] PublicSearchCategory = {-1, 128, 256, 512};

    public static final String GENERAL_KEYWORD_SEARCH_URL = "v1/generalSearch/keyword?search=";
    public static final String GENERAL_SEARCH_URL = "v1/generalSearch?search=";
    public static final String[] GeneralSearchFilter = {"All", "Contacts", "Related Matter", "Property", "Bank", "Government Offices", "Legal Firm", "Documents"};
    public static final int[] GeneralSearchCategory = {0, 1, 2, 4, 8, 16, 32, 64};

    public static final String GENERAL_SESSION_ID  = "{334E910C-CC68-4784-9047-0F23D37C9CF9}";
    public static final String GENERAL_EMAIL = "iPhone@denning.com.my";

    public static final String TEMP_ACTIVATION_CODE = "654321";

    public static final String DASHBOARD_MAIN_URL = "v1/app/dashboard/main";
    public static final String DASHBOARD_MY_DUETASK_URL = "v1/app/dashboard/spaCheckList";

    public static final String DASHBOARD_S1_MATTERLISTING_GET_URL = "v1/app/dashboard/S1";

    public static final String DASHBOARD_S10_GET_URL =  "v1/app/dashboard/S10";

    public static final String DASHBOARD_S11_GET_URL = "v1/app/dashboard/S11";

    public static final String DASHBOARD_COMPLETION_TRACKING_HEADER_GET_URL = "v1/app/dashboard/spaCompletionDate";

    public static final String CHAT_GET_URL = "denningapi/v2/chat/contact?userid=";

    public static final String CHAT_ADD_FAVORITE = "https://www.denningonline.com.my/denningapi/v1/chat/contact/fav";

    public static final String MATTERSIMPLE_GET_URL = "v1/matter/simpleList?search=";

    public static final String MATTER_LIST_GET_URL  = "v1/matter?search=";

    public static final String PRESET_BILL_GET_URL = "v1/PresetBill?search=";

    public static final String TAXINVOICE_CALCULATION_URL  = "v1/Calculation/Invoice/draft";

    public static final String QUOTATION_SAVE_URL  = "v1/Quotation";

    public static final String BILL_SAVE_URL  = "v1/TaxInvoice";

    public static final String INVOICE_FROM_QUOTATION = "v1/convert/quotation/taxinvoice";

    public static final String ACCOUNT_TYPE_GET_LIST_URL   = "v1/account/type?search=";

    public static final String RECEIPT_SAVE_URL = "v1/ClientReceipt";

    public static final String RECEIPT_UPDATE_URL = "v1/Receipt";

    public static final String ACCOUNT_CHEQUE_ISSUEER_GET_URL  = "v1/account/ChequeIssuerBank?search=";

    public static final String BANK_BRANCH_GET_LIST_URL  = "v1/bank/Branch?search=";

    public static final String PAYMENT_MODE_GET_URL  = "v1/Table/cboPaymentMode?search=";

    public static final String TRANSACTION_DESCRIPTION_RECEIPT_GET = "v1/table/cboTransactionDesc?docCode=R&search=";

    public static final String TAXINVOICE_ALL_GET_URL  = "v1/TaxInvoiceX/all";

    public static final String GENERAL_CONTACT_URL = "v1/generalSearch/cust";

    public static final String CONTACT_GET_DETAIL_URL = "v1/app/contact/";

    public static final String REPORT_VIEWER_PDF_TAXINVOICE_URL  = "v1/ReportViewer/pdf/TaxInvoice/";

    public static final String REPORT_VIEWER_PDF_QUATION_URL  = "v1/ReportViewer/pdf/Quotation/";

    public static final String RECEIPT_FROM_QUOTATION = "v1/convert/quotation/receipt";

    public static final String RECEIPT_FROM_TAXINVOICE = "v1/convert/taxinvoice/receipt";

    public static final String QUOTATION_GET_LIST_URL = "v1/Quotation?search=";

    public static final String LEAVE_SUBMITTED_BY_URL = "v1/WhoAmI";

    public static final String LEAVE_RECORD_GET_URL = "v1/Table/StaffLeave?search=";

    public static final String LEAVE_RECORD_URL = "v1/Table/StaffLeave/";

    public static final String LEAVE_TYPE_GET_URL = "v1/generalSelection/frmStaffLeave/leaveType?search=";

    public static final String STAFF_LEAVE_SAVE_URL = "v1/Table/StaffLeave";

    public static final String CONTACT_IDTYPE_URL = "v1/IDType?search=";

    public static final String CONTACT_TITLE_URL  = "v1/Salutation?search=";

    public static final String CONTACT_POSTCODE_URL = "v1/Postcode?search=";

    public static final String CONTACT_CITY_URL  = "v1/city?search=";

    public static final String CONTACT_STATE_URL  = "v1/State?search=";

    public static final String CONTACT_COUNTRY_URL = "v1/Country?search=";

    public static final String CONTACT_CITIZENSHIP_URL = "v1/Citizenship?search=";

    public static final String CONTACT_OCCUPATION_URL = "v1/Occupation?search=";

    public static final String CONTACT_IRDBRANCH_URL = "v1/IRDBranch?search=";

    public static final String CONTACT_ID_NAME_DUPLICATE  = "v1/generalSearch?category=1&isAutoComplete=1&search=";

    public static final String CONTACT_SAVE_URL  = "v1/app/contact";

    public static final String MATTER_SAVE_URL = "v1/app/matter";

    public static final String CONTACT_GETLIST_URL = "v1/party?search=";

    public static final String MATTER_FILE_STATUS_GET_LIST_URL = "v1/FileStatus?search=";

    public static final String STAFF_GET_URL = "v1/Staff?type=";

    public static final String MATTER_BRANCH_GET_URL  = "v1/table/ProgramOwner?search=";

    public static final String PROPERTY_GET_LIST_URL = "v1/Property?search=";

    public static final String CONTACT_SOLICITOR_GET_LIST_URL = "v1/Solicitor?search=";

    public static final String COURTDIARY_COURT_GET_LIST_URL = "v1/courtDiary/court?search=";

    public static final String CASE_TYPE_GET_LIST_URL = "v1/table/CaseType?search=";

    public static final String COURT_CORAM_GET_LIST_URL = "v1/courtDiary/coram?search=";

    public static final String MATTER_LITIGATION_GET_LIST_URL = "v1/matter/litigationCase?search=";

    public static final String COURT_OFFICE_PLACE_GET_LIST_URL = "v1/OfficeDiary/AppointmentPlace?search=";

    public static final String COURT_PERSONAL_PLACE_GET_LIST_URL  = "v1/PersonalDiary/AppointmentPlace?search=";

    public static final String COURT_HEARINGTYPE_GET_URL = "v1/courtDiary/hearingType?search=";

    public static final String COURT_HEARINGDETAIL_GET_URL = "v1/courtDiary/hearingDetails?search=";

    public static final String COURT_OFFICE_APPOINTMENT_GET_LIST_URL  = "v1/OfficeDiary/AppointmentDetails?search=";

    public static final String COURT_SAVE_UPATE_URL  = "v1/CourtDiary";

    public static final String COURT_DECISION_GET_URL = "v1/courtDiary/decision?search=";

    public static final String OFFICE_DIARY_SAVE_URL  = "v1/OfficeDiary";

    public static final String PERSONAL_DIARY_SAVE_URL = "v1/PersonalDiary";

    public static final String COURT_NEXTDATE_TYPE_GET_URL = "v1/generalSelection/frmCourtDiary/nextDateType?search=";

    public static final String PROPERTY_SAVE_URL = "v1/Property";

    public static final String PROPERTY_TYPE_GET_LIST_URL = "v1/generalSelection/frmProperty/propertyType?search=";

    public static final String PROPERTY_TITLE_ISSUED_GET_URL = "v1/generalSelection/frmProperty/TitleIssued?search=";

    public static final String PROPERTY_TITLE_TYPE_GET_URL   = "v1/Property/TitleType?search=";

    public static final String PROPERTY_LOT_TYPE_GET_URL = "v1/Property/LotType?search=";

    public static final String PROPERTY_MUKIM_TYPE_GET_URL = "v1/Property/MukimType?search=";

    public static final String PROPERTY_MUKIM_GET_LIST_URL = "v1/Mukim?search=";

    public static final String PROPERTY_AREA_TYPE_GET_URL = "v1/Property/AreaType?search=";

    public static final String PROPERTY_TENURE_TYPE_GET_URL  = "v1/Property/TenureType?search=";

    public static final String PROPERTY_RESTRICTION_GET_URL  = "v1/generalSelection/frmProperty/restrictionInInterest?search=";

    public static final String PROPERTY_RESTRICTION_AGAINST_GET_URL = "v1/Property/RestrictionAgainst?search=";

    public static final String PROPERTY_APPROVING_AUTHORITY_GET_URL  = "v1/generalSelection/frmProperty/ApprovingAuthority?search=";

    public static final String PROPERTY_LANDUSE_GET_URL  = "v1/Property/LandUse?search=";

    public static final String PROPERTY_PROJECT_HOUSING_GET_URL  = "v1/HousingProject?search=";

    public static final String PROPERTY_PARCEL_TYPE_GETLIST_URL  = "v1/Property/ParcelType?search=";

    public static final String PROPERTY_MASTER_TITLE_GETLIST_URL  = "v1/Property/MasterTitle?search=";

    public static final String  THIRD_PARTY_UPLOAD_CATEGORY  = "https://www.denningonline.com.my/denningapi/v1/catPersonal?email=";

    public static final String  MATTER_STAFF_TRANSIT_FOLDER = "v1/app/matter/fileFolder";

    public static final String  MATTER_STAFF_CONTACT_FOLDER = "v1/app/contactFolder";

    public static final String  MATTER_STAFF_FILEFOLDER = "v1/app/matter/fileFolder";

    /*
    *   Chat Tags
    * */

    public static final String kChatDenningTag = "denning";
    public static final String kChatColleaguesTag = "colleagues";
    public static final String kChatClientsTag = "clients";
    public static final String kChatMattersTag = "matters";

    /*
    *   Group Position Tag
     */

    public static final String kGroupPositionTag = "position";

    /*
    * User role for chat dialog
     */

    public static final String kRoleDenningTag = "role_denning";
    public static final String kRoleAdminTag = "role_admin";
    public static final String kRoleReaderTag = "role_reader";
    public static final String kRoleStaffTag = "role_normal";
    public static final String kRoleClientTag = "role_client";

    /*
    *  User position
    * */
    public static final String kDenningPeople = "denningPeople";
    public static final String kColleague = "colleague";
    public static final String kClient = "client";
    public static final String kPublicUser = "public user";

    // Search list view type

    public static final int DENNING_SEARCH = -1;

    public static final int CONTACT_TYPE = 0;
    public static final int MATTER_TYPE = 1;
    public static final int DOCUMENT_TYPE = 2;
    public static final int PROPERTY_TYPE = 3;
    public static final int BANK_TYPE = 4;
    public static final int GOVERNMENT_LAND_OFFICES = 5;
    public static final int GOVERNMENT_PTG_OFFICES = 6;
    public static final int LEGAL_FIRM = 7;
    public static final int DOCUMENT_FOR_CONTACT_TYPE = 8;
    public static final int DOCUMENT_FOR_PROPERTY_TYPE = 9;

    public static final int SHARE_BRANCH_TYPE = 20;

    public static final int SHARE_UPLOAD_TYPE = 30;

    // Search Detail list view type
    public static final int HEADER_TYPE = 0;
    public static final int GENERAL_TYPE = 1;
    public static final int LAST_TYPE = 2;
    public static final int FILE_TYPE = 3;
    public static final int FOLDER_TYPE = 4;
    public static final int LEDGER_TYPE = 5;
    public static final int THREE_TYPE = 6;
    public static final int ONE_TYPE = 7;
    public static final int ONE_BUTTON_TYPE = 8;
    public static final int ONE_BUTTON_WHITE_TYPE = 9;
    public static final int TWO_BUTTON_TYPE = 10;
    public static final int INPUT_TYPE = 11;
    public static final int LEFT_BUTTON_TYPE = 12;
    public static final int RIGHT_BUTTON_TYPE = 13;
    public static final int PHONE_TYPE = 14;
    public static final int ONE_ROW_ADD_TYPE = 15;
    public static final int GENERAL_ADD_TYPE = 16;
    public static final int TWO_COLUMN_TYPE = 17;
    public static final int TWO_COLUMN_LEFT_DETAIL_RIGHT_INPUT_TYPE = 18;
    public static final int TWO_COLUMN_DETAIL_TYPE = 19;
    public static final int TWO_COLUMN_LEFT_INPUT_RIGHT_DETAIL_TYPE = 20;
    public static final int SELECTION_TYPE = 21;
    public static final int DISABLE_TYPE = 22;


    // Search detail for matter
    public static final int MATTER_HEADER = 0;
    public static final int MATTER_INFORMATION = 1;
    public static final int MATTER_COURT_INFORMATION = 2;
    public static final int MATTER_PARTIES_GROUP = 3;
    public static final int MATTER_SOLICITORS = 4;
    public static final int MATTER_PROPERTIES = 5;
    public static final int MATTER_BANKS = 6;
    public static final int MATTER_RM_GROUP = 7;
    public static final int MATTER_DATE_GROUP = 8;
    public static final int MATTER_TEXT_GROUP = 9;
    public static final int MATTER_FILES_LEDGERS = 10;

    // DashboardModel View Type
    public static final int DASHBOARD_FIRST_SECTION = 0;
    public static final int DASHBOARD_SECOND_SECTION = 1;
    public static final int DASHBOARD_THIRD_SECTION = 2;
    public static final int DASHBOARD_FORTH_SECTION = 3;
    public static final int DASHBOARD_FIFTH_SECTION = 4;

    public static final int DASHBOARD_THIRD_SECTION_HEADER = 0;
    public static final int DASHBOARD_FORTH_SECTION_HEADER = 1;



    // Dashboard second level
    public static final int DASHBOARD_FILE_LISTING = 0;
    public static final int DASHBOARD_CALENDAR = 1;
    public static final int DASHBOARD_MY_DUETASK = 2;
    public static final int DASHBOARD_COLLECTION = 3;
    public static final int DASHBOARD_COMPLETION_DATE = 4;
    public static final int DASHBOARD_MORE = 5;

    // Activity request code
    public static final int REQUEST_CODE = 1;
    public static final int LOGOUT_REQUEST_CODE = 2;

    public static final int SIMPLE_MATTER_REQUEST_CODE = 2;
    public static final int MATTER_REQUEST_CODE = 3;
    public static final int CONTACT_REQUEST_CODE = 4;
    public static final int PRESET_REQUEST_CODE = 5;
    public static final int VIEW_QUOTATION_REQUEST_CODE = 6;
    public static final int ACCOUNT_TYPE_REQUEST_CODE = 7;
    public static final int PAYMENT_MODE_REQUEST_CODE = 8;
    public static final int TAX_REQUEST_CODE = 9;
    public static final int ID_TYPE_REQUEST_CODE = 10;
    public static final int TITLE_REQUEST_CODE = 11;
    public static final int IRD_BRANCH_REQUEST_CODE = 12;
    public static final int FILE_STATUS_RQUEST_CODE = 13;
    public static final int STAFF_REQUEST_CODE = 14;
    public static final int SOLICITOR_ADD_REQUEST_CODE = 15;
    public static final int MATTER_BRANCH_REQUEST_CODE = 17;
    public static final int PARTY_ADD_REQUEST_CODE = 18;
    public static final int PROPERTY_ADD_REQUEST_CODE = 19;
    public static final int BANK_ADD_REQUEST_CODE = 21;
    public static final int COURT_DIARY_COURT_REQUEST_CODE = 22;
    public static final int COURT_CASE_TYPE_REQUEST_CODE = 23;
    public static final int COURT_CORAM_REQUEST_CODE = 24;
    public static final int TAX_SELECTION_REQUEST_CODE = 25;
    public static final int CODE_DESC_REQUEST_CODE = 26;
    public static final int DESC_REQUEST_CODE = 27;
    public static final int MUKIM_REQUEST_CODE = 28;
    public static final int PROJECT_HOUSING_REQUEST_CODE = 29;
    public static final int MASTER_TITLE_REQUEST_CODE = 30;
    public static final int AREA_TYPE_REQUEST_CODE = 31;
    public static final int OCCUPATION_REQUEST_CODE = 32;

    public static final int PERMISSION_REQUEST_CODE = 1;

    // Quickblox Chat
    public static final String QB_PASSWORD  = "denningIT";

    /*
        Broadcast messages
     */

    public static final String DOCUMENT_DOWNLOAD_PROGRESS = "Document Download";

    // Add
    public static String[] first_add_labels = {"Contact", "Property", "Matter"};
    public static String[] second_add_labels = {"Court Diary", "Office Diary", "Leave Application"};
    public static String[] third_add_labels = { "Quotation", "Tax Invoice", "Receipt"};
    public static String[] forth_add_labels = {"Attendance"};

    public static int[] first_add_images = {R.drawable.icon_contact_blue, R.drawable.icon_property, R.drawable.icon_matter};
    public static int[] second_add_images = {R.drawable.icon_court, R.drawable.icon_court, R.drawable.icon_leave};
    public static int[] third_add_images = {R.drawable.icon_quotation, R.drawable.icon_taxinvoice, R.drawable.icon_receipt};
    public static int[] forth_add_images = {R.drawable.icon_attendance};

    // Add Quotation , Bill
    String[] headers = {"Quotation Details", "Quotation Analysis"};

    public static String[] quotation_detail_labels = {"Quotation No(Auto assigned)", "File No.", "Matter", "Quotation to", "Preset Code", "Price", "Loan", "Month", "Rental", "Calculate"};
    public static boolean[] quotation_detail_has_details = {false, true, true, true, true, false,false, false,false, false};

    public static String[] bill_detail_labels = {"Convert Quotation", "Bill No(Auto assigned)", "File No.", "Matter", "Bill to", "Preset Code", "Price", "Loan", "Month", "Rental", "Calculate"};
    public static boolean[] bill_detail_has_details = {true, false, true, true, true, true, false,false, false,false, false};

    public static String[] receipt_labels = {"File No. (System)", "Bill No.", "Account Type", "Received From", "Amount", "Transaction"};
    public static boolean[] receipt_has_details = {true, true, true, true, false, true};

    public static String[] mode_payment_labels = {"Mode", "Issuer Bank", "Bank Branch", "Cheque No. / Ref. No.", "Cheque Amount", "Remarks"};
    public static boolean[] mode_payment_receipt_has_details = {true, true, true, false, false, false};

    public static String[] quotation_analysis_labels = {"Professional Fees", "Disb. with GST", "Disbursement", "GST", "Total.", "Save and View", "Convert To Tax Invoice", "Issue Receipt"};
    public static boolean[] quotation_analysis_has_details = {true, true, true, true, false, false, false, false};

    public static String[] bill_analysis_labels = {"Professional Fees", "Disb. with GST", "Disbursement", "GST", "Total.", "Save and View", "Issue Receipt"};
    public static boolean[] bill_analysis_has_details = {true, true, true, true, false, false, false};

    // Leave Application
    public static String[] leave_app_labels = {"Start Date", "End Date", "Type of Leave", "No. of Days", "Staff Remarks", "Submitted By", "Submit"};
    public static boolean[] leave_app_has_details = {true, true, true, false, false, false, false};

    // Contact
    public static String[] contact_personal_info_labels = {"ID Type *", "ID No *", "Old IC", "Name *", "Title"};
    public static boolean[] contact_personal_info_details = {true, false, false, false, true};

    public static String[] contact_contact_info_labels = {"Address 1", "Address 2", "Address 3", "Postcode", "Town", "State", "Country", "Phone (Mobile)", "Phone (Home)", "Phone (Office)", "Fax", "Email", "Website", "Contact Person"};
    public static boolean[] contact_contact_info_details = {false, false, false, true, true, true, true, false, false, false, false, false, false, false};

    public static String[] contact_other_info_labels = {"Citizenship", "Date of Birth", "Occupation", "Tax File No.", "IRD Branch"};
    public static boolean[] contact_other_info_details = {true, true, true, false, true};

    public static String[] contact_company_info_labels = {"Registered Office"};
    public static boolean[] contact_company_info_details = {false};

    public static String[] contact_invitation_labels = {"Invite to Denning"};
    public static boolean[] contact_invitation_details = {false};

    // Property
    public static String[] property_main_labels = {"Property Type", "Individual / Strata Title", "ID (System assigned)"};
    public static boolean[] property_main_details = {true, true, false};

    public static String[] property_title_details_labels = {"Mukim Type"};

    // Matter
    public static String[] matter_information_labels = {"File No (Auto assigned)", "Ref 2", "Primary Client", "File Status", "Partner-in-Charge", "LA-in-Charge", "Clerk-in-Charge", "Matter", "Branch", "File Location", "Pocket Location", "Storage Location", "Save"};
    public static boolean[] matter_information_details = {false, false, true, true, true, true, true, true, true, false, false, false, false};

    public static String[] matter_remarks_labels = {"Notes"};
    public static boolean[] matter_remarks_details = {false};

    public static String[] matter_case_details_labels = {"Case Type", "Type No", "Court", "Place", "Judge", "SAR"};
    public static boolean[] matter_case_details_details = {true, false, true, true, true, true};

    public static String[] matter_properties_labels = {"Add Property"};
    public static boolean[] matter_properties_details = {false};

    public static String[] matter_partygroup_labels = {};
    public static boolean[] matter_partygroup_details = {};

    public static String[] matter_solicitors_labels = {"Vendor's Solicitors", "Purchaser's Solicitors", "Borrower's Solicitors"};
    public static boolean[] matter_solicitors_details = {false, false, false};

    public static String[] matter_banks_labels = {"Vendor's Bank", "Borrower's Bank"};
    public static boolean[] matter_banks_details = {false, false, false};

    public static String[] matter_important_RM_labels = {};
    public static boolean[] matter_important_RM_details = {};

    public static String[] matter_important_dates_labels = {};
    public static boolean[] matter_important_dates_details = {};
}
