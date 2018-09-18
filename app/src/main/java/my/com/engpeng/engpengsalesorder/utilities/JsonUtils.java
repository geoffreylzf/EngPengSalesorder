package my.com.engpeng.engpengsalesorder.utilities;

import android.content.Context;

import org.json.JSONObject;

import java.net.HttpURLConnection;

public class JsonUtils {

    private static final String COD = "cod";
    private static final String ACTION = "action";
    private static final String TYPE = "type";
    private static final String LAST_SYNC_DATE = "last_sync_date";
    private static final String LIMIT_START = "limit_start";
    private static final String LIMIT_ROW = "limit_row";
    private static final String TOTAL_ROW = "total_row";

    public static boolean getAuthentication(Context context, String jsonStr) {
        try {
            JSONObject json = new JSONObject(jsonStr);

            if (json.has(COD)) {
                int errorCode = json.getInt(COD);

                switch (errorCode) {
                    case HttpURLConnection.HTTP_OK:
                        return true;
                    case HttpURLConnection.HTTP_NOT_FOUND:
                        UiUtils.showToastMessage(context, "The request URL was not found");
                        return false;
                    case HttpURLConnection.HTTP_NOT_ACCEPTABLE:
                        UiUtils.showToastMessage(context, "Insufficient data to login");
                        return false;
                    case HttpURLConnection.HTTP_UNAUTHORIZED:
                        UiUtils.showToastMessage(context, "Unauthorized (Please check username or password)");
                        return false;
                    default:
                        UiUtils.showToastMessage(context, "Unknown error");
                        return false;
                }
            }
            UiUtils.showToastMessage(context, "Error (no respond)");
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            UiUtils.showToastMessage(context, "Error (" + e.getMessage() + ")");
            return false;
        }
    }

    public static String getAction(String jsonStr) {
        try {
            JSONObject json = new JSONObject(jsonStr);

            if (json.has(ACTION)) {
                return json.getString(ACTION);
            }
            return "";
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String getType(String jsonStr) {
        try {
            JSONObject json = new JSONObject(jsonStr);

            if (json.has(TYPE)) {
                return json.getString(TYPE);
            }
            return "";
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String getLastSyncDate(String jsonStr) {
        try {
            JSONObject json = new JSONObject(jsonStr);

            if (json.has(LAST_SYNC_DATE)) {
                return json.getString(LAST_SYNC_DATE);
            }
            return "";
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static int getLimitStart(String jsonStr) {
        try {
            JSONObject json = new JSONObject(jsonStr);

            if (json.has(LIMIT_START)) {
                return json.getInt(LIMIT_START);
            }
            return -1;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    public static int getTotalRow(String jsonStr) {
        try {
            JSONObject json = new JSONObject(jsonStr);

            if (json.has(TOTAL_ROW)) {
                return json.getInt(TOTAL_ROW);
            }
            return 0;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
}
