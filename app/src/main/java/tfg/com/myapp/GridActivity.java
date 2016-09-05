package tfg.com.myapp;

import android.app.Activity;
import android.content.SharedPreferences;
import android.hardware.Camera.Size;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.Toast;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;

import java.io.File;
import java.util.List;

public class GridActivity extends Activity implements CvCameraViewListener2, OnTouchListener,SensorEventListener {

    public GridActivityCamOp mOpenCvCameraView;
    private static final String TAG = "TFG:Grid:Activity";
    File folder;

    //Variables para pitch and roll
    private SensorManager sManager;
    float Rot[] = null; //for gravity rotational data
    float I[] = null; //for magnetic rotational data
    float accels[] = new float[3];
    float mags[] = new float[3];
    float[] values = new float[3];

    float pitch;
    boolean isRed = true;
    boolean inRange = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "called onCreate");

        sManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.layout_grid);

        mOpenCvCameraView = (GridActivityCamOp) findViewById(R.id.OpenCvView);
        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(this);

        folder = new File(Environment.getExternalStorageDirectory()	+ "/ECG-Analyzer/tmp" );
        if(!folder.isDirectory()){
            if(folder.mkdir()){
                Log.i(TAG, "Folder created successfully");
            }else{
                Log.i(TAG, "Folder couldn't be created");
            }
        }
    }


    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i(TAG, "OpenCV loaded successfully");
                    mOpenCvCameraView.enableView();
                    startCamera();
                    mOpenCvCameraView.takePicture();
                    mOpenCvCameraView.setOnTouchListener(GridActivity.this);

                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };

    @Override
    public void onResume(){
        Log.i(TAG, "called onResume");
        super.onResume();

        Bundle extras = getIntent().getExtras();
        String mmSeg = extras.getString("mmSeg");
        String mmVolt = extras.getString("mmVolt");
        mOpenCvCameraView.sGs.setMmVolt(mmVolt);
        mOpenCvCameraView.sGs.setMmSeg(mmSeg);


        Log.i(TAG, "mmSeg obtenidos: " + mmSeg);
        Log.i(TAG, "mmVolt obtenidos: " + mmVolt);

        if(!OpenCVLoader.initDebug()){
            Log.i(TAG, "opencv initialization failed");
        }else{
            Log.i(TAG, "opencv initialization success");
        }

        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);

        sManager.registerListener(this, sManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
        sManager.registerListener(this, sManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD), SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void startCamera(){

        SharedPreferences pref = PreferenceManager
                .getDefaultSharedPreferences(this);

        int numBestPrev = Integer.parseInt(pref.getString("typeCal", "0"));

        List<Size> mResolutionList = mOpenCvCameraView.getResolutionList();
        Size resolution = mResolutionList.get(numBestPrev);

        String res =  resolution.width + "x" + resolution.height;
        mOpenCvCameraView.sGs.setImgWidth(resolution.width);
        mOpenCvCameraView.sGs.setImgHeight(resolution.height);

        Log.i(TAG, "Resolucion inicializada de : " + res);
        mOpenCvCameraView.setResolution(resolution);
    }

    @Override
    public void onPause(){
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    public void onDestroy() {
        super.onDestroy();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
        deleteFile(folder);
    }


    public void deleteFile(File element) {
        if (element.isDirectory()) {
            for (File sub : element.listFiles()) {
                deleteFile(sub);
            }
        }
        if(element.delete()){
            Log.i(TAG, "Folder deleted successfully");
        }else{
            Log.i(TAG, "Couldn't delete this folder");
        }
    }

    public void onCameraViewStarted(int width, int height) {
    }

    public void onCameraViewStopped() {
    }

    public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
        return inputFrame.rgba();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if(inRange) {
            Log.i(TAG, "onTouch event");
            mOpenCvCameraView.sGs.setMustTakePhoto(true);
            mOpenCvCameraView.takePicture();
            Toast.makeText(this, "Photo Saved", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, getString(R.string.MustHoriz), Toast.LENGTH_LONG).show();
        }
        return false;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        switch (event.sensor.getType()){
            case Sensor.TYPE_MAGNETIC_FIELD:
                mags = event.values.clone();
                break;
            case Sensor.TYPE_ACCELEROMETER:
                accels = event.values.clone();
                break;
        }

        if (mags != null && accels != null) {
            Rot = new float[9];
            I= new float[9];
            SensorManager.getRotationMatrix(Rot, I, accels, mags);
            // Correct if screen is in Landscape

            float[] outR = new float[9];
            SensorManager.remapCoordinateSystem(Rot, SensorManager.AXIS_X, SensorManager.AXIS_Z, outR);
            SensorManager.getOrientation(outR, values);

            pitch = values[1] * 57.2957795f;
            mags = null; //retrigger the loop when things are repopulated
            accels = null; ////retrigger the loop when things are repopulated

            FrameLayout le = (FrameLayout) findViewById(R.id.frameLayoutGrid);
            if (pitch >= 80.0F && pitch < 91.0F && isRed) {

                le.setBackgroundResource(R.drawable.take_foto_border_green);
                isRed = false;
                inRange = true;
            }else if (pitch < 80.0F || pitch >= 91.0F){
                if(!isRed){
                    le.setBackgroundResource(R.drawable.take_foto_border_red);
                    isRed = true;
                    inRange = false;
                }
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
