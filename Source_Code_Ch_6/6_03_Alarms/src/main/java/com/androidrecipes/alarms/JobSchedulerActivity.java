package com.androidrecipes.alarms;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class JobSchedulerActivity extends Activity implements View.OnClickListener {

    //Application provides a unique ID for each job
    private static final int JOB_ID = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        //Attach the listener to both buttons
        findViewById(R.id.start).setOnClickListener(this);
        findViewById(R.id.stop).setOnClickListener(this);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onClick(View view) {
        JobScheduler scheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
        long interval = 5 * 1000; //5 seconds

        JobInfo info = new JobInfo.Builder(JOB_ID,
                new ComponentName(getPackageName(), WorkerService.class.getName()))
                .setPeriodic(interval)
                .build();

        switch (view.getId()) {
            case R.id.start:
                //Android will return the same Job ID anytime the same info
                // is passed to schedule(), it will not duplicate jobs
                int result = scheduler.schedule(info);
                if (result <= 0) {
                    Toast.makeText(this, "Error Scheduling Job", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.stop:
                //The Job ID must match what was passed to schedule, so keep it around
                scheduler.cancel(JOB_ID);
                break;
            default:
                break;
        }
    }
}
