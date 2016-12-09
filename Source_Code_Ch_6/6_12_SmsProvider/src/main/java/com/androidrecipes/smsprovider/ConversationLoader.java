package com.androidrecipes.smsprovider;

import android.content.AsyncTaskLoader;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.provider.Telephony.Mms;
import android.provider.Telephony.MmsSms;
import android.provider.Telephony.Sms;
import android.provider.Telephony.Sms.Conversations;
import android.telephony.TelephonyManager;

import java.util.List;

public class ConversationLoader extends AsyncTaskLoader<List<MessageItem>> {

    public static final String[] PROJECTION = new String[]{
            //Determine if message is SMS or MMS
            MmsSms.TYPE_DISCRIMINATOR_COLUMN,
            //Base item ID
            BaseColumns._ID,
            //Conversation (thread) ID
            Conversations.THREAD_ID,
            //Date values
            Sms.DATE,
            Sms.DATE_SENT,
            // For SMS only
            Sms.ADDRESS,
            Sms.BODY,
            Sms.TYPE,
            // For MMS only
            Mms.SUBJECT,
            Mms.MESSAGE_BOX
    };

    //Thread ID of the conversation we are loading
    private long mThreadId;
    //This device's number
    private String mDeviceNumber;

    public ConversationLoader(Context context) {
        this(context, -1);
    }

    public ConversationLoader(Context context, long threadId) {
        super(context);
        mThreadId = threadId;
        //Obtain the phone number of this device, if available
        TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        mDeviceNumber = manager.getLine1Number();
    }

    @Override
    protected void onStartLoading() {
        //Reload on every init request
        forceLoad();
    }

    @Override
    public List<MessageItem> loadInBackground() {
        Uri uri;
        String[] projection;
        if (mThreadId < 0) {
            //Load all conversations
            uri = MmsSms.CONTENT_CONVERSATIONS_URI;
            projection = null;
        } else {
            //Load just the requested thread
            uri = ContentUris.withAppendedId(MmsSms.CONTENT_CONVERSATIONS_URI, mThreadId);
            projection = PROJECTION;
        }

        Cursor cursor = getContext().getContentResolver().query(
                uri,
                projection,
                null,
                null,
                null);

        return MessageItem.parseMessages(getContext(), cursor, mDeviceNumber);
    }
}
