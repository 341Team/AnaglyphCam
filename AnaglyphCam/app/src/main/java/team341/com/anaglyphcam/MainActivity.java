package team341.com.anaglyphcam;



        import java.io.File;
        import java.io.FileOutputStream;
        import java.io.IOException;

        import android.app.Activity;
        import android.app.Dialog;
        import android.app.ProgressDialog;
        import android.content.Intent;
        import android.content.pm.ActivityInfo;
        import android.graphics.Bitmap;
        import android.graphics.BitmapFactory;
        import android.graphics.BitmapRegionDecoder;
        import android.graphics.Canvas;
        import android.graphics.Color;
        import android.graphics.ColorFilter;
        import android.graphics.Matrix;
        import android.graphics.Point;
        import android.graphics.RectF;
        import android.graphics.drawable.Drawable;
        import android.hardware.Camera;
        import android.hardware.Camera.CameraInfo;
        import android.hardware.Camera.Size;
        import android.hardware.Sensor;
        import android.hardware.SensorEvent;
        import android.hardware.SensorEventListener;
        import android.hardware.SensorManager;
        import android.os.AsyncTask;
        import android.os.Bundle;
        import android.os.Environment;
        import android.os.Handler;
        import android.support.annotation.DrawableRes;
        import android.util.Log;
        import android.view.Display;
        import android.view.Surface;
        import android.view.SurfaceHolder;
        import android.view.SurfaceView;
        import android.view.View;
        import android.view.Window;
        import android.view.WindowManager;
        import android.view.animation.Animation;
        import android.view.animation.AnimationUtils;
        import android.widget.ImageView;
        import android.widget.TextView;


public class MainActivity extends Activity {

