package com.example.vuhung.video10minutes;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vuhung.video10minutes.Adapter.AvatarChildAdapter;
import com.example.vuhung.video10minutes.Adapter.IClickListenerAvatarChildAdapter;
import com.example.vuhung.video10minutes.Database.DBChild;
import com.example.vuhung.video10minutes.Database.DBRoutes;
import com.example.vuhung.video10minutes.Model.Child;
import com.example.vuhung.video10minutes.Model.Route;

import java.util.ArrayList;

import static com.example.vuhung.video10minutes.NewRouteActivity.iDRunRoute;
import static com.example.vuhung.video10minutes.RoutesActivity.alarmService;


public class TimeRemain extends AppCompatActivity implements IClickListenerAvatarChildAdapter {
    public static boolean isAlarmVideo = true;
    ProgressBar progressBar;
    TextView tvTime, tvRouteName;
    ImageButton imgbtnStartAlarm;
    Button btnAdd5Minutes;
    boolean isAddTime = false;
    boolean routeRun = false;
    public static boolean isStartAlarm = false;
    long timeCurrent;
    public static long time;
    public static DBRoutes dbRoutes;
    Route route;
    int iDRouteCurrent;
    MyBroadCastReceiver myBroadCastReceiver;
    public static final String BROADCAST_ACTION = "com.ojastec.broadcastreceiverdemo";
    RecyclerView rvChildrenAvatar;
    AvatarChildAdapter avatarChildAdapter;


    @Override
    protected void onResume() {
        super.onResume();
        iDRouteCurrent = route.getId();
        route = dbRoutes.getRouteById(iDRouteCurrent);
        Log.d("zxcv","id "+iDRouteCurrent +"  " +route.getId());
        time = route.getTime();
        timeCurrent = route.getTimeCurrent();
        updateTextAndProgressbar(time,timeCurrent);
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
        dbRoutes.update(route.getId(),new Route(route.getName(),childrenSelectUpdate,route.getIcon(),route.getTimeCurrent(),route.getTime()));
        route = dbRoutes.getRouteById(route.getId());
        avatarChildAdapter = new AvatarChildAdapter(route.getListChildren(), this, this);
        rvChildrenAvatar.setAdapter(avatarChildAdapter);
        rvChildrenAvatar.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_remain);

        progressBar = findViewById(R.id.progressBar);
        imgbtnStartAlarm = findViewById(R.id.btn_start_alarm);
        btnAdd5Minutes = findViewById(R.id.btn_add_5minutes_main);
        tvTime = findViewById(R.id.tv_time);

        rvChildrenAvatar = findViewById(R.id.rv_children_avatar);
        tvRouteName = findViewById(R.id.tv_route_name_time_remain);

        progressBar.setProgress(0);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BROADCAST_ACTION);
        myBroadCastReceiver = new MyBroadCastReceiver();
        registerReceiver(myBroadCastReceiver, intentFilter);

        dbRoutes = new DBRoutes(this);


        Intent i1 = TimeRemain.this.getIntent();
        if (i1.getBooleanExtra("start", false)) {
            Log.d("abcde", "idRoute " + iDRunRoute);
            route = dbRoutes.getRouteById(iDRunRoute);
            timeCurrent = time;
            isStartAlarm = true;
            routeRun = true;
            imgbtnStartAlarm.setImageResource(R.drawable.pause_utton);
        } else {
            Intent i = TimeRemain.this.getIntent();
            Log.d("zxcv", "route id run " + i.getIntExtra("route_id", -1));
            if (i.getIntExtra("route_id", -1) >= 0) {
                route = dbRoutes.getRouteById(i.getIntExtra("route_id", -1));
                if (iDRunRoute == route.getId()) {
                    time = route.getTime();
                    timeCurrent = route.getTimeCurrent();
                    updateTextAndProgressbar(time, timeCurrent);
                    isStartAlarm = true;
                    routeRun = true;
                    imgbtnStartAlarm.setImageResource(R.drawable.pause_utton);
                } else {
                    imgbtnStartAlarm.setImageResource(R.drawable.play_button);
                    route = dbRoutes.getRouteById(i.getIntExtra("route_id", -1));
                    time = route.getTime();
                    timeCurrent = route.getTimeCurrent();
                    updateTextAndProgressbar(time, timeCurrent);
                }
                Log.d("abcde", "route name " + route.getName());
            }
        }

        tvRouteName.setText(route.getName());
        avatarChildAdapter = new AvatarChildAdapter(route.getListChildren(), this, this);
        rvChildrenAvatar.setAdapter(avatarChildAdapter);
        rvChildrenAvatar.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        imgbtnStartAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isStartAlarm) { //stop
                    if (routeRun) {
                        iDRunRoute = -1;
                        routeRun = false;
                        imgbtnStartAlarm.setImageResource(R.drawable.play_button);
                        alarmService.stopAlarm();
                        alarmService.setTimeBefore(time);
                        updateTextAndProgressbar(alarmService.getTimeBefore(), timeCurrent);
                        isStartAlarm = false;
                        dbRoutes.update(route.getId(), new Route(route.getName(), route.getListChildren(), route.getIcon(), timeCurrent, time));
                        route = dbRoutes.getRouteByName(route.getName());
                    } else {
                        Toast.makeText(TimeRemain.this, "The other timer is running!", Toast.LENGTH_SHORT).show();
                    }
                } else {//start
                    routeRun = true;
                    iDRunRoute = route.getId();
                    imgbtnStartAlarm.setImageResource(R.drawable.pause_utton);
                    alarmService.setTimeBefore(time);
                    alarmService.setTime(timeCurrent);
                    alarmService.startAlarm(route.getName());
                    updateTextAndProgressbar(alarmService.getTimeBefore(), timeCurrent);
                    isStartAlarm = true;
                }
            }
        });
        btnAdd5Minutes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                time = time + 5 * 60 * 1000;
                timeCurrent = timeCurrent + 5 * 60 * 1000;
                dbRoutes.update(route.getId(), new Route(route.getName(), route.getListChildren(), route.getIcon(), timeCurrent, time));
                route = dbRoutes.getRouteByName(route.getName());

                if (isStartAlarm) {
                    alarmService.stopAlarm();
                    alarmService.setTimeBefore(time);
                    alarmService.setTime(timeCurrent);
                    alarmService.startAlarm(route.getName());

                } else {
                    isAddTime = true;
                    alarmService.setTime(timeCurrent);
                    alarmService.setTimeBefore(time);
                    updateTextAndProgressbar(alarmService.getTimeBefore(), timeCurrent);
                }
            }
        });
