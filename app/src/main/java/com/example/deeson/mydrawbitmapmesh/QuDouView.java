package com.example.deeson.mydrawbitmapmesh;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by heshaobo on 2018/1/4.
 */

public class QuDouView extends View {

    private int mWidth, mHeight;//View 的宽高
    //作用范围半径
    private int r = 50;
    //将图像分成多少格
    private int WIDTH = 200;
    private int HEIGHT = 200;
    //交点坐标的个数
    private int COUNT = (WIDTH + 1) * (HEIGHT + 1);
    //用于保存COUNT的坐标
    //x0, y0, x1, y1......
    private float[] verts = new float[COUNT * 2];
    //用于保存原始的坐标
    private float[] orig = new float[COUNT * 2];
    private Bitmap mBitmap;

    public QuDouView(Context context) {
        super(context);
        init();
    }

    public QuDouView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public QuDouView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }


    private void init() {
        circlePaint = new Paint();
        circlePaint.setStyle(Paint.Style.STROKE);
        circlePaint.setStrokeWidth(5);
        circlePaint.setColor(Color.parseColor("#bc2a35"));

        directionPaint = new Paint();
        directionPaint.setStyle(Paint.Style.FILL);
        directionPaint.setStrokeWidth(10);
        directionPaint.setColor(Color.parseColor("#bc2a35"));
    }

    private void initView() {
        int index = 0;
        Bitmap oriBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.test00);
        mBitmap = zoomBitmap(oriBitmap, mWidth, mHeight);
        float bmWidth = mBitmap.getWidth();
        float bmHeight = mBitmap.getHeight();

        for (int i = 0; i < HEIGHT + 1; i++) {
            float fy = bmHeight * i / HEIGHT;
            for (int j = 0; j < WIDTH + 1; j++) {
                float fx = bmWidth * j / WIDTH;
                //X轴坐标 放在偶数位
                verts[index * 2] = fx;
                orig[index * 2] = verts[index * 2];
                //Y轴坐标 放在奇数位
                verts[index * 2 + 1] = fy;
                orig[index * 2 + 1] = verts[index * 2 + 1];
                index += 1;
            }
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
        initView();
    }

    private Bitmap zoomBitmap(Bitmap bitmap, int width, int height) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        Matrix matrix = new Matrix();
        float scaleWidth = ((float) width / w);
        float scaleHeight = ((float) height / h);
        float scale = Math.min(scaleWidth,scaleHeight);
        matrix.postScale(scale, scale);
        return Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmapMesh(mBitmap, WIDTH, HEIGHT, verts, 0, null, 0, null);
        if (showCircle) {
            canvas.drawCircle(startX, startY, r, circlePaint);
        }
        if (showDirection) {
            canvas.drawLine(startX, startY, moveX, moveY, directionPaint);
        }
    }

    private float startX, startY, moveX, moveY;
    boolean showCircle;
    boolean showDirection;
    private Paint circlePaint;
    private Paint directionPaint;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //绘制变形区域
                startX = event.getX();
                startY = event.getY();
                showCircle = true;
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                //绘制变形方向
                moveX = event.getX();
                moveY = event.getY();
                showDirection = true;
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                showCircle = false;
                showDirection = false;

                //调用warp方法根据触摸屏事件的坐标点来扭曲verts数组
                warp((int) event.getX(), (int) event.getY());

                break;
        }
        return true;
    }

    private void warp(int endX, int endY) {
        for (int i = 0; i < COUNT * 2; i += 2) {
            float dx = verts[i] - endX;
            float dy = verts[i + 1] - endY;
            float dd = dx * dx + dy * dy;

            if (dd < r * r) {
                verts[i] =  endX;
                verts[i + 1] = endY;
            }
        }
        invalidate();
    }


    /**
     * 一键恢复
     */
    public void resetView() {
        for (int i = 0; i < verts.length; i++) {
            verts[i] = orig[i];
        }
        invalidate();
    }
}
