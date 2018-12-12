package com.example.vuhung.video10minutes;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vuhung.video10minutes.Adapter.ChildAdapter;
import com.example.vuhung.video10minutes.Adapter.IClickListenerChildAdapter;
import com.example.vuhung.video10minutes.Model.Child;
import com.example.vuhung.video10minutes.Database.DBChild;
import com.example.vuhung.video10minutes.Database.DBRoutes;
import com.example.vuhung.video10minutes.Model.Route;

import java.util.ArrayList;

import static com.example.vuhung.video10minutes.RoutesActivity.alarmService;
import static com.example.vuhung.video10minutes.TimeRemain.time;

public class NewRouteActivity extends AppCompatActivity implements IClickListenerChildAdapter {
    public static int iDRunRoute = -1;
    TextView tvQuickTime, tvManualTime, tvSave, tvTitle;
    EditText edtRouteName;
    FrameLayout layoutIcon;
    ImageView imgIcon;
    RecyclerView rvChildren;
    ChildAdapter childAdapter;
    DBChild dbChild;
    DBRoutes dbRoutes;
    Route routeUpdate;
    ArrayList<Child> allChildren = new ArrayList<Child>();
    ArrayList<Child> listChildren = new ArrayList<Child>();
    ArrayList<Route> allRoute = new ArrayList<Route>();
    int chooseTime = -1;
    long timeCurrent;
    boolean isUpdateRoute = false;
    ImageView imgStart;
    FragmentQuickTime fragmentQuickTime;
    FragmentManualTime fragmentManualTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_route);
        rvChildren = findViewById(R.id.rv_children);
        tvManualTime = findViewById(R.id.tv_manual_time);
        tvQuickTime = findViewById(R.id.tv_quick_time);
        tvSave = findViewById(R.id.tv_save);
        imgStart = findViewById(R.id.img_start);
        imgIcon = findViewById(R.id.img_icon);
        layoutIcon = findViewById(R.id.layout_icon);
        edtRouteName = findViewById(R.id.edt_route_name);
        tvTitle = findViewById(R.id.update_or_new_route);
        imgIcon.setImageResource(R.drawable.icon_route);

        dbChild = new DBChild(this);
        dbRoutes = new DBRoutes(this);

        allChildren = dbChild.getAllChild();
        allRoute = dbRoutes.getAllRoute();
        time = 0;
        chooseTime = 1;
        tvTitle.setText("New route");
        Intent intent = this.getIntent();
        if (intent.getIntExtra("route_id_update",-1)>=0) {
            tvTitle.setText("Update route");
            isUpdateRoute = true;
            chooseTime = 1;
            routeUpdate = dbRoutes.getRouteById(intent.getIntExtra("route_id_update",-1));
            edtRouteName.setText(routeUpdate.getName());
            timeCurrent = routeUpdate.getTimeCurrent();
            time = routeUpdate.getTime();
            Log.d("abcd", "route update id" + routeUpdate.getId() + "name " + routeUpdate.getName() + " time " + time);
            listChildren = routeUpdate.getListChildren();
        }
        childAdapter = new ChildAdapter(allChildren, listChildren, this, this);
        rvChildren.setAdapter(childAdapter);
        rvChildren.setLayoutManager(new LinearLayoutManager(this));

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentQuickTime = new FragmentQuickTime();
        fragmentTransaction.add(R.id.frameContent, fragmentQuickTime);
        fragmentTransaction.commit();

        tvManualTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseTime = 2;
                fragmentManualTime = new FragmentManualTime();
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frameContent, fragmentManualTime);
                fragmentTransaction.commit();
            }
        });
        tvQuickTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseTime = 1;
                fragmentQuickTime = new FragmentQuickTime();
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frameContent, fragmentQuickTime);
                fragmentTransaction.commit();
            }
        });
        tvSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!edtRouteName.getText().toString().trim().isEmpty()) {

                    if (chooseTime == 1) {
                        fragmentQuickTime = new FragmentQuickTime();
                        if (fragmentQuickTime.getQuickTime() > 0) {
                            time = (long) fragmentQuickTime.getQuickTime();
                            timeCurrent = time;
                        } else {
                            time = routeUpdate.getTime();
                            timeCurrent = routeUpdate.getTimeCurrent();
                        }
                    } else if (chooseTime == 2) {
                        FragmentManualTime fragmentManualTime = new FragmentManualTime();
                        time = ((long) fragmentManualTime.getManualTime());
                        timeCurrent = time;
                    }

                    Log.d("abcde", "time  " + time +" curren "+timeCurrent);
                    if (time > 0) {
                        if (listChildren.size() > 0) {
                            if (!isUpdateRoute) {
                                if (dbRoutes.getRouteByName(edtRouteName.getText().toString().trim()).getName() == null) {
                                    int ID;
                                    if (allRoute.size() > 0) {
                                        ID = allRoute.get(allRoute.size() - 1).getId() + 1;
                                    } else ID = 0;
                                    Log.d("abcd", "ID  " + ID);
                                    dbRoutes.addRoutes(new Route(ID, edtRouteName.getText().toString().trim(), listChildren, R.raw.icon3, timeCurrent, time));
                                    allRoute = dbRoutes.getAllRoute();

                                } else
                                    Toast.makeText(NewRouteActivity.this, "The name has exist! ", Toast.LENGTH_SHORT).show();

                            } else {
                                if (iDRunRoute != routeUpdate.getId()) {
                                    dbRoutes.update(routeUpdate.getId(), new Route(edtRouteName.getText().toString().trim(), listChildren, R.raw.icon3, timeCurrent, time));
                                    Toast.makeText(NewRouteActivity.this, "Update completed!", Toast.LENGTH_SHORT).show();
                                    allRoute = dbRoutes.getAllRoute();
                                } else
                                    Toast.makeText(NewRouteActivity.this, "Don't add new route, this route is running!", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(NewRouteActivity.this, "Please select at least one Child!", Toast.LENGTH_SHORT).show();
                        }
                    } else
                        Toast.makeText(NewRouteActivity.this, "Please set your alarm time!", Toast.LENGTH_SHORT).show();
                } else
                    Toast.makeText(NewRouteActivity.this, "Enter name child!", Toast.LENGTH_SHORT).show();

            }
        });
        imgStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!edtRouteName.getText().toString().trim().isEmpty()) {
                    if (chooseTime == 1) {
                        fragmentQuickTime = new FragmentQuickTime();
                        if (fragmentQuickTime.getQuickTime() > 0) {
                            time = (long) fragmentQuickTime.getQuickTime();
                            timeCurrent = time;
                        } else {
                            if (routeUpdate!=null){
                                time = routeUpdate.getTime();
                                timeCurrent = routeUpdate.getTimeCurrent();
                            }
                        }
                    } else if (chooseTime == 2) {
                        FragmentManualTime fragmentManualTime = new FragmentManualTime();
                        time = ((long) fragmentManualTime.getManualTime());
                        timeCurrent = time;
                    }

                    Log.d("abcde", "time  " + time);
                    if (time > 0) {
                        if (listChildren.size() > 0) {
                            if (!isUpdateRoute) {
                                if (dbRoutes.getRouteByName(edtRouteName.getText().toString().trim()).getName() == null) {
                                    int ID;
                                    if (allRoute.size() > 0) {
                                        ID = allRoute.get(allRoute.size() - 1).getId() + 1;
                                    } else ID = 0;
                                    Log.d("abcd", "ID  " + ID);
                                    dbRoutes.addRoutes(new Route(ID, edtRouteName.getText().toString().trim(), listChildren, R.raw.icon3, timeCurrent, time));
                                    allRoute = dbRoutes.getAllRoute();
                                    Toast.makeText(NewRouteActivity.this, "Added new route", Toast.LENGTH_SHORT).show();
                                    if (iDRunRoute < 0) {
                                        alarmService.setTimeBefore(time);
                                        alarmService.setTime(timeCurrent);

                                        iDRunRoute = ID;
                                        alarmService.startAlarm(dbRoutes.getRouteById(iDRunRoute).getName());
                                        Intent intent = new Intent(NewRouteActivity.this, TimeRemain.class);
                                        intent.putExtra("route_id",iDRunRoute);
                                        startActivity(intent);
                                    } else {
                                        Toast.makeText(NewRouteActivity.this, "The other timer is running!", Toast.LENGTH_SHORT).show();
                                    }

                                } else
                                    Toast.makeText(NewRouteActivity.this, "The name has exist! ", Toast.LENGTH_SHORT).show();

                            } else {
                                if (iDRunRoute != routeUpdate.getId()) {
                                    dbRoutes.update(routeUpdate.getId(), new Route(edtRouteName.getText().toString().trim(), listChildren, R.raw.icon3, timeCurrent, time));
                                    Toast.makeText(NewRouteActivity.this, "Update completed!", Toast.LENGTH_SHORT).show();
                                    allRoute = dbRoutes.getAllRoute();
                                    alarmService.setTimeBefore(time);
                                    alarmService.setTime(timeCurrent);
                                    alarmService.startAlarm(routeUpdate.getName());
                                    iDRunRoute = routeUpdate.getId();
                                    Intent intent = new Intent(NewRouteActivity.this, TimeRemain.class);
                                    intent.putExtra("route_id", iDRunRoute);
                                    startActivity(intent);
                                } else {
                                    Toast.makeText(NewRouteActivity.this, "Don't add new route, this route is running!", Toast.LENGTH_SHORT).show();
                                }

                            }
                        } else {
                            Toast.makeText(NewRouteActivity.this, "Please select at least one Child!", Toast.LENGTH_SHORT).show();
                        }
                    } else
                        Toast.makeText(NewRouteActivity.this, "Please set your alarm time!", Toast.LENGTH_SHORT).show();
                } else
                    Toast.makeText(NewRouteActivity.this, "Enter name route!", Toast.LENGTH_SHORT).show();

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        allChildren = dbChild.getAllChild();
        childAdapter = new ChildAdapter(allChildren, listChildren, this, this);
        rvChildren.setAdapter(childAdapter);
        rvChildren.setLayoutManager(new LinearLayoutManager(this));
        for (int i = 0; i < listChildren.size(); i++) {
            int j = 0;
            boolean b = true;
            while (j < allChildren.size() && !b) {
                if (listChildren.get(i).getId() == allChildren.get(j).getId()) {
                    b = true;
                } else {
                    b = false;
                    j++;
                }
            }
            if (j > allChildren.size()) {
                listChildren.remove(i);
            }
        }
    }

    public void backActivity(View view) {
        onBackPressed();
    }


    @Override
    public void onPositionClickedItem(View v, int position) {

    }

    @Override
    public void onLongClickedItem(View v, int position) {

    }

    @Override
    public void onSwitchItem(int position, boolean isChecked) {
        if (!isChecked) {
            for (int i = 0; i < listChildren.size(); i++) {
                if (allChildren.get(position).getId() == listChildren.get(i).getId()) {
                    listChildren.remove(i);
                }
            }
        } else {
            int i = 0;
            boolean isAdded = false;
            while (i < listChildren.size() && !isAdded) {
                if (allChildren.get(position).getId() != listChildren.get(i).getId()) {
                    i++;
                } else {
                    isAdded = true;
                }
            }
            if (!isAdded) {
                listChildren.add(allChildren.get(position));
            }
        }
        Log.d("switch_check", String.valueOf(listChildren.size()));
    }

    @Override
    public void gotoAddChild() {
        startActivity(new Intent(this, ChildActivity.class));
    }

    @Override
    public void gotoUpdateChild(Child child) {
        Intent intent = new Intent(this, ChildActivity.class);
        intent.putExtra("child_id_update", child.getId());
        startActivity(intent);
    }
}
