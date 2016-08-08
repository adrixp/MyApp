package tfg.com.myapp;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class DrawLines extends View {
    public Paint paint = new Paint();
    private static final String TAG = "TFG:DrawLines:Activity";
    private double OnePxTomm = 0.264583333;

    public DrawLines(Context context) {
        super(context);
        paint.setColor(Color.RED);
        Log.i(TAG, "se ini 1 ");
    }

    public DrawLines(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        Log.i(TAG, "se ini 2 ");
    }

    public DrawLines(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint.setColor(Color.RED);
        Log.i(TAG, "se ini 3 " );
    }


    @Override
    public void onDraw(Canvas canvas) {
        //Verticales si el movil esta vertical, No en Landscape
        //                igual      igual

        Log.i(TAG, "canvas Width: " + canvas.getWidth());
        Log.i(TAG, "canvas Height: " + canvas.getHeight());


        canvas.drawLine(92, 42.428f, 884, 42.428f, paint);
        canvas.drawLine(92, 84.857f, 884, 84.857f, paint);
        canvas.drawLine(92, 127.285f, 884, 127.285f, paint);
        canvas.drawLine(92, 169.714f, 884, 169.714f, paint);
        canvas.drawLine(92, 212.142f, 884, 212.142f, paint);
        canvas.drawLine(92, 254.571f, 884, 254.571f, paint);
        canvas.drawLine(92, 297, 884, 297, paint);
        canvas.drawLine(92, 339.428f, 884, 339.428f, paint);
        canvas.drawLine(92, 381.857f, 884, 381.857f, paint);
        canvas.drawLine(92, 424.285f, 884, 424.285f, paint);
        canvas.drawLine(92, 466.714f, 884, 466.714f, paint);
        canvas.drawLine(92, 509.142f, 884, 509.142f, paint);
        canvas.drawLine(92, 551.571f, 884, 551.571f, paint);



        //Horizontales si el movil esta vertical, No en Landscape
        //             igual  igual
        canvas.drawLine(92, 0, 92, 2000, paint);
        canvas.drawLine(171.2f, 0, 171.2f, 2000, paint);
        canvas.drawLine(250.4f, 0, 250.4f, 2000, paint);
        canvas.drawLine(329.6f, 0, 329.6f, 2000, paint);
        canvas.drawLine(408.8f, 0, 408.8f, 2000, paint);
        canvas.drawLine(488, 0, 488, 2000, paint);
        canvas.drawLine(567.2f, 0, 567.2f, 2000, paint);
        canvas.drawLine(646.4f, 0, 646.4f, 2000, paint);
        canvas.drawLine(725.6f, 0, 725.6f, 2000, paint);
        canvas.drawLine(804.8f, 0, 804.8f, 2000, paint);
        canvas.drawLine(884, 0, 884, 2000, paint);
    }

}
