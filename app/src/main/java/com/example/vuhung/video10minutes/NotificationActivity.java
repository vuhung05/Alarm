package com.example.vuhung.video10minutes;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.example.vuhung.video10minutes.Adapter.AvatarChildAdapter;
import com.example.vuhung.video10minutes.Adapter.IClickListenerAvatarChildAdapter;
import com.example.vuhung.video10minutes.Database.DBChild;
import com.example.vuhung.video10minutes.Model.Child;
import com.example.vuhung.video10minutes.Model.Route;
import com.ncorti.slidetoact.SlideToActView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import static com.example.vuhung.video10minutes.NewRouteActivity.iDRunRoute;
import static com.example.vuhung.video10minutes.RoutesActivity.alarmService;
import static com.example.vuhung.video10minutes.TimeRemain.dbRoutes;
import static com.example.vuhung.video10minutes.TimeRemain.isAlarmVideo;
import static com.example.vuhung.video10minutes.TimeRemain.isStartAlarm;
import static com.example.vuhung.video10minutes.TimeRemain.time;


public class NotificationActivity extends AppCompatActivity implements IClickListenerAvatarChildAdapter {
    public  static boolean isTimeUp = false;

    SlideToActView slideToActView;
    VideoView videoView;
    FrameLayout layoutNotification;
    Button btnAdd5Minutes;
    ImageButton imgbtnSmile;
    private int currentApiVersion;
    MediaPlayer mp;
    String path;
    boolean isPlay = false;
    Vibrator vibrator;
    Handler handler = new Handler();
    Runnable runnable;
    Route route;
    RecyclerView rvChildrenAvatar;
    TextView tvRouteName;
    AvatarChildAdapter avatarChildAdapter;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        unlockScreen();
        currentApiVersion = android.os.Build.VERSION.SDK_INT;
        final int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

        // This work only for android 4.4+
        if (currentApiVersion >= Build.VERSION_CODES.KITKAT) {
            getWindow().getDecorView().setSystemUiVisibility(flags);
            // Code below is to handle presses of Volume up or Volume down.
            // Without this, after pressing volume buttons, the navigation bar will
            // show up and won't hide
            final View decorView = getWindow().getDecorView();
            decorView
                    .setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {

                        @Override
                        public void onSystemUiVisibilityChange(int visibility) {
                            if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                                decorView.setSystemUiVisibility(flags);
                            }
                        }
                    });
        }


        setContentView(R.layout.activity_notification);
        slideToActView = findViewById(R.id.slide_to_finish);
        // videoView = findViewById(R.id.videoview_notify);
        layoutNotification = (FrameLayout) findViewById(R.id.layout_notification);
        btnAdd5Minutes = findViewById(R.id.btn_add_5minutes);
        imgbtnSmile = findViewById(R.id.imgbtn_smile);
        route = dbRoutes.getRouteById(iDRunRoute);
        rvChildrenAvatar = findViewById(R.id.rv_children_avatar2);
        tvRouteName = findViewById(R.id.tv_route_name_notification);
        tvRouteName.setText(route.getName());
        avatarChildAdapter = new AvatarChildAdapter(route.getListChildren(), this, this);
        rvChildrenAvatar.setAdapter(avatarChildAdapter);
        rvChildrenAvatar.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));


        alarmService.stopAlarm();
        isStartAlarm = false;
        alarmService.showNotificationFinish(route.getName());
        isTimeUp = true;

        Animation animation = new AlphaAnimation(1, 0);
        animation.setDuration(1000);
        animation.setInterpolator(new LinearInterpolator());
        animation.setRepeatCount(Animation.INFINITE);
        animation.setRepeatMode(Animation.REVERSE);
        imgbtnSmile.startAnimation(animation);

        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        runnable = new Runnable() {
            @Override
            public void run() {
                Log.d("abc", "vibrate");
                vibrates();
            }
        };