    SurfaceView sv;
    SurfaceHolder holder;
    HolderCallback holderCallback;
    Camera camera;
    File left;
    int n = 0;
    File right;
    File pictures;
    File result_f;
    ImageView resul;
    TextView instruction;
    Bitmap result;
    final int CAMERA_ID = 0;
    final boolean FULL_SCREEN = true;
    Bitmap left_bi, right_bi;
    int k = 0;
    boolean check = false;
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Log.i("Log1", "created!!!");
        super.onCreate(savedInstanceState);
        pictures = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES+"/LeftRight");
        final Intent intent = new Intent(MainActivity.this, FullscreenActivity.class);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        n = 0;
        startActivity(intent);
        pictures.mkdirs();
        result_f = new File(pictures, "result.jpg");
        left = new File(pictures, "left.jpg");
        right = new File(pictures, "right.jpg");
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        sv = (SurfaceView) findViewById(R.id.surfaceView);
        holder = sv.getHolder();
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        holderCallback = new HolderCallback();
        holder.addCallback(holderCallback);
        int cute =1;
        resul = (ImageView) findViewById(R.id.resul);
        instruction = (TextView) findViewById(R.id.instruction);


    }

    @Override
    protected void onResume() {
        super.onResume();
        camera = Camera.open(CAMERA_ID);
        setPreviewSize(false);
        camera.stopPreview();
        try {
            camera.setPreviewDisplay(holder);
            camera.startPreview();
            setCameraDisplayOrientation(CAMERA_ID);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (camera != null) {
            camera.setPreviewCallback(null);
            camera.stopPreview();
            camera.release();
            camera = null;
        }

    }

    public void TakePhoto(View view) {

        Animation animation = AnimationUtils.loadAnimation(this, R.anim.animation);
        view.startAnimation(animation);

        Camera.Parameters params = camera.getParameters();
        params.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
        camera.setParameters(params);
        if (k%2==0) {
            camera.autoFocus(new Camera.AutoFocusCallback() {
                @Override
                public void onAutoFocus(boolean success, Camera camera) {
                    if (success) {
                        camera.takePicture(null, null, new Camera.PictureCallback() {
                            @Override
                            public void onPictureTaken(byte[] data, Camera camera) {
                                FileOutputStream fos;
                                Log.i("file", "begin file creating");
                                try {
                                        left.createNewFile();
                                        fos = new FileOutputStream(left);
                                        fos.write(data);
                                        check = true;
                                    Log.i("file", "File recorded");
                                    k++;
                                    Log.i("photo", Integer.toString(k) + " Done");
                                    fos.close();
                                    check = true;
                                    instruction.setText("Slightly move your device left or right");
                                    resul.setImageBitmap(BitmapFactory.decodeFile(left.getAbsolutePath()));
                                    //resul.setRotation(resul.getRotation()+90);

                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                                camera.stopPreview();
                                try {
                                    camera.setPreviewDisplay(holder);
                                    camera.startPreview();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            }
                        });
                    }
                }
            });
        }
        else
        {
            resul.setImageBitmap(null);
            camera.takePicture(null, null, new Camera.PictureCallback() {
                @Override
                public void onPictureTaken(byte[] data, Camera camera) {
                    FileOutputStream fos;
                    Log.i("file", "begin file creating");
                    try {
                        right.createNewFile();
                        fos = new FileOutputStream(right);
                        fos.write(data);
                        check = true;
                        dialog = ProgressDialog.show(MainActivity.this, "Creating...", "Please, wait.");
                        new makeResult().execute();
                        k = 0;
                        int cute = 1;
                        while (!result_f.exists()) cute = 1;
                        Log.i("photo", Integer.toString(k) + " Done");
                        fos.close();
                        instruction.setText("");
                        check = true;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    camera.stopPreview();
                    try {
                        camera.setPreviewDisplay(holder);
                        camera.startPreview();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            });
            k++;
        }

    }
    class HolderCallback implements SurfaceHolder.Callback {

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            try {
                camera.setPreviewDisplay(holder);

                camera.startPreview();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width,
                                   int height) {
            setCameraDisplayOrientation(CAMERA_ID);
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            if (camera != null) {

            }
        }

    }

    void setPreviewSize(boolean fullScreen) {

        // получаем размеры экрана
        Display display = getWindowManager().getDefaultDisplay();
        boolean widthIsMax = display.getWidth() > display.getHeight();

        // определяем размеры превью камеры
        Size size = camera.getParameters().getPreviewSize();

        RectF rectDisplay = new RectF();
        RectF rectPreview = new RectF();

        // RectF экрана, соотвествует размерам экрана
        rectDisplay.set(0, 0, display.getWidth(), display.getHeight());

        // RectF первью
        if (widthIsMax) {
            // превью в горизонтальной ориентации
            rectPreview.set(0, 0, size.width, size.height);
        } else {
            // превью в вертикальной ориентации
            rectPreview.set(0, 0, size.height, size.width);
        }

        Matrix matrix = new Matrix();
        // подготовка матрицы преобразования
        if (!fullScreen) {
            // если превью будет "втиснут" в экран (второй вариант из урока)
            matrix.setRectToRect(rectPreview, rectDisplay,
                    Matrix.ScaleToFit.START);
        } else {
            // если экран будет "втиснут" в превью (третий вариант из урока)
            matrix.setRectToRect(rectDisplay, rectPreview,
                    Matrix.ScaleToFit.START);
            matrix.invert(matrix);
        }
        // преобразование
        matrix.mapRect(rectPreview);

        // установка размеров surface из получившегося преобразования
        sv.getLayoutParams().height = (int) (rectPreview.bottom);
        sv.getLayoutParams().width = (int) (rectPreview.right);
    }

    void setCameraDisplayOrientation(int cameraId) {
        // определяем насколько повернут экран от нормального положения
        int rotation = getWindowManager().getDefaultDisplay().getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }

        int result = 0;

        // получаем инфо по камере cameraId
        CameraInfo info = new CameraInfo();
        Camera.getCameraInfo(cameraId, info);

        // задняя камера
        if (info.facing == CameraInfo.CAMERA_FACING_BACK) {
            result = ((360 - degrees) + info.orientation);
        } else
            // передняя камера
            if (info.facing == CameraInfo.CAMERA_FACING_FRONT) {
                result = ((360 - degrees) - info.orientation);
                result += 360;
            }
        result = result % 360;
        camera.setDisplayOrientation(result);
    }
    public class makeResult extends AsyncTask<String, Void, String> //непосредственно сам парсер
    {
        @Override
        protected String doInBackground(String... arg)
        {
            Log.i("Making file", "begin");
            Bitmap left_bi = BitmapFactory.decodeFile(left.getPath());
            Bitmap right_bi = BitmapFactory.decodeFile(right.getPath());
            Log.i("Making file", "bitmaps created");

            int height = left_bi.getHeight();
            int width = left_bi.getWidth();
            FileOutputStream fos;
            result = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

            for (int i = 0; i< height-1; i++)
                for (int j = 0; j<width-1; j++)
                {
                   int r = Color.red(left_bi.getPixel(j,i));
                   int g = Color.green(right_bi.getPixel(j, i));
                   int b = Color.blue(right_bi.getPixel(j, i));
                   result.setPixel(j,i,Color.rgb(r,g,b));
                }
            Log.i("Making file", "result created");
            try
            {
                result_f.createNewFile();
                fos = new FileOutputStream(result_f);
                result.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                Log.i("Making file", "result recorded");
                if (fos != null) {
                    fos.close();
                }
                Intent intent = new Intent(MainActivity.this, ShowResult.class);
                intent.putExtra("result",result_f.getAbsolutePath());
                startActivity(intent);
                dialog.dismiss();

            } catch (IOException e)
            {

            }


            return null;
        }

    }

}

