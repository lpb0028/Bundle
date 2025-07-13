package com.example.overlaying;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;

public class BundleService extends Service {

    public SimulationLayout simulation;
    private Manager manager;
    private Context context;

    @Override
    public void onCreate() {
        Manager.serviceRunning = true;
        System.out.println("BundleService - Set serviceRunning to true");
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = intent.getAction();
        if(action != null)
            switch(action) {
                case "CLEAR":
                    simulation.clearEntities();
                    break;
                case "STOP":
                    manager.Close();
                    stopForeground(STOP_FOREGROUND_REMOVE);
                    stopSelf();
                    break;
            }
        return START_NOT_STICKY;
    }

    public Notification createNotification() {
        PendingIntent clearScreen = PendingIntent.getService(context, 0, new Intent(context, BundleService.class).setAction("CLEAR"), PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);
        PendingIntent stopAdventure = PendingIntent.getService(context, 0, new Intent(context, BundleService.class).setAction("STOP"), PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);
        PendingIntent openApp = PendingIntent.getActivity(context, 0, new Intent(context, Activity.class), PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);

        return new NotificationCompat.Builder(context, Settings.NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.bundle_item)
                .setContentTitle("Bundle")
                .setContentText("Adventure mode active")
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .addAction(new NotificationCompat.Action(null, "Clear", clearScreen))
                .addAction(new NotificationCompat.Action(null, "Stop Adventuring", stopAdventure))
                .setContentIntent(openApp)
                .build();
    }
    @Override
    public void onDestroy() {
        Manager.serviceRunning = false;
        System.out.println("BundleService - Set serviceRunning to false");
    }
    @Override
    public IBinder onBind(Intent intent) {
        return new ServiceBinder();
    }

    public class ServiceBinder extends Binder {
        public void Initialize(Context context, Manager manager) {
            BundleService.this.context = context;
            BundleService.this.manager = manager;
        }
        public void StartAdventure() {
            System.out.println("ServiceBinder - start adventure");

            notificationSetup();
            startForeground(1, createNotification());

            simulationSetup();
            simulation.StartAdventure();
        }
        public void StopAdventure() {
            System.out.println("ServiceBinder - stop adventure");
            if(simulation != null) simulation.StopAdventure();
            manager.transparentFrame.removeView(simulation);
        }
        public void StopService() {
            System.out.println("ServiceBinder - stop service");
            BundleService.this.stopSelf();
        }
        public Manager RetreiveManager() {
            System.out.println("ServiceBinder - retrieve manager");
            return manager;
        }
        private void notificationSetup() {
            NotificationChannel channel = new NotificationChannel(
                    Settings.NOTIFICATION_CHANNEL_ID,
                    "Bundle Notifications",
                    NotificationManager.IMPORTANCE_LOW);

            getSystemService(NotificationManager.class).createNotificationChannel(channel);
            System.out.println("ServiceBinder - notificationSetup");
        }
        private void simulationSetup() {
            simulation = new SimulationLayout(context, manager);
            manager.transparentFrame.addView(simulation, Settings.TRANSPARENT_FRAME_PARAMS);
            System.out.println("ServiceBinder - simulationSetup");
        }
    }
}
