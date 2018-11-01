package own.iconload;


import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;





    public class Main extends AppCompatActivity
            implements GLSurfaceView.Renderer,
            View.OnTouchListener,
            ScaleGestureDetector.OnScaleGestureListener {

    String TAG = "IconLoad";
    private GLSurfaceView view;

    public float aspectratio = 1.0f;
    public float centerx = 0.0f;
    public float centery = 0.0f;
    public float zoom = 0.4f;
    float ratio = 0.0f;
    private ScaleGestureDetector detector;


    public boolean UNLOADTEXTURES = false;
    public float display_density;
    public IconLoad test = new IconLoad("test.png", 5);
    public IconLoad star = new IconLoad("star_cyan.png", 10);
    public GL10 lastKnownGL;
    boolean LocationIconEnabled = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        detector = new ScaleGestureDetector(this, this);


        view = findViewById(R.id.surface);
        view.setOnTouchListener(this);
        view.setPreserveEGLContextOnPause(true);
        view.setEGLContextClientVersion(1);
        view.setRenderer(this);
        view.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);

    }


    @Override
    protected void onResume() {
        super.onResume();
        view.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        view.onPause();
    }

        @Override
        public boolean onScale(ScaleGestureDetector scaleGestureDetector) {
            if (scaleGestureDetector.getScaleFactor() != 0) {
                zoom *= scaleGestureDetector.getScaleFactor();
                view.requestRender();
            }
            return true;
        }

        @Override
        public boolean onScaleBegin(ScaleGestureDetector scaleGestureDetector) {
            return true;
        }

        @Override
        public void onScaleEnd(ScaleGestureDetector scaleGestureDetector) {

        }

        // region Listener
        private float previousX, previousY;
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            detector.onTouchEvent(motionEvent);
            if (motionEvent.getPointerCount() == 1) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        previousX = motionEvent.getX();
                        previousY = motionEvent.getY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        previousX = motionEvent.getX();
                        previousY = motionEvent.getY();

                        Log.i(TAG, "x: "+previousX+" y: "+previousY);

                        //Matrix.translateM(mvpMatrix, 0, previousX, previousY, 0f);
                        //Matrix.translateM(rotationMatrix, 0, previousX, previousY, 0);
          /*
          if (previousX != motionEvent.getX()) {
            //Matrix.rotateM(rotationMatrix, 0, motionEvent.getX() - previousX, 0, 1, 0);
            Matrix.translateM(rotationMatrix, 0, previousX, previousY, 0);

          }
          if (previousY != motionEvent.getY()) {
            //Matrix.rotateM(rotationMatrix, 0, motionEvent.getY() - previousY, 1, 0, 0);
            Matrix.translateM(rotationMatrix, 0, previousX, previousY, 0);
          }
          */

                        //this.view.requestRender();
                        //previousX = motionEvent.getX();
                        //previousY = motionEvent.getY();
                        break;

                }
            }

            return true;
        }



    public void onSurfaceCreated(GL10 gl, EGLConfig eGLConfig) {
        lastKnownGL = gl;
        int[] iArr = new int[1];
        gl.glGetIntegerv(GL10.GL_MAX_TEXTURE_SIZE, iArr, 0);

        Log.d(TAG, "onSurfaceCreated");
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("OpenGL Vendor:     ");
        stringBuilder.append(gl.glGetString(GL10.GL_VENDOR));
        Log.v("GRAPHICS", stringBuilder.toString());
        stringBuilder = new StringBuilder();
        stringBuilder.append("OpenGL Renderer:   ");
        stringBuilder.append(gl.glGetString(GL10.GL_RENDERER));
        Log.v("GRAPHICS", stringBuilder.toString());
        stringBuilder = new StringBuilder();
        stringBuilder.append("OpenGL Version:    ");
        stringBuilder.append(gl.glGetString(GL10.GL_VERSION));
        Log.v("GRAPHICS", stringBuilder.toString());
        stringBuilder = new StringBuilder();
        stringBuilder.append("OpenGL Max texture size = ");
        stringBuilder.append(iArr[0]);
        Log.i("GRAPHICS", stringBuilder.toString());

        gl.glDisable(GL10.GL_DITHER);
        gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_FASTEST);
        gl.glHint(GL10.GL_LINE_SMOOTH_HINT, GL10.GL_FASTEST);
        gl.glShadeModel(GL10.GL_FLAT);
        gl.glDisable(GL10.GL_DEPTH_TEST);
        gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
        int screenheight = this.getResources().getDisplayMetrics().heightPixels;
        int screenwidth = this.getResources().getDisplayMetrics().widthPixels;
        int density = this.getResources().getDisplayMetrics().densityDpi;
        display_density = density;

        //this.radarscreenbase = 1.0f - ((20.0f * this.display_density) / ((float) i));
        //this.statusbarscreenbase = 1.0f - ((colorbarheight * this.display_density) / ((float) (i / 2)));
        //unloadTextures(gl);
        Log.d(TAG, "onSurfaceCreated complete");
    }

    public void onDrawFrame(GL10 gl) {
        gl.glShadeModel(GL10.GL_FLAT);
        gl.glEnable(GL10.GL_BLEND);
        gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
        gl.glColor4x(0, 0, 0, 0);
        try {

            if (UNLOADTEXTURES) {
                UNLOADTEXTURES = false;
                unloadTextures(gl);
                return;
            }
            gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
            gl.glLoadIdentity();
            gl.glTranslatef(0.0f, 0.0f, -1000.0f);
            gl.glScalef(this.zoom, this.zoom, this.zoom);
            gl.glTranslatef(this.centerx, this.centery, 0.0f);
            if (ratio > 1.0f) {
                gl.glScalef(this.aspectratio, this.aspectratio, 1.0f);
            }
            if (this.lastKnownGL != gl) {
                Log.d(TAG, "First time drawing with a known valid GL");
                this.lastKnownGL = gl;
            }




            gl.glClear(16640);



             //conus.draw(gl);
            enable2d(gl);

            test.drawIcon(gl, "test.png", 100, 100);
            star.drawIcon(gl, "star_cyan.png", 200, 300);
            //testicon.drawIcon(gl);

            //draw usa
            //usa.draw(gl);

            /*

                if (this.CONUSVIEWENABLED) {
                    if (this.displayHold && !this.hide_states_when_panning) {
                        this.usa.draw(gl10);
                    } else if (!this.displayHold) {
                        this.usa.draw(gl10);
                    }
                }
            */





            disable2d(gl);




        } catch (Exception e) {
            StringBuilder stringBuilder3 = new StringBuilder();
            stringBuilder3.append("Unhandled Exception in Renderer ");
            stringBuilder3.append(e.toString());
            Log.e(TAG, stringBuilder3.toString());
            e.printStackTrace();
        }

    }







    public void onSurfaceChanged(GL10 gl10, int width, int height) {
        StringBuilder stringBuilder;
        lastKnownGL = gl10;

        stringBuilder = new StringBuilder();
        stringBuilder.append("onSurfaceChanged ");
        stringBuilder.append(width);
        stringBuilder.append("/");
        stringBuilder.append(height);
        Log.v(TAG, stringBuilder.toString());

        System.gc();
        gl10.glViewport(0, 0, width, height);

        if (height != 0) {
            aspectratio = ((float)width) / ((float)height);
        }
        if (aspectratio < 1.0f) {
            aspectratio = 1.0f;
        }


        stringBuilder = new StringBuilder();
        stringBuilder.append("Aspect Ratio ");
        stringBuilder.append(aspectratio);
        stringBuilder.append(" Width ");
        stringBuilder.append(width);
        stringBuilder.append(" Height ");
        stringBuilder.append(height);
        Log.d(TAG, stringBuilder.toString());

        float ratio = ((float) width) / ((float) height);
        gl10.glMatrixMode(GL10.GL_PROJECTION);
        gl10.glLoadIdentity();
        GLU.gluPerspective(gl10, 45.0f, ratio, 1.0f, 1000.0f);

        Log.d(TAG, "Ratio set to "+ratio);
        Log.i(TAG, "Surface Changed Rerender");

    }



    public void setTranslate(float f, float f2) {
        float f3 = (aspectratio * 0.724f) / zoom;
        centerx += f * f3;
        centery -= f2 * f3;
    }


    private void enable2d(GL10 gl) {
        gl.glMatrixMode(GL10.GL_PROJECTION);
        gl.glPushMatrix();
        gl.glLoadIdentity();
        gl.glMatrixMode(GL10.GL_MODELVIEW);
        gl.glPushMatrix();
        gl.glLoadIdentity();
    }

    private void disable2d(GL10 gl) {
        gl.glMatrixMode(GL10.GL_PROJECTION);
        gl.glPopMatrix();
        gl.glMatrixMode(GL10.GL_MODELVIEW);
        gl.glPopMatrix();
    }



    private void unloadTextures(GL10 gl) {

        //conus.unloadTextures(gl);
        test.unloadTextures(gl);
        star.unloadTextures(gl);

    }




}