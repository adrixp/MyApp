package tfg.com.helpers;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.Button;

import java.io.File;
import java.io.FileOutputStream;

import tfg.com.myapp.Photo_Crop;
import tfg.com.myapp.Photo_Preview;
import tfg.com.myapp.R;

/**
 * Created by adria on 09/11/2016.
 */

public class BitmapCropTask extends AsyncTask<Integer, Void, Integer> {
    String pathImgToCrop;
    String newPath;
    String name;
    int xIni;
    int yIni;
    int xFin;
    int yFin;
    Button bt;
    private Context context;
    boolean release;
    String prevname;
    Activity activity;
    int numComp;
    int isJpeg;

    public BitmapCropTask(String mPathImgToCrop, String mNewPath, String mName, int mxIni, int myIni, int mxFin, int myFin, Button mBt, boolean mRelease, Context mContext, String mPrevname, Activity mActivity, int mNumComp, int mIsJpeg) {
        pathImgToCrop = mPathImgToCrop;
        newPath = mNewPath;
        name = mName;
        xIni = mxIni;
        yIni = myIni;
        xFin = mxFin;
        yFin = myFin;
        bt = mBt;
        release = mRelease;
        context = mContext;
        prevname = mPrevname;
        activity = mActivity;
        numComp = mNumComp;
        isJpeg = mIsJpeg;
    }

    // Decode image in background.
    @Override
    protected Integer doInBackground(Integer... params) {

        try {

            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inJustDecodeBounds = false;
            opts.inPreferredConfig = Bitmap.Config.RGB_565;
            opts.inDither = true;

            Bitmap myBitmap = BitmapFactory.decodeFile(pathImgToCrop,opts);
            Bitmap croppedBitmap= Bitmap.createBitmap(myBitmap, xIni, yIni, xFin -xIni, yFin - yIni);

            File fileCropped = new File(newPath, name);

            FileOutputStream fos = new FileOutputStream(fileCropped);

            if(isJpeg == 1){
                croppedBitmap.compress(Bitmap.CompressFormat.JPEG, numComp, fos);
            }else{
                croppedBitmap.compress(Bitmap.CompressFormat.PNG, numComp, fos);
            }


            fos.flush();
            fos.close();

            if (myBitmap != null)
            {
                myBitmap.recycle();
                myBitmap = null;
                System.gc();
            }
        }catch (Exception e){
            e.printStackTrace();
            return 0;
        }
        return 1;
    }

    // Once complete, see if ImageView is still around and set bitmap.
    @Override
    protected void onPostExecute(Integer i) {
        if(release){
            bt.setEnabled(true);
            bt.setBackgroundResource(R.drawable.mybutton);

            try {
                new AlertDialog.Builder(activity).setTitle(context.getResources().getString(R.string.proccesOrnotTitleDer))
                        .setMessage(context.getResources().getString(R.string.proccesOrnotDer))
                        .setPositiveButton(context.getResources().getString(R.string.proccesOrnotYes), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                Intent i = new Intent(activity, Photo_Preview.class);
                                i.putExtra("photoPath", newPath + "/" + prevname);
                                i.putExtra("photoName", prevname);
                                activity.startActivity(i);
                            }
                        })
                        .setNegativeButton(context.getResources().getString(R.string.proccesOrnotLater), null)
                        .show();

            }catch (Exception e){

                e.printStackTrace();
            }
        }
    }
}
