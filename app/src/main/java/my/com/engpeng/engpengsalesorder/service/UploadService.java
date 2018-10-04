package my.com.engpeng.engpengsalesorder.service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import androidx.core.app.NotificationCompat;

import my.com.engpeng.engpengsalesorder.R;
import my.com.engpeng.engpengsalesorder.asyncTask.UploadAsyncTask;
import my.com.engpeng.engpengsalesorder.database.AppDatabase;
import my.com.engpeng.engpengsalesorder.executor.AppExecutors;

import static my.com.engpeng.engpengsalesorder.Global.I_KEY_LOCAL;
import static my.com.engpeng.engpengsalesorder.Global.SO_STATUS_CONFIRM;

public class UploadService extends Service implements
        UploadAsyncTask.UploadAsyncTaskListener {

    private static final String UPLOAD_NOTIFICATION_CHANNEL_ID = "UPLOAD";
    private static final int UPLOAD_NOTIFICATION_ID = 1002;

    private NotificationCompat.Builder notificationBuilder;
    private NotificationManager notificationManager;
    boolean isLocal;
    private AppDatabase mDb;

    public UploadService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mDb = AppDatabase.getInstance(getApplicationContext());
        setupNotificationBuilder();
        setupNotificationManager();
    }

    private void setupNotificationBuilder() {
        notificationBuilder = new NotificationCompat.Builder(this, UPLOAD_NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(android.R.drawable.stat_sys_upload)
                .setContentTitle("Upload")
                .setOngoing(true);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            notificationBuilder.setPriority(NotificationCompat.PRIORITY_HIGH);
        }
    }

    private void setupNotificationManager() {
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(
                    UPLOAD_NOTIFICATION_CHANNEL_ID,
                    getString(R.string.main_notification_channel_name),
                    NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(mChannel);
        }
    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {

        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                int count = mDb.salesorderDao().getCountByStatusUpload(SO_STATUS_CONFIRM, 0);
                if (count != 0) {
                    startForeground(UPLOAD_NOTIFICATION_ID, notificationBuilder.build());

                    isLocal = intent.getBooleanExtra(I_KEY_LOCAL, false);
                    UploadAsyncTask uploadAsyncTask = new UploadAsyncTask(mDb, isLocal, UploadService.this);
                    uploadAsyncTask.execute();
                }else{
                    stopSelf();
                }
            }
        });

        return UploadService.super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void initialProgress() {
        String content_text = "Prepare upload to server...";
        notificationBuilder.setContentText(content_text)
                .setProgress(0, 0, true);
        notificationManager.notify(UPLOAD_NOTIFICATION_ID, notificationBuilder.build());
    }

    @Override
    public void completeProgress() {
        notificationBuilder
                .setSmallIcon(android.R.drawable.stat_sys_upload_done)
                .setContentText("Upload complete")
                .setOngoing(false)
                .setProgress(0, 0, false);
        notificationManager.notify(UPLOAD_NOTIFICATION_ID, notificationBuilder.build());
        stopForeground(STOP_FOREGROUND_DETACH);
        stopSelf();
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
