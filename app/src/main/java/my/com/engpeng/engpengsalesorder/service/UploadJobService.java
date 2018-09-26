package my.com.engpeng.engpengsalesorder.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

import my.com.engpeng.engpengsalesorder.R;
import my.com.engpeng.engpengsalesorder.asyncTask.UploadAsyncTask;
import my.com.engpeng.engpengsalesorder.database.AppDatabase;
import my.com.engpeng.engpengsalesorder.executor.AppExecutors;

import static my.com.engpeng.engpengsalesorder.Global.I_KEY_LOCAL;
import static my.com.engpeng.engpengsalesorder.Global.SO_STATUS_CONFIRM;

public class UploadJobService extends JobService implements UploadAsyncTask.UploadAsyncTaskListener {

    private UploadAsyncTask uploadAsyncTask;

    private static final String UPLOAD_NOTIFICATION_CHANNEL_ID = "UPLOAD_SCHEDULE";
    private static final int UPLOAD_NOTIFICATION_SCHEDULE_ID = 10002;

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

        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                int count = mDb.salesorderDao().getCountByStatusUpload(SO_STATUS_CONFIRM, 0);
                if (count != 0) {
                    startForeground(UPLOAD_NOTIFICATION_SCHEDULE_ID, notificationBuilder.build());

                    UploadAsyncTask uploadAsyncTask = new UploadAsyncTask(mDb, false, UploadJobService.this);
                    uploadAsyncTask.execute();
                }else{
                    stopSelf();
                }
            }
        });

        return true;
    }

    private void setupNotificationBuilder() {
        notificationBuilder = new NotificationCompat.Builder(this, UPLOAD_NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(android.R.drawable.stat_sys_download)
                .setContentTitle("Auto Upload")
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
                    UPLOAD_NOTIFICATION_CHANNEL_ID,
                    getString(R.string.main_notification_channel_name),
                    NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(mChannel);
        }
    }

    @Override
    public void completeProgress() {
        notificationBuilder
                .setSmallIcon(android.R.drawable.stat_sys_download_done)
                .setContentText("Upload complete")
                .setOngoing(false)
                .setProgress(0, 0, false);
        notificationManager.notify(UPLOAD_NOTIFICATION_SCHEDULE_ID, notificationBuilder.build());
        stopForeground(STOP_FOREGROUND_DETACH);
        stopSelf();
        jobFinished(job, false);
    }

    @Override
    public boolean onStopJob(JobParameters job) {
        if (uploadAsyncTask != null) uploadAsyncTask.cancel(true);
        return true; //return true to indicate this job should retry
    }
}
