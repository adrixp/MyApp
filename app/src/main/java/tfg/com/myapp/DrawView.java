package tfg.com.myapp;

import java.util.ArrayList;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class DrawView extends View {

	public static Point[] points = new Point[4];

    /**
     * point1 and point 3 are of same group and same as point 2 and point4
     */
    int groupId = -1;
    public static ArrayList<ColorBall> colorBalls = new ArrayList<>();
    // array that holds the balls
    private int balID = 0;
    // variable to know what ball is being dragged
    Paint paint;
    Canvas canvas;

    public DrawView(Context context) {
        super(context);
        paint = new Paint();
        setFocusable(true); // necessary for getting the touch events
        canvas = new Canvas();
    }

    public DrawView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public DrawView(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint();
        setFocusable(true); // necessary for getting the touch events
        canvas = new Canvas();
    }

    // the method that draws the balls
    @Override
    protected void onDraw(Canvas canvas) {
        if(points[3]==null) //point4 null when user did not touch and move on screen.
            return;
        int left, top, right, bottom;
        left = points[0].x;
        top = points[0].y;
        right = points[0].x;
        bottom = points[0].y;
        for (int i = 1; i < points.length; i++) {
            left = left > points[i].x ? points[i].x:left;
            top = top > points[i].y ? points[i].y:top;
            right = right < points[i].x ? points[i].x:right;
            bottom = bottom < points[i].y ? points[i].y:bottom;
        }
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeWidth(5);

        //draw stroke
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.parseColor("#AADB1255"));
        paint.setStrokeWidth(2);
        canvas.drawRect(
                    left + colorBalls.get(0).getWidthOfBall() / 2,
                    top + colorBalls.get(0).getWidthOfBall() / 2, 
                    right + colorBalls.get(2).getWidthOfBall() / 2, 
                    bottom + colorBalls.get(2).getWidthOfBall() / 2, paint);
        //fill the rectangle
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.parseColor("#55DB1255"));
        paint.setStrokeWidth(0);
        canvas.drawRect(
                left + colorBalls.get(0).getWidthOfBall() / 2,
                top + colorBalls.get(0).getWidthOfBall() / 2, 
                right + colorBalls.get(2).getWidthOfBall() / 2, 
                bottom + colorBalls.get(2).getWidthOfBall() / 2, paint);

        //draw the corners
        // draw the balls on the canvas
        paint.setColor(Color.BLUE);
        paint.setTextSize(18);
        paint.setStrokeWidth(0);
        for (int i =0; i < colorBalls.size(); i ++) {
            ColorBall ball = colorBalls.get(i);
            canvas.drawBitmap(ball.getBitmap(), ball.getX(), ball.getY(),
                    paint);

            canvas.drawText("" + (i+1), ball.getX(), ball.getY(), paint);
        }
    }

    // events when touching the screen
    public boolean onTouchEvent(MotionEvent event) {
        int eventaction = event.getAction();

        int X = (int) event.getX();
        int Y = (int) event.getY();

        switch (eventaction) {

        case MotionEvent.ACTION_DOWN: // touch down so check if the finger is on
                                        // a ball
            if (points[0] == null) {
                //initialize rectangle.
                points[0] = new Point();
                points[0].x = X;
                points[0].y = Y;

                points[1] = new Point();
                points[1].x = X;
                points[1].y = Y + 30;

                points[2] = new Point();
                points[2].x = X + 30;
                points[2].y = Y + 30;

                points[3] = new Point();
                points[3].x = X +30;
                points[3].y = Y;

                balID = 2;
                groupId = 1;
                 // declare each ball with the ColorBall class
                for (Point pt : points) {
                     colorBalls.add(new ColorBall(getContext(), R.mipmap.gray_circle, pt));
                }
            } else {
                //resize rectangle
                balID = -1;
                groupId = -1;
                for (int i = colorBalls.size()-1; i>=0; i--) {
                    ColorBall ball = colorBalls.get(i);
                    // check if inside the bounds of the ball (circle)
                    // get the center for the ball
                    int centerX = ball.getX() + ball.getWidthOfBall();
                    int centerY = ball.getY() + ball.getHeightOfBall();
                    paint.setColor(Color.CYAN);
                    // calculate the radius from the touch to the center of the
                    // ball
                    double radCircle = Math
                            .sqrt((double) (((centerX - X) * (centerX - X)) + (centerY - Y)
                                    * (centerY - Y)));

                    if (radCircle < ball.getWidthOfBall()) {

                        balID = ball.getID();
                        if (balID == 1 || balID == 3) {
                            groupId = 2;
                        } else {
                            groupId = 1;
                        }
                        invalidate();
                        break;
                    }
                    invalidate();
                }
            }
            break;

        case MotionEvent.ACTION_MOVE: // touch drag with the ball


            if (balID > -1) {
                // move the balls the same as the finger
                colorBalls.get(balID).setX(X);
                colorBalls.get(balID).setY(Y);

                paint.setColor(Color.CYAN);
                if (groupId == 1) {
                    colorBalls.get(1).setX(colorBalls.get(0).getX());
                    colorBalls.get(1).setY(colorBalls.get(2).getY());
                    colorBalls.get(3).setX(colorBalls.get(2).getX());
                    colorBalls.get(3).setY(colorBalls.get(0).getY());
                } else {
                    colorBalls.get(0).setX(colorBalls.get(1).getX());
                    colorBalls.get(0).setY(colorBalls.get(3).getY());
                    colorBalls.get(2).setX(colorBalls.get(3).getX());
                    colorBalls.get(2).setY(colorBalls.get(1).getY());
                }

                invalidate();
            }

            break;

        case MotionEvent.ACTION_UP:
            // touch drop - just do things here after dropping

            break;
        }
        // redraw the canvas
        invalidate();
        return true;

    }
}