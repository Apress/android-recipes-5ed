package com.androidrecipes.alarms;

import android.annotation.TargetApi;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class WorkerService extends JobService {

    private static final int MSG_JOB = 1;

    //Simple queue handler for executing the jobs that are scheduled
    private Handler mJobProcessor = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            JobParameters params = (JobParameters) msg.obj;
            Log.i("WorkerService", "Executing Job " + params.getJobId());
            //After completing our asynchronous work, we must trigger
            // jobFinished() to allow the next scheduled task to run.
            doWork();
            jobFinished(params, false);

            return true;
        }
    });

    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        Log.d("WorkerService", "Start Job " + jobParameters.getJobId());
        //To simulate a long task, we delay execution by 7.5 seconds
        mJobProcessor.sendMessageDelayed(
                Message.obtain(mJobProcessor, MSG_JOB, jobParameters),
                7500
        );

        /*
         * Return false if the job was synchronously completed here,
         * true if you need to do more background work. In the latter
         * case, you must call jobFinished() to notify the system of
         * completion.
         */
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        Log.w("WorkerService", "Stop Job " + jobParameters.getJobId());
        //When a request to stop comes in, we have to cancel any pending jobs
        mJobProcessor.removeMessages(MSG_JOB);

        /*
         * Return true to have the job rescheduled, false to drop it
         */
        return false;
    }

    private void doWork() {
        //Perform an interesting operation, we'll just display the current time
        Calendar now = Calendar.getInstance();
        DateFormat formatter = SimpleDateFormat.getTimeInstance();
        Toast.makeText(this, formatter.format(now.getTime()), Toast.LENGTH_SHORT).show();
    }
}
