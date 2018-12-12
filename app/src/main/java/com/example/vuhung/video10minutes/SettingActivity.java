package com.example.vuhung.video10minutes;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Environment;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;


import com.example.vuhung.video10minutes.Model.FileModel;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class SettingActivity extends AppCompatActivity {
    Toolbar actionbar;
    SeekBar seekBarVolume;
    Switch aSwitchVibrate;
    ImageButton imgbtnRecordAudio, imgbtnRecordVideo;
    TextView tvAlarm;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION_RESULT) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getNameAudio();
                Log.d("abcd", "name audio " + nameAudio);
                imgbtnRecordAudio.setImageResource(R.drawable.btn_video_busy);
                isRecordAudio = true;
                startRecordAudio();
                Toast.makeText(this, "Permission successfully granded!", Toast.LENGTH_SHORT).show();
            }
        }
        if (requestCode == REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION_RESULT) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                createAudioFolder();
                createVideoFolder();
                Toast.makeText(this, "Permission successfully granded!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public static  int REQUEST_RECORD_AUDIO_PERMISSION_RESULT = 1;
    public static int REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION_RESULT = 0;
    public static boolean enVibrate;
    AudioManager audioManager = null;
    MediaPlayer mp;
    boolean isPlay = false, isRecordAudio = false;
    String audioPath, videoPath;
    MediaRecorder mRecorder;
    String nameAudio = "User Alarm 0", nameVideo = "User Video Alarm 0";
    ArrayList<FileModel> listFilesAudio = new ArrayList<FileModel>();
    ArrayList<FileModel> listFilesVideo = new ArrayList<FileModel>();
    SharedPreferences sharedPreferences;

    @Override
    protected void onResume() {
        super.onResume();
        String path = sharedPreferences.getString("path_alarm","");
        if (path.equals("")){
            tvAlarm.setText("Default");
        }else {
            File file = new File(path);
            tvAlarm.setText(file.getName().substring(0, file.getName().length() - 4));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        seekBarVolume = findViewById(R.id.seekBar_volume);
        aSwitchVibrate = findViewById(R.id.switch_vibrate);
        imgbtnRecordAudio = findViewById(R.id.imgbtn_record_audio);
        imgbtnRecordVideo = findViewById(R.id.imgbtn_record_video);
        tvAlarm = findViewById(R.id.tv_alarm);
        sharedPreferences = getSharedPreferences("MY_ALARM",Context.MODE_PRIVATE);
        String path = sharedPreferences.getString("path_alarm","");
        Log.d("abcde",path);

        if (sharedPreferences.getBoolean("vibrate",false)){
            aSwitchVibrate.setChecked(true);
        }else aSwitchVibrate.setChecked(false);
        if (!path.equals("")) {
            File file = new File(path);
            tvAlarm.setText(file.getName().substring(0, file.getName().length() - 4));
        }else {
            tvAlarm.setText("Default");
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                createAudioFolder();
                createVideoFolder();
            } else {
                if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    Toast.makeText(this, "App needs to be able to save", Toast.LENGTH_SHORT).show();
                }
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION_RESULT);
            }
        } else {
            createAudioFolder();
            createVideoFolder();
        }


        //change volume
        controlVolume();
        //enable vs disable vibrate;
        aSwitchVibrate.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
               if (isChecked) {
                   enVibrate = true;
                   sharedPreferences = getSharedPreferences("MY_ALARM", Context.MODE_PRIVATE);
                   SharedPreferences.Editor editor = sharedPreferences.edit();
                   editor.putBoolean("vibrate",true);
                   Log.d("abcde", "vibrate check ");
                   editor.apply();
                   Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                   if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                       vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
                   } else {
                       //deprecated in API 26
                       vibrator.vibrate(500);
                   }
               }else {
                    sharedPreferences = getSharedPreferences("MY_ALARM", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    Log.d("abcde", "vibrate not check ");
                    editor.putBoolean("vibrate",false);
                    editor.apply();
                }
            }
        });
        imgbtnRecordAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isRecordAudio) {
                    imgbtnRecordAudio.setImageResource(R.drawable.micro);
                    mRecorder.stop();
                    mRecorder.release();
                    mRecorder = null;;
                    AlertDialogRecording();
                    isRecordAudio = false;
                }else {
                    checkPermissionRecordAudio();
                }
            }
        });
        imgbtnRecordVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getNameVideo();
                Intent intent = new Intent(SettingActivity.this,RecordVideoActivity.class);
                intent.putExtra("namevideo",nameVideo);
                startActivity(intent);
            }
        });
    }
    void getNameAudio(){
        listFilesAudio.removeAll(listFilesAudio);
        String path1 = Environment.getExternalStorageDirectory().toString() + "/Ringtones/MyRecord";
        File directory = new File(path1);
        File[] files = directory.listFiles();
        int j = 0;
        for (int i = 0; i < files.length; i++) {
            String name =  files[i].getName();
            ///storage/emulated/0/Ringtones/MyRecord/User Alarm 0.mp3
            if ((name.length()>11)&&("mp3".equals(name.substring(name.length()-3,name.length())))) {
                if ("User Alarm".equals(name.substring(0, 10))) {

                  if(j<=Integer.parseInt(name.substring(11,name.length()-4))) {
                      j = Integer.parseInt(((name.substring(11, name.length()-4)).trim()))+1;
                      nameAudio = "User Alarm " + j;
                  }
                }
                Log.d("abcd", "name audio1 " + name.substring(0, 10));
                Log.d("abcd", "name audio " + nameAudio);
            }
        }
    }
    void getNameVideo(){
        listFilesVideo.removeAll(listFilesVideo);
        String path1 = Environment.getExternalStorageDirectory().toString() +  "/Ringtones/MyRecord";
        File directory = new File(path1);
        File[] files = directory.listFiles();
        int j = 0;
        for (int i = 0; i < files.length; i++) {
            String name =  files[i].getName().substring(0, files[i].getName().length() - 4);

            if (name.length()>17) {
                if ("User Video Alarm".equals(name.substring(0, 16))) {
                    if(j<=Integer.parseInt(name.substring(17, name.length()).trim())) {
                        j = Integer.parseInt(((name.substring(17, name.length())).trim()))+1;
                        nameVideo = "User Video Alarm " + j;
                    }
                }
                Log.d("abcde", "name video " + nameVideo);
            }
        }
    }
    void controlVolume(){
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        seekBarVolume.setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
        seekBarVolume.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC));

        seekBarVolume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
                        progress, 0);
