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
import android.widget.FrameLayout;

/**
 * 图片剪裁组件
 * Created by zhangshuo on 2017/11/2.
 */
public class ImageCutView extends FrameLayout {

    private Context context;

    private Bitmap bitmap;

    private ImageScaleView imageScaleView;
    private FrameSelectView frameSelectView;

    /**
     * 剪裁后的图片输出宽度
     */
    private int outputWidth;
    /**
     * 剪裁后的图片输出高度
     */
    private int outputHeight;

    public ImageCutView(Context context) {
        super(context);
        this.init(context);
    }

    public ImageCutView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.init(context);
    }

    public ImageCutView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.init(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public ImageCutView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.init(context);
    }

    private void init(Context context) {
        this.context = context;
        this.imageScaleView = new ImageScaleView(context);
        this.frameSelectView = new FrameSelectView(context);
        addView(this.imageScaleView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        addView(this.frameSelectView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);

        this.imageScaleView.setDrawListener(new ImageScaleView.OnImageDrawListener() {
            @Override
            public void onDrawFinish() {
                frameSelectView.setRectBitmap(imageScaleView.getRectBitmap());
            }
        });
    }

    public void setImageBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
        this.imageScaleView.setImageBitmap(bitmap);
    }

    /**
     * 剪裁后的图片输出宽度
     * @return
     */
    public int getOutputWidth() {
        return outputWidth;
    }

    /**
     * 剪裁后的图片输出宽度
     * @param outputWidth
     */
    public void setOutputWidth(int outputWidth) {
        this.outputWidth = outputWidth;
    }

    /**
     * 剪裁后的图片输出高度
     * @return
     */
    public int getOutputHeight() {
        return outputHeight;
    }

    /**
     * 剪裁后的图片输出高度
     * @param outputHeight
     */
    public void setOutputHeight(int outputHeight) {
        this.outputHeight = outputHeight;
    }

    /**
     * 剪裁图片并返回<br>
     * 根据图片在展示进行的缩放和平移，对选择框进行逆向平移和缩放，然后再进行裁剪
     * @return
     */
    public Bitmap cut() {
        if (null == bitmap) return null;
        RectF rectCut = this.frameSelectView.getRectSelected();
        if (null == rectCut) return bitmap;

        /**
         * 把选择框根据图片缩放后的平移，进行逆平移
         */
        rectCut.set(rectCut.left - imageScaleView.getTranslateX(),
                rectCut.top - imageScaleView.getTranslateY(),
                rectCut.right - imageScaleView.getTranslateX(),
                rectCut.bottom - imageScaleView.getTranslateY());

        /**
         * 把选择框根据图片的缩放进行逆缩放
         */
        rectCut.set(rectCut.left / imageScaleView.getRatio(),
                rectCut.top / imageScaleView.getRatio(),
                rectCut.right / imageScaleView.getRatio(),
                rectCut.bottom / imageScaleView.getRatio());

        Bitmap cuttedBitmap = Bitmap.createBitmap(bitmap, (int) rectCut.left, (int) rectCut.top, (int) (rectCut.right - rectCut.left), (int) (rectCut.bottom - rectCut.top));
        if(outputWidth > 0 && outputHeight > 0){
            return Bitmap.createScaledBitmap(cuttedBitmap, outputWidth, outputHeight, false);
        }else{
            return cuttedBitmap;
        }
    }

}
