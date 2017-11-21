# CropImage
android自定义图片剪裁组件；	

## 说明    
当前版本只实现了最基础的截图功能，更多功能还有待完善，当然你也可以自己拓展；	

## 使用    
####引用	
compile 'com.itz:ImageCutter:1.0.0'		

#### 布局文件中：	
<?xml version="1.0" encoding="utf-8"?>
<RelativeLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context="com.itzs.cropimage.MainActivity">

    <com.itzs.imagecutter.ImageCutView
        android:id="@+id/icv_main"
        android:layout_width="match_parent"
        android:layout_height="match_parent"/>

    <Button
        android:id="@+id/btn_cut"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_alignParentBottom="true"
        android:layout_centerHorizontal="true"
        android:text="剪裁"/>
</RelativeLayout>
	
#### 代码中：	
imageCutView = (ImageCutView) this.findViewById(R.id.icv_main);		

参数：	

imageCutView.setOutputWidth(500);	

imageCutView.setOutputHeight(500);	

传入原图：	

imageCutView.setImageBitmap(bitmap);	
	
裁剪图片：	

Bitmap cutBitmap = imageCutView.cut();	

## 截图		

![image1](https://github.com/ZhangSir/CropImage/blob/master/Screenshot_2017-11-20-16-46-21-460_com.itzs.cropimage.png)




