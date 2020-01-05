package creativestation.smartgas;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

import creativestation.smartgas.Preferences.PrefManager;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final int NOTIFICATION_ID_1 = 0;
    private static final int NOTIFICATION_ID_2 = 1;

    private static final String NOTIFICATION_CHANNEL_1_NAME = "Kadar Gas";
    private static final String NOTIFICATION_CHANNEL_1_DESC = "Notifikasi Kebocoran Gas";
    private static final String NOTIFICATION_CHANNEL_1_ID = "CHANNEL_1";

    private static final String NOTIFICATION_CHANNEL_2_NAME = "Berat Isi Gas";
    private static final String NOTIFICATION_CHANNEL_2_DESC = "Notifikasi Gas Hampir Habis";
    private static final String NOTIFICATION_CHANNEL_2_ID = "CHANNEL_2";

    public PrefManager prefManager;
    String gaschild;

    @Override
            public void onMessageReceived(RemoteMessage remoteMessage) {
                prefManager = new PrefManager(this);
                gaschild = prefManager.getAlat();
                /*gaschild = "smartgas1";*/
                createNotificationChannel();
                // Check if message contains a data payload.
                if (remoteMessage.getData().size() > 0) {
                    if (remoteMessage.getFrom().startsWith("/topics/kadar"+gaschild)){
                        showNotification(NOTIFICATION_CHANNEL_1_ID,NOTIFICATION_ID_1, remoteMessage.getData().get("title"),
                                remoteMessage.getData().get("body"));
                    }
                    if (remoteMessage.getFrom().startsWith("/topics/lpg"+gaschild)){
                        showNotification(NOTIFICATION_CHANNEL_2_ID,NOTIFICATION_ID_2, remoteMessage.getData().get("title"),
                                remoteMessage.getData().get("body"));
                    }
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
        }
    }

    private void createNotificationChannel(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel notificationChannel_1 = new NotificationChannel(NOTIFICATION_CHANNEL_1_ID,
                    NOTIFICATION_CHANNEL_1_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel_1.setDescription(NOTIFICATION_CHANNEL_1_DESC);

            NotificationChannel notificationChannel_2 = new NotificationChannel(NOTIFICATION_CHANNEL_2_ID,
                    NOTIFICATION_CHANNEL_2_NAME, NotificationManager.IMPORTANCE_HIGH);
            notificationChannel_2.setDescription(NOTIFICATION_CHANNEL_2_DESC);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            assert notificationManager != null;

            notificationManager.createNotificationChannel(notificationChannel_1);
            notificationManager.createNotificationChannel(notificationChannel_2);
        }
    }

    private void showNotification(String channel, int id, String title, String body) {
        Intent intent = new Intent(this, HomeActivity.class).putExtra("pindah",channel);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 , intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, channel)
                .setContentTitle(title)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentText(body)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent)
                .setPriority(Notification.PRIORITY_MAX)
                .setDefaults(Notification.DEFAULT_ALL);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            notificationBuilder.setPriority(Notification.PRIORITY_HIGH);
        }

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        assert notificationManager != null;
        notificationManager.notify(id, notificationBuilder.build());
    }
}
