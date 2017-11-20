package com.itzs.imagecutter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

/**
 * 框选组件
 * Created by zhangshuo on 2017/11/2.
 */
public class FrameSelectView extends View {

    private final float DEFAULT_STROKE_WIDTH = 10F;

    private final int DEFAULT_NOMAL_COLOR = 0XFFFFFFFF;
    private final int DEFAULT_PRESSED_COLOR = 0XFF008000;

    private Context context;

    private Bitmap bitmap;

    private RectF rectBitmap, rectFrame;

    private float frameW, frameH;

    private DisplayMetrics metrics;

    private int nomalColor, pressedColor;
    /**
     * 画笔的宽度
     */
    private float strokeWidth;
    private Paint paint;

    private float outWidth, outHeight;

    private float aspectX = 1, aspectY = 1;

    private boolean holdAspect = true;

    private float lastX, lastY;

    /**
     * 边角可触摸区域边长，即可拖动整个选择框的内部触摸区域到选择框的边距。
     */
    private float cornerLength = 16;

    /**
     * 选择框的左右或上下边之间的最小距离
     */
    private float minSpace = 3 * cornerLength;

    /**
     * 记录当前的移动方式，-1在框外，0在框内，1左上角，2右上角，3右下角，4左下角， 5左边触摸区，6上边触摸区，7右边触摸区，8下边触摸区；
     */
    private int moveType = -1;

    public FrameSelectView(Context context) {
        super(context);
        this.init(context);
    }

