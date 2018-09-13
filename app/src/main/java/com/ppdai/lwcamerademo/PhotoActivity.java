package com.ppdai.lwcamerademo;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.File;
import java.io.FileOutputStream;

public class PhotoActivity extends AppCompatActivity {

    private static final String TAG = "PhotoActivity";
    private SurfaceView mSurfaceView;
    private SurfaceHolder mSurfaceHolder;
    private Camera mCamera;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);

        mSurfaceView = (SurfaceView) findViewById(R.id.surface_view);

        mSurfaceHolder = mSurfaceView.getHolder();
        mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        mSurfaceHolder.addCallback(new NewSurfaceHoler());

    }


    class NewSurfaceHoler implements SurfaceHolder.Callback {

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            try {

                Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
                int numberOfCameras = Camera.getNumberOfCameras();
                for (int i = 0; i < numberOfCameras; i++) {
                    Camera.getCameraInfo(i, cameraInfo);
                    if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                        mCamera = Camera.open(i);
                        mCamera.setPreviewDisplay(holder);
                        mCamera.setDisplayOrientation(getPreviewDegree(PhotoActivity.this));
                        mCamera.startPreview();

                        /**
                         * 相机开启需要时间 延时takePicture
                         */
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mCamera.takePicture(null, null, new Camera.PictureCallback() {
                                    @Override
                                    public void onPictureTaken(byte[] data, Camera camera) {
                                        Bitmap source = BitmapFactory.decodeByteArray(data, 0, data.length);
                                        int degree = Config.readPictureDegree(getFilePath());
                                        Bitmap bitmap = Config.rotaingImageView(degree, source);

                                        Config.Image = bitmap;
                                        saveBitmap(bitmap, new File(getFilePath()));

                                    }
                                });
                            }
                        }, 2000);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            Camera.Parameters parameters = mCamera.getParameters(); // 获取各项参数
            parameters.setPictureFormat(PixelFormat.JPEG); // 设置图片格式
            parameters.setJpegQuality(100); // 设置照片质量

            /**
             * 以下不设置在某些机型上报错
             */
            int mPreviewHeight = parameters.getPreviewSize().height;
            int mPreviewWidth = parameters.getPreviewSize().width;
            parameters.setPreviewSize(mPreviewWidth, mPreviewHeight);
            parameters.setPictureSize(mPreviewWidth, mPreviewHeight);

            mCamera.setParameters(parameters);
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            mCamera.stopPreview();
            mCamera.unlock();
            mCamera.release();
        }
    }

    public String getFilePath() {
        return getFileDir(this) + "/12333.jpg";
//        return Environment.getExternalStorageDirectory().getPath() +"/LWCameraDemo"+ "/12333.jpg";
    }

    private String getFileDir(Context context) {
        boolean canCreateOutside = Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !isExternalStorageRemovable();

        if (canCreateOutside) {
            File filesExternalDir = context.getExternalFilesDir(null);
            if (filesExternalDir != null) {
                return filesExternalDir.getPath();
            }
        }

        // Application must have this dir
        return context.getFilesDir().getPath();
    }

    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    public static boolean isExternalStorageRemovable() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            return Environment.isExternalStorageRemovable();
        }
        return true;
    }

    public void saveBitmap(Bitmap bitmap, File f) {

        if (f.exists()) {
            f.delete();
        }
        try {
            FileOutputStream out = new FileOutputStream(f);
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.flush();
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            setResult(0);
            finish();
        }
    }


    /**
     * 调整预览旋转角度
     *
     * @param activity
     * @return
     */
    public static int getPreviewDegree(Activity activity) {
        // 获得手机的方向
        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        int degree = 0;
        // 根据手机的方向计算相机预览画面应该选择的角度
        switch (rotation) {
            case Surface.ROTATION_0:
                degree = 90;
                break;
            case Surface.ROTATION_90:
                degree = 0;
                break;
            case Surface.ROTATION_180:
                degree = 270;
                break;
            case Surface.ROTATION_270:
                degree = 180;
                break;
        }
        return degree;
    }

}
