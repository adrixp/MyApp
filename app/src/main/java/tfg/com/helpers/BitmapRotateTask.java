package tfg.com.helpers;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.view.View;

import java.io.File;
import java.io.FileOutputStream;

import tfg.com.myapp.Photo_Crop;
import tfg.com.myapp.R;

/**
 * Created by adria on 09/11/2016.
 */

public class BitmapRotateTask extends AsyncTask<Integer, Void, Integer> {
    private String path;
    private float angle;
    private String name;
    private int numComp;
    private View bt;
    private Activity activity;
    private Context ctx;
    private String ext;
    private String gridSettWyH [];

    public BitmapRotateTask(String mPath, float mAngle, String mName, int mNumComp, View mBt, Activity mActivity, Context mCtx, String mExt, String [] mGridSettWyH) {
        this.path = mPath;
        this.angle = mAngle;
        this.name = mName;
        this.numComp = mNumComp;
        this.bt = mBt;
        this.activity = mActivity;
        this.ctx = mCtx;
        this.ext = mExt;
        this.gridSettWyH = mGridSettWyH;
    }

    private static Bitmap RotateBitmap(Bitmap source, float angle)
    {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    // Decode image in background.
    @Override
    protected Integer doInBackground(Integer... params) {

        try {

            Bitmap myBitmap = BitmapFactory.decodeFile(path);
            Bitmap rotateBitmap = RotateBitmap(myBitmap,angle);
            String pathReal = path.substring(0, path.indexOf("/" + name));

            if (ext.equals("jpg")){
                ext = ".jpg";
            }else{
                ext = ".png";
            }

            String newName = "ECG_Rotated" + ext;
            File fileRotate = new File(pathReal, newName);

            FileOutputStream fos = new FileOutputStream(fileRotate, false);

            if (ext.equals(".jpg")) {
                rotateBitmap.compress(Bitmap.CompressFormat.JPEG, numComp, fos);
            } else {
                rotateBitmap.compress(Bitmap.CompressFormat.PNG, numComp, fos);
            }

            fos.flush();
            fos.close();

            createGrid(path, name, rotateBitmap.getWidth(), rotateBitmap.getHeight());

            path = pathReal + "/" + newName;
            name = newName;

        }catch (Exception e){
            e.printStackTrace();
            return 0;
        }
        return 1;
    }

    // Once complete, see if ImageView is still around and set bitmap.
    @Override
    protected void onPostExecute(Integer i) {
        bt.setEnabled(true);
        bt.setBackgroundResource(R.drawable.mybutton);

        new AlertDialog.Builder(activity).setTitle(ctx.getString(R.string.rotated))
                .setMessage(ctx.getString(R.string.cropOrnot))
                .setPositiveButton(ctx.getString(R.string.proccesOrnotYes), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                        Intent i = new Intent(activity, Photo_Crop.class);
                        i.putExtra("photoPath", path);
                        i.putExtra("photoName", name);
                        activity.startActivity(i);
                    }
                })
                .setNegativeButton(ctx.getString(R.string.proccesOrnotLater), null)
                .show();
    }

    private void createGrid(String path, String name, int width, int height){

        try{
            //Guardamos la rejilla

            Paint paint = new Paint();
            paint.setColor(Color.RED);

            int numPxH = Integer.parseInt(gridSettWyH[1]);
            int numPxW = Integer.parseInt(gridSettWyH[0]);

            Bitmap bmpBase = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
            Canvas canvasToSave = new Canvas(bmpBase);

            //Lineas Horiz
            //                1ºigual      3ºigual
            //ancho:1440 px  alto: 1080 px
            for (int i = 0; i <= height/numPxH; i++){
                if(i == height/numPxH){
                    canvasToSave.drawLine(0, i*numPxH-1, width, i*numPxH-1, paint);
                }else{
                    canvasToSave.drawLine(0, i*numPxH, width, i*numPxH, paint);
                }
            }
            //Lineas Verticales
            //             2ºigual  4ºigual
            //ancho:1440 px  alto: 1080 px

            for (int i = 0; i <= width/numPxW; i++){
                if(i == width/numPxW){
                    canvasToSave.drawLine(i*numPxW-1, 0, i*numPxW-1, height, paint);
                }else{
                    canvasToSave.drawLine(i*numPxW, 0, i*numPxW, height, paint);
                }
            }
            String pathReal = path.substring(0, path.indexOf("/" + name));
            File photoGrid = new File(pathReal, "Grid_Rotated" + ext);

            FileOutputStream fos3 = new FileOutputStream(photoGrid);
            if(ext.equals(".jpg")){
                bmpBase.compress(Bitmap.CompressFormat.JPEG, numComp, fos3);
            }else{
                bmpBase.compress(Bitmap.CompressFormat.PNG, numComp, fos3);
            }

            fos3.flush();
            fos3.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
