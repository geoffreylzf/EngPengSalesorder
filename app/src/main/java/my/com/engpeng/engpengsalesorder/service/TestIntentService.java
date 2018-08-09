package my.com.engpeng.engpengsalesorder.service;

import android.app.IntentService;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URL;
import java.net.URLEncoder;

import my.com.engpeng.engpengsalesorder.Global;
import my.com.engpeng.engpengsalesorder.R;
import my.com.engpeng.engpengsalesorder.database.AppDatabase;
import my.com.engpeng.engpengsalesorder.database.priceSetting.PriceSettingEntry;
import my.com.engpeng.engpengsalesorder.database.tableList.TableInfoEntry;
import my.com.engpeng.engpengsalesorder.utilities.JsonUtils;
import my.com.engpeng.engpengsalesorder.utilities.NetworkUtils;

import static my.com.engpeng.engpengsalesorder.Global.ACTION_REFRESH;
import static my.com.engpeng.engpengsalesorder.Global.sPassword;
import static my.com.engpeng.engpengsalesorder.Global.sUsername;


public class TestIntentService extends IntentService {

    private static final String UPDATE_PRICE_SETTING_NOTIFICATION_CHANNEL_ID = "UPDATE_PRICE_SETTING";
    private static final int UPDATE_PRICE_SETTING_NOTIFICATION_ID = 1001;
    private static final int PROGRESS_MAX = 100;
    private static final int PROGRESS_MIN = 0;
    private NotificationCompat.Builder notificationUpdateBuilder;
    private NotificationManager notificationManager;
    private int progress_current = 0;
    private String last_sync_date;
    private AppDatabase mDb;

    public TestIntentService() {
        super("TestIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        mDb = AppDatabase.getInstance(getApplicationContext());

        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(
                    UPDATE_PRICE_SETTING_NOTIFICATION_CHANNEL_ID,
                    getString(R.string.main_notification_channel_name),
                    NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(mChannel);
        }

        notificationUpdateBuilder = new NotificationCompat.Builder(this, UPDATE_PRICE_SETTING_NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(android.R.drawable.stat_sys_download)
                .setContentTitle("Update Price Setting")
                .setContentText("Downloading from server...")
                .setOngoing(true);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            notificationUpdateBuilder.setPriority(NotificationCompat.PRIORITY_HIGH);
        }

        notificationUpdateBuilder.setProgress(PROGRESS_MAX, PROGRESS_MIN, false);
        notificationManager.notify(UPDATE_PRICE_SETTING_NOTIFICATION_ID, notificationUpdateBuilder.build());

        startForeground(UPDATE_PRICE_SETTING_NOTIFICATION_ID, notificationUpdateBuilder.build());

        InsertDataTask();

        stopForeground(STOP_FOREGROUND_DETACH);

        notificationUpdateBuilder
                .setSmallIcon(android.R.drawable.stat_sys_download_done)
                .setContentText("Update complete")
                .setOngoing(false)
                .setProgress(0, 0, false);
        notificationManager.notify(UPDATE_PRICE_SETTING_NOTIFICATION_ID, notificationUpdateBuilder.build());
    }

    private void InsertDataTask() {

        last_sync_date = Global.getCurrentDateTime();

        try {

            String username = sUsername;
            String password = sPassword;
            String data = "&" + URLEncoder.encode("type", NetworkUtils.ENCODE) + "="
                    + URLEncoder.encode("price_setting", NetworkUtils.ENCODE);
            URL url = NetworkUtils.buildUrl(NetworkUtils.MODULE_GET_HOUSE_KEEPING, false);
            String json = NetworkUtils.sendPostToHttpUrl(url, username, password, data);

            if (json != null && !json.equals("")) {
                if (JsonUtils.getAuthentication(this, json)) {
                    String table_info_type = JsonUtils.getType(json);
                    String action = JsonUtils.getAction(json);

                    if (table_info_type.equals(PriceSettingEntry.TABLE_NAME)) {
                        if (action.equals(ACTION_REFRESH)) {
                            mDb.priceSettingDao().deleteAll();
                        }
                        InsertPriceSetting(json);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void InsertPriceSetting(String jsonStr) {
        try {

            JSONObject json = new JSONObject(jsonStr);
            JSONArray jsonArray = json.getJSONArray(PriceSettingEntry.TABLE_NAME);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                PriceSettingEntry priceSetting = new PriceSettingEntry(jsonObject);

                mDb.priceSettingDao().insertPriceSetting(priceSetting);

                TableInfoEntry tableInfo = new TableInfoEntry(PriceSettingEntry.TABLE_NAME, last_sync_date, (i + 1), jsonArray.length());
                mDb.tableInfoDao().insertTableInfo(tableInfo);

                double row = i + 1;
                double total = (double) jsonArray.length();
                int percentage = (int) (row / total * 100);

                if (percentage != progress_current) {
                    if (percentage % 5 == 0) {
                        notificationUpdateBuilder.setContentText("Updating...").setProgress(PROGRESS_MAX, percentage, false);
                        notificationManager.notify(UPDATE_PRICE_SETTING_NOTIFICATION_ID, notificationUpdateBuilder.build());
                        progress_current = percentage;
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
