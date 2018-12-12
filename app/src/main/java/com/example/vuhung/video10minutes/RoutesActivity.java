package com.example.vuhung.video10minutes;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vuhung.video10minutes.Adapter.IClickListenerRouteAdapter;
import com.example.vuhung.video10minutes.Adapter.RouteAdapter;
import com.example.vuhung.video10minutes.Database.DBRoutes;
import com.example.vuhung.video10minutes.Model.Route;
import com.example.vuhung.video10minutes.Service.AlarmService;

import java.util.ArrayList;

import static com.example.vuhung.video10minutes.NewRouteActivity.iDRunRoute;
import static com.example.vuhung.video10minutes.SettingActivity.REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION_RESULT;

public class RoutesActivity extends AppCompatActivity implements IClickListenerRouteAdapter {
    ImageButton imgbtnAddRoutes;

    Toolbar actionbar;
    RecyclerView rvRoute;
    private ServiceConnection connection;
    public static AlarmService alarmService;
    public static boolean isBound = false;
    MyBroadCastReceiver myBroadCastReceiver;
    public static final String BROADCAST_ACTION = "com.ojastec.broadcastreceiverdemo";
    DBRoutes dbRoutes;
    ArrayList<Route> routesList = new ArrayList<Route>();
    RouteAdapter routeAdapter;



    @Override
    protected void onStop() {
        super.onStop();
        if (isBound) {
            this.unbindService(connection);
            isBound = false;
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION_RESULT) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission successfully granded!", Toast.LENGTH_SHORT).show();
            }
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_routes);
        actionbar = (Toolbar) findViewById(R.id.actionbar_main);
        TextView mTitle = (TextView) actionbar.findViewById(R.id.actionbar_title);
        setSupportActionBar(actionbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        // Khởi tạo ServiceConnection
        connection = new ServiceConnection() {
            // Phương thức này được hệ thống gọi khi kết nối tới service bị lỗi
            @Override
            public void onServiceDisconnected(ComponentName name) {
                isBound = false;
            }

            // Phương thức này được hệ thống gọi khi kết nối tới service thành công
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                AlarmService.MyBinder binder = (AlarmService.MyBinder) service;
                alarmService = binder.getService(); // lấy đối tượng alarmService
                isBound = true;
            }
        };
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
            } else {
                if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    Toast.makeText(this, "App needs to be able to save", Toast.LENGTH_SHORT).show();
                }
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION_RESULT);
            }
        }

        imgbtnAddRoutes = findViewById(R.id.imgbtn_add_routes)   ;
        rvRoute = findViewById(R.id.rv_routes);
        dbRoutes= new DBRoutes(this);
        routesList = dbRoutes.getAllRoute();
        routeAdapter = new RouteAdapter(routesList,this,this);
        rvRoute.setAdapter(routeAdapter);
        rvRoute.setLayoutManager(new LinearLayoutManager(this));
        final Intent intent = new Intent(this, AlarmService.class);
        bindService(intent, connection, Context.BIND_AUTO_CREATE);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BROADCAST_ACTION);
        myBroadCastReceiver = new MyBroadCastReceiver();
        registerReceiver(myBroadCastReceiver, intentFilter);

        imgbtnAddRoutes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RoutesActivity.this, NewRouteActivity.class);
                Bundle bundle = new Bundle();
                bundle.putBoolean("add_routes", true);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        routesList = dbRoutes.getAllRoute();
        routeAdapter = new RouteAdapter(routesList,this,this);
        rvRoute.setAdapter(routeAdapter);
        rvRoute.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.setting, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_setting:
                startActivity(new Intent(RoutesActivity.this, SettingActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onclick(View view) {
        startActivity(new Intent(this, NewRouteActivity.class));
    }

    @Override
    public void gotoUpdateRoute(Route route) {
        if (route.getId()==iDRunRoute){
            Toast.makeText(this, "This timer is running!", Toast.LENGTH_SHORT).show();
        }else {
            Intent intent = new Intent(this, NewRouteActivity.class);
            intent.putExtra("route_id_update", route.getId());
            startActivity(intent);
        }
    }
    @Override
    public void itemLongClick(final int position) {
        if (routesList.get(position).getId()==iDRunRoute){
            Toast.makeText(RoutesActivity.this,"This timer is running!",Toast.LENGTH_SHORT).show();
        }else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Do you want to delete this route?");
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
                    dbRoutes.deleteRoute(routesList.get(position));
                    routesList.remove(position);
                    routeAdapter.notifyItemRemoved(position);
                    routeAdapter.notifyDataSetChanged();
                    dialogInterface.dismiss();
                }
            });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }
    }

    @Override
    public void gotoTimeRemaining(int iD,long timeCurrent) {
        Intent intent = new Intent(this, TimeRemain.class);
        intent.putExtra("route_id", iD);
        intent.putExtra("route_time_current",timeCurrent);
        startActivity(intent);
    }
    public class MyBroadCastReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            if (iDRunRoute>=0) {
                long timeCurrent = intent.getLongExtra("data", 0); // data is a key specified to intent while sending broadcast
                Log.e("abc", "data== " + timeCurrent);
                routeAdapter.updateTimeCurrent(iDRunRoute,timeCurrent);
                updateTimeItem(alarmService.getTimeBefore(), timeCurrent);
            }
        }
    }
    void updateTimeItem(long max, long current) {
        long h = current / 3600000;
        long m = current % 3600000 / 60000;
        long s = current % 60000 / 1000;
        String hms = String.format("%02d:%02d:%02d", h, m, s);
        Log.d("abcd","time current " +hms);
    }
}



