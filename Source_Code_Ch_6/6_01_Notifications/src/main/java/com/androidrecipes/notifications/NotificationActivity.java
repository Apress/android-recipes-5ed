package com.androidrecipes.notifications;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.RemoteInput;
import android.view.View;
import android.widget.RadioGroup;

import static com.androidrecipes.notifications.ReplyReceiverService.KEY_TEXT_REPLY;

public class NotificationActivity extends Activity {

    private static final String GROUP_KEY_MESSAGES = "messages";
    private RadioGroup mOptionsGroup;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        mOptionsGroup = (RadioGroup) findViewById(R.id.options_group);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            findViewById(R.id.option_reply).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.option_reply).setVisibility(View.GONE);
        }
    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//        
//        Intent intent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
//        startActivity(intent);
//    }

    public void onPostClick(View v) {
        final int noteId = mOptionsGroup.getCheckedRadioButtonId();
        final Notification note;
        switch (noteId) {
            case R.id.option_basic:
            case R.id.option_bigtext:
            case R.id.option_bigpicture:
            case R.id.option_inbox:
                note = buildStyledNotification(noteId);
                break;
            case R.id.option_bundled:
                note = createBundledNotification();
                break;
            case R.id.option_reply:
                note = createDirectReplyNotification();
                break;
            case R.id.option_private:
            case R.id.option_secret:
            case R.id.option_headsup:
                note = buildSecuredNotification(noteId);
                break;
            default:
                throw new IllegalArgumentException("Unknown Type");
        }

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(noteId, note);
    }

    private Notification buildStyledNotification(int type) {
        Intent launchIntent =
                new Intent(this, NotificationActivity.class);
        PendingIntent contentIntent =
                PendingIntent.getActivity(this, 0, launchIntent, 0);

        // Create notification with the time it was fired
        NotificationCompat.Builder builder = new NotificationCompat.Builder(
                NotificationActivity.this);

        builder.setSmallIcon(R.drawable.ic_launcher)
                .setTicker("Something Happened")
                .setWhen(System.currentTimeMillis())
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_SOUND)
                .setContentTitle("We're Finished!")
                .setContentText("Click Here!")
                .setContentIntent(contentIntent);

        switch (type) {
            case R.id.option_basic:
                //Return the simple notification
                return builder.build();
            case R.id.option_bigtext:
                //Include two actions
                builder.addAction(android.R.drawable.ic_menu_call,
                        "Call", contentIntent);
                builder.addAction(android.R.drawable.ic_menu_recent_history,
                        "History", contentIntent);
                //Use the BigTextStyle when expanded
                NotificationCompat.BigTextStyle textStyle =
                        new NotificationCompat.BigTextStyle(builder);
                textStyle.bigText("Here is some additional text to be displayed when the notification is "
                        + "in expanded mode.  I can fit so much more content into this giant view!");

                return textStyle.build();
            case R.id.option_bigpicture:
                //Add one additional action
                builder.addAction(android.R.drawable.ic_menu_compass,
                        "View Location", contentIntent);
                //Use the BigPictureStyle when expanded
                NotificationCompat.BigPictureStyle pictureStyle =
                        new NotificationCompat.BigPictureStyle(builder);
                pictureStyle.bigPicture(BitmapFactory.decodeResource(getResources(), R.drawable.dog));

                return pictureStyle.build();
            case R.id.option_inbox:
                //Use the InboxStyle when expanded
                NotificationCompat.InboxStyle inboxStyle =
                        new NotificationCompat.InboxStyle(builder);
                inboxStyle.setSummaryText("4 New Tasks");
                inboxStyle.addLine("Make Dinner");
                inboxStyle.addLine("Call Mom");
                inboxStyle.addLine("Call Wife First");
                inboxStyle.addLine("Pick up Kids");

                return inboxStyle.build();
            default:
                throw new IllegalArgumentException("Unknown Type");
        }
    }

    private Notification createDirectReplyNotification() {
        String replyLabel = getResources().getString(R.string.reply_label);
        RemoteInput remoteInput = new RemoteInput.Builder(KEY_TEXT_REPLY)
                .setLabel(replyLabel)
                .build();
        Intent intent = new Intent(this, ReplyReceiverService.class);
        PendingIntent replyPendingIntent = PendingIntent.getService(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);
        NotificationCompat.Action action =
                new NotificationCompat.Action.Builder(R.drawable.ic_stat_name,
                        getString(R.string.label), replyPendingIntent)
                        .addRemoteInput(remoteInput)
                        .build();
        return new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_stat_name)
                .setContentTitle(getString(R.string.title))
                .setContentText(getString(R.string.content))
                .addAction(action)
                .build();
    }

    private Notification createBundledNotification() {
        NotificationManagerCompat notificationManager =
                NotificationManagerCompat.from(this);
        for (int i = 1; i <= 5; i++) {
            Notification notif = new NotificationCompat.Builder(this)
                    .setContentTitle("New message from " + i)
                    .setContentText("Message " + i)
                    .setSmallIcon(R.drawable.ic_stat_name)
                    .setGroup(GROUP_KEY_MESSAGES)
                    .build();
            notificationManager.notify(i, notif);
        }

        return new NotificationCompat.Builder(this)
                .setContentTitle("5 new messages")
                .setSmallIcon(R.drawable.ic_stat_name)
                .setStyle(new NotificationCompat.InboxStyle()
                        .setBigContentTitle("5 new messages")
                        .setSummaryText("name@domain.com"))
                .setGroup(GROUP_KEY_MESSAGES)
                .setGroupSummary(true)
                .build();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    //These properties can be overridden by the user's notification settings
    private Notification buildSecuredNotification(int type) {
        Intent launchIntent =
                new Intent(this, NotificationActivity.class);
        PendingIntent contentIntent =
                PendingIntent.getActivity(this, 0, launchIntent, 0);

        //Construct the base notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle("Account Balance Update")
                .setContentText("Your account balance is -$250.00")
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText("Your account balance is -$250.00; pay us please "
                                + "or we will be forced to take legal action!"))
                .setContentIntent(contentIntent);

        switch (type) {
            case R.id.option_private:
                //Provide a unique version for secured lockscreens
                Notification publicNote = new Notification.Builder(this)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentTitle("Account Notification")
                        .setContentText("An important message has arrived.")
                        .setContentIntent(contentIntent)
                        .build();

                return builder.setPublicVersion(publicNote)
                        .build();
            case R.id.option_secret:
                //Hide the notification from a secured lockscreen completely
                return builder.setVisibility(Notification.VISIBILITY_SECRET)
                        .build();
            case R.id.option_headsup:
                //Show a heads-up notification when posted
                return builder.setDefaults(Notification.DEFAULT_SOUND)
                        .setPriority(Notification.PRIORITY_HIGH)
                        .build();
            default:
                throw new IllegalArgumentException("Unknown Type");
        }
    }
}