//                SharedPreferences sharedPreferences = getSharedPreferences("MY_ALARM",Context.MODE_PRIVATE);
//                if(!sharedPreferences.getString("path_alarm","").equals("")){
//                   String path = sharedPreferences.getString("path_alarm","");
//                    if (isPlay) mp.stop();
//                    mp = new MediaPlayer();
//                    try {
//                        mp.setDataSource(path);
//                        mp.prepare();
//                        mp.start();
//                        isPlay = true;
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                if (isPlay) mp.stop();
                mp = MediaPlayer.create(SettingActivity.this,R.raw.sound);
                mp.start();
                isPlay = true;
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (isPlay) mp.stop();
            }
        });
    }

    void startRecordAudio(){
        final File file = getOutputAudioFile(nameAudio);
        audioPath = file.getAbsolutePath();
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mRecorder.setOutputFile(file.getAbsolutePath());
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        try {
            mRecorder.prepare();
        } catch (IOException e) {
        }
        mRecorder.start();
    }
    public void AlertDialogRecording() {
        Log.d("abcd", "show");
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Save audio");
        builder.setCancelable(false);

        builder.setPositiveButton("Discard", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                new File(audioPath).delete();
            }
        });
        builder.setNegativeButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("path_alarm", audioPath);
                editor.putString("alarmName", nameAudio);
                editor.apply();
                tvAlarm.setText(nameAudio);
                dialogInterface.dismiss();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private static File getOutputAudioFile(String name) {
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_RINGTONES), "MyRecord");
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("MyRecord", "failed to create directory");
                return null;
            }
        }
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                name + ".mp3");
        return mediaFile;
    }
    private void createVideoFolder() {
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_RINGTONES), "MyRecord");
        if (!mediaStorageDir.exists()) {
            mediaStorageDir.mkdirs();
        }
    }
    private void createAudioFolder() {
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_RINGTONES), "MyRecord");
        if (!mediaStorageDir.exists()) {
            mediaStorageDir.mkdirs();
        }
    }
    public void backActivity(View view) {
        onBackPressed();
    }
    public void gotoAlarmSelection(View view) {
        startActivity(new Intent(this,AlarmSelectionActivity.class));
    }
    void checkPermissionRecordAudio(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                    == PackageManager.PERMISSION_GRANTED) {
                getNameAudio();
                Log.d("abcd", "name audio " + nameAudio);
                imgbtnRecordAudio.setImageResource(R.drawable.btn_video_busy);
                isRecordAudio = true;
                startRecordAudio();
            } else {
                if (shouldShowRequestPermissionRationale(Manifest.permission.RECORD_AUDIO)) {
                    Toast.makeText(this, "App needs to be able", Toast.LENGTH_SHORT).show();
                }
                requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO},
                        REQUEST_RECORD_AUDIO_PERMISSION_RESULT);
            }
        } else {
            getNameAudio();
            Log.d("abcd", "name audio " + nameAudio);
            imgbtnRecordAudio.setImageResource(R.drawable.btn_video_busy);
            isRecordAudio = true;
            startRecordAudio();
        }
    }

    public void gotoChildManager(View view) {
        startActivity(new Intent(this,PassengerManagerActivity.class));
    }
}
