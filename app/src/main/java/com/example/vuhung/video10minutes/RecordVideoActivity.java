package com.example.vuhung.video10minutes;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.SurfaceTexture;
import android.graphics.drawable.ColorDrawable;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.MediaRecorder;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class RecordVideoActivity extends AppCompatActivity {

    private static final int REQUEST_CAMERA_PPERMISSION_RESULT = 0;
    private static final int REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION_RESULT = 1;

    private boolean isFrontCamera = false;

    private AutoFitTextureView mTextureView;
    private TextureView.SurfaceTextureListener mSurfaceTextureListener = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
//            Toast.makeText(getApplicationContext(),"TextureView is available",Toast.LENGTH_SHORT).show();
            setupCamera(width, height, isFrontCamera);
            Log.d("abcd", "mTextureView size 1  " + width + "   " + height);
            connectCamera();
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            return false;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {

        }
    };

    private CameraDevice mCameraDevice;
    private CameraDevice.StateCallback mCameraDeviceStateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(CameraDevice camera) {
            mCameraDevice = camera;
            if (isRecording) {
                try {
                    createVideoFileName(name);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                startRecord();
                mMediaRecorder.start();
            } else {
                startPreview();
            }
            //Toast.makeText(getApplicationContext(), "Camera connection made!", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onDisconnected(CameraDevice camera) {
            camera.close();
            mCameraDevice = null;
        }

        @Override
        public void onError(CameraDevice camera, int error) {
            camera.close();
            mCameraDevice = null;
        }
    };

    private HandlerThread mBackgoundHandlerThread;
    private Handler mBackgoundHandler;
    private String mCameraId;
    private Size mPreviewSize;
    private Size mVideSize;

    private MediaRecorder mMediaRecorder;
    private int mTotalRotation;
    private CaptureRequest.Builder mCaptureRequestBuilder;

    private ImageButton imgbtnRecord, imgbtnChangeCamera;
    private boolean isRecording = false;

    private File mVideoFolder;
    private String mVideoFileName;

    private static SparseIntArray ORIENTATIONS = new SparseIntArray();

    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 0);
        ORIENTATIONS.append(Surface.ROTATION_90, 90);
        ORIENTATIONS.append(Surface.ROTATION_180, 180);
        ORIENTATIONS.append(Surface.ROTATION_270, 270);
    }

    public void subTime(View view) {
        if (lTime > 0) {
            lTime = lTime - 1000;
            if (isRecording) {
                timer.cancel();
                timer = new CounterClass(lTime, 1000);
                timer.start();
            } else {
                timer = new CounterClass(lTime, 1000);
                tvTime.setText(textTime(lTime));
            }
        }
    }

    public void addTime(View view) {
        lTime = lTime + 1000;
        if (isRecording) {
            timer.cancel();
            timer = new CounterClass(lTime, 1000);
            timer.start();
        } else {
            timer = new CounterClass(lTime, 1000);
            tvTime.setText(textTime(lTime));
        }
    }

    public void backActivity(View view) {
        onBackPressed();
    }


    private static class CompareSizeByArea implements Comparator<Size> {
        @Override
        public int compare(Size lhs, Size rhs) {

            return Long.signum(((long) lhs.getWidth() * lhs.getHeight()) / ((long) rhs.getWidth() * rhs.getHeight()));
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
            lTime = millis;
            long s = millis / 1000;
            String hms = String.format("%02d", s);
            tvTime.setText(hms);
        }

        @Override
        public void onFinish() {
            stopRecord();
        }
    }

    TextView tvTime;
    CountDownTimer timer;
    FrameLayout layoutPreview;
    CircleImageView videoThumbnails;
    boolean isPlayVideo = false;
    final long TIME = 10000;
    long lTime = TIME;
    private int currentApiVersion;
    String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        setContentView(R.layout.activity_record_video);

        mMediaRecorder = new MediaRecorder();
        layoutPreview = findViewById(R.id.layout_preview);
        mTextureView = new AutoFitTextureView(this);
        videoThumbnails = findViewById(R.id.video_thumbnails);
        imgbtnRecord = findViewById(R.id.imgbtn_record);
        imgbtnChangeCamera = findViewById(R.id.imgbtn_change_camera);

        Intent intent = this.getIntent();
        name= intent.getStringExtra("namevideo");

        tvTime = findViewById(R.id.tv_time);
        timer = new CounterClass(lTime, 1000);
        resetLayoutPreview();
        imgbtnRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isRecording) {
                    stopRecord();
                } else {
                    imgbtnChangeCamera.setVisibility(View.INVISIBLE);
                    videoThumbnails.setVisibility(View.INVISIBLE);
                    if (!isPlayVideo) {
                        timer.start();
                        checkWriteStoragePermission();
                        Log.d("videofilename", mVideoFileName);
                    } else {
                        resetLayoutPreview();
                        isPlayVideo = false;
                        videoThumbnails.setVisibility(View.INVISIBLE);
                    }
                }
            }
        });
        videoThumbnails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isPlayVideo = true;
                VideoView videoView = new VideoView(RecordVideoActivity.this);
