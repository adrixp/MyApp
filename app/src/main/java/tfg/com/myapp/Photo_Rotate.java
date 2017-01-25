package tfg.com.myapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;

import tfg.com.helpers.SandboxView;

/**
 * Created by adria on 19/01/2017.
 */

public class Photo_Rotate extends Activity{

    private static final String TAG = "TFG:Rotate:Activity";

    private String path;
    private String name;
    SandboxView sandboxView;

    private int numComp = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_photo_rotate);

        Bundle extras = getIntent().getExtras();
        path = extras.getString("photoPath");
        name = extras.getString("photoName");


        Bitmap myBitmap = BitmapFactory.decodeFile(path);
        int w = myBitmap.getWidth();
        int h = myBitmap.getHeight();
        sandboxView = (SandboxView) findViewById(R.id.sandboxView);
        sandboxView.bitmap = myBitmap;
        sandboxView.height = h;
        sandboxView.width = w;

        if(h > 2000 && w > 4000){
            sandboxView.scale = 0.35f;
        }else if(h > 1000 && w > 2000){
            sandboxView.scale = 0.55f;
        }else{
            sandboxView.scale = 0.80f;
        }

        SharedPreferences pref = PreferenceManager
                .getDefaultSharedPreferences(this);

        numComp = Integer.parseInt(pref.getString("typeComp", "0")) * 10 + 10;

    }

    private static float getDegreesFromRadians(float angle) {
        return (float)(angle * 180.0 / Math.PI);
    }

    public void rotate(View view) {
        Toast.makeText(this, getString(R.string.working), Toast.LENGTH_SHORT).show();
        Button bt = (Button) findViewById(R.id.rotateButton);
        bt.setEnabled(false);
        bt.setBackgroundColor(Color.parseColor("#BDBDBD"));

        Bitmap myBitmap = BitmapFactory.decodeFile(path);
        Bitmap rotateBitmap = RotateBitmap(myBitmap,getDegreesFromRadians(sandboxView.angle));
        String pathReal = path.substring(0, path.indexOf("/" + name));
        String ext = "";
        if (name.equals("ECG.jpg")){
            ext = ".jpg";
        }else{
            ext = ".png";
        }

        String newName = "ECG_Rotated" + ext;
        File fileRotate = new File(pathReal, newName);
        try {
            FileOutputStream fos = new FileOutputStream(fileRotate);

            if (ext.equals(".jpg")) {
                rotateBitmap.compress(Bitmap.CompressFormat.JPEG, numComp, fos);
            } else {
                rotateBitmap.compress(Bitmap.CompressFormat.PNG, numComp, fos);
            }

            fos.flush();
            fos.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        lanzarOptions(pathReal + "/" + newName,newName,this, bt);
    }

    public static Bitmap RotateBitmap(Bitmap source, float angle)
    {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }


    public void lanzarOptions(final String p, final String n, final Activity activity, Button bt) {
        bt.setEnabled(true);
        bt.setBackgroundResource(R.drawable.mybutton);

        new AlertDialog.Builder(activity).setTitle(getString(R.string.rotated))
                .setMessage(getString(R.string.cropOrnot))
                .setPositiveButton(getString(R.string.proccesOrnotYes), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                        Intent i = new Intent(activity, Photo_Crop.class);
                        i.putExtra("photoPath", p);
                        i.putExtra("photoName", n);
                        startActivity(i);
                    }
                })
                .setNegativeButton(getString(R.string.proccesOrnotLater), null)
                .show();
    }
}
