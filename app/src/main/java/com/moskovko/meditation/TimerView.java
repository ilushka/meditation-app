package com.moskovko.meditation;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

/**
 * Created by ilushka on 9/18/17.
 */

public class TimerView extends View {
    private static final String TAG = "TimerView";

    private Paint mBarBackgroundPaint;
    private Paint mRingColor;
    private Rect mViewRect;
    private float mRingX, mRingY, mRingRadius, mRingWidth;
    private ArrayList<TimerPoint> mTimerPoints;

    private Paint mDebugPaint;
    private Point mDebugLineP0 = null, mDebugLineP1 = null;

    private class TimerPoint {
        public float x, y, radius;
        public Paint paint;

        public TimerPoint(float x, float y) {
            this.x = x;
            this.y = y;
            radius = 100;
            paint = new Paint();
            paint.setColor(Color.GREEN);
            paint.setStyle(Paint.Style.FILL);
        }
    }

    public TimerView(Context context, AttributeSet attrs) {
        super(context, attrs);

        mRingX = 500;
        mRingY = 500;
        mRingRadius = 300;
        mRingWidth = 150;


        /*
        // load custom attributes from the xml file
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.TimerView,
                0, 0);
        try {
            mForegroundColor = a.getColor(R.styleable.BatteryChargeView_foregroundColor,
                    Color.parseColor("#000000"));
            mBackgroundColor = a.getColor(R.styleable.BatteryChargeView_backgroundColor,
                    Color.parseColor("#FFFFFF"));

            String orientation = a.getString(R.styleable.BatteryChargeView_orientation);
            if (orientation.equals("horizontal")) {
                mIsHorizontal = true;
            } else {
                mIsHorizontal = false;
            }

            String units = a.getString(R.styleable.BatteryChargeView_units);
            if (units.equals("amperehour")) {
                mTexUnit = AMPEREHOUR;
            } else if (units.equals("percentage")) {
                mTexUnit = PERCENTAGE;
            }

            mScaledValue = a.getInt(R.styleable.BatteryChargeView_scaledValue, 100);
        } finally {
            a.recycle();
        }
        */

        // paint
        mRingColor = new Paint();
        mRingColor.setColor(Color.RED);
        mRingColor.setStyle(Paint.Style.STROKE);
        mRingColor.setStrokeWidth(mRingWidth);

        mBarBackgroundPaint = new Paint();
        mBarBackgroundPaint.setColor(Color.BLUE);
        mBarBackgroundPaint.setStyle(Paint.Style.FILL);

        // MONKEY:
        mDebugPaint = new Paint();
        mDebugPaint.setColor(Color.CYAN);
        mDebugPaint.setStyle(Paint.Style.STROKE);
        mDebugPaint.setStrokeWidth(5);

        /*
        mBarForegroundPaint = new Paint();
        mBarForegroundPaint.setColor(mForegroundColor);
        mBarForegroundPaint.setStyle(Paint.Style.FILL);
        mBackgroundTextPaint = new Paint();
        mBackgroundTextPaint.setColor(ContextCompat.getColor(context, R.color.backgroundText));
        mBackgroundTextPaint.setStyle(Paint.Style.FILL);
        mBackgroundTextPaint.setTextAlign(Paint.Align.CENTER);
        mBackgroundTextPaint.setTextSize(getResources().getDisplayMetrics().scaledDensity * 20);
        mForegroundTextPaint = new Paint();
        mForegroundTextPaint.setColor(ContextCompat.getColor(context, R.color.foregroundText));
        mForegroundTextPaint.setStyle(Paint.Style.FILL);
        mForegroundTextPaint.setTextAlign(Paint.Align.CENTER);
        mForegroundTextPaint.setTextSize(getResources().getDisplayMetrics().scaledDensity * 20);

        mCurrentChargeLevel = FULL_CHARGE;
        mText = mScaledValue + mTexUnit;
        */

        mTimerPoints = new ArrayList<TimerPoint>();
    }

    /*
    public void setCurrentChargeLevel(float level) {
        // TODO: cancel old animation
        ValueAnimator animation = ValueAnimator.ofFloat(mCurrentChargeLevel, level);
        // scale animation time to how much movement the bar needs to make
        animation.setDuration((int)(FULL_BAR_ANIMATION_DURATION *
                Math.abs((mCurrentChargeLevel - level))));
        animation.addUpdateListener(this);
        animation.start();
    }
    */

    /*
    @Override
    public void onAnimationUpdate(ValueAnimator animation) {
        mCurrentChargeLevel = ((Float)animation.getAnimatedValue()).floatValue();

        // calculate offset that is subtracted from size representing 100%
        int widthOffset = 0, heightOffset = 0;
        if (mIsHorizontal) {
            widthOffset = (int)(mBackgroundBarRect.width() * (1 - mCurrentChargeLevel));
        } else {
            heightOffset = (int)(mBackgroundBarRect.height() * (1 - mCurrentChargeLevel));
        }
        // update the rectangle of the bar
        mForegroundBarRect.set(0, 0, mBackgroundBarRect.width() - widthOffset,
                mBackgroundBarRect.height() - heightOffset);

        mText = Integer.toString((int)(mScaledValue * mCurrentChargeLevel)) + mTexUnit;

        invalidate();
    }
    */


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        mViewRect = new Rect(0, 0, width, height);

        /*
        // get center coordinates of percentage text
        mTextX = mBackgroundBarRect.width() / 2;
        // offset by height of text bounds to center it on y axis
        Rect textBounds = new Rect();
        String text = FULL_CHARGE_STR;
        mBackgroundTextPaint.getTextBounds(text, 0, text.length(), textBounds);
        mTextY = (mBackgroundBarRect.height() / 2) + (textBounds.height() / 2);
        */