    public FrameSelectView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.init(context);
    }

    public FrameSelectView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.init(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public FrameSelectView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.init(context);
    }

    private void init(Context context) {
        this.context = context;
        this.rectBitmap = new RectF();
        this.rectFrame = new RectF();
        this.nomalColor = DEFAULT_NOMAL_COLOR;
        this.pressedColor = DEFAULT_PRESSED_COLOR;
        this.strokeWidth = DEFAULT_STROKE_WIDTH;

        this.cornerLength = dip2px(16);

        metrics = new DisplayMetrics();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(metrics);

        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(nomalColor);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(strokeWidth);
    }

    public void setRectBitmap(int left, int top, int right, int bottom) {
        rectBitmap.set(left, top, right, bottom);
        invalidate();
    }

    public void setRectBitmap(RectF rectBitmap) {
        this.rectBitmap.set(rectBitmap.left, rectBitmap.top, rectBitmap.right, rectBitmap.bottom);
        invalidate();
    }


    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        if (changed) {

            RectF rectF = new RectF(left, top, right, bottom);
            if (null != rectBitmap && rectBitmap.left != rectBitmap.right && rectBitmap.top != rectBitmap.bottom) {
                //已设置图片的显示区域
                rectF.set(rectBitmap.left, rectBitmap.top, rectBitmap.right, rectBitmap.bottom);
            }else{
                //未设置图片的显示区域，默认使用组件的区域作为图片的显示区域
                rectBitmap.set(left, top, right, bottom);
            }

            //默认选择框的最大边不得超过显示区域最小边的1/2
            float minBound = Math.min(rectF.right - rectF.left, rectF.bottom - rectF.top);
            float maxAspect = Math.max(aspectX, aspectY);
            float base = (minBound / 2) / maxAspect;
            float frameW = base * aspectX;
            float frameH = base * aspectY;

            float leftSpace = (getWidth() - frameW) / 2;
            float topSpace = (getHeight() - frameH) / 2;

            float l = left + leftSpace;
            float t = top + topSpace;
            float r = leftSpace + frameW;
            float b = topSpace + frameH;

            rectFrame.set(l, t, r, b);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawRect(rectFrame, paint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastX = event.getX();
                lastY = event.getY();
                moveType = analysisLocation(lastX, lastY);
                break;
            case MotionEvent.ACTION_MOVE:
                if (moveType == -1) {
                    return true;
                }
                float offsetX, offsetY;
                offsetX = event.getX() - lastX;
                offsetY = event.getY() - lastY;
                float tL, tT, tR, tB;
                if (moveType == 0) {
                    //拖动整个选择框
                    tL = rectFrame.left + offsetX;
                    tT = rectFrame.top + offsetY;
                    tR = rectFrame.right + offsetX;
                    tB = rectFrame.bottom + offsetY;
                    float rectW = rectFrame.right - rectFrame.left;
                    float rectH = rectFrame.bottom - rectFrame.top;
//                    if(offsetX < 0 && tL < rectBitmap.left){
//                        tL = rectBitmap.left;
//                        tR = tL + (rectFrame.right - rectFrame.left);
//                    }else if(offsetX > 0 && tR > rectBitmap.right){
//                        tR = rectBitmap.right;
//                        tL = tR - (rectFrame.right - rectFrame.left);
//                    }
//                    if(offsetY < 0 && tT < rectBitmap.top){
//                        tT = rectBitmap.top;
//                        tB = tT + (rectFrame.bottom - rectFrame.top);
//                    }else if(offsetY > 0 && tB > rectBitmap.bottom){
//                        tB = rectBitmap.bottom;
//                        tT = tB - (rectFrame.bottom - rectFrame.top);
//                    }

                    tL = correct(tL, rectBitmap.left, rectBitmap.right - rectW);
                    tT = correct(tT, rectBitmap.top, rectBitmap.bottom - rectH);
                    tR = tL + rectW;
                    tB = tT + rectH;

                    rectFrame.set(tL, tT, tR, tB);
                } else if (moveType == 1) {
                    //拖动左上角
                    tL = rectFrame.left + offsetX;
                    tT = rectFrame.top + offsetY;
                    tL = correct(tL, rectBitmap.left, rectFrame.right - minSpace);
                    tT = correct(tT, rectBitmap.top, rectFrame.bottom - minSpace);
                    rectFrame.set(tL, tT, rectFrame.right, rectFrame.bottom);
                } else if (moveType == 2) {
                    //拖动右上角
                    tT = rectFrame.top + offsetY;
                    tR = rectFrame.right + offsetX;
                    tT = correct(tT, rectBitmap.top, rectFrame.bottom - minSpace);
                    tR = correct(tR, rectFrame.left + minSpace, rectBitmap.right);
                    rectFrame.set(rectFrame.left, tT, tR, rectFrame.bottom);
                } else if (moveType == 3) {
                    //拖动右下角
                    tR = rectFrame.right + offsetX;
                    tB = rectFrame.bottom + offsetY;
                    tR = correct(tR, rectFrame.left + minSpace, rectBitmap.right);
                    tB = correct(tB, rectFrame.top + minSpace, rectBitmap.bottom);
                    rectFrame.set(rectFrame.left, rectFrame.top, tR, tB);
                } else if (moveType == 4) {
                    //拖动左下角
                    tL = rectFrame.left + offsetX;
                    tB = rectFrame.bottom + offsetY;
                    tL = correct(tL, rectBitmap.left, rectFrame.right - minSpace);
                    tB = correct(tB, rectFrame.top + minSpace, rectBitmap.bottom);
                    rectFrame.set(tL, rectFrame.top, rectFrame.right, tB);
                } else if (moveType == 5) {
                    //拖动左边
                    tL = rectFrame.left + offsetX;
                    tL = correct(tL, rectBitmap.left, rectFrame.right - minSpace);
                    rectFrame.set(tL, rectFrame.top, rectFrame.right, rectFrame.bottom);
                } else if (moveType == 6) {
                    //拖动上边
                    tT = rectFrame.top + offsetY;
                    tT = correct(tT, rectBitmap.top, rectFrame.bottom - minSpace);
                    rectFrame.set(rectFrame.left, tT, rectFrame.right, rectFrame.bottom);
                } else if (moveType == 7) {
                    //拖动右边
                    tR = rectFrame.right + offsetX;
                    tR = correct(tR, rectFrame.left + minSpace, rectBitmap.right);
                    rectFrame.set(rectFrame.left, rectFrame.top, tR, rectFrame.bottom);
                } else if (moveType == 8) {
                    //拖动下边
                    tB = rectFrame.bottom + offsetY;
                    tB = correct(tB, rectFrame.top + minSpace, rectBitmap.bottom);
                    rectFrame.set(rectFrame.left, rectFrame.top, rectFrame.right, tB);
                }

                lastX = event.getX();
                lastY = event.getY();

                invalidate();
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                moveType = -1;
                lastX = 0;
                lastY = 0;
                break;

        }
        return true;
    }

    /**
     * 分析位置，-1在框外，0在框内，1左上角，2右上角，3右下角，4左下角， 5左边触摸区，6上边触摸区，7右边触摸区，8下边触摸区；
     *
     * @param x
     * @param y
     * @return
     */
    private int analysisLocation(float x, float y) {
        //可拖动整个选择框的内部触摸区域的left
        float inL = rectFrame.left + cornerLength;
        //可拖动整个选择框的内部触摸区域的top
        float inT = rectFrame.top + cornerLength;
        //可拖动整个选择框的内部触摸区域的right
        float inR = rectFrame.right - cornerLength;
        //可拖动整个选择框的内部触摸区域的bottom
        float inB = rectFrame.bottom - cornerLength;

        if (isInclude(x, y, inL, inT, inR, inB)) {
            //在可拖动整个选择框的内部触摸区域内
            return 0;
        } else if (isInclude(x, y, rectFrame.left, rectFrame.top, inL, inT)) {
            //在左上角的可触摸区域内
            return 1;
        } else if (isInclude(x, y, inR, rectFrame.top, rectFrame.right, inT)) {
            //在右上角的可触摸区域内
            return 2;
        } else if (isInclude(x, y, inR, inB, rectFrame.right, rectFrame.bottom)) {
            //在右下角的可触摸区域内
            return 3;
        } else if (isInclude(x, y, rectFrame.left, inB, inL, rectFrame.bottom)) {
            //在左下角的可触摸区域
            return 4;
        } else if (isInclude(x, y, rectFrame.left, inT, inL, inB)) {
            //在左边可触摸区域
            return 5;
        } else if (isInclude(x, y, inL, rectFrame.top, inR, inT)) {
            //在上边可触摸区域
            return 6;
        } else if (isInclude(x, y, inR, inT, rectFrame.right, inB)) {
            //在右边可触摸区域
            return 7;
        } else if (isInclude(x, y, inL, inB, inR, rectFrame.bottom)) {
            //在下边可触摸区域
            return 8;
        }
        return -1;
    }

    /**
     * 判断目标坐标是否位于指定区域内
     *
     * @param targetX
     * @param targetY
     * @param left
     * @param top
     * @param right
     * @param bottom
     * @return
     */
    private boolean isInclude(float targetX, float targetY, float left, float top, float right, float bottom) {
        if (targetX > left && targetX < right && targetY > top && targetY < bottom) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 矫正目标值，使得min <= target <= max；
     *
     * @param target
     * @param min
     * @param max
     * @return
     */
    private float correct(float target, float min, float max) {
        if (target < min) return min;
        if (target > max) return max;
        return target;
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public int dip2px(float dpValue) {
        final float scale = getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 获取选中区域
     * @return
     */
    public RectF getRectSelected(){
        return rectFrame;
    }
}
