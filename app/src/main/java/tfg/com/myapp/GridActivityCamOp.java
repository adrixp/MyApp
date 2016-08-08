package tfg.com.myapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.hardware.Camera.PictureCallback;
import android.os.Environment;
import android.util.AttributeSet;
import android.util.Log;
import org.opencv.android.JavaCameraView;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class GridActivityCamOp extends JavaCameraView implements PictureCallback {

    private static final String TAG = "TFG:Activity";

    public SaveGridSett sGs = new SaveGridSett();

    public GridActivityCamOp(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setResolution(Size resolution) {
        disconnectCamera();
        mMaxHeight = resolution.height;
        mMaxWidth = resolution.width;
        connectCamera(getWidth(), getHeight());
        mCamera.startPreview();
    }

    public List<Size> getResolutionList() {
        return mCamera.getParameters().getSupportedPreviewSizes();
    }

    public void takePicture() {
        Log.i(TAG, "Taking picture");
        // Postview and jpeg are sent in the same buffers if the queue is not empty when performing a capture.
        // Clear up buffers to avoid mCamera.takePicture to be stuck because of a memory issue
        mCamera.setPreviewCallback(null);

        // PictureCallback is implemented by the current class
        mCamera.takePicture(null, null, this);
    }

    @Override
    public void onPictureTaken(byte[] data, Camera camera) {

        // The camera preview was automatically stopped. Start it again.
        mCamera.startPreview();
        mCamera.setPreviewCallback(this);

        // Write the image in a file (in jpeg format)
        if(sGs.isMustTakePhoto()){

            Log.i(TAG, "Saving a bitmap to file");
            try {

                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy_HH-mm-ss", Locale.GERMAN);
                String nameFolder = sdf.format(new Date());

                File folder = new File(Environment.getExternalStorageDirectory()	+ "/ECG-Analyzer/" + nameFolder);
                if(!folder.isDirectory()){
                    if(folder.mkdir()){
                        String path = Environment.getExternalStorageDirectory().getPath() +
                                "/ECG-Analyzer/" + nameFolder;

                        String name = "ECG.jpg";
                        File photo = new File(path, name);
                        //write JPG file
                        FileOutputStream fos = new FileOutputStream(photo);

                        fos.write(data);
                        fos.close();


                        //write txt file
                        String dataSet = "Datos para ECG.jpg:\n" + "Velocidad papel: " + sGs.getMmSeg() + "mm/s \n"
                                + "Voltaje: " + sGs.getMmVolt() + "mm/mV \n"
                                + "Tamaño: " + sGs.getImgWidth() + "x" + sGs.getImgHeight() + "\n";
                        File settings = new File(path, "settings.txt");

                        FileOutputStream fos2 = new FileOutputStream(settings);

                        fos2.write(dataSet.getBytes());
                        fos2.close();


                        //write Grid (rejilla) in jpg
                        Paint paint = new Paint();
                        paint.setColor(Color.RED);

                        Bitmap bmpBase = Bitmap.createBitmap(sGs.getImgWidth(), sGs.getImgHeight(), Bitmap.Config.ARGB_8888);
                        Canvas canvasToSave = new Canvas(bmpBase);


                        //Verticales si el movil esta vertical, No en Landscape
                        //                     igual      igual
                        canvasToSave.drawLine(0, 0, 1000, 0, paint);
                        canvasToSave.drawLine(0, 42.857f, 1000, 42.857f, paint);
                        canvasToSave.drawLine(0, 85.714f, 1000, 85.714f, paint);
                        canvasToSave.drawLine(0, 128.571f, 1000, 128.571f, paint);
                        canvasToSave.drawLine(0, 171.428f, 1000, 171.428f, paint);
                        canvasToSave.drawLine(0, 214.285f, 1000, 214.285f, paint);
                        canvasToSave.drawLine(0, 257.142f, 1000, 257.142f, paint);
                        canvasToSave.drawLine(0, 300, 1000, 300, paint);
                        canvasToSave.drawLine(0, 342.857f, 1000, 342.857f, paint);
                        canvasToSave.drawLine(0, 385.714f, 1000, 385.714f, paint);
                        canvasToSave.drawLine(0, 428.571f, 1000, 428.571f, paint);
                        canvasToSave.drawLine(0, 471.428f, 1000, 471.428f, paint);
                        canvasToSave.drawLine(0, 514.285f, 1000, 514.285f, paint);
                        canvasToSave.drawLine(0, 557.142f, 1000, 557.142f, paint);
                        canvasToSave.drawLine(0, 599, 1000, 599, paint);


                        //Horizontales si el movil esta vertical, No en Landscape
                        //                   igual  igual
                        canvasToSave.drawLine(0, 0, 0, 2000, paint);
                        canvasToSave.drawLine(80, 0, 80, 2000, paint);
                        canvasToSave.drawLine(160, 0, 160, 2000, paint);
                        canvasToSave.drawLine(240, 0, 240, 2000, paint);
                        canvasToSave.drawLine(320, 0, 320, 2000, paint);
                        canvasToSave.drawLine(400, 0, 400, 2000, paint);
                        canvasToSave.drawLine(480, 0, 480, 2000, paint);
                        canvasToSave.drawLine(560, 0, 560, 2000, paint);
                        canvasToSave.drawLine(640, 0, 640, 2000, paint);
                        canvasToSave.drawLine(720, 0, 720, 2000, paint);
                        canvasToSave.drawLine(799, 0, 799, 2000, paint);


                        String nameGrid = "Grid.jpg";
                        File photoGrid = new File(path, nameGrid);

                        FileOutputStream fos3 = new FileOutputStream(photoGrid);
                        bmpBase.compress(Bitmap.CompressFormat.PNG, 100, fos3);

                        fos3.flush();
                        fos3.close();
                    }
                }


            } catch (java.io.IOException e) {
                Log.e(TAG, "Exception in photoCallback", e);
            }
        }


    }
}
