package com.example.vuhung.video10minutes.Service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.example.vuhung.video10minutes.Database.DBRoutes;
import com.example.vuhung.video10minutes.NotificationActivity;
import com.example.vuhung.video10minutes.R;
import com.example.vuhung.video10minutes.RoutesActivity;
import com.example.vuhung.video10minutes.TimeRemain;

import java.io.IOException;


public class AlarmService extends Service {
    private IBinder binder;
    static CountDownTimer timer;
    public static long timeMax;
    public static long timeBefore;
    public static long timeCurrent;
    String name;
    int idRoute;
    NotificationManager mNotificationManager;
    NotificationCompat.Builder mBuilder;
    DBRoutes dbRoutes;

    MediaPlayer mp;

    public static long getTimeCurrent() {
        return timeCurrent;
    }

    public static void setTimeCurrent(long timeCurrent) {
        AlarmService.timeCurrent = timeCurrent;
    }

    public static long getTimeBefore() {
        return timeBefore;
    }

    public static void setTimeBefore(long timeBefore) {
        AlarmService.timeBefore = timeBefore;
    }

    public static void setTime(long time) {
        AlarmService.timeMax = time;
    }

    public static long getTime() {
        return timeMax;
    }

    @Override
    public void onCreate() {
        Log.d("alarmservice", "oncreate");
        binder = new MyBinder();
        super.onCreate();
    }

    // Bắt đầu một Service
    @Override
    public IBinder onBind(Intent intent) {
        Log.d("alarmservice", "onbind");
        return binder;
    }

    // Kết thúc một Service
    @Override
    public boolean onUnbind(Intent intent) {
        Log.d("alarmservice", "onunbind");
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
        return super.onUnbind(intent);
    }

    public void startAlarm(String routeName) {
        timer = new CounterClass(timeMax, 1000);
        timer.start();
        name = routeName;
        dbRoutes = new DBRoutes(this);
        idRoute = dbRoutes.getRouteByName(name).getId();
        Log.d("zxcv","id run route " +idRoute);
    }
    public void stopAlarm() {
        timer.cancel();
        if (mNotificationManager != null) {
            mNotificationManager.cancelAll();
        }
    }

    public class MyBinder extends Binder {
        // phương thức này trả về đối tượng MyService
        public AlarmService getService() {
            return AlarmService.this;
        }
    }

    public class CounterClass extends CountDownTimer {
        /**
         * @param millisInFuture    The number of millis in the future from the call
         *                          to {@link #start()} until the countdown is done and {@link #onFinish()}
         *                          is called.
         * @param countDownInterval The interval along the way to receive
         *                          {@link #onTick(long)} callbacks.
         */
        public CounterClass(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            long millis = millisUntilFinished;
//            long m = millis/60000;
//            long s = millis %60000/1000;
//            String hms = String.format("%02d:%02d", m,s);
//            Log.d("abcd",hms);
            //send data to activity
            timeCurrent = millis;
            Intent broadCastIntent = new Intent();
            broadCastIntent.setAction(TimeRemain.BROADCAST_ACTION);
            broadCastIntent.putExtra("data", millis);
            sendBroadcast(broadCastIntent);
            long h = millis / 3600000;
            long m = millis % 3600000 / 60000;
            long s = millis % 60000 / 1000;
            String hms = String.format("%02d:%02d:%02d", h, m, s);
            mBuilder = new NotificationCompat.Builder(getApplicationContext())
                    .setSmallIcon(R.drawable.icon_add_child)
                    .setContentTitle("Active route: "+name)
                    .setContentText("Time remaining: "+hms)
                    .setAutoCancel(false)
                    .setOngoing(true);
            Intent resultIntent = new Intent(getApplicationContext(), TimeRemain.class);
            resultIntent.putExtra("route_id", idRoute);
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(getApplicationContext());
            stackBuilder.addParentStack(RoutesActivity.class);
            stackBuilder.addNextIntent(resultIntent);
            PendingIntent resultPendingIntent =
                    stackBuilder.getPendingIntent(
                            0,
                            PendingIntent.FLAG_UPDATE_CURRENT
                    );
            mBuilder.setContentIntent(resultPendingIntent);
            mNotificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.notify(1, mBuilder.build());
            Log.d("idrun", String.valueOf(idRoute));
        }
        @Override
        public void onFinish() {
            timer.cancel();
            timeBefore = 0;
            if (mNotificationManager!=null){
                mNotificationManager.cancel(1);
            }
            Intent intent1 = new Intent(AlarmService.this, NotificationActivity.class);
            intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent1);
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(1);
    }
    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(1);
    }
    public synchronized void playAlarm(String path){
        mp = new MediaPlayer();
        try {
            mp.setDataSource(path);
            mp.prepare();
            mp.start();
            mp.setLooping(true);
            Log.d("abcd ", "play ringtone " + path);
            Toast.makeText(this, "ringtone is playing", Toast.LENGTH_SHORT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void playVideoAlarm(String path){

    }
    public void playAlarmDefault(){
        mp = MediaPlayer.create(this, R.raw.sound);
        mp.start();
        mp.setLooping(true);

    }
    public void stopRing(){
        if (mp !=null)
        {
            mp.stop();
        }
    }
    public void showNotificationFinish(){
        if (mNotificationManager!=null){
            mNotificationManager.cancel(1);
        }
        mBuilder = new NotificationCompat.Builder(getApplicationContext())
                .setSmallIcon(R.drawable.icon_add_child)
                .setContentTitle("Active route: "+name)
                .setContentText("Time is up!")
                .setAutoCancel(false)
                .setOngoing(true);
        Intent resultIntent = new Intent(getApplicationContext(), NotificationActivity.class);
        resultIntent.putExtra("a",true);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(getApplicationContext());
        stackBuilder.addParentStack(TimeRemain.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(1, mBuilder.build());
    }
    public void hideNotificationFinish(){
        if (mNotificationManager!=null){
            mNotificationManager.cancel(1);
        }
    }
}


