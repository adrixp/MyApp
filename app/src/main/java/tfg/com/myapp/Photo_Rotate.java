package tfg.com.myapp;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import tfg.com.helpers.BitmapRotateTask;
import tfg.com.helpers.DrawLinesRot;
import tfg.com.helpers.SandboxView;

/**
 * Created by adria on 19/01/2017.
 */

public class Photo_Rotate extends Activity{

    private static final String TAG = "TFG:Rotate:Activity";

    private String path;
    private String name;
    SandboxView sandboxView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_photo_rotate);

        Bundle extras = getIntent().getExtras();
        path = extras.getString("photoPath");
        name = extras.getString("photoName");

        String pathReal = path.substring(0, path.indexOf("/" + name));
        DrawLinesRot drawLinesRot = (DrawLinesRot) findViewById(R.id.drawRotLines);
        drawLinesRot.path = pathReal;


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
    }

    private static float getDegreesFromRadians(float angle) {
        return (float)(angle * 180.0 / Math.PI);
    }

    public void rotate(View view) {
        view.setEnabled(false);
        view.setBackgroundColor(Color.parseColor("#BDBDBD"));

        String [] parts = readFile().split("\n");
        String ext = parts[parts.length - 10].split(" ")[1];
        int numComp = Integer.parseInt(parts[parts.length -9].split(" ")[1]);
        String [] gridSettWyH = parts[parts.length-1].split(" ")[2].split("x");

        BitmapRotateTask task = new BitmapRotateTask(path, getDegreesFromRadians(sandboxView.angle), name, numComp ,view, this, getApplicationContext(), ext, gridSettWyH);
        task.execute(1);

        Toast.makeText(this, getString(R.string.working), Toast.LENGTH_LONG).show();
    }

    public String readFile(){

        File file = new File(path.substring(0, path.indexOf(name)), "settings.txt");
        //Read text from file
        String line;
        String text = "";

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));

            while ((line = br.readLine()) != null) {
                text = text + line + "\n";
            }
            br.close();
        }
        catch (IOException e) {
            //You'll need to add proper error handling here
            text = "";
        }
        return text;
    }
}
