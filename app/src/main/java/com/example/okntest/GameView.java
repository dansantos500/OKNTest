package com.example.okntest;

import java.text.SimpleDateFormat;
import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


import java.io.File;
import java.io.IOException;
import java.util.List;
import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.ActivityCompat;
import android.view.SurfaceView;
import android.view.SurfaceHolder;
import android.widget.Button;

import java.io.File;

public class GameView extends Activity implements SurfaceHolder.Callback, ActivityCompat.OnRequestPermissionsResultCallback {

    //////////////////////////////////////////////////////////////////////////////////////////////////
    private static final int MEDIA_RECORDER_REQUEST = 0;
    public static final int MEDIA_TYPE_VIDEO = 2;
    private boolean first=true;
    static Camera mCamera;
    // private TextureView mPreview;
    private MediaRecorder mMediaRecorder;
    private File mOutputFile;

    private boolean isRecording = false;
    private static final String TAG = "Recorder";
    private Button captureButton;

    private final String[] requiredPermissions = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.CAMERA,
    };
    /////////////////////////////////////////////////////////////////////////////////////////////////
    SurfaceView  cameraView,transparentView;
    int  deviceHeight,deviceWidth;
    SurfaceHolder holder,holderTransparent;
    private MainThread thread;
    private CharacterSprite characterSprite;
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        cameraView=(SurfaceView)findViewById(R.id.surfaceView);
        holder=cameraView.getHolder();
        holder.addCallback((SurfaceHolder.Callback)this);
        //
        transparentView=(SurfaceView)findViewById(R.id.surfaceView2);
        holderTransparent=transparentView.getHolder();
        holderTransparent.addCallback((SurfaceHolder.Callback)this);
        holderTransparent.setFormat(PixelFormat.TRANSLUCENT);
        transparentView.setZOrderMediaOverlay(true);
        //getting the device heigth and width

        deviceWidth=getScreenWidth();

        deviceHeight=getScreenHeight();
        releaseCamera();
        mCamera = null;
    }

    public static int getScreenWidth() {

        return Resources.getSystem().getDisplayMetrics().widthPixels;

    }


    public static int getScreenHeight() {

        return Resources.getSystem().getDisplayMetrics().heightPixels;

    }

    @Override
    public void surfaceChanged (SurfaceHolder holder, int format, int width, int height){

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        thread = new MainThread(transparentView.getHolder(), this);
        // load a bitmap from the drawable folder
        Bitmap b = BitmapFactory.decodeResource(getResources(), R.drawable.test1);
        // resize the bitmap to 150x100 (width x height)
        Bitmap scaled = Bitmap.createScaledBitmap(b, 500, 50, true);
        characterSprite  = new CharacterSprite(scaled);
        thread.setRunning(true);
        thread.start();
        if(!first) {
            CountDownTimer mCountdowntimer=new CountDownTimer(5000, 5000) {//countdown Period =5000

                public void onTick(long millisUntilFinished) {
                    //textView.setText("seconds remaining: " + millisUntilFinished / 1000);
                }

                public void onFinish() {
                    //mMediaRecorder.stop();
                    onCaptureClick();
                }

            }.start();
        }else{
            first=false;
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder){
        //onCaptureClick(); //for release a camera

        boolean retry = true;
        while (retry) {
            try {
                thread.setRunning(false);
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            retry = false;
        }
    }
    public void update() {
        characterSprite.update();

    }


    public void draw(Canvas canvas) {
        //if (canvas != null) {

            canvas.drawColor(Color.WHITE);
            //Paint paint = new Paint();
            //paint.setColor(Color.rgb(250, 0, 0));

            //canvas.drawRect(characterSprite.screenWidth/2, characterSprite.screenHeight/2, (characterSprite.screenWidth/2)+100, (characterSprite.screenHeight/2)+100, paint);
            characterSprite.draw(canvas);

        //}
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////
    public void onCaptureClick() {

        if (areCameraPermissionGranted()){
            startCapture();
            /////
            CountDownTimer mCountdowntimer=new CountDownTimer(10000, 1000) {//countdown Period =5000

                public void onTick(long millisUntilFinished) {
                    //textView.setText("seconds remaining: " + millisUntilFinished / 1000);
                }

                public void onFinish() {
                    //mMediaRecorder.stop();
                    startCapture();
                }

            }.start();

            /////
        } else {
            requestCameraPermissions();
        }
    }

    private void startCapture(){

        if (isRecording) {
            // BEGIN_INCLUDE(stop_release_media_recorder)

            // stop recording and release camera
            try {
                ////timing
                mMediaRecorder.stop();  // stop the recording
                ///
            } catch (RuntimeException e) {
                // RuntimeException is thrown when stop() is called immediately after start().
                // In this case the output file is not properly constructed ans should be deleted.
                Log.d(TAG, "RuntimeException: stop() is called immediately after start()");
                //noinspection ResultOfMethodCallIgnored
                mOutputFile.delete();
            }
            releaseMediaRecorder(); // release the MediaRecorder object
            mCamera.lock();         // take camera access back from MediaRecorder

            // inform the user that recording has stopped
            //setCaptureButtonText("Capture");
            isRecording = false;
            releaseCamera();
            // END_INCLUDE(stop_release_media_recorder)
            /////////////////////////////////////////////////////////////////////////////////////////
            Toast.makeText(getApplicationContext(),
                    mOutputFile.getAbsolutePath().toString(),
                    Toast.LENGTH_SHORT)
                    .show();
           Intent toy = new Intent(GameView.this,EndScreen.class);
           startActivity(toy);
            ///////////////////////////////////////////////////////////////////////////////////////////
        } else {

            // BEGIN_INCLUDE(prepare_start_media_recorder)

            new MediaPrepareTask().execute(null, null, null);

            // END_INCLUDE(prepare_start_media_recorder)

        }
    }

   // private void setCaptureButtonText(String title) {
        //captureButton.setText(title);
    //}

    @Override
    protected void onPause() {
        super.onPause();
        // if we are using MediaRecorder, release it first
        releaseMediaRecorder();
        // release the camera immediately on pause event
        releaseCamera();
    }

    private void releaseMediaRecorder(){
        if (mMediaRecorder != null) {
            // clear recorder configuration
            mMediaRecorder.reset();
            // release the recorder object
            mMediaRecorder.release();
            mMediaRecorder = null;
            // Lock camera for later use i.e taking it back from MediaRecorder.
            // MediaRecorder doesn't need it anymore and we will release it if the activity pauses.
            mCamera.lock();
        }
    }

    private void releaseCamera(){
        if (mCamera != null){
            // release the camera for other applications
            mCamera.release();
            mCamera = null;
        }
    }
    private static File getOutputMediaFile(int type){

        // Check that the SDCard is mounted
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "MyCameraVideo");


        // Create the storage directory(MyCameraVideo) if it does not exist
        if (! mediaStorageDir.exists()){

            if (! mediaStorageDir.mkdirs()){

               // output.setText("Failed to create directory MyCameraVideo.");

               // Toast.makeText(ActivityContext, "Failed to create directory MyCameraVideo.",
                      //  Toast.LENGTH_LONG).show();

                Log.d("MyCameraVideo", "Failed to create directory MyCameraVideo.");
                return null;
            }
        }


        // Create a media file name

        // For unique file name appending current timeStamp with file name
        java.util.Date date= new java.util.Date();
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
                .format(date.getTime());

        File mediaFile;

        if(type == MEDIA_TYPE_VIDEO) {

            // For unique video file name appending current timeStamp with file name
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "VID_"+ timeStamp + ".mp4");

        } else {
            return null;
        }

        return mediaFile;
    }
    private boolean prepareVideoRecorder(){

        // BEGIN_INCLUDE (configure_preview)
        //mCamera = CameraHelper.getDefaultCameraInstance();
        mCamera = Camera.open(1);
        // We need to make sure that our preview and recording video size are supported by the
        // camera. Query camera to find all the sizes and choose the optimal size given the
        // dimensions of our preview surface.
        Camera.Parameters parameters = mCamera.getParameters();
        List<Camera.Size> mSupportedPreviewSizes = parameters.getSupportedPreviewSizes();
        List<Camera.Size> mSupportedVideoSizes = parameters.getSupportedVideoSizes();
        Camera.Size optimalSize = CameraHelper.getOptimalVideoSize(mSupportedVideoSizes,
                mSupportedPreviewSizes, cameraView.getWidth(), cameraView.getHeight());

        // Use the same size for recording profile.
        CamcorderProfile profile = CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH);
        profile.videoFrameWidth = optimalSize.width;
        profile.videoFrameHeight = optimalSize.height;

        // likewise for the camera object itself.
        parameters.setPreviewSize(profile.videoFrameWidth, profile.videoFrameHeight);
        mCamera.setParameters(parameters);

        try {
            // Requires API level 11+, For backward compatibility use {@link setPreviewDisplay}
            // with {@link SurfaceView}
            mCamera.setPreviewDisplay(cameraView.getHolder());
        } catch (IOException e) {
            Log.e(TAG, "Surface texture is unavailable or unsuitable" + e.getMessage());
            return false;
        }

        // END_INCLUDE (configure_preview)


        // BEGIN_INCLUDE (configure_media_recorder)
        mMediaRecorder = new MediaRecorder();

        // Step 1: Unlock and set camera to MediaRecorder
        mCamera.unlock();
        mMediaRecorder.setCamera(mCamera);

        // Step 2: Set sources
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT );
        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

        // Step 3: Set a CamcorderProfile (requires API Level 8 or higher)
        mMediaRecorder.setProfile(profile);

        // Step 4: Set output file
        mOutputFile = getOutputMediaFile(2);
        if (mOutputFile == null) {
            return false;
        }

        //mOutputFile.getPath()
        mMediaRecorder.setOutputFile(mOutputFile.getPath());

        // END_INCLUDE (configure_media_recorder)

        // Step 5: Prepare configured MediaRecorder
        try {
            mMediaRecorder.prepare();
        } catch (IllegalStateException e) {
            Log.d(TAG, "IllegalStateException preparing MediaRecorder: " + e.getMessage());
            releaseMediaRecorder();
            return false;
        } catch (IOException e) {
            Log.d(TAG, "IOException preparing MediaRecorder: " + e.getMessage());
            releaseMediaRecorder();
            return false;
        }
        return true;
    }

    private boolean areCameraPermissionGranted() {

        for (String permission : requiredPermissions){
            if (!(ActivityCompat.checkSelfPermission(this, permission) ==
                    PackageManager.PERMISSION_GRANTED)){
                return false;
            }
        }
        return true;
    }

    private void requestCameraPermissions(){
        ActivityCompat.requestPermissions(
                this,
                requiredPermissions,
                MEDIA_RECORDER_REQUEST);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        if (MEDIA_RECORDER_REQUEST != requestCode) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            return;
        }

        boolean areAllPermissionsGranted = true;
        for (int result : grantResults){
            if (result != PackageManager.PERMISSION_GRANTED){
                areAllPermissionsGranted = false;
                break;
            }
        }

        if (areAllPermissionsGranted){
            startCapture();
        } else {
            // User denied one or more of the permissions, without these we cannot record
            // Show a toast to inform the user.
            Toast.makeText(getApplicationContext(),
                    getString(R.string.need_camera_permissions),
                    Toast.LENGTH_SHORT)
                    .show();
        }
    }

    /**
     * Asynchronous task for preparing the {@link android.media.MediaRecorder} since it's a long blocking
     * operation.
     */
    class MediaPrepareTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... voids) {
            // initialize video camera
            if (prepareVideoRecorder()) {
                // Camera is available and unlocked, MediaRecorder is prepared,
                // now you can start recording

                mMediaRecorder.start();

                isRecording = true;
            } else {
                // prepare didn't work, release the camera
                releaseMediaRecorder();
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (!result) {
                GameView.this.finish();
            }
            // inform the user that recording has started
            //setCaptureButtonText("Stop");
            //Log.d(TAG,  mOutputFile.getAbsolutePath() );
            Toast.makeText(getApplicationContext(),
                    "Started Recording",
                    Toast.LENGTH_SHORT)
                    .show();

        }
    }



    ////////////////////////////////////////////////////////////////////////////////////////////////////
}
