package my.com.engpeng.engpengsalesorder.utilities;

import android.content.Context;
import android.content.SharedPreferences;

import my.com.engpeng.engpengsalesorder.model.User;

import static my.com.engpeng.engpengsalesorder.Global.PREF_KEY;
import static my.com.engpeng.engpengsalesorder.Global.P_KEY_COMPANY_ID;
import static my.com.engpeng.engpengsalesorder.Global.P_KEY_PASSWORD;
import static my.com.engpeng.engpengsalesorder.Global.P_KEY_USERNAME;

public class SharedPreferencesUtils {

    public static SharedPreferences getSharedPreferences(Context context){
        return context.getSharedPreferences(PREF_KEY, Context.MODE_PRIVATE);
    }

    public static void saveCompanyId(Context context, long company_id) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong(P_KEY_COMPANY_ID, company_id);
        editor.apply();
    }

    public static long getCompanyId(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_KEY, Context.MODE_PRIVATE);
        if (prefs.contains(P_KEY_COMPANY_ID)) {
            return prefs.getLong(P_KEY_COMPANY_ID, 0);
        } else {
            return 0;
        }
    }

    public static void clearCompanyId(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(P_KEY_COMPANY_ID);
        editor.apply();
    }

    public static void saveUsernamePassword(Context context, String username, String password) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(P_KEY_USERNAME, username);
        editor.putString(P_KEY_PASSWORD, password);
        editor.apply();
    }

    public static User getUsernamePassword(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_KEY, Context.MODE_PRIVATE);
        if (prefs.contains(P_KEY_USERNAME) && prefs.contains(P_KEY_PASSWORD)) {
            return new User(prefs.getString(P_KEY_USERNAME, ""), prefs.getString(P_KEY_PASSWORD, ""));
        } else {
            return null;
        }
    }

    public static void clearUsernamePassword(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(P_KEY_USERNAME);
        editor.remove(P_KEY_PASSWORD);
        editor.apply();
    }


}
