package com.moskovko.meditation;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

/**
 * Created by ilushka on 9/18/17.
 */

public class TimerView extends View {
    private static final String TAG = "TimerView";

    private static final int MARK_WIDTH = 50;
    private static final int MARK_HEIGHT = 200;

    private Paint mBarBackgroundPaint;
    private Paint mRingColor;
    private Rect mViewRect;
    private float mRingX, mRingY, mRingRadius, mRingWidth;
    private ArrayList<TimerMark> mTimerMarks;
    private TimerMark mPendingMark = null;

    private Paint mDebugPaint;
    private ArrayList<Float> mDebugLines;

    private class TimerMark {
        public float degrees, radius;
        public Rect rect;
        public Point center;
        public Paint paint;

        private int height, width;

        public TimerMark(Point center, int height, int width) {
            this.center = new Point(center);
            this.degrees = 0 - (float)Math.toDegrees(Math.atan(((0 - mRingY) - (0 - center.y)) / (mRingX - center.x)));
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
            this.degrees = 0 - (float)Math.toDegrees(Math.atan(((0 - mRingY) - (0 - newCenter.y)) / (mRingX - newCenter.x)));
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

        mDebugPaint = new Paint();
        mDebugPaint.setColor(Color.CYAN);
        mDebugPaint.setStyle(Paint.Style.STROKE);
        mDebugPaint.setStrokeWidth(5);
        mDebugLines = new ArrayList<Float>();

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
        canvas.drawRect(mViewRect, mBarBackgroundPaint);
        canvas.drawCircle(mRingX, mRingY, mRingRadius, mRingColor);
        for (TimerMark tp : mTimerMarks) {
            canvas.save(Canvas.MATRIX_SAVE_FLAG);
            canvas.rotate(tp.degrees, tp.center.x, tp.center.y);
            canvas.drawRect(tp.rect, tp.paint);
            canvas.restore();
        }
        if (mPendingMark != null) {
            canvas.save(Canvas.MATRIX_SAVE_FLAG);
            canvas.rotate(mPendingMark.degrees, mPendingMark.center.x, mPendingMark.center.y);
            canvas.drawRect(mPendingMark.rect, mPendingMark.paint);
            canvas.restore();
        }

        /* MONKEY:
        if (!mDebugLines.isEmpty()) {
            // ned to get array of primitives (float)
            float[] points = new float[mDebugLines.size()];
            Iterator<Float> itr = mDebugLines.iterator();
            for (int ii = 0; ii < points.length; ++ii) {
                points[ii] = itr.next().floatValue();
            }
            mDebugLines.clear();
            canvas.drawLines(points, mDebugPaint);
        }
        */
    }

    // MONKEY:
    private void drawDebugLineBetweenCenterAndTouch(float x, float y) {
        mDebugLines.add(x);
        mDebugLines.add(y);
        mDebugLines.add(mRingX);
        mDebugLines.add(mRingY);
    }

    // MONKEY:
    private void drawDebugPerpendicularLineAtPoint(float x1, float y1, float x2, float y2, float px, float py) {
        float m = ((-1 * y2) - (-1 * y1)) / (x2 - x1);
        float b = (-1 * y1) - (m * x1);

        // https://math.stackexchange.com/questions/175896/finding-a-point-along-a-line-a-certain-distance-away-from-another-point
        float d = 1;
        float v1 = (x2 - x1);
        float v2 = ((-1 * y2) - (-1 * y1));
        px = x1 + (float)(d * (v1 / Math.sqrt(Math.pow(v1, 2) + Math.pow(v2, 2))));
        py = -1 * (float)((-1 * y1) * (d * (v2 / Math.sqrt(Math.pow(v1, 2) + Math.pow(v2, 2)))));

        float m_perp = (-1 / m);
        float b_perp = (-1 * py) - (m_perp * px);
        if (m_perp > 0) {
            mDebugLines.add(0f);
            mDebugLines.add(b_perp * -1);
            mDebugLines.add((0 - b_perp) / m_perp);
            mDebugLines.add(0f);
        } else {
            mDebugLines.add(0f);
            mDebugLines.add(b_perp * -1);
            mDebugLines.add((-1000 - b_perp) / m_perp);
            mDebugLines.add(1000f);
        }
    }

    private Point getNearestPointOnRing(int x, int y) {
        int intersectionX, intersectionY;
        // linear equation of line between (mCenterX, y) and circles center
        if (mRingX == x) {
            // vertical line
            float A = 1;
            float B = -2 * mRingY;
            float C = (float)(Math.pow(mRingY, 2) - Math.pow(mRingRadius, 2));
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
        Point snapPoint = getNearestPointOnRing((int)event.getX(), (int)event.getY());
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (isPointOnRing(event.getX(), event.getY())) {
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

        // debug
        /*
        drawDebugLineBetweenCenterAndTouch(event.getX(), event.getY());
        if (mPendingMark != null) {
            drawDebugPerpendicularLineAtPoint(mPendingMark.getmCenterX(), mPendingMark.getY(), mRingX, mRingY, mPendingMark.getmCenterX(), mPendingMark.getY());
        }
        */
        return true;
    }

    public void clearTimerPoints() {
        mTimerMarks.clear();
        invalidate();
    }
}
