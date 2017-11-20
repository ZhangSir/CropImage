package com.itzs.cropimage;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.MainThread;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.itzs.imagecutter.ImageCutView;

public class MainActivity extends AppCompatActivity {

    private final String TAG = MainActivity.class.getSimpleName();

    private ImageCutView imageCutView;
    private Button btnCut;
    private Bitmap bitmap;

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            imageCutView.setImageBitmap(bitmap);
            return false;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageCutView = (ImageCutView) this.findViewById(R.id.icv_main);
        btnCut = (Button) this.findViewById(R.id.btn_cut);

        imageCutView.setOutputWidth(500);
        imageCutView.setOutputHeight(500);

        load();

        btnCut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap cutBitmap = imageCutView.cut();
                imageCutView.setImageBitmap(cutBitmap);
            }
        });
    }

    private void load(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                bitmap = getBitmap(R.mipmap.phone);
                handler.sendMessage(handler.obtainMessage());
            }
        }).start();
    }

    private Bitmap getBitmap(int resId){
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), resId, options);
        int inSampleSize = 1;
        while ((options.outWidth / inSampleSize) > metrics.widthPixels && (options.outHeight / inSampleSize) > metrics.heightPixels) { // &&
            inSampleSize *= 2;
        }

        options.inJustDecodeBounds = false;
        options.inSampleSize = inSampleSize;

        Log.d(TAG, "width: " + options.outWidth);
        Log.d(TAG, "height: " + options.outHeight);
        Log.d(TAG, "inSampleSize: " + inSampleSize);

        bitmap = BitmapFactory.decodeResource(getResources(), resId, options);

        Log.d(TAG, "width: " + options.outWidth);
        Log.d(TAG, "height: " + options.outHeight);

        return bitmap;
    }
}
