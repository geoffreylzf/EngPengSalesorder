package my.com.engpeng.engpengsalesorder;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

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
    public static final String I_KEY_CUSTOMER_COMPANY_ID = "I_KEY_CUSTOMER_COMPANY_ID";

    public static final String DATE_DISPLAY_FORMAT = "EEE, d MMM yyyy";
    public static final String DATE_SAVE_FORMAT = "yyyy-MM-dd";

    public static String getCurrentDateTime(){
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        Date currentTime = Calendar.getInstance().getTime();
        return df.format(currentTime);
    }

    public static String getTableDisplayName(String table_name){
        if(table_name.equals(ItemPackingEntry.TABLE_NAME)){
            return ItemPackingEntry.TABLE_DISPLAY_NAME;
        }else if(table_name.equals(PriceSettingEntry.TABLE_NAME)){
            return PriceSettingEntry.TABLE_DISPLAY_NAME;
        }else if(table_name.equals(CustomerCompanyEntry.TABLE_NAME)){
            return CustomerCompanyEntry.TABLE_DISPLAY_NAME;
        }else if(table_name.equals(CustomerCompanyAddressEntry.TABLE_NAME)){
            return CustomerCompanyAddressEntry.TABLE_DISPLAY_NAME;
        }
        return null;
    }
}