//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
//        } else {
//            //deprecated in API 26
//            vibrator.vibrate(500);
//        }
        Intent i = this.getIntent();
        boolean play = (i.getBooleanExtra("a", false));//da phat hay chua
        Log.d("ring","isplay"+play);
        sharedPreferences = getSharedPreferences("MY_ALARM", Context.MODE_PRIVATE);
        if (sharedPreferences.getBoolean("vibrate", false)) {
            vibrates();
        }
        if (!sharedPreferences.getString("path_alarm", "").equals("")) {
            path = sharedPreferences.getString("path_alarm", "");
            if (isAlarmVideo) {
                videoView = new VideoView(this);
                layoutNotification.addView(videoView);
                videoView.setVideoPath(path);
                videoView.start();
                Log.d("abcd ", "play video " + path);
                videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        mp.setLooping(true);
                    }
                });
            } else {
                ImageView imageView = new ImageView(this);
                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
                params.bottomMargin = 12;
                imageView.setLayoutParams(params);
                imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                imageView.setImageResource(R.drawable.backgroudw);
                layoutNotification.addView(imageView);
                if (!play) {
                    alarmService.playAlarm(path);
                }
            }
        } else {
            ImageView imageView = new ImageView(this);
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
            params.bottomMargin = 12;
            imageView.setLayoutParams(params);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            imageView.setImageResource(R.drawable.backgroudw);
            layoutNotification.addView(imageView);
            if (!play) {

                alarmService.playAlarmDefault();
            }
        }


        slideToActView.setOnSlideCompleteListener(new SlideToActView.OnSlideCompleteListener() {
            @Override
            public void onSlideComplete(@NotNull SlideToActView slideToActView) {
                iDRunRoute = -1;
                isTimeUp = false;
                alarmService.stopRing();
                alarmService.hideNotificationFinish();
                handler.removeCallbacks(runnable);
                startActivity(new Intent(NotificationActivity.this, RoutesActivity.class));
                finish();
            }
        });
        btnAdd5Minutes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isTimeUp = false;
                iDRunRoute = route.getId();
                time = time + 5 * 60 * 1000;
                alarmService.stopRing();
                alarmService.setTimeBefore(time);
                alarmService.setTime(5 * 60 * 1000);
                handler.removeCallbacks(runnable);
                dbRoutes.update(route.getId(), new Route(route.getName(), route.getListChildren(), route.getIcon(), 5 * 60 * 1000, time));
                route = dbRoutes.getRouteById(route.getId());
                alarmService.startAlarm(route.getName());
                isStartAlarm = true;
                alarmService.hideNotificationFinish();
                Intent i = new Intent(NotificationActivity.this, TimeRemain.class);
                i.putExtra("start", true);
                Toast.makeText(NotificationActivity.this, "Alarm after 5 minutes", Toast.LENGTH_SHORT).show();
                startActivity(i);
                finish();
            }
        });
    }

    private void unlockScreen() {
        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
    }

    private void vibrates() {
        vibrator.vibrate(new long[]{0, 500, 200, 200, 500, 200, 200}, -1);
        handler.postDelayed(runnable, 2500);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnable);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            //preventing default implementation previous to android.os.Build.VERSION_CODES.ECLAIR
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    //full screen
    @SuppressLint("NewApi")
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (currentApiVersion >= Build.VERSION_CODES.KITKAT && hasFocus) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    @Override
    public void ClickAvatarChild(int iD) {
        Intent intent = new Intent(this, ChildActivity.class);
        intent.putExtra("child_id_update", iD);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!sharedPreferences.getString("path_alarm", "").equals("")) {
            path = sharedPreferences.getString("path_alarm", "");
            if (isAlarmVideo) {
                videoView = new VideoView(this);
                layoutNotification.addView(videoView);
                videoView.setVideoPath(path);
                videoView.start();
                Log.d("abcd ", "play video " + path);
                videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        mp.setLooping(true);
                    }
                });
            }
        }

        route = dbRoutes.getRouteById(route.getId());
        DBChild dbChild = new DBChild(this);
        tvRouteName.setText(route.getName());
        ArrayList<Child> allChildren = new ArrayList<Child>();
        ArrayList<Child> childrenSelect = new ArrayList<Child>();
        childrenSelect = dbRoutes.getRouteById(route.getId()).getListChildren();
        allChildren = dbChild.getAllChild();
        ArrayList<Child> childrenSelectUpdate = new ArrayList<Child>();
        for (int j = 0; j < allChildren.size(); j++) {
            for (int i = 0; i < childrenSelect.size(); i++) {
                if (childrenSelect.get(i).getId() == allChildren.get(j).getId()) {
                    childrenSelectUpdate.add(allChildren.get(j));
                    Log.d("abcde", "children select " + String.valueOf(childrenSelectUpdate.size()));
                }
            }
        }
        dbRoutes.update(route.getId(), new Route(route.getName(), childrenSelectUpdate, route.getIcon(), route.getTimeCurrent(), route.getTime()));
        route = dbRoutes.getRouteById(route.getId());
        avatarChildAdapter = new AvatarChildAdapter(route.getListChildren(), this, this);
        rvChildrenAvatar.setAdapter(avatarChildAdapter);
        rvChildrenAvatar.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
    }
}