//                MediaController mediaController = null;
//                if (mediaController == null) {
//                    mediaController = new MediaController(RecordVideoActivity.this);
//                    // Neo vị trí của MediaController với VideoView.
//                    mediaController.setAnchorView(videoView);
//                    // Sét đặt bộ điều khiển cho VideoView.
//                    videoView.setMediaController(mediaController);
//                }
                videoView.setVideoPath(mVideoFileName);
                videoView.start();
                layoutPreview.removeAllViews();
                layoutPreview.addView(videoView);
            }
        });
        imgbtnChangeCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isFrontCamera = !isFrontCamera;
                resetLayoutPreview();
            }
        });

    }

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
    protected void onResume() {
        super.onResume();
        startBackgoundThread();
        if (mTextureView.isAvailable()) {
            setupCamera(mTextureView.getWidth(), mTextureView.getHeight(), isFrontCamera);
            Log.d("abcd", "textureview size  " + mTextureView.getWidth() + "   " + mTextureView.getHeight());
            connectCamera();
        } else {
            mTextureView.setSurfaceTextureListener(mSurfaceTextureListener);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA_PPERMISSION_RESULT) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getApplicationContext(), "Application will not run without camera services", Toast.LENGTH_SHORT).show();
            }
            if (grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getApplicationContext(), "Application will not havve audio on record", Toast.LENGTH_SHORT).show();
            }
        }
        if (requestCode == REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION_RESULT) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                isRecording = true;
                imgbtnRecord.setImageResource(R.drawable.btn_video_busy);
                try {
                    createVideoFileName(name);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Toast.makeText(this, "Permission successfully granded!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "App needs to save video to run", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onPause() {
        if (isRecording) {
            resetLayoutPreview();
            new File(mVideoFileName).delete();
        }
        closeCamera();
        stopBackgoundThread();
        super.onPause();

    }
    String textTime(long time) {
        return
                String.format("%02d", lTime / 1000);
    }

    void resetLayoutPreview() {
        closeCamera();
        videoThumbnails.setVisibility(View.INVISIBLE);
        tvTime.setText(textTime(lTime));
        if (mTextureView.getParent() != null) {
            ((ViewGroup) mTextureView.getParent()).removeView(mTextureView); // <- fix
        }
        layoutPreview.addView(mTextureView);
    }

    private void DialogSaveVideo() {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_save_video);
        dialog.setCanceledOnTouchOutside(false);
        Button btnSaveVideo = dialog.findViewById(R.id.btn_save_video);
        Button btnCancelVideo = dialog.findViewById(R.id.btn_cancel_video);
        final TextView textView = dialog.findViewById(R.id.tv_save_video);
        final View view = dialog.findViewById(R.id.view);
        LinearLayout dialogLayout = findViewById(R.id.dialog_layout);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#50FFFFFF")));
        btnSaveVideo.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#50FFFFFF")));
        btnCancelVideo.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#50FFFFFF")));

        btnSaveVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(RecordVideoActivity.this, "saved", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                // update media store
                Intent mediaStoreUpdateIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                mediaStoreUpdateIntent.setData(Uri.fromFile(new File(mVideoFileName)));
                sendBroadcast(mediaStoreUpdateIntent);
                Log.d("abcd","pathVideo "+ mVideoFileName);
                // /storage/emulated/0/Movies/Video10Minutes/VID_20181121_164151.mp4
                videoThumbnails.setVisibility(View.VISIBLE);
                Bitmap bMap = ThumbnailUtils.createVideoThumbnail(mVideoFileName, MediaStore.Video.Thumbnails.MICRO_KIND);
                videoThumbnails.setImageBitmap(bMap);
                SharedPreferences sharedPreferences = getSharedPreferences("MY_ALARM",Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("path_alarm", mVideoFileName);
                editor.apply();
            }
        });
        btnCancelVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(RecordVideoActivity.this, "canceled", Toast.LENGTH_SHORT).show();
                new File(mVideoFileName).delete();
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    void stopRecord() {
        mMediaRecorder.stop();
        mMediaRecorder.reset();
        timer.cancel();
        imgbtnChangeCamera.setVisibility(View.VISIBLE);
        isRecording = false;
        imgbtnRecord.setImageResource(R.drawable.btn_video_online);
        lTime = TIME;
        tvTime.setText(textTime(lTime));
        resetLayoutPreview();
        DialogSaveVideo();
    }

    private void setupCamera(int width, int height, boolean isFrontCamera) {
        CameraManager cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            for (String cameraId : cameraManager.getCameraIdList()) {
                CameraCharacteristics cameraCharacteristics = cameraManager.getCameraCharacteristics(cameraId);
                if (isFrontCamera) {
                    if (cameraCharacteristics.get(CameraCharacteristics.LENS_FACING) !=
                            CameraCharacteristics.LENS_FACING_FRONT) {
                        continue;
                    }
                } else {
                    Log.d("aaaaa", String.valueOf(isFrontCamera));
                    if (cameraCharacteristics.get(CameraCharacteristics.LENS_FACING) !=
                            CameraCharacteristics.LENS_FACING_BACK) {
                        continue;
                    }
                }
                StreamConfigurationMap map = cameraCharacteristics.get(cameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
                //Log.d("aaaa",map.getOutputSizes(MediaRecorder.class)[0].getWidth()+" "+map.getOutputSizes(MediaRecorder.class)[0].getHeight());
                //set up camera khi xoay man hinh
                int deviceOrientation = getWindowManager().getDefaultDisplay().getRotation();
                mTotalRotation = sensorToDeviceRotation(cameraCharacteristics, deviceOrientation);
                boolean swapRotation = mTotalRotation == 90 || mTotalRotation == 270;
                int rotateWidth = width;
                int rotateHeight = height;
                if (swapRotation) {
                    rotateWidth = height;
                    rotateHeight = width;
                }


                mPreviewSize = chooseOptimalSize(map.getOutputSizes(SurfaceTexture.class), rotateWidth, rotateHeight);
                // mPreviewSize = new Size(1920,1080);
                int orientation = getResources().getConfiguration().orientation;
                if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    mTextureView.setAspectRatio(
                            mPreviewSize.getWidth(), mPreviewSize.getHeight());
                } else {
                    mTextureView.setAspectRatio(
                            mPreviewSize.getHeight(), mPreviewSize.getWidth());
                }

                mVideSize = chooseOptimalSize(map.getOutputSizes(MediaRecorder.class), mPreviewSize.getWidth(), mPreviewSize.getHeight());
                //mVideSize = mPreviewSize;
                // mImageSize= mPreviewSize;
                mCameraId = cameraId;

                Log.d("abcd", "previewsize" + mPreviewSize.getWidth() + " " + mPreviewSize.getHeight());
                Log.d("abcd", "videosize" + mVideSize.getWidth() + " " + mVideSize.getHeight());
                return;
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void connectCamera() {
        CameraManager cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    cameraManager.openCamera(mCameraId, mCameraDeviceStateCallback, mBackgoundHandler);
                } else {
                    if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                        Toast.makeText(this, "Video app required access to camera", Toast.LENGTH_SHORT).show();
                    }
                    requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO}, REQUEST_CAMERA_PPERMISSION_RESULT);
                }
            } else {
                cameraManager.openCamera(mCameraId, mCameraDeviceStateCallback, mBackgoundHandler);
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void startRecord() {
        try {
            setupMediaRecorder();
            SurfaceTexture surfaceTexture = mTextureView.getSurfaceTexture();
            surfaceTexture.setDefaultBufferSize(mPreviewSize.getWidth(), mPreviewSize.getHeight());
            Surface previewSurface = new Surface(surfaceTexture);
            Surface recordSurface = mMediaRecorder.getSurface();
            mCaptureRequestBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_RECORD);
            mCaptureRequestBuilder.addTarget(previewSurface);
            mCaptureRequestBuilder.addTarget(recordSurface);
            mCameraDevice.createCaptureSession(Arrays.asList(previewSurface, recordSurface),
                    new CameraCaptureSession.StateCallback() {
                        @Override
                        public void onConfigured(CameraCaptureSession session) {
                            try {
                                session.setRepeatingRequest(
                                        mCaptureRequestBuilder.build(), null, null);
                            } catch (CameraAccessException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onConfigureFailed(CameraCaptureSession session) {

                        }
                    }, null);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (CameraAccessException e) {
        }
    }

    private void startPreview() {
        SurfaceTexture surfaceTexture = mTextureView.getSurfaceTexture();
        surfaceTexture.setDefaultBufferSize(mPreviewSize.getWidth(), mPreviewSize.getHeight());
        Surface previewSurface = new Surface(surfaceTexture);
        try {
            mCaptureRequestBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            mCaptureRequestBuilder.addTarget(previewSurface);

            mCameraDevice.createCaptureSession(Arrays.asList(previewSurface),
                    new CameraCaptureSession.StateCallback() {

                        @Override
                        public void onConfigured(CameraCaptureSession session) {
                            session = session;
                            try {
                                session.setRepeatingRequest(mCaptureRequestBuilder.build(),
                                        null, mBackgoundHandler);

                            } catch (CameraAccessException e) {
                                e.printStackTrace();
                            }

                        }

                        @Override
                        public void onConfigureFailed(CameraCaptureSession session) {
                            Toast.makeText(getApplicationContext(), "Unable to setup camera preview", Toast.LENGTH_SHORT).show();

                        }
                    }, null);

        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void closeCamera() {
        if (mCameraDevice != null) {
            mCameraDevice.close();
            mCameraDevice = null;
        }
    }

    private void startBackgoundThread() {
        mBackgoundHandlerThread = new HandlerThread("Camera2VideoImage");
        mBackgoundHandlerThread.start();
        mBackgoundHandler = new Handler(mBackgoundHandlerThread.getLooper());
    }

    private void stopBackgoundThread() {
        mBackgoundHandlerThread.quitSafely();
        try {
            mBackgoundHandlerThread.join();
            mBackgoundHandlerThread = null;
            mBackgoundHandler = null;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static int sensorToDeviceRotation(CameraCharacteristics cameraCharacteristics, int deviceOrientation) {
        int sensorOrientation = cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION);
        deviceOrientation = ORIENTATIONS.get(deviceOrientation);
        return (sensorOrientation + deviceOrientation + 360) % 360;
    }

    private static Size chooseOptimalSize(Size[] choices, int width, int height) {
        List<Size> bigEnough = new ArrayList<Size>();
        List<Size> bigEnough1 = new ArrayList<Size>();
        for (Size option : choices) {
            Log.d("listsize1", option.getWidth() + "  " + option.getHeight());
            if (((option.getWidth() * 100 / width) == option.getHeight() * 100 / height) && (option.getHeight() <= height) && (option.getWidth() <= width)) {
                bigEnough.add(option);
                Log.d("listsize2", option.getWidth() + "  " + option.getHeight() + "  " + option.getWidth() / width);
            } else if (option.getWidth() <= width && option.getHeight() <= height) {
                bigEnough1.add(option);
            }
        }
        if (bigEnough.size() > 0) {
            return Collections.min(bigEnough, new CompareSizeByArea());
        } else if (bigEnough1.size() > 0) {
            return Collections.min(bigEnough1, new CompareSizeByArea());
        } else {
            return choices[0];
        }
    }

    private File createVideoFileName(String name) throws IOException {
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String path1 = Environment.getExternalStorageDirectory().toString() + "/Ringtones/MyRecord";
        File videoFile = new File(path1+"/"+
                name + ".mp4");
        mVideoFileName = videoFile.getAbsolutePath();
        return videoFile;
    }

    private void checkWriteStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                isRecording = true;
                imgbtnRecord.setImageResource(R.drawable.btn_video_busy);
                try {
                    createVideoFileName(name);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                startRecord();
                mMediaRecorder.start();
            } else {
                if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    Toast.makeText(this, "App needs to be able to save video", Toast.LENGTH_SHORT).show();
                }
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION_RESULT);
            }
        } else {
            isRecording = true;
            imgbtnRecord.setImageResource(R.drawable.btn_video_busy);
            try {
                createVideoFileName(name);
            } catch (IOException e) {
                e.printStackTrace();
            }
            startRecord();
            mMediaRecorder.start();
        }
    }

    private void setupMediaRecorder() throws IOException {
        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mMediaRecorder.setOutputFile(mVideoFileName);
        mMediaRecorder.setVideoEncodingBitRate(20000000);//quality
        mMediaRecorder.setVideoFrameRate(30);
        mMediaRecorder.setVideoSize(mVideSize.getWidth(), mVideSize.getHeight());
        mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
        mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        mMediaRecorder.setOrientationHint(mTotalRotation);
        mMediaRecorder.prepare();
    }
}
