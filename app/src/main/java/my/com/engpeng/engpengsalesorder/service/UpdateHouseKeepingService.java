package my.com.engpeng.engpengsalesorder.service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

import java.util.ArrayList;
import java.util.List;

import my.com.engpeng.engpengsalesorder.R;
import my.com.engpeng.engpengsalesorder.asyncTask.UpdateHouseKeepingAsyncTask;
import my.com.engpeng.engpengsalesorder.database.AppDatabase;
import my.com.engpeng.engpengsalesorder.database.branch.BranchEntry;
import my.com.engpeng.engpengsalesorder.database.customerCompany.CustomerCompanyEntry;
import my.com.engpeng.engpengsalesorder.database.customerCompanyAddress.CustomerCompanyAddressEntry;
import my.com.engpeng.engpengsalesorder.database.itemPacking.ItemPackingEntry;
import my.com.engpeng.engpengsalesorder.database.priceSetting.PriceSettingEntry;
import my.com.engpeng.engpengsalesorder.database.tableList.TableInfoEntry;
import my.com.engpeng.engpengsalesorder.utilities.StringUtils;

import static my.com.engpeng.engpengsalesorder.Global.ACTION_GET_ALL_TABLE;
import static my.com.engpeng.engpengsalesorder.Global.I_KEY_ACTION;
import static my.com.engpeng.engpengsalesorder.Global.I_KEY_LOCAL;
import static my.com.engpeng.engpengsalesorder.Global.I_KEY_TABLE;

public class UpdateHouseKeepingService extends Service implements
        UpdateHouseKeepingAsyncTask.UpdateHouseKeepingAsyncTaskListener {

    private static final String UPDATE_HOUSE_KEEPING_NOTIFICATION_CHANNEL_ID = "UPDATE_HOUSE_KEEPING";
    private static final int UPDATE_HOUSE_KEEPING_NOTIFICATION_ID = 1001;

    private static final int PROGRESS_MAX = 100;
    private static final int PROGRESS_MIN = 0;

    private NotificationCompat.Builder notificationBuilder;
    private NotificationManager notificationManager;
    private int progressCurrent = 0;
    boolean isLocal;
    private AppDatabase mDb;

    public UpdateHouseKeepingService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mDb = AppDatabase.getInstance(getApplicationContext());
        setupNotificationBuilder();
        setupNotificationManager();
    }

    private void setupNotificationBuilder() {
        notificationBuilder = new NotificationCompat.Builder(this, UPDATE_HOUSE_KEEPING_NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(android.R.drawable.stat_sys_download)
                .setContentTitle("Update House Keeping")
                .setOngoing(true);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            notificationBuilder.setPriority(NotificationCompat.PRIORITY_HIGH);
        }
    }

    private void setupNotificationManager() {
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(
                    UPDATE_HOUSE_KEEPING_NOTIFICATION_CHANNEL_ID,
                    getString(R.string.main_notification_channel_name),
                    NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(mChannel);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        startForeground(UPDATE_HOUSE_KEEPING_NOTIFICATION_ID, notificationBuilder.build());

        String table = intent.getStringExtra(I_KEY_TABLE);
        String action = intent.getStringExtra(I_KEY_ACTION);
        isLocal = intent.getBooleanExtra(I_KEY_LOCAL, false);

        List<TableInfoEntry> tableInfoList = new ArrayList<>();
        if (table.equals(ACTION_GET_ALL_TABLE)) {
            tableInfoList.add(new TableInfoEntry(BranchEntry.TABLE_NAME, false));
            tableInfoList.add(new TableInfoEntry(CustomerCompanyEntry.TABLE_NAME, false));
            tableInfoList.add(new TableInfoEntry(CustomerCompanyAddressEntry.TABLE_NAME, false));
            tableInfoList.add(new TableInfoEntry(ItemPackingEntry.TABLE_NAME, false));
            tableInfoList.add(new TableInfoEntry(PriceSettingEntry.TABLE_NAME, false));
        } else {
            tableInfoList.add(new TableInfoEntry(table, false));
        }

        UpdateHouseKeepingAsyncTask updateHouseKeepingAsyncTask = new UpdateHouseKeepingAsyncTask(this, mDb, tableInfoList, action, isLocal, this, 0);
        updateHouseKeepingAsyncTask.execute();

        return START_STICKY;
    }

    @Override
    public void initialProgress(String table) {
        startNotificationProgress(table);
    }

    @Override
    public void updateProgress(String type, double row, double total) {
        updateNotificationProgress(type, row, total);
    }

    @Override
    public void completeProgress() {
        completeNotificationProgress();
    }

    private void startNotificationProgress(String table) {

        String content_text = "Reading " + StringUtils.getTableDisplayName(table) + " data from server...";
        notificationBuilder.setContentText(content_text)
                .setProgress(PROGRESS_MAX, PROGRESS_MIN, false);
        notificationManager.notify(UPDATE_HOUSE_KEEPING_NOTIFICATION_ID, notificationBuilder.build());

    }

    private void updateNotificationProgress(String table, double row, double total) {
        int percentage = (int) (row / total * 100);
        String content_text = "Updating " + StringUtils.getTableDisplayName(table) + "...";

        if (percentage != progressCurrent) {
            if (percentage % 5 == 0) {
                notificationBuilder.setContentText(content_text).setProgress(PROGRESS_MAX, percentage, false);
                notificationManager.notify(UPDATE_HOUSE_KEEPING_NOTIFICATION_ID, notificationBuilder.build());
                progressCurrent = percentage;
            }
        }
    }

    private void completeNotificationProgress() {
        notificationBuilder
                .setSmallIcon(android.R.drawable.stat_sys_download_done)
                .setContentText("Update complete")
                .setOngoing(false)
                .setProgress(0, 0, false);
        notificationManager.notify(UPDATE_HOUSE_KEEPING_NOTIFICATION_ID, notificationBuilder.build());
        stopForeground(STOP_FOREGROUND_DETACH);
        stopSelf();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
