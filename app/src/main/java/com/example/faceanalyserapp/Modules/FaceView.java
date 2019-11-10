package com.example.faceanalyserapp.Modules;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.faceanalyserapp.R;

import java.util.ArrayList;
import java.util.List;

import io.fotoapparat.facedetector.Rectangle;

public class FaceView extends View {
    private final List<Rect> rectangles = new ArrayList<>();
    private final Paint strokePaint = new Paint();

    public FaceView(Context context) {
        super(context);
    }

    public FaceView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        applyAttributes(context, attrs);
    }

    public FaceView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        applyAttributes(context, attrs);
    }

    private void applyAttributes(Context context, AttributeSet attrs) {
        TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.RectanglesView);

        try {
            strokePaint.setStyle(Paint.Style.STROKE);
            strokePaint.setColor(
                    attributes.getColor(R.styleable.RectanglesView_rectanglesColor, Color.BLUE)
            );
            strokePaint.setStrokeWidth(
                    attributes.getDimensionPixelSize(R.styleable.RectanglesView_rectanglesStrokeWidth, 1)
            );
        } finally {
            attributes.recycle();
        }
    }

    /**
     * Updates rectangles which will be drawn.
     *
     * @param rectangles rectangles to draw.
     */
    public void setRectangles(@NonNull List<Rectangle> rectangles) {
        ensureMainThread();

        this.rectangles.clear();

        for (Rectangle rectangle : rectangles) {
            final int left = (int) (rectangle.getX() * getWidth());
            final int top = (int) (rectangle.getY() * getHeight());
            final int right = left + (int) (rectangle.getWidth() * getWidth());
            final int bottom = top + (int) (rectangle.getHeight() * getHeight());

            this.rectangles.add(
                    new Rect(left, top, right, bottom)
            );
        }

        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        Paint textPaint = new Paint();
        textPaint.setAntiAlias(true);
        textPaint.setTextSize(40f);
        textPaint.setColor(Color.WHITE);

        for (Rect rectangle : rectangles) {
            canvas.drawRect(rectangle, strokePaint);
            //    canvas.drawText("Face Detected !", rectangle.centerX(), rectangle.centerY(), textPaint);
        }
    }

    private void ensureMainThread() {
        if (Looper.getMainLooper() != Looper.myLooper()) {
            throw new IllegalThreadStateException("This method must be called from the main thread");
        }
    }
}
