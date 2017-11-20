package com.itzs.imagecutter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.View;

/**
 * 图片缩放展示组件
 * Created by zhangshuo on 2017/11/2.
 */
public class ImageScaleView extends View {

    private Context context;

    private Bitmap bitmap;

    private float width, heigh;

    private Matrix matrix;

    private RectF rectBitmap;

    /**
     * 最终图片缩放的倍数
     */
    private float ratio = 1;
    /**
     * 最终缩放后的图片在x方向上移动的距离
     */
    private float translateX = 0;
    /**
     * 最终缩放后的图片在y方向上移动的距离
     */
    private float translateY = 0;

    private OnImageDrawListener drawListener;

    public ImageScaleView(Context context) {
        super(context);
        this.init(context);
    }

    public ImageScaleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.init(context);
    }

    public ImageScaleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.init(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public ImageScaleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.init(context);
    }

    private void init(Context context) {
        this.context = context;
        this.matrix = new Matrix();
        this.rectBitmap = new RectF();
    }

    public void setImageBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
        invalidate();
    }

    public RectF getRectBitmap() {
        return rectBitmap;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        this.width = getWidth();
        this.heigh = getHeight();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (null == bitmap) return;
        calculateMatrix();
        canvas.drawBitmap(bitmap, matrix, null);
        if (null != drawListener) {
            drawListener.onDrawFinish();
        }
    }

    private Matrix calculateMatrix() {
        if (null == bitmap) return null;
        float bitmapW = bitmap.getWidth();
        float bitmapH = bitmap.getHeight();
        float ratioW, ratioH;
        this.matrix.reset();
        if (bitmapW > width || bitmapH > heigh) {
            if (bitmapW > width && bitmapH > heigh) {
                //图片宽高都大于屏幕宽高，比较各自差值比，谁的差值比小，就以谁的缩放比例为准
                ratioW = width / bitmapW;
                ratioH = heigh / bitmapH;
                if (ratioW < ratioH) {
                    matrix.postScale(ratioW, ratioW);
                    float translateY = (heigh - bitmapH * ratioW) / 2;
                    matrix.postTranslate(0, translateY);
                    this.rectBitmap.set(0, translateY, width, heigh - translateY);

                    this.ratio = ratioW;
                    this.translateX = 0;
                    this.translateY = translateY;
                } else {
                    matrix.postScale(ratioH, ratioH);
                    float translateX = (width - bitmapW * ratioH) / 2;
                    matrix.postTranslate(translateX, 0);
                    this.rectBitmap.set(translateX, 0, width - translateX, heigh);

                    this.ratio = ratioH;
                    this.translateX = translateX;
                    this.translateY = 0;
                }
            } else if (bitmapW > width) {
                //图片宽大于屏幕宽，则以宽的差值缩放比例为准
                ratioW = width / bitmapW;
                matrix.postScale(ratioW, ratioW);
                float translateY = (heigh - bitmapH * ratioW) / 2;
                matrix.postTranslate(0, translateY);
                this.rectBitmap.set(0, translateY, width, heigh - translateY);

                this.ratio = ratioW;
                this.translateX = 0;
                this.translateY = translateY;
            } else if (bitmapH > heigh) {
                //图片高大于屏幕高，则以高的差值缩放比例为准
                ratioH = heigh / bitmapH;
                matrix.postScale(ratioH, ratioH);
                float translateX = (width - bitmapW * ratioH) / 2;
                matrix.postTranslate(translateX, 0);
                this.rectBitmap.set(translateX, 0, width - translateX, heigh);

                this.ratio = ratioH;
                this.translateX = translateX;
                this.translateY = 0;
            }
        } else {
            //图片宽高都小于屏幕宽高，比较各自的差值比，谁的差值比小，就以谁的缩放比例为准
            ratioW = width / bitmapW;
            ratioH = heigh / bitmapH;
            if (ratioW < ratioH) {
                matrix.postScale(ratioW, ratioW);
                float translateY = (heigh - bitmapH * ratioW) / 2;
                matrix.postTranslate(0, translateY);
                this.rectBitmap.set(0, translateY, width, heigh - translateY);

                this.ratio = ratioW;
                this.translateX = 0;
                this.translateY = translateY;
            } else {
                matrix.postScale(ratioH, ratioH);
                float translateX = (width - bitmapW * ratioH) / 2;
                matrix.postTranslate(translateX, 0);
                this.rectBitmap.set(translateX, 0, width - translateX, heigh);

                this.ratio = ratioH;
                this.translateX = translateX;
                this.translateY = 0;
            }
        }
        return matrix;
    }

    /**
     * 最终图片缩放的倍数
     * @return
     */
    public float getRatio() {
        return ratio;
    }

    /**
     * 最终缩放后的图片在x方向上移动的距离
     * @return
     */
    public float getTranslateX() {
        return translateX;
    }

    /**
     * 最终缩放后的图片在y方向上移动的距离
     * @return
     */
    public float getTranslateY() {
        return translateY;
    }

    public void setDrawListener(OnImageDrawListener drawListener) {
        this.drawListener = drawListener;
    }

    interface OnImageDrawListener {
        void onDrawFinish();
    }
}
