    package com.example.vuhung.video10minutes;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.vuhung.video10minutes.Adapter.AlarmAdapter;
import com.example.vuhung.video10minutes.Adapter.IClickListenerAlarmAdapter;
import com.example.vuhung.video10minutes.Model.FileModel;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import static com.example.vuhung.video10minutes.TimeRemain.isAlarmVideo;

public class AlarmSelectionActivity extends AppCompatActivity implements IClickListenerAlarmAdapter {

    ArrayList<FileModel> listFiles = new ArrayList<FileModel>();
    RecyclerView rvAlarm;
    AlarmAdapter alarmAdapter;
    SharedPreferences sharedPref ;
    MediaPlayer mp;
    boolean isPlay = false; //dsdadas

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_selection);
        rvAlarm = (RecyclerView) findViewById(R.id.recycler_view_file);
        loadFiles();
        alarmAdapter = new AlarmAdapter(listFiles, this,this);
        // Attach the adapter to the recyclerview to populate items
        rvAlarm.setAdapter(alarmAdapter);
        // Set layout manager to position the items
        rvAlarm.setLayoutManager(new LinearLayoutManager(this));
        // That's all!

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isPlay) mp.stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isPlay) mp.stop();
    }
    @Override
    public void onPositionClickedItem(View v, int position) {
        if (isPlay) mp.stop();
        mp = new MediaPlayer();
        try {
            mp.setDataSource(listFiles.get(position).getPath());
            mp.prepare();
            mp.start();
            isPlay = true;
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onLongClickedItem(View v, final int position) {
        SharedPreferences sharedPreferences = getSharedPreferences("MY_ALARM", Context.MODE_PRIVATE);
       if( !sharedPreferences.getString("path_alarm", "").equals(listFiles.get(position).getPath())) {
           AlertDialog.Builder builder = new AlertDialog.Builder(this);
           builder.setMessage("Do you want to delete this alarm?");
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
                   new File(listFiles.get(position).getPath()).delete();
                   listFiles.remove(position);
                   alarmAdapter.notifyDataSetChanged();
                   dialogInterface.dismiss();
               }
           });
           AlertDialog alertDialog = builder.create();
           alertDialog.show();
       }else {
           Toast.makeText(AlarmSelectionActivity.this, "This alarm is using!", Toast.LENGTH_SHORT).show();
       }


    }

    @Override
    public void onCheckedRadioButton(int position) {
        if (listFiles.get(position).getPath().substring(listFiles.get(position).getPath().length()-3,listFiles.get(position).getPath().length()).equals("mp4"))
        {
            sharedPref = getSharedPreferences("MY_ALARM", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("path_alarm", listFiles.get(position).getPath());
            editor.apply();
            isAlarmVideo = true;
        }else {
            if (listFiles.get(position).getPath().substring(listFiles.get(position).getPath().length()-3,listFiles.get(position).getPath().length()).equals("mp3")){
                sharedPref = getSharedPreferences("MY_ALARM", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString("path_alarm", listFiles.get(position).getPath());
                editor.apply();
                isAlarmVideo = false;
            }
        }

        Log.d("path_alarm", listFiles.get(position).getPath().substring(listFiles.get(position).getPath().length()-3,listFiles.get(position).getPath().length()));
    }
    void loadFiles(){
        listFiles.removeAll(listFiles);
        String path1 = Environment.getExternalStorageDirectory().toString() + "/Ringtones/MyRecord";
        File directory = new File(path1);
        File[] files = directory.listFiles();
        Log.d("abcd","size " +files.length);
        for (int i = 0; i < files.length; i++) {
            FileModel file = new FileModel(path1 + "/" + files[i].getName(), files[i].getName().substring(0, files[i].getName().length() - 4));
            listFiles.add(file);
        }
    }

    public void backActivity(View view) {
        onBackPressed();
    }

    public void setAlarmDefault(View view) {
        sharedPref = getSharedPreferences("MY_ALARM", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("path_alarm", "");
        editor.apply();
        isAlarmVideo = false;
        alarmAdapter = new AlarmAdapter(listFiles, this,this);
        // Attach the adapter to the recyclerview to populate items
        rvAlarm.setAdapter(alarmAdapter);
        // Set layout manager to position the items
        rvAlarm.setLayoutManager(new LinearLayoutManager(this));
    }
}
