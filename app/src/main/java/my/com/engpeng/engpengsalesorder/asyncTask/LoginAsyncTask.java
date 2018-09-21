package my.com.engpeng.engpengsalesorder.asyncTask;

import android.os.AsyncTask;

import java.io.IOException;
import java.net.URL;

import my.com.engpeng.engpengsalesorder.utilities.NetworkUtils;

public class LoginAsyncTask extends AsyncTask<Void, Void, String> {

    private String username, password, data;
    private boolean isLocal;
    private LoginAsyncTaskListener latListener;

    public interface LoginAsyncTaskListener{
        void onPreExecute();
        void onResultReturn(String json);
    }

    public LoginAsyncTask(String username, String password, String data, boolean isLocal, LoginAsyncTaskListener latListener) {
        this.username = username;
        this.password = password;
        this.data = data;
        this.isLocal = isLocal;
        this.latListener = latListener;
    }

    @Override
    protected void onPreExecute() {
        latListener.onPreExecute();
    }

    @Override
    protected String doInBackground(Void... voids) {

        URL url = NetworkUtils.buildUrl(NetworkUtils.MODULE_AUTH_LOGIN, isLocal);
        String json = null;

        try {
            json = NetworkUtils.sendPostToHttpUrl(url, username, password, data);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return json;
    }

    @Override
    protected void onPostExecute(String json) {
        latListener.onResultReturn(json);
    }
}
