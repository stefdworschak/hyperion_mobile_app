package com.example.hyperionapp;

import android.annotation.TargetApi;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;

import androidx.core.app.NotificationCompat;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MyFirebaseService extends FirebaseMessagingService {
    /* Class handling the FirebaseMessaging logic */

    // Declare and instantiate class constants
    private static final String MSG_TAG = "Firebase MsgBody:";
    private static final String TAG = "MyFirebaseMsgService";
    private static final int OPEN_CODE_INTENT = 100;
    private final int NOTIFICATION_ID = 606;

    final private EncryptionService encryption = new EncryptionService();
    final private Gson gson = new Gson();
    // Declare and instantiate class variables
    private JSONObject sessionJSON;
    private Checkin future_session = null;
    private static String session_id = "";
    private static String session_shared = "";
    private static String session_documents = "";
    private static String user_id = null;


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
            user_id = remoteMessage.getData().get("user_id");

            // Check what kind of message it is
            // "New Session" indicates a future session that was scheduled during the
            // diagnosis

            if(session_documents.equals("\"New Session\"")){
                // Call method to schedule future session
                Log.d("NEW SESSION", "YES");
                scheduleFutureSession();
            } else {
                // Call method to send notification to app
                sendNotification("Give permission to share your personal data");
            }

        } else {
            // Otherwise write an error to the log
            Log.d(MSG_TAG, "No remote message size");
        }

    }

    private void scheduleFutureSession(){
        try{
            // Retrieve the JSON string from the message and parse it to JSON format
            sessionJSON = new JSONObject(session_shared);
            JSONObject session_details = new JSONObject(sessionJSON.get("session_details").toString());
            // Convert the session_checkin from String to Date
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            Date session_checkin = sdf.parse(sessionJSON.get("session_checkin").toString());
            // Create a new empty list as a placeholder for future session documents
            List<SessionDocument> documents = new ArrayList<>();
            // Instantiate a new Checkin instance with the follow-up information
            future_session = new Checkin(sessionJSON.get("session_id").toString(), session_details.get("symptoms").toString(),
                    session_details.get("symptoms_duration").toString(), session_details.get("pre_conditions").toString(),
                    session_details.get("pain_scale").toString(), 0, "", documents);
            future_session.setSession_checkin(session_checkin);
        } catch(JSONException jEx){
            // If an error occurs print the stackTrace
            jEx.getStackTrace();
        } catch(ParseException pEx){
            // If an error occurs print the stackTrace
            pEx.getStackTrace();
        } catch(Exception e){
            e.printStackTrace();
        }
        Log.d("NEW SESSION", "YES");
        Log.d("NEW SESSION USER", user_id);
        Log.d("NEW SESSION NULL", future_session.toString() + "");
        // If the future session did not trigger an error and the user_id is present
        if(future_session != null && !user_id.equals("")) {
            // Declare and instantiate method internal variables
            final String SYMMETRIC_ALIAS = "hyperion_symmetric_" + user_id;
            final String DATA_FILENAME = user_id + "_hyperion.enc";
            // Read the user's saved encrypted file contents from the App storage
            String encrypted_data = encryption.basicRead(MyFirebaseService.this, DATA_FILENAME);
            Log.d("NEW SESSION ENC", encrypted_data);
            // Decrypt the data retrieved from the file using the Symmetric key from the Android
            // Keystore
            String json_data = encryption.decryptSymmetric(encrypted_data, SYMMETRIC_ALIAS);
            // Declare a new PatientDetails instance
            PatientDetails p;
            // Check if the decrypted data is null (this happens if the data is empty/
            // or there was an error in decrypting the data
            if(json_data != null) {
                Log.d("ADD NEW SESSION", "TRUE");
                // Reference: https://mkyong.com/java/how-do-convert-java-object-to-from-json-format-gson-api/
                // Use the GSON class to parse the decrypted data from JSON String
                // to a new PatientDetails instance
                p = gson.fromJson(json_data, PatientDetails.class);
                List<Checkin> sessions = p.getPatientSessions();
                sessions.add(future_session);
                // Save data to encrypted local file
                String msg = encryption.saveData(p, SYMMETRIC_ALIAS, MyFirebaseService.this, DATA_FILENAME);
            }
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
            //notificationChannel.setAllowBubbles(true);
            notificationChannel.setBypassDnd(true);
            // Add notification channel to notification manager
            notificationManager.createNotificationChannel(notificationChannel);
        }
        // Send notification channel with ID
        notificationManager.notify(NOTIFICATION_ID, notificationCompatBuilder.build());
    }
}
