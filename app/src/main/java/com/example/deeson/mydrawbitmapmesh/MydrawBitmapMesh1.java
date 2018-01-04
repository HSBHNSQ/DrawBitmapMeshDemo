package com.example.deeson.mydrawbitmapmesh;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by heshaobo on 2018/1/4.
 */

public class MydrawBitmapMesh1 extends View {

    private Bitmap mbitmap;
    //将图片划分成200*200个小格
    private static final int WIDTH=200;
    private static final int HEIGHT=200;
    //小格相交的总的点数
    private int COUNT=(WIDTH+1)*(HEIGHT+1);
    private float[] verts=new float[COUNT*2];
    private float[] origs=new float[COUNT*2];
    private float k;
    public MydrawBitmapMesh1(Context context) {
        super(context);
        init();
    }
    public MydrawBitmapMesh1(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();

    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmapMesh(mbitmap, WIDTH, HEIGHT, verts, 0, null, 0, null);
//        canvas.drawBitmap(mbitmap, 100, 300, null);
        invalidate();
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        touchwrap(event.getX(),event.getY());
        return super.onTouchEvent(event);
    }
    private void touchwrap(float x, float y) {
        for(int i=0;i<COUNT*2;i+=2){
            //x/y轴每个点坐标与当前x/y坐标的距离
            float dx=x-origs[i+0];
            float dy=y-origs[i+1];
            float dd=dx*dx+dy*dy;
            //计算每个坐标点与当前点（x、y）之间的距离
            float d=(float) Math.sqrt(dd);
            //计算扭曲度，距离当前点越远的点扭曲度越小
            float pull=80000/((float)(dd*d));
            //对verts重新赋值
            if(pull>=1){
                verts[i+0]=x;
                verts[i+1]=y;
            }else{
                verts[i+0]=origs[i+0]+dx*pull;
                verts[i+1]=origs[i+1]+dy*pull;
            }

        }
        invalidate();

    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
    public void init(){
        int index=0;
        mbitmap= BitmapFactory.decodeResource(getResources(),R.drawable.test00);
        float bitmapwidth=mbitmap.getWidth();
        float bitmapheight=mbitmap.getHeight();
        for(int i=0;i<HEIGHT+1;i++){
            float fy=bitmapwidth/HEIGHT*i;
            for(int j=0;j<WIDTH+1;j++){
                float fx=bitmapheight/WIDTH*j;
                //偶数位记录x坐标  奇数位记录Y坐标
                origs[index*2+0]=verts[index*2+0]=fx;
                origs[index*2+1]=verts[index*2+1]=fy;
                index++;
            }
        }

    }
}
