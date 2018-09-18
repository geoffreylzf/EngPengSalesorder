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

    public static String sUsername = "geoffrey.lee";
    public static String sPassword = "12345";

    public static final int GET_HOUSE_KEEPING_LOADER_ID = 1001;
    public static final int RECEIVE_HOUSE_KEEPING_LOADER_ID = 1002;

    public static final int RC_CONFIRM_DIALOG_ID = 8001;

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

    public static final String I_KEY_PRICE_GROUP_ID = "I_KEY_PRICE_GROUP_ID";
    public static final String I_KEY_PRICE_BY_WEIGHT = "I_KEY_DELIVERY_DATE";
    public static final String I_KEY_FACTOR = "I_KEY_FACTOR";

    public static final String I_KEY_REVEAL_ANIMATION_SETTINGS = "I_KEY_REVEAL_ANIMATION_SETTINGS";

    public static final String DATE_DISPLAY_FORMAT = "EEE, d MMM yyyy";
    public static final String DATE_SAVE_FORMAT = "yyyy-MM-dd";
    public static final String DATETIME_SAVE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String YEARMONTH_SAVE_FORMAT = "yyyy-MM";
    public static final String YEAR_SAVE_FORMAT = "yyyy";

    //DIALOG TAG
    public static final String ALERT_DIALOG_TAG = "ALERT_DIALOG_TAG";
    public static final String CONFIRM_DIALOG_TAG = "CONFIRM_DIALOG_TAG";

    //Salesorder Status
    public static final String SO_STATUS_DRAFT = "DRAFT";
    public static final String SO_STATUS_CONFIRM = "CONFIRM";

    public static final String DATE_TYPE_YEAR = "YEAR";
    public static final String DATE_TYPE_MONTH = "MONTH";
    public static final String DATE_TYPE_DAY = "DAY";

    public static final String RUNNING_CODE_SALESORDER = "S";

    public enum PriceMethod {
        STANDARD, CUSTOMER, SELF
    }
}
