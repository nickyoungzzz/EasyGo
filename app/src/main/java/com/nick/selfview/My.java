package com.nick.selfview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;

import static android.support.constraint.Constraints.TAG;

public class My extends View {

    RectF rectf = new RectF();

    RectF rectF1 = new RectF();

    Paint paint = new Paint();

    Disposable disposable;

    public My(Context context) {
        super(context);
    }

    public My(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint.setColor(Color.RED);
        paint.setAntiAlias(true);
        paint.setStrokeWidth(5);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        rectf.set(0, 0 , getMeasuredWidth(), 20);
        paint.setColor(Color.RED);
        paint.setStrokeCap(Paint.Cap.ROUND);
        canvas.drawRoundRect(rectf, 20, 20, paint);
        paint.setColor(Color.YELLOW);
        canvas.drawRoundRect(0, 0, rectF1.right, 20, 0, 0, paint);
//        canvas.drawText("hello", 100f, 100f, paint);
    }

    interface I {
        float  t(long currentTime, long duration, float distance);
    }

    public void setI(I i) {
        this.i = i;
    }

    private I i = (currentTime, duration, distance) -> {

        float c = currentTime / 1000f;

        float d = duration / 1000f;

        float a = 4 * distance / d / d;

        float v = distance / d;

//        return v * c;

//        return a * c * c / 2;

        if (c <= d / 2) {
            Log.i(TAG, TAG + "--->" + a * c);
            return a * c * c / 2 ;
        } else {
            Log.i(TAG, TAG + "--->" +  (d/2*a - a * c));
            return distance - a * (d - c) * (d - c) / 2;
        }
    };

        public void set() {
            if (disposable == null || disposable.isDisposed()) {
                disposable = Observable.interval(20, TimeUnit.MILLISECONDS)
                        .map(aLong -> i.t((aLong) * 20, 4000, 721.321f))
                        .subscribe(aFloat -> {

                            rectF1.right = aFloat;
                            postInvalidate();
                            if (aFloat >= 721.321f) {
                                disposable.dispose();
                            }
                        });
            }
        }
}
