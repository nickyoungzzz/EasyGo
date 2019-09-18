package com.nick.selfview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.nick.base.R;

public class CircleProgress extends View {

    public void setProgress(int progress) {
        this.progress = progress;
        invalidate();
    }

    public void setMax(int max)
    {
        this.max = max;
    }

    public int getProgress() {
        return progress;
    }

    public int getMax() {
        return max;
    }

    private int progress;

    private int max;

    private int color;

    private int radius;

    private Point p = new Point();

    private Paint paint = new Paint();

    private Paint paint1 = new Paint();

    RectF rect1 = new RectF();

    RectF rect2 = new RectF();

    public CircleProgress(Context context) {
        this(context, null, 0);
    }

    public CircleProgress(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleProgress(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CircleProgress, defStyleAttr, 0);
        progress = typedArray.getInteger(R.styleable.CircleProgress_progress, 0);
        color = typedArray.getColor(R.styleable.CircleProgress_color, context.getResources().getColor(R.color.colorAccent));
        radius = typedArray.getInteger(R.styleable.CircleProgress_radius, 100);
        max = typedArray.getInteger(R.styleable.CircleProgress_max, 100);
        typedArray.recycle();
        paint.setColor(color);
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setDither(true);
        paint.setStrokeWidth(5);
        paint1.setColor(color);
        paint1.setAntiAlias(true);
        paint1.setStyle(Paint.Style.FILL);
        paint1.setColor(Color.WHITE);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int height = getPaddingBottom() + getPaddingTop() + 2 * radius;
        int width = getPaddingLeft() + getPaddingRight() + 2 * radius;
        p.set(getLeft() + getPaddingLeft() + radius, getTop() + getPaddingTop() + radius);
        rect1.top = p.x - radius;
        rect1.left = p.y - radius;
        rect1.right = p.x + radius;
        rect1.bottom = p.y + radius;
        rect2.top = p.x - radius + 10;
        rect2.left = p.y - radius + 10;
        rect2.right = p.x + radius -10;
        rect2.bottom = p.y + radius - 10;
        setMeasuredDimension(resolveSize(width, widthMeasureSpec), resolveSize(height, heightMeasureSpec));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawCircle(p.x, p.y, radius, paint);
        canvas.drawCircle(p.x, p.y, radius - 10,  paint);
        paint1.setColor(color);
        canvas.drawArc(rect1, -90.0f, progress * 3.6f + 1, true, paint1);
        paint1.setColor(Color.WHITE);
        canvas.drawArc(rect2, -90.0f, progress * 3.6f + 1, true, paint1);
    }

}