//        btnAlarmRingtones.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//            startActivity(new Intent(TimeRemain.this,AlarmRingtoneActivity.class));
//            }
//        });
    }


    @Override
    protected void onStart() {
        super.onStart();


    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    void updateTextAndProgressbar(long max, long current) {
        progressBar.setMax((int) max);
        long h = current / 3600000;
        long m = current % 3600000 / 60000;
        long s = current % 60000 / 1000;
        String hms = String.format("%02d : %02d : %02d", h, m, s);
        String st;
        if (isAddTime) {
            isAddTime = false;
            st = String.valueOf((int) (max - current));
        } else {
            st = String.valueOf((int) (max - current + 1000));
        }
        tvTime.setText(hms);
        progressBar.setProgress(Integer.parseInt(st));
        //notification
    }

    @Override
    public void ClickAvatarChild(int iD) {
        Intent intent = new Intent(this, ChildActivity.class);
        intent.putExtra("child_id_update", iD);
        startActivity(intent);
    }

    public void cancelOnClick(View view) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you want to cancel this route?");
        builder.setCancelable(false);
        builder.setPositiveButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.setNegativeButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Log.d("abcdid","id "+iDRouteCurrent+"  "+iDRunRoute);
                if (iDRouteCurrent == iDRunRoute) {
                    iDRunRoute = -1;
                    routeRun = false;
                    imgbtnStartAlarm.setImageResource(R.drawable.play_button);
                    alarmService.stopAlarm();
                    alarmService.setTimeBefore(time);
                    updateTextAndProgressbar(alarmService.getTimeBefore(), timeCurrent);
                    isStartAlarm = false;
                }
                startActivity(new Intent(TimeRemain.this,RoutesActivity.class));
                dialogInterface.dismiss();
                finish();

            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void editOnclick(View view) {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Do you want to edit route?");
            builder.setCancelable(false);
            builder.setPositiveButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            builder.setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Log.d("abcdid","id "+iDRouteCurrent+"  "+iDRunRoute);
                    if (iDRouteCurrent == iDRunRoute) {
                        iDRunRoute = -1;
                        routeRun = false;
                        imgbtnStartAlarm.setImageResource(R.drawable.play_button);
                        alarmService.stopAlarm();
                        alarmService.setTimeBefore(time);
                        updateTextAndProgressbar(alarmService.getTimeBefore(), timeCurrent);
                        isStartAlarm = false;
                        dbRoutes.update(route.getId(), new Route(route.getName(), route.getListChildren(), route.getIcon(), timeCurrent, time));
                        route = dbRoutes.getRouteByName(route.getName());
                    }
                    Intent intent = new Intent(TimeRemain.this, NewRouteActivity.class);
                    intent.putExtra("route_id_update", iDRouteCurrent);
                    startActivity(intent);
                    dialogInterface.dismiss();
                }
            });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
    }

    class MyBroadCastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (routeRun) {
                try {
                    Log.d("abc progressbar max", String.valueOf((int) alarmService.getTimeBefore()));
                    timeCurrent = intent.getLongExtra("data", 0); // data is a key specified to intent while sending broadcast
                    Log.e("abc", "data== " + timeCurrent);
                    updateTextAndProgressbar(alarmService.getTimeBefore(), timeCurrent);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
}
