package my.com.engpeng.engpengsalesorder.utilities;

import android.net.Uri;
import android.util.Base64;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Scanner;

import my.com.engpeng.engpengsalesorder.Global;

public class NetworkUtils {


    public final static String PARAM_TYPE = "type";
    public final static String PARAM_ACTION = "action";
    public final static String PARAM_UNIQUE_ID = "uniqueId";
    public final static String PARAM_EMAIL = "email";

    final static String DEPLOYMENT_URL = "http://epgroup.dlinkddns.com:5030/eperp/";
    final static String ENGPENG_BASE_URL = DEPLOYMENT_URL;
    final static String LOCAL_URL = "http://192.168.8.1:8833/eperp/";
    final static String PARAM_MODULE = "r";

    final public static String MODULE_GET_HOUSE_KEEPING = "ApiMobileSalesorder/GetHouseKeeping";
    final public static String MODULE_UPLOAD = "ApiMobileSalesorder/Upload";
    final public static String MODULE_GET_HISTORY = "ApiMobileSalesorder/GetHistory";
    final public static String MODULE_AUTH_LOGIN = "MobileAuth/Login";

    final public static String ENCODE = "UTF-8";

    public static URL buildUrl(String module, boolean is_local) {
        String connection_url = ENGPENG_BASE_URL;

        if (is_local) {
            connection_url = LOCAL_URL;
        }

        Uri builtUri = Uri.parse(connection_url).buildUpon()
                .appendQueryParameter(PARAM_MODULE, module)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    public static String sendPostToHttpUrl(URL url, String username, String password, String data) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

        String user_pass = username + ":" + password;
        String basic_auth = "Basic " + new String(Base64.encode(user_pass.getBytes(), android.util.Base64.DEFAULT));
        urlConnection.setRequestProperty("Authorization", basic_auth);
        urlConnection.setRequestMethod("POST");
        urlConnection.setDoOutput(true);

        try {
            OutputStreamWriter wr = new OutputStreamWriter(urlConnection.getOutputStream());
            wr.write(data);
            wr.flush();

            InputStream in = urlConnection.getInputStream();
            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");
            boolean hasInput = scanner.hasNext();


            if (hasInput) {
                String str = scanner.next();
                return str;
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }

    public static String buildParam(String key, String value) {
        try {
            return "&" + URLEncoder.encode(key, ENCODE) + "=" + URLEncoder.encode(value, ENCODE);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
}
