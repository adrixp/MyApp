package tfg.com.myapp;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Environment;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class DrawLines extends View {
    public Paint paint = new Paint();
    private static final String TAG = "TFG:DrawLines:Activity";
    //5 mm son 18.897638 px
    // 4px son 1 mm ||| 20px son 5mm ||| 40px son 10mm

    public int height = 1080;
    public int width = 1440;
    public int numPxH = 20;
    public int numPxW = 20;


    public DrawLines(Context context) {
        super(context);
        paint.setColor(Color.GREEN);
    }

    public DrawLines(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public DrawLines(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint.setColor(Color.GREEN);
    }

    public String readFile(){
        String path = Environment.getExternalStorageDirectory().getPath() + "/ECG-Analyzer";
        File file = new File(path, "sharedGridOptions.txt");
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
        //System.out.println("ReadFileREquest: texto Leido: " + text);
        return text;
    }


    @Override
    public void onDraw(Canvas canvas) {
        Log.i(TAG, "canvas Width: " + canvas.getWidth());
        Log.i(TAG, "canvas Height: " + canvas.getHeight());

        String dataSet = readFile();
        String direction = "";

        if(dataSet != null && dataSet != ""){
            String settingsParts [] = dataSet.split("\n");

            direction = settingsParts[settingsParts.length-4].split(" ")[2];
            String widthLine = settingsParts[settingsParts.length-3].split(" ")[2].split("m")[0];

            String WyH [] = settingsParts[settingsParts.length-2].split(" ")[1].split("x");
            paint.setStrokeWidth(Float.parseFloat(widthLine));

            height = Integer.parseInt(WyH[1]);
            width = Integer.parseInt(WyH[0]);

            /*

             String gridSettWyH [] = settingsParts[settingsParts.length-1].split(" ")[2].split("x");
            numPxH = Integer.parseInt(gridSettWyH[1]);
            numPxW = Integer.parseInt(gridSettWyH[0]);

            Log.i(TAG, "Entra y: " + width + "x" + height + "rej: " + numPxW + "x" + numPxH);*/
        }

        if(direction.equals("Vertical")){
            for (int i = canvas.getWidth()/10; i <= canvas.getWidth(); i = i + canvas.getWidth()/10){
                canvas.drawLine(i, 0, i, canvas.getHeight(), paint);
                Log.i(TAG, "xxxxv: " + i);
            }
        }else{
            for (int i = canvas.getHeight()/5; i <= canvas.getHeight(); i = i + canvas.getHeight()/5){
                canvas.drawLine(0, i, canvas.getWidth(), i, paint);
                Log.i(TAG, "xxxxh: " + i);
            }
        }


        /*
         //Lineas Horiz
            //                1ºigual      3ºigual
            //ancho:1440 px  alto: 1080 px
            for (int i = 0; i <= width/numPxW; i++){
                canvas.drawLine(i*numPxW, 0, i*numPxW, height, paint);
            }


          //Lineas Verticales
            //             2ºigual  4ºigual
            //ancho:1440 px  alto: 1080 px
            for (int i = 0; i <= height/numPxH; i++){
                canvas.drawLine(0, i*numPxH, width, i*numPxH, paint);
            }

         */
    }
}
