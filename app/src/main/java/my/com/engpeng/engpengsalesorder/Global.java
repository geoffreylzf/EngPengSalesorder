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

    public static final int GET_HOUSE_KEEPING_LOADER_ID = 1001;
    public static final int RECEIVE_HOUSE_KEEPING_LOADER_ID = 1002;

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

    public static final String DATE_DISPLAY_FORMAT = "EEE, d MMM yyyy";
    public static final String DATE_SAVE_FORMAT = "yyyy-MM-dd";

    public static String getCurrentDateTime() {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        Date currentTime = Calendar.getInstance().getTime();
        return df.format(currentTime);
    }

    public static String getDisplayPrice(double price) {
        return String.format(Locale.getDefault(), "RM %.2f", price);
    }

    public static String getDisplayQty(int qty) {
        return String.format(Locale.getDefault(), "Qty: %d", qty);
    }

    public static String getDisplayWgt(double wgt) {
        return String.format(Locale.getDefault(), "Wgt: %sKg", (new DecimalFormat("##.###")).format(wgt));
    }

    public static String getTableDisplayName(String table_name) {
        if (table_name.equals(ItemPackingEntry.TABLE_NAME)) {
            return ItemPackingEntry.TABLE_DISPLAY_NAME;
        } else if (table_name.equals(PriceSettingEntry.TABLE_NAME)) {
            return PriceSettingEntry.TABLE_DISPLAY_NAME;
        } else if (table_name.equals(CustomerCompanyEntry.TABLE_NAME)) {
            return CustomerCompanyEntry.TABLE_DISPLAY_NAME;
        } else if (table_name.equals(CustomerCompanyAddressEntry.TABLE_NAME)) {
            return CustomerCompanyAddressEntry.TABLE_DISPLAY_NAME;
        }else if (table_name.equals(BranchEntry.TABLE_NAME)) {
            return BranchEntry.TABLE_DISPLAY_NAME;
        }
        return null;
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager inputManager = (InputMethodManager) activity
                .getSystemService(Context.INPUT_METHOD_SERVICE);

        // check if no view has focus:
        View currentFocusedView = activity.getCurrentFocus();
        if (currentFocusedView != null) {
            inputManager.hideSoftInputFromWindow(currentFocusedView.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    public enum PriceMethod {
        STANDARD, CUSTOMER, SELF
    }
}
