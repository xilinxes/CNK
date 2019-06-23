package com.example.cnk;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MessageNotifficationService extends Service {

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("Users");
    String userId, nickname;
    String NOTIFICATION_CNANNEL_ID = "Messages";
    Handler h = new Handler();
    SharedPreferences sPref;
    Boolean pr = true;
    Boolean chechkDestroy = true;
    long[] vibrPattern = new long[]{0, 300, 500, 500};


    public MessageNotifficationService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        run.run();


    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return START_STICKY;

    }

    @Override
    public void onDestroy() {
        startService(new Intent(getApplicationContext(), MessageNotifficationService.class));
        pr = false;
    }

    private void showNotification(String userWithName, String title, String body) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Intent resultIntent = new Intent(this, DialogsWindow.class);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CNANNEL_ID, "Notifff", NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.setDescription("EDMT Channel");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.BLUE);
            notificationChannel.enableVibration(true);
            notificationChannel.setVibrationPattern(vibrPattern);
            notificationChannel.enableLights(true);
            notificationManager.createNotificationChannel(notificationChannel);
        }
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CNANNEL_ID);
        notificationBuilder.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.ic_data_usage_black_24dp)
                .setContentTitle(title)
                .setPriority(100)
                .setVibrate(vibrPattern)
                .setContentText(body)
                .setContentIntent(resultPendingIntent)
                .setContentInfo("INFO");
        notificationManager.notify(userWithName.hashCode(), notificationBuilder.build());
    }

    Runnable run = new Runnable() {

        @Override
        public void run() {
            sPref = getSharedPreferences("Saves", MODE_PRIVATE);
            userId = String.valueOf(sPref.getInt("USER_ID", 1));
            nickname = sPref.getString("Name", "");
            myRef.child(userId).child("dialogs_info").child("allCountMessages").addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    if (pr) {
                        String userWithName = dataSnapshot.getKey();
                        showNotification(userWithName, nickname, "Вам новое сообщение от " + userWithName);
                        h.postDelayed(run, 3000);
                    }
                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            h.postDelayed(this, 1000);
        }
    };

}
