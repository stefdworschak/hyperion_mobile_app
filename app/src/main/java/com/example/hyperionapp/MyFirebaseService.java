package com.example.hyperionapp;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import androidx.core.app.NotificationCompat;

public class MyFirebaseService extends FirebaseMessagingService {
    /* Class handling the FirebaseMessaging logic */

    // Declare and instantiate class constants
    private static final String MSG_TAG = "Firebase MsgBody:";
    private static final String TAG = "MyFirebaseMsgService";
    private static final int OPEN_CODE_INTENT = 100;
    private final int NOTIFICATION_ID = 606;
    private static String session_id = "";
    private static String session_shared = "";
    private static String session_documents = "";

    // Reference: https://firebase.google.com/docs/cloud-messaging/concept-options
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        /*
         * Override the FirebaseMessaging onMessageReceived with custom logic
         * @param RemoteMessage remoteMessage The message received from FirebaseMessagingService
         * @return void
         */

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {

            // Extract session specific details from the message
            session_id = remoteMessage.getData().get("session_id");
            session_shared = remoteMessage.getData().get("session_shared");
            session_documents = remoteMessage.getData().get("session_documents");
            // Call method to send notification to app
            sendNotification("Give permission to share your personal data");
        } else {
            // Otherwise write an error to the log
            Log.d(MSG_TAG, "No remote message size");
        }

    }

    private void sendNotification(String messageBody) {
        /*
         * Method to send Notification to the app
         * @param String messageBody The message body to be sent to the app
         * @return void
         */

        // Instantiate method constants
        final String CHANNEL_ID = "FCM_DEFAULT_CHANNEL";
        final NotificationManager notificationManager = (NotificationManager) getApplication().getSystemService(Context.NOTIFICATION_SERVICE);

        // Create new intent to redirect the user to the CodeActivity when the user
        // clicks on the "Share" action
        // Add extra info for CodeActivity logic
        Intent notifyIntent = new Intent(this, CodeActivity.class);
        notifyIntent.putExtra("session_id", session_id);
        notifyIntent.putExtra("session_shared", session_shared);
        notifyIntent.putExtra("session_documents", session_documents);
        notifyIntent.putExtra("notification_id", "" + NOTIFICATION_ID);
        notifyIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        // Convert Intent to PendingIntent for the notification
        PendingIntent fullScreenPendingIntent = PendingIntent.getActivity(
                getApplicationContext(), OPEN_CODE_INTENT,
                notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Create new Notification Builder instance with a custom channel ID
        NotificationCompat.Builder notificationCompatBuilder =
                new NotificationCompat.Builder(
                        getApplicationContext(), CHANNEL_ID);
        // Set Notification Builder attributes
        notificationCompatBuilder.setSmallIcon(R.drawable.ic_send_black_24dp)
                        .setContentTitle("Data Sharing Request")
                        .setContentText(messageBody)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(messageBody))
                        .setWhen(0)
                        .setAutoCancel(true)
                        .setOngoing(false)
                        .setContentIntent(fullScreenPendingIntent)
                        .setPriority(NotificationCompat.PRIORITY_MAX)
                        .setCategory(NotificationCompat.CATEGORY_CALL)
                        .addAction(R.drawable.ic_file_upload_black_24dp, getString(R.string.share_notifications),
                                fullScreenPendingIntent)
                        .setFullScreenIntent(fullScreenPendingIntent, true);
        // If the sdk is Oreo or bigger the app needs to use Notification channel to send the
        // notification to the app
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            // Create new notification channel and set attributes
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, "hyperion_notification",
                    NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.setAllowBubbles(true);
            notificationChannel.setBypassDnd(true);
            // Add notification channel to notification manager
            notificationManager.createNotificationChannel(notificationChannel);
        }
        // Send notification channel with ID
        notificationManager.notify(NOTIFICATION_ID, notificationCompatBuilder.build());
    }
}
