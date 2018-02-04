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

    private static final int MARK_WIDTH = 100;
    private static final int MARK_HEIGHT = 200;
    private static final int TEXT_SIZE = 150;

    private Paint mBarBackgroundPaint;
    private Paint mRingColor;
    private Paint mTextPaint;
    private Rect mViewRect;
    private float mRingX, mRingY, mRingRadius, mRingWidth;
    private ArrayList<TimerMark> mTimerMarks;
    private TimerMark mPendingMark = null;

    private class TimerMark {
        public float degrees, radius;
        public Rect rect;
        public Point center;
        public Paint paint;

        private int height, width;

        public TimerMark(Point center, int height, int width) {
            this.center = new Point(center);
            this.degrees = 0 - (float)Math.toDegrees(Math.atan2(((0 - mRingY) - (0 - center.y)), (mRingX - center.x)));
            this.rect = new Rect(center.x - (width / 2), center.y - (height / 2), center.x + (width / 2), center.y + (height / 2));
            this.height = height;
            this.width = width;
            this.radius = height / 2;

            this.paint = new Paint();
            this.paint.setColor(Color.GREEN);
            this.paint.setStyle(Paint.Style.FILL);
        }

        public void setPoint(Point newCenter) {
            this.center = new Point(newCenter);
            this.rect = new Rect(center.x - (width / 2), center.y - (height / 2), center.x + (width / 2), center.y + (height / 2));
            this.degrees = 0 - (float)Math.toDegrees(Math.atan2(((0 - mRingY) - (0 - newCenter.y)), (mRingX - newCenter.x)));
        }

        public float getPositionPercentage() {
            double degrees = Math.toDegrees(Math.atan2(((0 - mRingY) - (0 - center.y)), (mRingX - center.x)));
            if (degrees <= -90 && degrees >= -180) {
                return (float)((Math.abs(degrees) - 90) / 360);
            } else if (degrees <= 180 && degrees >= 90) {
                return (float)(((180 - degrees) / 360) + 0.25);
            } else if (degrees < 90 && degrees >= 0) {
                return (float)(((90 - degrees) / 360) + 0.50);
            } else {
                return (float)(((Math.abs(degrees)) / 360) + 0.75);
            }
        }

        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            return false;
        }
    }

    public TimerView(Context context, AttributeSet attrs) {
        super(context, attrs);

        mRingX = 0;
        mRingY = 0;
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

        mTextPaint = new Paint();
        mTextPaint.setColor(Color.WHITE);
        mTextPaint.setStyle(Paint.Style.FILL);
        mTextPaint.setTextSize(TEXT_SIZE);

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

        mTimerMarks = new ArrayList<TimerMark>();
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
        setMeasuredDimension(mViewRect.width(), mViewRect.height());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // TODO: don't do any calculations here!!!!

        canvas.drawRect(mViewRect, mBarBackgroundPaint);

        canvas.save();

        // draw main ring
        canvas.translate(mViewRect.centerX(), mViewRect.centerY());
        canvas.drawCircle(mRingX, mRingY, mRingRadius, mRingColor);
        // draw already placed marks
        for (TimerMark tp : mTimerMarks) {
            canvas.save(Canvas.MATRIX_SAVE_FLAG);
            canvas.rotate(tp.degrees, tp.center.x, tp.center.y);
            canvas.drawRect(tp.rect, tp.paint);
            canvas.restore();
        }
        // draw currently dragged mark
        if (mPendingMark != null) {
            canvas.save(Canvas.MATRIX_SAVE_FLAG);
            canvas.rotate(mPendingMark.degrees, mPendingMark.center.x, mPendingMark.center.y);
            canvas.drawRect(mPendingMark.rect, mPendingMark.paint);
            canvas.restore();
            // draw center text
            String text =  Integer.toString(Math.round(mPendingMark.getPositionPercentage() * 100));
            canvas.drawText(text, 0, text.length(), (0 - (TEXT_SIZE / 2)), 0, mTextPaint);
        }

        canvas.restore();
    }

    private Point getNearestPointOnRing(int x, int y) {
        int intersectionX, intersectionY;
        if (x == 0) {
            // vertical line
            float A = 1;
            float B = -2 * mRingY;
            float C = (float)(Math.pow(mRingY, 2) - Math.pow(mRingRadius, 2));
            if (y < mRingY) {
                intersectionX = x;
                intersectionY = (int) ((-B - Math.sqrt(Math.pow(B, 2) - (4 * A * C))) / (2 * A));
            } else {
                intersectionX = x;
                intersectionY = (int) ((-B + Math.sqrt(Math.pow(B, 2) - (4 * A * C))) / (2 * A));
            }
        } else {
            float m = (y - mRingY) / (x - mRingX);
            float b = mRingY - (mRingX * m);
            // https://math.stackexchange.com/questions/228841/how-do-i-calculate-the-intersections-of-a-straight-line-and-a-circle
            float A = (float) (Math.pow(m, 2) + 1);
            float B = (float) (2 * ((m * b) - (m * mRingY) - mRingX));
            float C = (float) (Math.pow(mRingY, 2) - Math.pow(mRingRadius, 2) + Math.pow(mRingX, 2) - (2 * b * mRingY) + Math.pow(b, 2));
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

    private boolean isPointInTimerMark(float x, float y, TimerMark mark) {
        double temp = Math.pow((x - mark.center.x), 2) + Math.pow(y - mark.center.y, 2);
        if (temp < Math.pow(mark.radius, 2)) {
            return true;
        }
        return false;
    }

    private TimerMark findTimerMarkNearPoint(Point point) {
        for (TimerMark tp : mTimerMarks) {
            if (isPointInTimerMark(point.x, point.y, tp)) {
                return tp;
            }
        }
        return null;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int transX = (int)(event.getX() - mViewRect.centerX());
        int transY = (int)(event.getY() - mViewRect.centerY());
        Point snapPoint = getNearestPointOnRing(transX, transY);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (isPointOnRing(transX, transY)) {
                    // find out if user touching existing point
                    mPendingMark = findTimerMarkNearPoint(snapPoint);
                    if (mPendingMark == null) {
                        mPendingMark = new TimerMark(snapPoint, MARK_WIDTH, MARK_HEIGHT);
                    } else {
                        // existing point is getting moved
                        mTimerMarks.remove(mPendingMark);
                    }
                }
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                if (mPendingMark != null) {
                    mPendingMark.setPoint(snapPoint);
                }
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                if (mPendingMark != null) {
                    mTimerMarks.add(mPendingMark);
                    mPendingMark = null;
                }
                invalidate();
                break;
        }
        return true;
    }

    public void clearTimerPoints() {
        mTimerMarks.clear();
        invalidate();
    }
}
