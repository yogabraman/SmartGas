package creativestation.smartgas;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationChannelGroup;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import static android.app.Notification.EXTRA_NOTIFICATION_ID;

public class Notif extends AppCompatActivity {
    private static final int NOTIFICATION_ID_1 =0;
    private static final int NOTIFICATION_ID_2 =1;

    private static final String YES_ACTION = "creativestation.smartgas.YES_ACTION";
    private static final String NO_ACTION = "creativestation.smartgas.NO_ACTION";

    private static final String NOTIFICATION_CHANNEL_1_NAME = "Channel 1";
    private static final String NOTIFICATION_CHANNEL_1_DESC = "A Notification Channel 1";
    private static final String NOTIFICATION_CHANNEL_1_ID = "CHANNEL_1";

    private static final String NOTIFICATION_CHANNEL_2_NAME = "Channel 2";
    private static final String NOTIFICATION_CHANNEL_2_DESC = "A Notification Channel 2";
    private static final String NOTIFICATION_CHANNEL_2_ID = "CHANNEL_2";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notif);

        createNotificationChannel();

        findViewById(R.id.show1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showNotification(NOTIFICATION_CHANNEL_1_ID,NOTIFICATION_ID_1);
            }
        });

        findViewById(R.id.hide1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideNotification(NOTIFICATION_ID_1);
            }
        });

        findViewById(R.id.show2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showNotification(NOTIFICATION_CHANNEL_2_ID,NOTIFICATION_ID_2);
            }
        });

        findViewById(R.id.hide2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideNotification(NOTIFICATION_ID_2);
            }
        });
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

    private void showNotification(String channel, int id) {
        Intent snoozeIntent = new Intent(this, Notif.class);
        String title = getResources().getString(R.string.title);
        String title1 = "Penggunaan tidak wajar!!";
        String longText = "Penggunaan gas Anda diatas rata-rata. \n\nMatikan Regulator Sekarang?";
        snoozeIntent.setAction(YES_ACTION);
        snoozeIntent.setAction(NO_ACTION);
        snoozeIntent.putExtra(EXTRA_NOTIFICATION_ID, 0);
        PendingIntent snoozePendingIntent =
                PendingIntent.getBroadcast(this, 0, snoozeIntent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channel);
        builder.setDefaults(Notification.DEFAULT_ALL)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setTicker("New Notification")
                .setContentTitle(title)
                .setContentText(longText)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(longText))
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setAutoCancel(true)
                .setWhen(System.currentTimeMillis())
                .addAction(R.drawable.info, getString(R.string.no),
                        snoozePendingIntent)
                .addAction(R.drawable.info, getString(R.string.yes),
                        snoozePendingIntent);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN){
            builder.setPriority(Notification.PRIORITY_HIGH);
        }

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        assert notificationManager != null;
        notificationManager.notify(id, builder.build());
    }

    private void hideNotification(int id) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        assert notificationManager != null;
        notificationManager.cancel(id);
    }
}
