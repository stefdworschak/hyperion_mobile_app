package com.example.hyperionapp;

/**
 * Copyright 2016 Google Inc. All Rights Reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.app.Notification;
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
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

public class MyFirebaseService extends FirebaseMessagingService {

    private static final String MSG_TAG = "Firebase MsgBody:";
    private static final String TAG = "MyFirebaseMsgService";
    private static final int OPEN_CODE_INTENT = 100;
    private final int NOTIFICATION_ID = 606;
    private String CHANNEL_ID = "fcm_notification";
    private static String session_id = "";
    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // [START_EXCLUDE]
        // There are two types of messages data messages and notification messages. Data messages
        // are handled
        // here in onMessageReceived whether the app is in the foreground or background. Data
        // messages are the type
        // traditionally used with GCM. Notification messages are only received here in
        // onMessageReceived when the app
        // is in the foreground. When the app is in the background an automatically generated
        // notification is displayed.
        // When the user taps on the notification they are returned to the app. Messages
        // containing both notification
        // and data payloads are treated as notification messages. The Firebase console always
        // sends notification
        // messages. For more see: https://firebase.google.com/docs/cloud-messaging/concept-options
        // [END_EXCLUDE]

        Log.d(MSG_TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(MSG_TAG, "Message data payload: " + remoteMessage.getData());
            session_id = remoteMessage.getData().get("session_id");
            Log.d(MSG_TAG, "Session ID from message: " + session_id);

            if (/* Check if data needs to be processed by long running job */ false) {
                Log.d(MSG_TAG, "Runs scheduleJob");
                // For long-running tasks (10 seconds or more) use WorkManager.
                scheduleJob();
            } else {
                // Handle message within 10 seconds
                //handleNow(remoteMessage.getNotification().getBody());
                handleNow("Give permission to share your personal data");
            }

        } else {
            Log.d(MSG_TAG, "No remote message size");
        }

    }
    // [END receive_message]


    // [START on_new_token]

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    @Override
    public void onNewToken(String token) {
        Log.d(TAG, "Refreshed token: " + token);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(token);
    }
    // [END on_new_token]

    /**
     * Schedule async work using WorkManager.
     */
    private void scheduleJob() {
        Log.d(MSG_TAG, "Scheduled task.");
        // [START dispatch_job]
        /*OneTimeWorkRequest work = new OneTimeWorkRequest.Builder(MyWorker.class)
                .build();
        WorkManager.getInstance().beginWith(work).enqueue();*/
        // [END dispatch_job]
    }

    /**
     * Handle time allotted to BroadcastReceivers.
     */
    private void handleNow(String messageBody) {
        Log.d(TAG, "Short lived task is done.");
        try {
            sendNotification(messageBody);
        } catch(Exception ex){
            ex.printStackTrace();
        }
    }

    /**
     * Persist token to third-party servers.
     *
     * Modify this method to associate the user's FCM InstanceID token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private void sendRegistrationToServer(String token) {
        // TODO: Implement this method to send token to your app server.
    }

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param messageBody FCM message body received.
     */
    private void sendNotification(String messageBody) {
        Log.d(MSG_TAG, "Notification Method Triggered");
        String CHANNEL_ID = "FCM_DEFAULT_CHANNEL";
        int requestID = (int) System.currentTimeMillis();
        NotificationManager notificationManager = (NotificationManager) getApplication().getSystemService(Context.NOTIFICATION_SERVICE);
        Intent notifyIntent = new Intent(this, CodeActivity.class);
        Log.d("SESSION ID FCM", session_id);

        notifyIntent.putExtra("session_id", session_id);
        notifyIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent fullScreenPendingIntent = PendingIntent.getActivity(
                getApplicationContext(), OPEN_CODE_INTENT,
                notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder notificationCompatBuilder =
                new NotificationCompat.Builder(
                        getApplicationContext(), CHANNEL_ID);

        notificationCompatBuilder.setSmallIcon(R.drawable.ic_stat_ic_notification)
                        .setContentTitle("Data Sharing Request")
                        .setContentText(messageBody)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(messageBody))
                        .setWhen(0)
                        .setContentIntent(fullScreenPendingIntent)
                        .setPriority(NotificationCompat.PRIORITY_MAX)
                        .setCategory(NotificationCompat.CATEGORY_CALL)
                        .setAutoCancel(true)
                        .addAction(R.drawable.ic_file_upload_black_24dp, getString(R.string.share_notifications),
                                fullScreenPendingIntent)
                        .setFullScreenIntent(fullScreenPendingIntent, true);

        Log.d(MSG_TAG, notificationCompatBuilder.toString());

        //int NOTIFICATION_ID = (int) System.currentTimeMillis();
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, "hyperion_notification",
                    NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.setAllowBubbles(true);
            notificationChannel.setBypassDnd(true);
            notificationManager.createNotificationChannel(notificationChannel);
            Log.d(MSG_TAG, notificationChannel.toString());
        }

        notificationManager.notify(NOTIFICATION_ID, notificationCompatBuilder.build());
    }
}
