package tfg.com.myapp;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import tfg.com.helpers.DrawEraseMarks;

/**
 * Created by adria on 25/01/2017.
 */

public class Photo_EraseMarks extends Activity {

    private static final String TAG = "TFG:Erase:Activity";

    private String path;
    private String name;
    private DrawEraseMarks drawEraseMarks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_photo_erase_marks);

        Bundle extras = getIntent().getExtras();
        path = extras.getString("photoPath");
        name = extras.getString("photoName");


        Bitmap myBitmap = BitmapFactory.decodeFile(path);
        int w = myBitmap.getWidth();
        int h = myBitmap.getHeight();
        drawEraseMarks = (DrawEraseMarks) findViewById(R.id.drawEraseMarks);
        drawEraseMarks.bitmap = myBitmap;
        drawEraseMarks.height = h;
        drawEraseMarks.width = w;

        if( w > 3000){
            drawEraseMarks.scale = 0.35f;
            drawEraseMarks.scaleOrig = 0.35f;
        }else if(w > 2000 && w < 3000){
            drawEraseMarks.scale = 0.55f;
            drawEraseMarks.scaleOrig = 0.55f;
        }else if(w > 1000 && w < 2000){
            drawEraseMarks.scale = 1.0f;
            drawEraseMarks.scaleOrig = 1.0f;
        }else{
            drawEraseMarks.scale = 2.7f;
            drawEraseMarks.scaleOrig = 2.7f;
        }

        TextView tv = (TextView) findViewById(R.id.textViewErase);
        tv.setText(name);

        final Switch switchBmove = (Switch) findViewById(R.id.SwitchButtonMove);
        final Switch switchBdraw = (Switch) findViewById(R.id.SwitchButtonDraw);

        switchBmove.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    drawEraseMarks.isMoving = true;
                    switchBdraw.setChecked(false);
                }else{
                    drawEraseMarks.isMoving = false;
                    switchBdraw.setChecked(true);
                }
                drawEraseMarks.invalidate();
            }
        });

        switchBdraw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    drawEraseMarks.isMoving = false;
                    switchBmove.setChecked(false);
                }else{
                    drawEraseMarks.isMoving = true;
                    switchBmove.setChecked(true);
                }
                drawEraseMarks.invalidate();
            }
        });

    }

    public void save (View view){
        String [] parts = readFile().split("\n");

        int numComp = Integer.parseInt(parts[parts.length -9].split(" ")[1]);



        String pathReal = path.substring(0, path.indexOf("Derivaciones/" + name)) + "Erased Derivations";
        String nameReal = name.substring(11, name.indexOf(".jpg"));
        String extReal = name.substring(name.indexOf(".") + 1, name.length());

        File folder = new File(pathReal);

        Boolean notfailed = true;

        if(!folder.isDirectory()) {
            if (folder.mkdir()) {
                //Se crea el directorio y la derivación
                System.out.println("Se crea el directorio derivaciones");
            }else{
                System.out.println("Fallo al crear el directorio derivaciones");
                notfailed = false;
            }
        }

        if (notfailed){
            //System.out.println("traza " + pathReal + "/" + "Erased_Derivation_" + nameReal + "." + extReal);
            //System.out.println("traza numCOmp: " + numComp);
            drawEraseMarks.pathToSave = pathReal + "/" + "Erased_Derivation_" + nameReal + "." + extReal;
            drawEraseMarks.extToSave = extReal;
            drawEraseMarks.numComp = numComp;
            drawEraseMarks.canSave = true;
            drawEraseMarks.invalidate();
        }


    }

    public void undoErase (View view){
        if(drawEraseMarks.positionsDraw.size()>0){
            drawEraseMarks.positionsDraw.remove(drawEraseMarks.positionsDraw.size()-1);
            drawEraseMarks.invalidate();
        }
    }

    public void deleteAllErase (View view){
        if(drawEraseMarks.positionsDraw.size()>0){
            drawEraseMarks.positionsDraw.clear();
            drawEraseMarks.invalidate();
        }
    }

    public void scaleOrig (View view){
        drawEraseMarks.scale = drawEraseMarks.scaleOrig;
        drawEraseMarks.invalidate();
    }

    public String readFile(){

        File file = new File(path.substring(0, path.indexOf("Derivaciones/" + name)), "settings.txt");
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
