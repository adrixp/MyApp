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

public class DrawLinesRot extends View {
    public Paint paint = new Paint();
    private static final String TAG = "TFG:DrawLinesRot:Act";
    //5 mm son 18.897638 px
    // 4px son 1 mm ||| 20px son 5mm ||| 40px son 10mm

    public int height = 1080;
    public int width = 1440;
    public int numPxH = 20;
    public int numPxW = 20;


    public DrawLinesRot(Context context) {
        super(context);
    }

    public DrawLinesRot(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public DrawLinesRot(Context context, AttributeSet attrs) {
        super(context, attrs);
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
        Log.i(TAG, "Traza y: canvas Width: " + canvas.getWidth());
        Log.i(TAG, "Traza y: canvas Height: " + canvas.getHeight());

        int canvasWidth = canvas.getWidth();

        String dataSet = readFile();
        String direction = "";
        int numVert = 10;
        int numHoriz = 5;

        if(dataSet != null && dataSet != ""){
            String settingsParts [] = dataSet.split("\n");

            String color = settingsParts[settingsParts.length-6].split(" ")[1];

            if(color.equals("Green") || color.equals("Verde")) {
                paint.setColor(Color.GREEN);
            }else if(color.equals("Blue") || color.equals("Azul")){
                paint.setColor(Color.BLUE);
            }else if(color.equals("Cyan")){
                paint.setColor(Color.CYAN);
            }else if(color.equals("Magenta")){
                paint.setColor(Color.MAGENTA);
            }else if(color.equals("White") || color.equals("Blanco")){
                paint.setColor(Color.WHITE);
            }else{
                paint.setColor(Color.RED);
            }

            String numlines = settingsParts[settingsParts.length-5].split(" ")[2];
            numVert = Integer.parseInt(numlines.split("/")[1]) + 1;
            numHoriz = Integer.parseInt(numlines.split("/")[0]) + 1;

            Log.i(TAG, "Traza y: " + numHoriz + "h " + numVert + "v");

            direction = settingsParts[settingsParts.length-4].split(" ")[2];
            String widthLine = settingsParts[settingsParts.length-3].split(" ")[2].split("m")[0];

            String WyH [] = settingsParts[settingsParts.length-2].split(" ")[1].split("x");
            paint.setStrokeWidth(Float.parseFloat(widthLine));

            height = Integer.parseInt(WyH[1]);
            width = Integer.parseInt(WyH[0]);

            String gridSettWyH [] = settingsParts[settingsParts.length-1].split(" ")[2].split("x");
            numPxH = Integer.parseInt(gridSettWyH[1]);
            numPxW = Integer.parseInt(gridSettWyH[0]);

            Log.i(TAG, "Traza y: tamaño: " + width + "x" + height + " rej: " + numPxW + "x" + numPxH);
        }

        if(direction.equals("Vertical")){
            Log.i(TAG, "Traza y: V");
            for (int i = canvas.getWidth()/numVert; i < canvas.getWidth() -10; i = i + canvas.getWidth()/numVert){
                canvas.drawLine(i, 0, i, canvas.getHeight(), paint);
            }
        }else if (direction.equals("Horizontal")){
            Log.i(TAG, "Traza y: H");
            for (int i = canvas.getHeight()/numHoriz; i < canvas.getHeight() -10; i = i + canvas.getHeight()/numHoriz){
                canvas.drawLine(0, i, canvas.getWidth(), i, paint);
            }
        }else if(direction.equals("Both")){
            Log.i(TAG, "Traza y: Both");
            for (int i = canvasWidth/numVert; i < canvasWidth -10; i = i + canvasWidth/numVert){
                canvas.drawLine(i, 0, i, canvas.getHeight(), paint);
            }
            for (int i = canvas.getHeight()/numHoriz; i < canvas.getHeight() -10; i = i + canvas.getHeight()/numHoriz){
                canvas.drawLine(0, i, canvas.getWidth(), i, paint);
            }
        }else{
            Log.i(TAG, "Traza y: Rejilla");
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
        }

    }
}
