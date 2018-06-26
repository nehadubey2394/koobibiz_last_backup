package com.mualab.org.biz.fcm;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.mualab.org.biz.R;
import com.mualab.org.biz.modules.MainActivity;
import com.mualab.org.biz.modules.SplashActivity;
import com.mualab.org.biz.session.Session;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMessagingService";
    private String body;

    @SuppressLint("LongLogTag")
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        Log.d(TAG, "getFrom : " + remoteMessage.getFrom());
        Log.d(TAG, "getData : " + remoteMessage.getData().toString());
        Log.d(TAG, "getNotification : " + remoteMessage.getNotification().toString());

        if (remoteMessage.getData() != null) {
            notificationHandle(remoteMessage);
            Log.d(TAG, "getNotification : " + remoteMessage.getNotification().toString());
        }
    }

    private void notificationHandle(RemoteMessage remoteMessage) {
        /*  String type = remoteMessage.getData().get("type");
        if (type != null && !type.equals("")) {
            if (type.equals("Reminder_add") | type.equals("Reminder_delete") | type.equals("Reminder_update")) {
                String notification_id = remoteMessage.getData().get("notification_id");
            }
        }*/
        String title = remoteMessage.getData().get("title");
      //  String notification_id = remoteMessage.getData().get("notification_id");
        String intentType = "1";
        sendNotificationAddReminder(title, intentType, "1");

    }

    private void sendNotificationAddReminder(String title, String intentType, String notification_id) {
        Intent intent = null;
        Session session = new Session(this);
        if (session.isLoggedIn()) {
            //  if (intentType.equals("1")) {
            intent = new Intent(this, MainActivity.class);
            // intent.putExtra("notification_id", notification_id);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            // } else if (intentType.equals("2")) {
//                intent = new Intent(this, SuffererHomeActivity.class);
//                intent.putExtra("notification_id", notification_id);
//                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            //}
        }else {
            intent = new Intent(this, SplashActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        }

        PendingIntent pendingIntent = PendingIntent.getActivities(this, 0, new Intent[]{intent}, PendingIntent.FLAG_ONE_SHOT);
        Uri notificaitonSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setLargeIcon(BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.ic_launcher))
                .setSmallIcon(R.drawable.ic_launcher)
                .setColor(getResources().getColor(R.color.colorPrimary))
                .setContentTitle("Mualab Biz")
                .setContentText(title)
                .setAutoCancel(true)
                .setSound(notificaitonSound)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, notificationBuilder.build());

        if (intentType.equals("7")) {
            NotificationCompat.Builder notificationBuilder1 = new NotificationCompat.Builder(this)
                    .setLargeIcon(BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.ic_launcher))
                    .setSmallIcon(R.drawable.ic_launcher)
                    .setColor(getResources().getColor(R.color.colorPrimary))
                    .setContentTitle(title)
                    .setContentText(body)
                    .setAutoCancel(true)
                    .setSound(notificaitonSound)
                    .setContentIntent(pendingIntent);
            NotificationManager notificationManager1 = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager1.notify(0, notificationBuilder1.build());
        }


    }

}
