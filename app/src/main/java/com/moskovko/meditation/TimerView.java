package com.moskovko.meditation;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by ilushka on 9/18/17.
 */

public class TimerView extends View {
    private static final String TAG = "TimerView";

    private static final int MARK_WIDTH         = 100;
    private static final int MARK_HEIGHT        = 200;
    private static final int TEXT_SIZE          = 150;
    private static final int TIMER_INTERVAL     = 50;
    
    private Paint mTextPaint;
    private Rect mViewRect;
    private ArrayList<TimerMark> mTimerMarks;
    private TimerMark mPendingMark = null;
    private Timer mTimer;
    private TimerTask mTask;
    private boolean mIsRunning;
    private float mStartAngle, mSweepAngle;
    private float mCurrentMs, mFinalMs;
    private TimerRing mTimerRing;

    private class TimeMarkComparator implements Comparator<TimerMark> {
        public int compare(TimerMark a, TimerMark b) {
            if (a.getArcPosition() < b.getArcPosition()) return -1;
            else if (a.getArcPosition() == b.getArcPosition()) return 0;
            else return 1;
        }
    }

    private class TimerMark {
        public float mAngle, mRadius, mArcPosition;
        private int mHeight, mWidth;
        public Rect mRect;
        public Point mCenter;
        private Paint mPaint;

        public TimerMark(Point center, int height, int width) {
            this.setCenterPoint(center);
            this.mHeight = height;
            this.mWidth = width;
            this.mRadius = height / 2;
            this.mPaint = new Paint();
            this.mPaint.setColor(Color.GREEN);
            this.mPaint.setStyle(Paint.Style.FILL);
        }

        public void setCenterPoint(Point newCenter) {
            this.mCenter = new Point(newCenter);
            this.mRect = new Rect(mCenter.x - (mWidth / 2), mCenter.y - (mHeight / 2), mCenter.x + (mWidth / 2), mCenter.y + (mHeight / 2));
            this.mAngle = 0 - (float)Math.toDegrees(Math.atan2(((0 - TimerRing.RING_CENTER_Y) - (0 - newCenter.y)), (TimerRing.RING_CENTER_X - newCenter.x)));
            setArcPosition();
        }

        private void setArcPosition() {
            double degrees = Math.toDegrees(Math.atan2(((0 - TimerRing.RING_CENTER_Y) - (0 - mCenter.y)),
                                        (TimerRing.RING_CENTER_X - mCenter.x)));
            if (degrees <= -90 && degrees >= -180) {
                this.mArcPosition = (float)((Math.abs(degrees) - 90) / 360);
            } else if (degrees <= 180 && degrees >= 90) {
                this.mArcPosition = (float)(((180 - degrees) / 360) + 0.25);
            } else if (degrees < 90 && degrees >= 0) {
                this.mArcPosition = (float)(((90 - degrees) / 360) + 0.50);
            } else {
                this.mArcPosition = (float)(((Math.abs(degrees)) / 360) + 0.75);
            }
        }

