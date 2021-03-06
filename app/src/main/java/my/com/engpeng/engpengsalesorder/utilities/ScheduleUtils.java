package my.com.engpeng.engpengsalesorder.utilities;

import android.content.Context;
import androidx.annotation.NonNull;

import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.Driver;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.Trigger;

import java.util.concurrent.TimeUnit;

import my.com.engpeng.engpengsalesorder.service.UpdateHouseKeepingJobService;
import my.com.engpeng.engpengsalesorder.service.UploadJobService;

public class ScheduleUtils {
    private static final int UPDATE_REMINDER_INTERVAL_HOURS = 1;
    private static final int UPDATE_REMINDER_INTERVAL_SECONDS = (int) (TimeUnit.HOURS.toSeconds(UPDATE_REMINDER_INTERVAL_HOURS));
    private static final int UPDATE_SYNC_FLEXTIME_HOURS = 1;
    private static final int UPDATE_SYNC_FLEXTIME_SECONDS = (int) (TimeUnit.HOURS.toSeconds(UPDATE_SYNC_FLEXTIME_HOURS));
    private static final String AUTO_UPDATE_HOUSE_KEEPING_JOB_TAG = "AUTO_UPDATE_HOUSE_KEEPING_JOB_TAG";

    private static boolean isUpdateInitialized;

    synchronized public static void scheduleAutoUpdate(@NonNull final Context context) {
        if (isUpdateInitialized) return;

        Driver driver = new GooglePlayDriver(context);
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(driver);

        Job autoUpdateHouseKeepingJob = dispatcher.newJobBuilder()
                .setService(UpdateHouseKeepingJobService.class)
                .setTag(AUTO_UPDATE_HOUSE_KEEPING_JOB_TAG)
                .setConstraints(Constraint.ON_ANY_NETWORK)
                .setLifetime(Lifetime.FOREVER)
                .setRecurring(true)
                .setTrigger(Trigger.executionWindow(UPDATE_REMINDER_INTERVAL_SECONDS, UPDATE_REMINDER_INTERVAL_SECONDS + UPDATE_SYNC_FLEXTIME_SECONDS))
                .setReplaceCurrent(true)
                .build();

        dispatcher.schedule(autoUpdateHouseKeepingJob);
        isUpdateInitialized = true;
    }

    private static final int UPLOAD_REMINDER_INTERVAL_HOURS = 1;
    private static final int UPLOAD_REMINDER_INTERVAL_SECONDS = (int) (TimeUnit.HOURS.toSeconds(UPLOAD_REMINDER_INTERVAL_HOURS));
    private static final int UPLOAD_SYNC_FLEXTIME_HOURS = 1;
    private static final int UPLOAD_SYNC_FLEXTIME_SECONDS = (int) (TimeUnit.HOURS.toSeconds(UPLOAD_SYNC_FLEXTIME_HOURS));
    private static final String AUTO_UPLOAD_JOB_TAG = "AUTO_UPLOAD_JOB_TAG";
    private static boolean isUploadInitialized;

    synchronized public static void scheduleAutoUpload(@NonNull final Context context) {
        if (isUploadInitialized) return;

        Driver driver = new GooglePlayDriver(context);
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(driver);

        Job autoUpdateHouseKeepingJob = dispatcher.newJobBuilder()
                .setService(UploadJobService.class)
                .setTag(AUTO_UPLOAD_JOB_TAG)
                .setConstraints(Constraint.ON_ANY_NETWORK)
                .setLifetime(Lifetime.FOREVER)
                .setRecurring(true)
                .setTrigger(Trigger.executionWindow(UPLOAD_REMINDER_INTERVAL_SECONDS, UPLOAD_REMINDER_INTERVAL_SECONDS + UPLOAD_SYNC_FLEXTIME_SECONDS))
                .setReplaceCurrent(true)
                .build();

        dispatcher.schedule(autoUpdateHouseKeepingJob);
        isUploadInitialized = true;
    }
}
