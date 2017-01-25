package tfg.com.helpers;

import android.content.Context;
import android.graphics.Canvas;
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
    ArrayList<String> positions = new ArrayList<String>();
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

        for (String position: positions) {
            Log.i(TAG, "Traza y: positions: " + position);
        }

    }
    
}
