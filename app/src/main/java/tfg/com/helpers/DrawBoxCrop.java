package tfg.com.helpers;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;

/**
 * Created by adria on 25/01/2017.
 */

public class DrawBoxCrop extends View {

    private static final String TAG = "TFG:DrawBox:Activity";
    public ArrayList<String> positions = new ArrayList<String>();
    public Paint paint = new Paint();

    public DrawBoxCrop(Context context) {
        super(context);
    }

    public DrawBoxCrop(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public DrawBoxCrop(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    
    public void reDraw(){
        invalidate();
    }

    @Override
    public void onDraw(Canvas canvas) {
        Log.i(TAG, "Traza y: canvas Width: " + canvas.getWidth() + " Height: " + canvas.getHeight());

        paint.setColor(Color.RED);
        paint.setStrokeWidth(8);

        for (String position: positions) {
            Log.i(TAG, "Traza y: positions: " + position);
            String parts [] = position.split(",");
            //Ajuste de la resolucion con el canvas
            float resHoriz = Float.parseFloat(parts[4]);
            float resVert = Float.parseFloat(parts[5]);

            float xIni = (Float.parseFloat(parts[0]) * canvas.getWidth())  / resHoriz;
            float yIni = (Float.parseFloat(parts[1]) * canvas.getHeight()) / resVert ;
            float xFin = (Float.parseFloat(parts[2]) * canvas.getWidth())  / resHoriz;
            float yFin = (Float.parseFloat(parts[3]) * canvas.getHeight()) / resVert;

            canvas.drawLine(xIni, yIni, xFin, yIni, paint); //horiz sup
            canvas.drawLine(xIni, yIni, xIni, yFin, paint);//vert izq
            canvas.drawLine(xIni, yFin, xFin, yFin, paint); //horiz inferior
            canvas.drawLine(xFin, yIni, xFin, yFin, paint);//vert der
        }

    }
    
}
