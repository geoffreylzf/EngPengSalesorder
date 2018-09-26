package my.com.engpeng.engpengsalesorder.utilities;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.HttpURLConnection;

import my.com.engpeng.engpengsalesorder.model.Status;

public class JsonUtils {

    public static final String COD = "cod";
    public static final String SUCCESS = "success";
    public static final String MESSAGE = "message";
    public static final String ACTION = "action";
    public static final String TYPE = "type";
    public static final String IMPORTED_MOBILE_IDS = "imported_mobile_ids";
    public static final String LIMIT_START = "limit_start";
    public static final String LIMIT_ROW = "limit_row";
    public static final String TOTAL_ROW = "total_row";

    public static Status getAuthentication(String jsonStr) {
        try {
            JSONObject json = new JSONObject(jsonStr);

            if (json.has(COD)) {
                int errorCode = json.getInt(COD);

                switch (errorCode) {
                    case HttpURLConnection.HTTP_OK:
                        return new Status(true, "Success");
                    case HttpURLConnection.HTTP_NOT_FOUND:
                        return new Status(false, "The request URL was not found");
                    case HttpURLConnection.HTTP_NOT_ACCEPTABLE:
                        return new Status(false, "Insufficient data to login");
                    case HttpURLConnection.HTTP_UNAUTHORIZED:
                        return new Status(false, "Unauthorized (Please check username or password)");
                    default:
                        return new Status(false, "Unknown error");
                }
            }
            return new Status(false, "Error (no respond)");
        } catch (Exception e) {
            e.printStackTrace();
            return new Status(false, "Error (" + e.getMessage() + ")");
        }
    }

    public static Status getLoginAuthentication(String jsonStr) {
        try {
            JSONObject json = new JSONObject(jsonStr);
            if (json.has(SUCCESS)) {
                boolean success = json.getBoolean(SUCCESS);
                if (success) {
                    return new Status(true, "Success");
                } else {
                    String message = json.getString(MESSAGE);
                    return new Status(false, "Error (" + message + ")");
                }
            } else {
                return new Status(false, "Login Authentication Error (no respond)");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new Status(false, "Login Authentication Error (no respond)");
        }
    }

    public static String getString(String jsonStr, String key) {
        try {
            JSONObject json = new JSONObject(jsonStr);

            if (json.has(key)) {
                return json.getString(key);
            }
            return "";
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static JSONArray getJsonArray(String jsonStr, String key) {
        try {
            JSONObject json = new JSONObject(jsonStr);

            if (json.has(key)) {
                return json.getJSONArray(key);
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