        setMeasuredDimension(mViewRect.width(), mViewRect.height());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawRect(mViewRect, mBarBackgroundPaint);
        canvas.drawCircle(mRingX, mRingY, mRingRadius, mRingColor);
        for (TimerPoint tp : mTimerPoints) {
            canvas.drawCircle(tp.x, tp.y, tp.radius, tp.paint);
        }

        if (mDebugLineP0 != null && mDebugLineP1 != null) {
            canvas.drawLine(mDebugLineP0.x, mDebugLineP0.y, mDebugLineP1.x, mDebugLineP1.y, mDebugPaint);
        }
    }

    private void drawDebugLineBetweenCenterAndTouch(float x, float y) {
        mDebugLineP0 = new Point((int)x, (int)y);
        mDebugLineP1 = new Point((int)mRingX, (int)mRingY);
    }

    private void drawDebugLinearEquation(float x, float y) {
        float m = (mRingY - y) / (mRingX - x);
        float b = y - (m * x);
        Log.i(TAG, "MONKEY: m: " + m + " b: " + b);
        if (m < 0) {
            mDebugLineP0 = new Point((int) 0, (int) b);
            mDebugLineP1 = new Point((int) ((0 - b) / m), (int) 0);
        } else {
        }
    }

    private Point getNearestPoint(int x, int y) {
        int intersectionX, intersectionY;
        // linear equation of line between (x, y) and circles center
        if (mRingX == x) {
            // vertical line
            float A = 1;
            float B = -2 * mRingY;
            float C = (float)(Math.pow(mRingY, 2) - Math.pow(mRingRadius, 2));
            Log.i(TAG, "MONKEY: A: " + A + " B: " + B + " C: " + C);
            if (y < mRingY) {
                intersectionX = (int)x;
                intersectionY = (int) ((-B - Math.sqrt(Math.pow(B, 2) - (4 * A * C))) / (2 * A));
            } else {
                intersectionX = (int)x;
                intersectionY = (int) ((-B + Math.sqrt(Math.pow(B, 2) - (4 * A * C))) / (2 * A));
            }
        } else if (mRingY == y) {
            // horizontal line
            float A = 1;
            float B = -2 * mRingY;
            float C = (float)(Math.pow(mRingX, 2) - Math.pow(mRingRadius, 2));
            Log.i(TAG, "MONKEY: A: " + A + " B: " + B + " C: " + C);
            if (x < mRingX) {
                intersectionX = (int) ((-B - Math.sqrt(Math.pow(B, 2) - (4 * A * C))) / (2 * A));
                intersectionY = (int)y;
            } else {
                intersectionX = (int) ((-B + Math.sqrt(Math.pow(B, 2) - (4 * A * C))) / (2 * A));
                intersectionY = (int)y;
            }

        } else {
            float m = (y - mRingY) / (x - mRingX);
            float b = mRingY - (mRingX * m);
            Log.i(TAG, "MONKEY: m: " + m + " b: " + b);
            // https://math.stackexchange.com/questions/228841/how-do-i-calculate-the-intersections-of-a-straight-line-and-a-circle
            float A = (float) (Math.pow(m, 2) + 1);
            float B = (float) (2 * ((m * b) - (m * mRingY) - mRingX));
            float C = (float) (Math.pow(mRingY, 2) - Math.pow(mRingRadius, 2) + Math.pow(mRingX, 2) - (2 * b * mRingY) + Math.pow(b, 2));
            Log.i(TAG, "MONKEY: A: " + A + " B: " + B + " C: " + C);
            if (x < mRingX) {
                intersectionX = (int) ((-B - Math.sqrt(Math.pow(B, 2) - (4 * A * C))) / (2 * A));
                intersectionY = (int) ((m * ((-B - Math.sqrt(Math.pow(B, 2) - (4 * A * C))) / (2 * A))) + b);
            } else {
                intersectionX = (int) ((-B + Math.sqrt(Math.pow(B, 2) - (4 * A * C))) / (2 * A));
                intersectionY = (int) ((m * ((-B + Math.sqrt(Math.pow(B, 2) - (4 * A * C))) / (2 * A))) + b);
            }
        }
        return new Point(intersectionX, intersectionY);
    }

    private boolean isPointOnRing(float x, float y) {
        double temp = Math.pow((x - mRingX), 2) + Math.pow(y - mRingY, 2);
        // NOTE: the stroke is centered around the object's perimeter
        // check that touch is on the ring's stroke: coordinates are within outer circle and
        // outside of inner circle
        if (temp < Math.pow((mRingRadius + (mRingWidth / 2)), 2) &&
                temp > Math.pow((mRingRadius - (mRingWidth / 2)), 2)) {
            return true;
        }
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (isPointOnRing(event.getX(), event.getY())) {
            Log.i(TAG, "MONKEY: getX(): " + (int)event.getX() + " getY(): " + (int)event.getY());

            Point point = getNearestPoint((int)event.getX(), (int)event.getY());
            Log.i(TAG, "MONKEY: point.x: " + point.x + " point.y: " + point.y);
            mTimerPoints.add(new TimerPoint(point.x, point.y));
            //drawDebugLineBetweenCenterAndTouch(event.getX(), event.getY());
            //drawDebugLinearEquation(event.getX(), event.getY());
            invalidate();
        }
        return true;
    }

    public void clearTimerPoints() {
        mTimerPoints.clear();
        invalidate();
    }
}
