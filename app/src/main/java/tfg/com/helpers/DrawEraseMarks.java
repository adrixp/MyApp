package tfg.com.helpers;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import tfg.com.math.Vector2D;

public class DrawEraseMarks extends View implements OnTouchListener {

    private static final String TAG = "TFG:DrawEraseM:Activity";

    private Paint paint = new Paint();
    public Bitmap bitmap;
    public int width;
    public int height;
    public boolean isMoving = true;
    private Matrix transform = new Matrix();

    public boolean canSave = false;
    public String pathToSave = "";
    public String extToSave = "jpg";
    public int numComp = 100;

    private Vector2D position = new Vector2D();
    public float scale = 1;
    public float scaleOrig = 1;

    private TouchManager touchManager = new TouchManager(2);
    private boolean isInitialized = false;

    // Debug helpers to draw lines between the two touch points
    private Vector2D vca = null;
    private Vector2D vcb = null;
    private Vector2D vpa = null;
    private Vector2D vpb = null;

    public List<String> positionsDraw = new ArrayList<String>();

    private float incX = 0.0f;
    private float incY = 0.0f;
    private boolean scaling = false;

    public DrawEraseMarks(Context context) {
        super(context);
        setOnTouchListener(this);
        setDrawingCacheEnabled(true);
    }

    public DrawEraseMarks(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setOnTouchListener(this);
        setDrawingCacheEnabled(true);
    }

    public DrawEraseMarks (Context context, AttributeSet attrs) {
        super(context, attrs);
        setOnTouchListener(this);
        setDrawingCacheEnabled(true);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        /*paint.reset();
        canvas.*/
        if (!isInitialized) {
            int w = getWidth();
            int h = getHeight();
            position.set(w / 2, h / 2);
            isInitialized = true;
        }

        if(isMoving){
            transform.reset();
            transform.postTranslate(-width / 2.0f, -height / 2.0f);
            transform.postScale(scale, scale);
            transform.postTranslate(position.getX(), position.getY());
        }



        canvas.drawBitmap(bitmap, transform, paint);

        float finX = 0.0f;
        float finY = 0.0f;
        if(positionsDraw.size() > 0){
            String getFirst [] = positionsDraw.get(0).split("%");
            incX = position.getX()  - Float.parseFloat(getFirst[0]);
            incY = position.getY()  - Float.parseFloat(getFirst[1]);
        }


        for(String partPos : positionsDraw){
            String pos [] = partPos.split("%");

            finX = Float.parseFloat(pos[0]) + incX;
            finY = Float.parseFloat(pos[1]) + incY;


            if(finX >0.0f && finY >0.0f){
                float scaleAdj = (32*scale)/scaleOrig;
                paint.setColor(Color.WHITE);
                canvas.drawCircle(finX, finY, scaleAdj, paint);
            }

        }

        if(canSave){
            try{
                if(extToSave.equals("jpg")){
                    getDrawingCache().compress(Bitmap.CompressFormat.JPEG, numComp, new FileOutputStream(new File(pathToSave)));
                }else{
                    getDrawingCache().compress(Bitmap.CompressFormat.PNG, numComp, new FileOutputStream(new File(pathToSave)));
                }
            }catch (Exception e){
                e.printStackTrace();
            }
            canSave = false;
        }

    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        vca = null;
        vcb = null;
        vpa = null;
        vpb = null;

        try {
            touchManager.update(event);

            if (touchManager.getPressCount() == 1) { //Dibujar y mover
                scaling = false;
                if(!isMoving){
                    vca = touchManager.getPoint(0);
                    positionsDraw.add(vca.getX() + "%" + vca.getY());
                }else{
                    position.add(touchManager.moveDelta(0));
                }
            }
            else {
                if (touchManager.getPressCount() == 2) { //Zommear
                    scaling = true;
                    if(isMoving){

                        Vector2D current = touchManager.getVector(0, 1);
                        Vector2D previous = touchManager.getPreviousVector(0, 1);
                        float currentDistance = current.getLength();
                        float previousDistance = previous.getLength();

                        if (previousDistance != 0) {
                            scale *= currentDistance / previousDistance;
                        }
                    }
                }
            }

            invalidate();
        }
        catch(Throwable t) {
            t.printStackTrace();
        }
        return true;
    }

}