        public Paint getPaint()         { return this.mPaint; }
        public Point getCenter()        { return this.mCenter; }
        public Rect getRect()           { return this.mRect; }
        public float getRadius()        { return this.mRadius; }
        public float getAngle()         { return this.mAngle; }
        public float getArcPosition()   { return this.mArcPosition; }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            return false;
        }
    }

    private class TimerRing {
        public static final float RING_CENTER_X         = 0;
        public static final float RING_CENTER_Y         = 0;
        public static final float RING_STROKE_WIDTH     = 150;

        private Paint mRingPaint;
        private Paint mArcPaint;
        private RectF mRect;
        private float mRadius;

        public TimerRing(float radius) {
            this.mRect = new RectF((0 - radius), (0 - radius), radius, radius);
            this.mRadius = radius;
            this.mArcPaint = new Paint();
            this.mArcPaint.setColor(Color.BLUE);
            this.mArcPaint.setStyle(Paint.Style.STROKE);
            this.mArcPaint.setStrokeWidth(RING_STROKE_WIDTH + 5);
            this.mRingPaint = new Paint();
            this.mRingPaint.setColor(Color.RED);
            this.mRingPaint.setStyle(Paint.Style.STROKE);
            this.mRingPaint.setStrokeWidth(TimerRing.RING_STROKE_WIDTH);
        }

        public RectF getRect()      { return this.mRect; }
        public Paint getArcPaint()  { return this.mArcPaint; }
        public Paint getRingPaint() { return this.mRingPaint; }
        public float getRadius()    { return this.mRadius; }
    }

    public TimerView(Context context, AttributeSet attrs) {
        super(context, attrs);



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

        mTextPaint = new Paint();
        mTextPaint.setColor(Color.WHITE);
        mTextPaint.setStyle(Paint.Style.FILL);
        mTextPaint.setTextSize(TEXT_SIZE);

        mStartAngle = 0;
        mSweepAngle = 0;
        mCurrentMs = 0;
        mFinalMs = 60000;

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

        mTimer = new Timer();
        mTask = new TimerTask() {
            @Override
            public void run() {
                mCurrentMs += TIMER_INTERVAL;
                float timePosition = mCurrentMs / mFinalMs;
                if (mTimerMarks.size() > 0) {
                    TimerMark mark = mTimerMarks.get(0);
                    if (timePosition >= mark.getArcPosition()) {
                        Log.d("MONKEY", "timer event");
                        mTimerMarks.remove(mark);
                    }
                }
                mSweepAngle = timePosition * 360;
                TimerView.this.postInvalidate();
                if (mCurrentMs >= mFinalMs) {
                    mTimer.cancel();
                }
            }
        };

        mIsRunning = false;
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
            widthOffset = (int)(mBackgroundBarRect.mWidth() * (1 - mCurrentChargeLevel));
        } else {
            heightOffset = (int)(mBackgroundBarRect.mHeight() * (1 - mCurrentChargeLevel));
        }
        // update the rectangle of the bar
        mForegroundBarRect.set(0, 0, mBackgroundBarRect.mWidth() - widthOffset,
                mBackgroundBarRect.mHeight() - heightOffset);

        mText = Integer.toString((int)(mScaledValue * mCurrentChargeLevel)) + mTexUnit;

        invalidate();
    }
    */


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        if (width < height) mTimerRing = new TimerRing((width / 2) - TimerRing.RING_STROKE_WIDTH);
        else mTimerRing = new TimerRing((height / 2) - TimerRing.RING_STROKE_WIDTH);
        mViewRect = new Rect(0, 0, width, height);
        setMeasuredDimension(mViewRect.width(), mViewRect.height());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // TODO: don't do any calculations here!!!!

        canvas.save();
        canvas.translate(mViewRect.centerX(), mViewRect.centerY());

        canvas.save();
        canvas.rotate(-90, 0, 0);
        // draw main ring
        canvas.drawCircle(TimerRing.RING_CENTER_X, TimerRing.RING_CENTER_Y, mTimerRing.getRadius(),
                                mTimerRing.getRingPaint());
        // draw arc mask
        canvas.drawArc(mTimerRing.getRect(), mStartAngle, mSweepAngle, false,
                            mTimerRing.getArcPaint());
        canvas.restore();

        // draw already placed marks
        for (TimerMark tp : mTimerMarks) {
            canvas.save(Canvas.MATRIX_SAVE_FLAG);
            canvas.rotate(tp.getAngle(), tp.getCenter().x, tp.getCenter().y);
            canvas.drawRect(tp.getRect(), tp.getPaint());
            canvas.restore();
        }
        // draw currently dragged mark
        if (mPendingMark != null) {
            canvas.save(Canvas.MATRIX_SAVE_FLAG);
            canvas.rotate(mPendingMark.getAngle(), mPendingMark.getCenter().x,
                                mPendingMark.getCenter().y);
            canvas.drawRect(mPendingMark.getRect(), mPendingMark.getPaint());
            canvas.restore();
            // draw text
            String text =  Integer.toString(Math.round(mPendingMark.getArcPosition() * 100));
            canvas.drawText(text, 0, text.length(), (0 - (TEXT_SIZE / 2)), 0, mTextPaint);
        }

        canvas.restore();
    }

    private Point getNearestPointOnRing(int x, int y) {
        int intersectionX, intersectionY;
        if (x == 0) {
            // vertical line
            float A = 1;
            float B = -2 * TimerRing.RING_CENTER_Y;
            float C = (float)(Math.pow(TimerRing.RING_CENTER_Y, 2) -
                                    Math.pow(mTimerRing.getRadius(), 2));
            if (y < TimerRing.RING_CENTER_Y) {
                intersectionX = x;
                intersectionY = (int) ((-B - Math.sqrt(Math.pow(B, 2) - (4 * A * C))) / (2 * A));
            } else {
                intersectionX = x;
                intersectionY = (int) ((-B + Math.sqrt(Math.pow(B, 2) - (4 * A * C))) / (2 * A));
            }
        } else {
            float m = (y - TimerRing.RING_CENTER_Y) / (x - TimerRing.RING_CENTER_X);
            float b = TimerRing.RING_CENTER_Y - (TimerRing.RING_CENTER_X * m);
            // https://math.stackexchange.com/questions/228841/how-do-i-calculate-the-intersections-of-a-straight-line-and-a-circle
            float A = (float) (Math.pow(m, 2) + 1);
            float B = (float) (2 * ((m * b) - (m * TimerRing.RING_CENTER_Y) - TimerRing.RING_CENTER_X));
            float C = (float) (Math.pow(TimerRing.RING_CENTER_Y, 2) - Math.pow(mTimerRing.getRadius(), 2) +
                    Math.pow(TimerRing.RING_CENTER_X, 2) - (2 * b * TimerRing.RING_CENTER_Y) + Math.pow(b, 2));
            if (x < TimerRing.RING_CENTER_X) {
                intersectionX = (int) ((-B - Math.sqrt(Math.pow(B, 2) - (4 * A * C))) / (2 * A));
                intersectionY = (int) ((m * ((-B - Math.sqrt(Math.pow(B, 2) -
                                            (4 * A * C))) / (2 * A))) + b);
            } else {
                intersectionX = (int) ((-B + Math.sqrt(Math.pow(B, 2) - (4 * A * C))) / (2 * A));
                intersectionY = (int) ((m * ((-B + Math.sqrt(Math.pow(B, 2) -
                                            (4 * A * C))) / (2 * A))) + b);
            }
        }
        return new Point(intersectionX, intersectionY);
    }

    private boolean isPointOnRing(float x, float y) {
        double temp = Math.pow((x - TimerRing.RING_CENTER_X), 2) + Math.pow(y - TimerRing.RING_CENTER_Y, 2);
        // NOTE: the stroke is centered around the object's perimeter
        // check that touch is on the ring's stroke: coordinates are within outer circle and
        // outside of inner circle
        if (temp < Math.pow((mTimerRing.getRadius() + (TimerRing.RING_STROKE_WIDTH / 2)), 2) &&
                temp > Math.pow((mTimerRing.getRadius() - (TimerRing.RING_STROKE_WIDTH / 2)), 2)) {
            return true;
        }
        return false;
    }

    private boolean isPointInTimerMark(float x, float y, TimerMark mark) {
        double temp = Math.pow((x - mark.mCenter.x), 2) + Math.pow(y - mark.mCenter.y, 2);
        if (temp < Math.pow(mark.getRadius(), 2)) {
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
                    mPendingMark.setCenterPoint(snapPoint);
                }
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                if (mPendingMark != null) {
                    mTimerMarks.add(mPendingMark);
                    Collections.sort(mTimerMarks, new TimeMarkComparator());
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

    public boolean isRunning() {
        return mIsRunning;
    }

    public void start() {
        mTimer.schedule(mTask, 0, TIMER_INTERVAL);
        mIsRunning = true;
    }

    public void stop() {
        mTimer.cancel();
        mIsRunning = false;
    }
}
