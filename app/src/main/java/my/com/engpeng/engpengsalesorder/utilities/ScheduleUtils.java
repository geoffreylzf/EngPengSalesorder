package my.com.engpeng.engpengsalesorder.utilities;

import android.content.Context;
import android.support.annotation.NonNull;

import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.Driver;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.Trigger;

import java.util.concurrent.TimeUnit;

import my.com.engpeng.engpengsalesorder.service.TestJobService;
import my.com.engpeng.engpengsalesorder.service.UpdateHouseKeepingJobService;

public class ScheduleUtils {
    private static final int REMINDER_INTERVAL_MINUTES = 5;
    private static final int REMINDER_INTERVAL_SECONDS = (int) (TimeUnit.MINUTES.toSeconds(REMINDER_INTERVAL_MINUTES));
    private static final int SYNC_FLEXTIME_MINUTES = 5;
    private static final int SYNC_FLEXTIME_SECONDS = (int) (TimeUnit.MINUTES.toSeconds(SYNC_FLEXTIME_MINUTES));
    private static final String AUTO_UPDATE_HOUSE_KEEPING_JOB_TAG = "AUTO_UPDATE_HOUSE_KEEPING_JOB_TAG";

    private static boolean sInitialized;

    synchronized public static void scheduleAutoUpdate(@NonNull final Context context) {
        if (sInitialized) return;

        Driver driver = new GooglePlayDriver(context);
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(driver);

        Job autoUpdateHouseKeepingJob = dispatcher.newJobBuilder()
                .setService(UpdateHouseKeepingJobService.class)
                //.setService(TestJobService.class)
                .setTag(AUTO_UPDATE_HOUSE_KEEPING_JOB_TAG)
                .setConstraints(Constraint.ON_ANY_NETWORK)
                .setLifetime(Lifetime.FOREVER)
                .setRecurring(true)
                .setTrigger(Trigger.executionWindow(REMINDER_INTERVAL_SECONDS, REMINDER_INTERVAL_SECONDS + SYNC_FLEXTIME_SECONDS))
                //.setTrigger(Trigger.executionWindow(5, 10))
                .setReplaceCurrent(true)
                .build();

        dispatcher.schedule(autoUpdateHouseKeepingJob);
        sInitialized = true;
    }
}
