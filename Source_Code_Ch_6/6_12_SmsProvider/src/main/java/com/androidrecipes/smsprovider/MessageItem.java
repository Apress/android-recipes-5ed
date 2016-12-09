package com.androidrecipes.smsprovider;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.provider.Telephony.Mms;
import android.provider.Telephony.MmsSms;
import android.provider.Telephony.Sms;
import android.provider.Telephony.Sms.Conversations;
import android.provider.Telephony.TextBasedSmsColumns;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class MessageItem {
    /* Message Type Identifiers */
    private static final String TYPE_SMS = "sms";
    private static final String TYPE_MMS = "mms";

    static final String[] MMS_PROJECTION = new String[]{
            //Base item ID
            BaseColumns._ID,
            //MIME Type of the content for this part
            Mms.Part.CONTENT_TYPE,
            //Text content of a text/plain part
            Mms.Part.TEXT,
            //Path to binary content of a non-text part
            Mms.Part._DATA
    };

    /* Message Id */
    public long id;
    /* Thread (Conversation) Id */
    public long thread_id;
    /* Address string of message */
    public String address;
    /* Body string of message */
    public String body;
    /* Whether this message was sent or received on this device */
    public boolean incoming;
    /* MMS image attachment */
    public Uri attachment;

    /*
     * Construct a list of messages from the Cursor data
     * queried by the Loader
     */
    public static List<MessageItem> parseMessages(Context context, Cursor cursor, String myNumber) {
        List<MessageItem> messages = new ArrayList<MessageItem>();
        if (!cursor.moveToFirst()) {
            return messages;
        }
        //Parse each message based on the type identifiers
        do {
            String type = getMessageType(cursor);
            if (TYPE_SMS.equals(type)) {
                MessageItem item = parseSmsMessage(cursor);
                messages.add(item);
            } else if (TYPE_MMS.equals(type)) {
                MessageItem item = parseMmsMessage(context, cursor, myNumber);
                messages.add(item);
            } else {
                Log.w("TelephonyProvider", "Unknown Message Type");
            }
        } while (cursor.moveToNext());
        cursor.close();

        return messages;
    }

    /*
     * Read message type, if present in Cursor; otherwise
     * infer it from the column values present in the Cursor
     */
    private static String getMessageType(Cursor cursor) {
        int typeIndex = cursor.getColumnIndex(MmsSms.TYPE_DISCRIMINATOR_COLUMN);
        if (typeIndex < 0) {
            //Type column not in projection, use another discriminator
            String cType = cursor.getString(cursor.getColumnIndex(Mms.CONTENT_TYPE));
            //If a content type is present, this is an MMS message
            if (cType != null) {
                return TYPE_MMS;
            } else {
                return TYPE_SMS;
            }
        } else {
            return cursor.getString(typeIndex);
        }
    }

    /*
     * Parse out a MessageItem with contents from an SMS message
     */
    private static MessageItem parseSmsMessage(Cursor data) {
        MessageItem item = new MessageItem();
        item.id = data.getLong(data.getColumnIndexOrThrow(BaseColumns._ID));
        item.thread_id = data.getLong(data.getColumnIndexOrThrow(Conversations.THREAD_ID));

        item.address = data.getString(data.getColumnIndexOrThrow(Sms.ADDRESS));
        item.body = data.getString(data.getColumnIndexOrThrow(Sms.BODY));
        item.incoming = isIncomingMessage(data, true);
        return item;
    }

    /*
     * Parse out a MessageItem with contents from an MMS message
     */
    private static MessageItem parseMmsMessage(Context context, Cursor data, String myNumber) {
        MessageItem item = new MessageItem();
        item.id = data.getLong(data.getColumnIndexOrThrow(BaseColumns._ID));
        item.thread_id = data.getLong(data.getColumnIndexOrThrow(Conversations.THREAD_ID));

        item.incoming = isIncomingMessage(data, false);

        long _id = data.getLong(data.getColumnIndexOrThrow(BaseColumns._ID));

        //Query the address information for this message
        Uri addressUri = Uri.withAppendedPath(Mms.CONTENT_URI, _id + "/addr");
        Cursor addr = context.getContentResolver().query(
                addressUri,
                null,
                null,
                null,
                null);
        HashSet<String> recipients = new HashSet<String>();
        while (addr.moveToNext()) {
            String address = addr.getString(addr.getColumnIndex(Mms.Addr.ADDRESS));
            //Don't add our own number to the displayed list
            if (myNumber == null || !address.contains(myNumber)) {
                recipients.add(address);
            }
        }
        item.address = TextUtils.join(", ", recipients);
        addr.close();

        //Query all the MMS parts associated with this message
        Uri messageUri = Uri.withAppendedPath(Mms.CONTENT_URI, _id + "/part");
        Cursor inner = context.getContentResolver().query(
                messageUri,
                MMS_PROJECTION,
                Mms.Part.MSG_ID + " = ?",
                new String[]{String.valueOf(data.getLong(data.getColumnIndexOrThrow(Mms._ID)))},
                null);

        while (inner.moveToNext()) {
            String contentType = inner.getString(inner.getColumnIndexOrThrow(Mms.Part.CONTENT_TYPE));
            if (contentType == null) {
                continue;
            } else if (contentType.matches("image/.*")) {
                //Find any part that is an image attachment
                long partId = inner.getLong(inner.getColumnIndexOrThrow(BaseColumns._ID));
                item.attachment = Uri.withAppendedPath(Mms.CONTENT_URI, "part/" + partId);
            } else if (contentType.matches("text/.*")) {
                //Find any part that is text data
                item.body = inner.getString(inner.getColumnIndexOrThrow(Mms.Part.TEXT));
            }
        }

        inner.close();
        return item;
    }

    /*
     * Validate if the message is incoming or outgoing by the
     * type/box information listed in the provider
     */
    private static boolean isIncomingMessage(Cursor cursor, boolean isSms) {
        int boxId;
        if (isSms) {
            boxId = cursor.getInt(cursor.getColumnIndexOrThrow(Sms.TYPE));
            // Note that messages from the SIM card all have a boxId of zero.
            return (boxId == TextBasedSmsColumns.MESSAGE_TYPE_INBOX ||
                    boxId == TextBasedSmsColumns.MESSAGE_TYPE_ALL) ?
                    true : false;
        } else {
            boxId = cursor.getInt(cursor.getColumnIndexOrThrow(Mms.MESSAGE_BOX));
            // Note that messages from the SIM card all have a boxId of zero: Mms.MESSAGE_BOX_ALL
            return (boxId == Mms.MESSAGE_BOX_INBOX || boxId == Mms.MESSAGE_BOX_ALL) ?
                    true : false;
        }
    }
}
