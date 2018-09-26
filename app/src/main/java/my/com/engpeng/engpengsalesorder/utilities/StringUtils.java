package my.com.engpeng.engpengsalesorder.utilities;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import my.com.engpeng.engpengsalesorder.Global;
import my.com.engpeng.engpengsalesorder.database.branch.BranchEntry;
import my.com.engpeng.engpengsalesorder.database.customerCompany.CustomerCompanyEntry;
import my.com.engpeng.engpengsalesorder.database.customerCompanyAddress.CustomerCompanyAddressEntry;
import my.com.engpeng.engpengsalesorder.database.itemPacking.ItemPackingEntry;
import my.com.engpeng.engpengsalesorder.database.priceSetting.PriceSettingEntry;

import static my.com.engpeng.engpengsalesorder.Global.DATETIME_SAVE_FORMAT;
import static my.com.engpeng.engpengsalesorder.Global.DATE_SAVE_FORMAT;
import static my.com.engpeng.engpengsalesorder.Global.SALESORDER_YEARMONTH_FORMAT;
import static my.com.engpeng.engpengsalesorder.Global.YEARMONTH_SAVE_FORMAT;
import static my.com.engpeng.engpengsalesorder.Global.YEAR_SAVE_FORMAT;

public class StringUtils {

    public static String getDisplayRunningNo(String runningNo) {
        String[] arr = runningNo.split("-");
        return arr[1] + "-" + arr[2] + "-" + arr[3] + "-" + arr[4];
    }

    public static String getCurrentDateTime() {
        DateFormat df = new SimpleDateFormat(DATETIME_SAVE_FORMAT, Locale.US);
        Date currentTime = Calendar.getInstance().getTime();
        return df.format(currentTime);
    }

    public static String getCurrentDate() {
        DateFormat df = new SimpleDateFormat(DATE_SAVE_FORMAT, Locale.US);
        Date currentTime = Calendar.getInstance().getTime();
        return df.format(currentTime);
    }

    public static String getCurrentYearMonth() {
        DateFormat df = new SimpleDateFormat(YEARMONTH_SAVE_FORMAT, Locale.US);
        Date currentTime = Calendar.getInstance().getTime();
        return df.format(currentTime);
    }

    public static String getCurrentYear() {
        DateFormat df = new SimpleDateFormat(YEAR_SAVE_FORMAT, Locale.US);
        Date currentTime = Calendar.getInstance().getTime();
        return df.format(currentTime);
    }

    public static String getSoYearMonthFormat(String documentDate){
        SimpleDateFormat sdfOld = new SimpleDateFormat(DATE_SAVE_FORMAT, Locale.US);
        SimpleDateFormat sdfNew = new SimpleDateFormat(SALESORDER_YEARMONTH_FORMAT, Locale.US);
        try{
            return sdfNew.format(sdfOld.parse(documentDate).getTime());
        }catch (ParseException e) {
            return  documentDate;
        }
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
        } else if (table_name.equals(BranchEntry.TABLE_NAME)) {
            return BranchEntry.TABLE_DISPLAY_NAME;
        }
        return null;
    }

    public static String getDisplaySoStatus(String status) {
        if (status.equals(Global.SO_STATUS_CONFIRM)) {
            return "Confirm";
        } else if (status.equals(Global.SO_STATUS_DRAFT)) {
            return "Draft";
        } else if (status.equals(Global.SO_STATUS_ALL)) {
            return "All";
        }
        return "";
    }
}
