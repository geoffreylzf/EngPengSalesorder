package my.com.engpeng.engpengsalesorder.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import androidx.core.app.NotificationCompat;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

import java.util.ArrayList;
import java.util.List;

import my.com.engpeng.engpengsalesorder.R;
import my.com.engpeng.engpengsalesorder.asyncTask.UpdateHouseKeepingAsyncTask;
import my.com.engpeng.engpengsalesorder.database.AppDatabase;
import my.com.engpeng.engpengsalesorder.database.branch.BranchEntry;
import my.com.engpeng.engpengsalesorder.database.customerCompany.CustomerCompanyEntry;
import my.com.engpeng.engpengsalesorder.database.customerCompanyAddress.CustomerCompanyAddressEntry;
import my.com.engpeng.engpengsalesorder.database.itemCompany.ItemCompanyEntry;
import my.com.engpeng.engpengsalesorder.database.itemPacking.ItemPackingEntry;
import my.com.engpeng.engpengsalesorder.database.priceSetting.PriceSettingEntry;
import my.com.engpeng.engpengsalesorder.database.tableList.TableInfoEntry;
import my.com.engpeng.engpengsalesorder.database.taxCode.TaxCodeEntry;
import my.com.engpeng.engpengsalesorder.database.taxItemMatching.TaxItemMatchingEntry;
import my.com.engpeng.engpengsalesorder.database.taxType.TaxTypeEntry;
import my.com.engpeng.engpengsalesorder.utilities.SharedPreferencesUtils;
import my.com.engpeng.engpengsalesorder.utilities.StringUtils;

import static my.com.engpeng.engpengsalesorder.Global.ACTION_UPDATE;

public class UpdateHouseKeepingJobService extends JobService implements
        UpdateHouseKeepingAsyncTask.UpdateHouseKeepingAsyncTaskListener {

    private UpdateHouseKeepingAsyncTask updateHouseKeepingAsyncTask;

    private static final String UPDATE_HOUSE_KEEPING_NOTIFICATION_CHANNEL_ID = "UPDATE_HOUSE_KEEPING_SCHEDULE";
    private static final int UPDATE_HOUSE_KEEPING_NOTIFICATION_SCHEDULE_ID = 10001;

    private static final int PROGRESS_MAX = 100;
    private static final int PROGRESS_MIN = 0;

    private NotificationCompat.Builder notificationBuilder;
    private NotificationManager notificationManager;
    private int progressCurrent = 0;
    private AppDatabase mDb;

    private JobParameters job;

    @Override
    public boolean onStartJob(JobParameters job) {
        this.job = job;

        mDb = AppDatabase.getInstance(getApplicationContext());
        setupNotificationBuilder();
        setupNotificationManager();

        if (SharedPreferencesUtils.getUsernamePassword(this) != null) {
            startForeground(UPDATE_HOUSE_KEEPING_NOTIFICATION_SCHEDULE_ID, notificationBuilder.build());

            List<TableInfoEntry> tableInfoList = new ArrayList<>();
            tableInfoList.add(new TableInfoEntry(BranchEntry.TABLE_NAME, false));
            tableInfoList.add(new TableInfoEntry(CustomerCompanyEntry.TABLE_NAME, false));
            tableInfoList.add(new TableInfoEntry(CustomerCompanyAddressEntry.TABLE_NAME, false));
            tableInfoList.add(new TableInfoEntry(ItemCompanyEntry.TABLE_NAME, false));
            tableInfoList.add(new TableInfoEntry(ItemPackingEntry.TABLE_NAME, false));
            tableInfoList.add(new TableInfoEntry(PriceSettingEntry.TABLE_NAME, false));
            tableInfoList.add(new TableInfoEntry(TaxCodeEntry.TABLE_NAME, false));
            tableInfoList.add(new TableInfoEntry(TaxItemMatchingEntry.TABLE_NAME, false));
            tableInfoList.add(new TableInfoEntry(TaxTypeEntry.TABLE_NAME, false));

            String action = ACTION_UPDATE;

            updateHouseKeepingAsyncTask = new UpdateHouseKeepingAsyncTask(mDb, tableInfoList, action, false, this, 0, null, true);
            updateHouseKeepingAsyncTask.execute();
        } else {
            stopSelf();
        }

        return true;
    }

    private void setupNotificationBuilder() {
        notificationBuilder = new NotificationCompat.Builder(this, UPDATE_HOUSE_KEEPING_NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(android.R.drawable.stat_sys_download)
                .setContentTitle("Auto Update House Keeping")
                .setDefaults(Notification.DEFAULT_VIBRATE)
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
        jobFinished(job, false);
    }

    @Override
    public void errorProgress() {
        notificationBuilder
                .setSmallIcon(android.R.drawable.stat_notify_error)
                .setContentText("Auto update error")
                .setOngoing(false)
                .setProgress(0, 0, false);
        notificationManager.notify(UPDATE_HOUSE_KEEPING_NOTIFICATION_SCHEDULE_ID, notificationBuilder.build());
        stopForeground(STOP_FOREGROUND_DETACH);
        stopSelf();
        jobFinished(job, false);
    }

    private void startNotificationProgress(String table) {

        String content_text = "Reading " + StringUtils.getTableDisplayName(table) + " data from server...";
        notificationBuilder.setContentText(content_text)
                .setProgress(PROGRESS_MAX, PROGRESS_MIN, false);
        notificationManager.notify(UPDATE_HOUSE_KEEPING_NOTIFICATION_SCHEDULE_ID, notificationBuilder.build());

    }

    private void updateNotificationProgress(String table, double row, double total) {
        int percentage = (int) (row / total * 100);
        String content_text = "Updating " + StringUtils.getTableDisplayName(table) + "...";

        if (percentage != progressCurrent) {
            if (percentage % 20 == 0) {
                notificationBuilder.setContentText(content_text).setProgress(PROGRESS_MAX, percentage, false);
                notificationManager.notify(UPDATE_HOUSE_KEEPING_NOTIFICATION_SCHEDULE_ID, notificationBuilder.build());
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
        notificationManager.notify(UPDATE_HOUSE_KEEPING_NOTIFICATION_SCHEDULE_ID, notificationBuilder.build());
        stopForeground(STOP_FOREGROUND_DETACH);
        stopSelf();
    }

    @Override
    public boolean onStopJob(JobParameters job) {
        if (updateHouseKeepingAsyncTask != null) updateHouseKeepingAsyncTask.cancel(true);
        return true; //return true to indicate this job should retry
    }
}
