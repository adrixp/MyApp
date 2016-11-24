package tfg.com.myapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.ref.WeakReference;

/**
 * Created by adria on 09/11/2016.
 */

class BitmapWorkerTask extends AsyncTask<Integer, Void, Bitmap> {
    String pathImgToCrop;
    String newPath;
    String name;
    int xIni;
    int yIni;
    int xFin;
    int yFin;
    Button bt;
    Boolean release;

    public BitmapWorkerTask(String mpathImgToCrop, String mnewPath, String mname, int mxIni, int myIni, int mxFin, int myFin, Button mbt, Boolean mrelease) {
        pathImgToCrop = mpathImgToCrop;
        newPath = mnewPath;
        name = mname;
        xIni = mxIni;
        yIni = myIni;
        xFin = mxFin;
        yFin = myFin;
        bt = mbt;
        release = mrelease;
    }

    // Decode image in background.
    @Override
    protected Bitmap doInBackground(Integer... params) {

        try {
            //BitmapFactory.Options options = new BitmapFactory.Options();
            //options.inJustDecodeBounds = true;
            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inJustDecodeBounds = false;
            opts.inPreferredConfig = Bitmap.Config.RGB_565;
            opts.inDither = true;

            Bitmap myBitmap = BitmapFactory.decodeFile(pathImgToCrop,opts);
            Bitmap croppedBitmap= Bitmap.createBitmap(myBitmap, xIni, yIni, xFin -xIni, yFin - yIni);

            File fileCropped = new File(newPath, name);

            FileOutputStream fos = new FileOutputStream(fileCropped);
            croppedBitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);

            fos.flush();
            fos.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    // Once complete, see if ImageView is still around and set bitmap.
    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if(release){
            bt.setEnabled(true);
            bt.setBackgroundResource(R.drawable.mybutton);
        }
    }
}
