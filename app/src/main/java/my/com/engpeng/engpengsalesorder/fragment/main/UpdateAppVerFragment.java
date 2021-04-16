package my.com.engpeng.engpengsalesorder.fragment.main;

import android.animation.LayoutTransition;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Locale;

import my.com.engpeng.engpengsalesorder.R;
import my.com.engpeng.engpengsalesorder.utilities.UiUtils;


public class UpdateAppVerFragment extends Fragment {

    public static final String tag = "MAIN_UPDATE_APP_VER_FRAGMENT";

    private TextView tvVersionName, tvVersionCode;
    private Button btnUpdate;
    private CheckBox cbLocal;

    final static String GLOBAL_HOST = "http://epgroup.dyndns.org:5031/";
    final static String LOCAL_HOST = "http://192.168.8.6/";

    private String appCode = "";
    private String versionName;
    private int versionCode;

    private Dialog progressDialog;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_update_app_ver, container, false);

        ((ViewGroup) rootView.findViewById(R.id.uav_cl)).getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);


        tvVersionName = rootView.findViewById(R.id.uav_tv_current_ver_name);
        tvVersionCode = rootView.findViewById(R.id.uav_tv_current_ver_code);
        btnUpdate = rootView.findViewById(R.id.uav_btn_update_app_ver);
        cbLocal = rootView.findViewById(R.id.uav_cb_local);

        progressDialog = UiUtils.getProgressDialog(getContext());


        getActivity().setTitle("Update Application Version");

        try {
            PackageInfo pInfo = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);
            versionName = pInfo.versionName;
            versionCode = pInfo.versionCode;
            appCode = pInfo.packageName;

            tvVersionName.setText("Current Version Name : " + versionName);
            tvVersionCode.setText("Current Version Code : " + versionCode);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateApp();
            }
        });

        return rootView;
    }

    private void updateApp() {
        String host = GLOBAL_HOST;
        if (cbLocal.isChecked()) {
            host = LOCAL_HOST;
        }

        String url = String.format("%s%s%s%s",
                host,
                "api/info/mobile/apps/",
                appCode,
                "/latest");

        RequestQueue queue = Volley.newRequestQueue(getActivity());

        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        progressDialog.hide();
                        try {
                            int latestVerCode = response.getInt("version_code");

                            if (latestVerCode > versionCode) {
                                Uri uri = Uri.parse(response.getString("download_link"));
                                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                startActivity(intent);

                            } else if (latestVerCode == versionCode) {
                                UiUtils.showAlertDialog(getActivity().getSupportFragmentManager(),
                                        "Info", "Current app is the latest version");
                            } else {
                                UiUtils.showAlertDialog(getActivity().getSupportFragmentManager(),
                                        "Error",
                                        String.format(Locale.ENGLISH, "Current Ver : %d \nLatest Ver : %d", versionCode, latestVerCode));
                            }
                        } catch (Exception e) {
                            UiUtils.showAlertDialog(getActivity().getSupportFragmentManager(), "Error", e.toString());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.hide();
                        String err = "Unknown error";
                        if (error instanceof TimeoutError) {
                            err = "Timeout error";
                        } else {
                            if (error.networkResponse.data != null) {
                                try {
                                    err = new String(error.networkResponse.data, "UTF-8");
                                } catch (UnsupportedEncodingException e) {
                                    UiUtils.showAlertDialog(getActivity().getSupportFragmentManager(), "Error", e.toString());
                                }
                            }
                        }
                        UiUtils.showAlertDialog(getActivity().getSupportFragmentManager(), "Error", err);
                    }
                });

        progressDialog.show();
        queue.add(jsonRequest);

    }
}

