package my.com.engpeng.engpengsalesorder.service;

import android.util.Log;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

import my.com.engpeng.engpengsalesorder.Global;
import my.com.engpeng.engpengsalesorder.utilities.StringUtils;

public class TestJobService extends JobService {
    @Override
    public boolean onStartJob(JobParameters job) {
        Log.e("TestJobService", StringUtils.getCurrentDateTime() + " onStartJob");
        jobFinished(job, false);
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters job) {
        return true;
    }
}
