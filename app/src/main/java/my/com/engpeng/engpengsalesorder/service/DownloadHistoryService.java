package my.com.engpeng.engpengsalesorder.service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

import my.com.engpeng.engpengsalesorder.R;
import my.com.engpeng.engpengsalesorder.asyncTask.DownloadHistoryAsyncTask;
import my.com.engpeng.engpengsalesorder.database.AppDatabase;
import my.com.engpeng.engpengsalesorder.utilities.StringUtils;

import static my.com.engpeng.engpengsalesorder.Global.I_KEY_LOCAL;

public class DownloadHistoryService extends Service
        implements DownloadHistoryAsyncTask.DownloadHistoryAsyncTaskListener {

    private static final String GET_HISTORY_NOTIFICATION_CHANNEL_ID = "GET_HISTORY";
    private static final int GET_HISTORY_NOTIFICATION_ID = 1002;

    private static final int PROGRESS_MAX = 100;
    private static final int PROGRESS_MIN = 0;

    private NotificationCompat.Builder notificationBuilder;
    private NotificationManager notificationManager;
    private int progressCurrent = 0;
    boolean isLocal;
    private AppDatabase mDb;

    public DownloadHistoryService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mDb = AppDatabase.getInstance(getApplicationContext());
        setupNotificationBuilder();
        setupNotificationManager();
    }

    private void setupNotificationBuilder() {
        notificationBuilder = new NotificationCompat.Builder(this, GET_HISTORY_NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(android.R.drawable.stat_sys_download)
                .setContentTitle("Download History")
                .setOngoing(true);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            notificationBuilder.setPriority(NotificationCompat.PRIORITY_HIGH);
        }
    }

    private void setupNotificationManager() {
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(
                    GET_HISTORY_NOTIFICATION_CHANNEL_ID,
                    getString(R.string.main_notification_channel_name),
                    NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(mChannel);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        startForeground(GET_HISTORY_NOTIFICATION_ID, notificationBuilder.build());

        isLocal = intent.getBooleanExtra(I_KEY_LOCAL, false);

        DownloadHistoryAsyncTask downloadHistoryAsyncTask = new DownloadHistoryAsyncTask(isLocal, mDb,this);
        downloadHistoryAsyncTask.execute();

        return START_STICKY;
    }

    @Override
    public void initialProgress() {
        String content_text = "Reading history data from server...";
        notificationBuilder.setContentText(content_text)
                .setProgress(PROGRESS_MAX, PROGRESS_MIN, false);
        notificationManager.notify(GET_HISTORY_NOTIFICATION_ID, notificationBuilder.build());
    }

    @Override
    public void updateProgress(String table, double row, double total) {
        int percentage = (int) (row / total * 100);
        String content_text = "Updating " + StringUtils.getTableDisplayName(table) + "...";

        if (percentage != progressCurrent) {
            if (percentage % 5 == 0) {
                notificationBuilder.setContentText(content_text).setProgress(PROGRESS_MAX, percentage, false);
                notificationManager.notify(GET_HISTORY_NOTIFICATION_ID, notificationBuilder.build());
                progressCurrent = percentage;
            }
        }
    }

    @Override
    public void completeProgress() {
        notificationBuilder
                .setSmallIcon(android.R.drawable.stat_sys_download_done)
                .setContentText("Download history complete")
                .setOngoing(false)
                .setProgress(0, 0, false);
        notificationManager.notify(GET_HISTORY_NOTIFICATION_ID, notificationBuilder.build());
        stopForeground(STOP_FOREGROUND_DETACH);
        stopSelf();
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
