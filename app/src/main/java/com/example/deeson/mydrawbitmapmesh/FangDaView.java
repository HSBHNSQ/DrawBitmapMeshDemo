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

public class FangDaView extends View {

    private int mWidth, mHeight;//View 的宽高
    //作用范围半径
    private int r = 100;
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

    public FangDaView(Context context) {
        super(context);
        init();
    }

    public FangDaView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FangDaView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
    }

    private void initView() {
        int index = 0;
        Bitmap oriBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.test1);
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
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                //调用warp方法根据触摸屏事件的坐标点来扭曲verts数组
//                warp((int) event.getX(), (int) event.getY());
                warp(event.getX(),event.getY());
                break;
        }
        return true;
    }

    private void warp(float centerX, float centerY) {

        float strength = -0.2f;
        int range = r * r;
        for (int i = 0; i < COUNT * 2; i += 2) {
            float dx = verts[i] - centerX;
            float dy = verts[i + 1] - centerY;
            float distance = dx * dx + dy * dy;
            if (distance <= range) {
                float scaleFactor = 1 - distance / range;
                scaleFactor = 1 -  strength * scaleFactor;
                float scaleX = dx * scaleFactor + centerX;
                float scaleY = dy * scaleFactor + centerY;
//                if (PosX < 0) { // 放置越界
//                    PosX = 0;
//                } else if (PosX >= width) {
//                    PosX = width - 1;
//                }
//                if (PosY < 0) {
//                    PosY = 0;
//                } else if (PosY >= height) {
//                    PosY = height - 1;
//                }
                verts[i] =  scaleX;
                verts[i + 1] = scaleY;
            }
        }
        invalidate();
    }
    private void warp1(float endX, float endY) {

        float xishu  = 50f;
        float real_radius = r / xishu;
        for (int i = 0; i < COUNT * 2; i += 2) {
            float dx = verts[i] - endX;
            float dy = verts[i + 1] - endY;
            float dd = dx * dx + dy * dy;
            float d = (float) Math.sqrt(dd);
            if (d < r) {
//                float src_x = dx / xishu + endX;
//                float src_y = dy / xishu + endY;

                float src_x =  dx / xishu;
                float src_y =  dy / xishu;
                src_x = src_x * d / real_radius;
                src_y = src_y * d / real_radius;
                src_x = src_x + endX;
                src_y = src_y + endY;

                verts[i] =  src_x;
                verts[i + 1] = src_y;
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
