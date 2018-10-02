package my.com.engpeng.engpengsalesorder;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import my.com.engpeng.engpengsalesorder.database.branch.BranchEntry;
import my.com.engpeng.engpengsalesorder.database.customerCompany.CustomerCompanyEntry;
import my.com.engpeng.engpengsalesorder.database.customerCompanyAddress.CustomerCompanyAddressEntry;
import my.com.engpeng.engpengsalesorder.database.itemPacking.ItemPackingEntry;
import my.com.engpeng.engpengsalesorder.database.priceSetting.PriceSettingEntry;

public class Global {

    public static String sUsername;
    public static String sPassword;
    public static String sUniqueId;
    public static long sCompanyId;
    public static String sCompanyCode;
    public static String sCompanyName;
    public static String sCompanyShortName;

    public static final int RC_GOOGLE_SIGN_IN = 8001;
    public static final int RC_BLUETOOTH = 8002;

    //Preferences param keys
    public static final String PREF_KEY = "PREF_KEY";
    public static final String P_KEY_USERNAME = "P_KEY_USERNAME";
    public static final String P_KEY_PASSWORD = "P_KEY_PASSWORD";
    public static final String P_KEY_COMPANY_ID = "P_KEY_COMPANY_ID";
    public static final String P_KEY_UNIQUE_ID = "P_KEY_UNIQUE_ID";

    public static final String ACTION_REFRESH = "refresh";
    public static final String ACTION_UPDATE = "update";
    public static final String ACTION_GET_ALL_TABLE = "all";

    //Intent param keys
    public static final String I_KEY_ACTION = "I_KEY_ACTION";
    public static final String I_KEY_TABLE = "I_KEY_TABLE";
    public static final String I_KEY_LOCAL = "I_KEY_LOCAL";

    public static final String I_KEY_COMPANY_ID = "I_KEY_COMPANY_ID";
    public static final String I_KEY_CUSTOMER_COMPANY_ID = "I_KEY_CUSTOMER_COMPANY_ID";
    public static final String I_KEY_CUSTOMER_ADDRESS_ID = "I_KEY_CUSTOMER_ADDRESS_ID";
    public static final String I_KEY_DOCUMENT_DATE = "I_KEY_DOCUMENT_DATE";
    public static final String I_KEY_DELIVERY_DATE = "I_KEY_DELIVERY_DATE";
    public static final String I_KEY_LPO = "I_KEY_LPO";
    public static final String I_KEY_REMARK = "I_KEY_REMARK";
    public static final String I_KEY_PRINTOUT = "I_KEY_PRINTOUT";

    public static final String I_KEY_SALESORDER_ENTRY = "I_KEY_SALESORDER_ENTRY";
    public static final String I_KEY_TRANSITION_NAME = "I_KEY_TRANSITION_NAME";

    public static final String I_KEY_PRICE_GROUP_ID = "I_KEY_PRICE_GROUP_ID";
    public static final String I_KEY_PRICE_BY_WEIGHT = "I_KEY_DELIVERY_DATE";
    public static final String I_KEY_FACTOR = "I_KEY_FACTOR";

    public static final String I_KEY_REVEAL_ANIMATION_SETTINGS = "I_KEY_REVEAL_ANIMATION_SETTINGS";

    public static final String DATE_DISPLAY_FORMAT = "EEE, d MMM yyyy";
    public static final String DATE_SAVE_FORMAT = "yyyy-MM-dd";
    public static final String DATETIME_SAVE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String YEARMONTH_SAVE_FORMAT = "yyyy-MM";
    public static final String YEAR_SAVE_FORMAT = "yyyy";

    //Salesorder Status
    public static final String SO_STATUS_DRAFT = "DRAFT";
    public static final String SO_STATUS_CONFIRM = "CONFIRM";
    public static final String SO_STATUS_ALL = "ALL";

    public static final String DATE_TYPE_YEAR = "YEAR";
    public static final String DATE_TYPE_MONTH = "MONTH";
    public static final String DATE_TYPE_DAY = "DAY";

    public static final String LOG_TASK_UPLOAD = "LOG_TASK_UPLOAD";
    public static final String LOG_TASK_UPDATE_HK = "LOG_TASK_UPDATE_HK";

    public static final String RUNNING_CODE_SALESORDER = "S";
    public static final String SALESORDER_YEARMONTH_FORMAT = "yyyyMM";

    public enum PriceMethod {
        STANDARD, CUSTOMER, SELF
    }
}
