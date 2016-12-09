package com.androidrecipes.notifications;

import android.annotation.TargetApi;
import android.app.IntentService;
import android.app.Notification;
import android.app.RemoteInput;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;


@TargetApi(Build.VERSION_CODES.KITKAT_WATCH)
public class ReplyReceiverService extends IntentService {
    public static final String KEY_TEXT_REPLY = "key_text_reply";

    public ReplyReceiverService() {
        super("ReplyReceiverService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle resultsFromIntent = RemoteInput.getResultsFromIntent(intent);
        String reply = resultsFromIntent.getString(KEY_TEXT_REPLY, null);
        Log.d("Reply", "Message: " + reply);
        Notification notification = new NotificationCompat.Builder(this)
                .setRemoteInputHistory(new CharSequence[]{reply})
                .setSmallIcon(R.drawable.ic_stat_name)
                .build();
        NotificationManagerCompat.from(this)
                .notify(R.id.option_reply, notification);
    }
}
