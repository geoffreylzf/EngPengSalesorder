package my.com.engpeng.engpengsalesorder.asyncTask;

import android.app.Activity;
import android.util.Log;

import java.io.IOException;
import java.net.URL;

import my.com.engpeng.engpengsalesorder.utilities.NetworkUtils;

public class LoginRunnable implements Runnable {

    private Activity activity;
    private String username, password, data;
    private boolean isLocal;
    private LoginRunnableListener lrListener;
    String json;

    public LoginRunnable(Activity activity, String username, String password, String data, boolean isLocal, LoginRunnableListener lrListener) {
        this.username = username;
        this.password = password;
        this.data = data;
        this.isLocal = isLocal;
        this.lrListener = lrListener;
        this.activity = activity;
    }

    public interface LoginRunnableListener{
        void onStart();
        void onResult(String json);
    }

    @Override
    public void run() {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                lrListener.onStart();
            }
        });

        URL url = NetworkUtils.buildUrl(NetworkUtils.MODULE_AUTH_LOGIN, isLocal);

        try {
            json = NetworkUtils.sendPostToHttpUrl(url, username, password, data);
        } catch (IOException e) {
            e.printStackTrace();
        }

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                lrListener.onResult(json);
            }
        });
    }
}
