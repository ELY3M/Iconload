package own.iconload


import android.opengl.GLSurfaceView
import android.opengl.GLU
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import java.util.*

import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10


class Main : AppCompatActivity(), GLSurfaceView.Renderer, View.OnTouchListener,
    ScaleGestureDetector.OnScaleGestureListener {

    internal var TAG = "IconLoad"
    private var view: GLSurfaceView? = null

    var aspectratio = 1.0f
    var centerx = 0.0f
    var centery = 0.0f
    var zoom = 0.4f
    internal var ratio = 0.0f
    private var detector: ScaleGestureDetector? = null


    var UNLOADTEXTURES = false
    var display_density: Float = 0.toFloat()
    var test = IconLoad("test.png", 5f)
    var star = IconLoad("star_cyan.png", 10f)
    //var lastKnownGL: GL10 = GL10
    internal var LocationIconEnabled = false

    private val STARS_COUNT = 75
    private val RANDOM_SEED: Long = 1234567890
    protected val mRandom = Random(RANDOM_SEED)

    // region Listener
    private var previousX: Float = 0.toFloat()
    private var previousY: Float = 0.toFloat()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main)

        detector = ScaleGestureDetector(this, this)


        view = findViewById(R.id.surface)
        view!!.setOnTouchListener(this)
        view!!.preserveEGLContextOnPause = true
        view!!.setEGLContextClientVersion(1)
        view!!.setRenderer(this)
        view!!.renderMode = GLSurfaceView.RENDERMODE_WHEN_DIRTY

    }


    override fun onResume() {
        super.onResume()
        view!!.onResume()
    }

    override fun onPause() {
        super.onPause()
        view!!.onPause()
    }

    override fun onScale(scaleGestureDetector: ScaleGestureDetector): Boolean {
        if (scaleGestureDetector.scaleFactor != 0f) {
            zoom *= scaleGestureDetector.scaleFactor
            view!!.requestRender()
        }
        return true
    }

    override fun onScaleBegin(scaleGestureDetector: ScaleGestureDetector): Boolean {
        return true
    }

    override fun onScaleEnd(scaleGestureDetector: ScaleGestureDetector) {

    }

    override fun onTouch(view: View, motionEvent: MotionEvent): Boolean {
        detector!!.onTouchEvent(motionEvent)
        if (motionEvent.pointerCount == 1) {
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    previousX = motionEvent.x
                    previousY = motionEvent.y
                }
                MotionEvent.ACTION_MOVE -> {
                    previousX = motionEvent.x
                    previousY = motionEvent.y

                    Log.i(TAG, "x: $previousX y: $previousY")
                }
            }//Matrix.translateM(mvpMatrix, 0, previousX, previousY, 0f);
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
          *///this.view.requestRender();
            //previousX = motionEvent.getX();
            //previousY = motionEvent.getY();
        }

        return true
    }


    override fun onSurfaceCreated(gl: GL10, eGLConfig: EGLConfig) {
        val iArr = IntArray(1)
        gl.glGetIntegerv(GL10.GL_MAX_TEXTURE_SIZE, iArr, 0)

        Log.d(TAG, "onSurfaceCreated")
        var stringBuilder = StringBuilder()
        stringBuilder.append("OpenGL Vendor:     ")
        stringBuilder.append(gl.glGetString(GL10.GL_VENDOR))
        Log.v("GRAPHICS", stringBuilder.toString())
        stringBuilder = StringBuilder()
        stringBuilder.append("OpenGL Renderer:   ")
        stringBuilder.append(gl.glGetString(GL10.GL_RENDERER))
        Log.v("GRAPHICS", stringBuilder.toString())
        stringBuilder = StringBuilder()
        stringBuilder.append("OpenGL Version:    ")
        stringBuilder.append(gl.glGetString(GL10.GL_VERSION))
        Log.v("GRAPHICS", stringBuilder.toString())
        stringBuilder = StringBuilder()
        stringBuilder.append("OpenGL Max texture size = ")
        stringBuilder.append(iArr[0])
        Log.i("GRAPHICS", stringBuilder.toString())

        gl.glDisable(GL10.GL_DITHER)
        gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_FASTEST)
        gl.glHint(GL10.GL_LINE_SMOOTH_HINT, GL10.GL_FASTEST)
        gl.glShadeModel(GL10.GL_FLAT)
        gl.glDisable(GL10.GL_DEPTH_TEST)
        gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA)
        val screenheight = this.resources.displayMetrics.heightPixels
        val screenwidth = this.resources.displayMetrics.widthPixels
        val density = this.resources.displayMetrics.densityDpi
        display_density = density.toFloat()

        //this.radarscreenbase = 1.0f - ((20.0f * this.display_density) / ((float) i));
        //this.statusbarscreenbase = 1.0f - ((colorbarheight * this.display_density) / ((float) (i / 2)));
        //unloadTextures(gl);
        Log.d(TAG, "onSurfaceCreated complete")
    }

    override fun onDrawFrame(gl: GL10) {
        gl.glShadeModel(GL10.GL_FLAT)
        gl.glEnable(GL10.GL_BLEND)
        gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA)
        gl.glColor4x(0, 0, 0, 0)
        try {

            if (UNLOADTEXTURES) {
                UNLOADTEXTURES = false
                unloadTextures(gl)
                return
            }
            gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f)
            gl.glLoadIdentity()
            gl.glTranslatef(0.0f, 0.0f, -1000.0f)
            gl.glScalef(this.zoom, this.zoom, this.zoom)
            gl.glTranslatef(this.centerx, this.centery, 0.0f)
            if (ratio > 1.0f) {
                gl.glScalef(this.aspectratio, this.aspectratio, 1.0f)
            }



            gl.glClear(16640)


            //conus.draw(gl);
            enable2d(gl)

            test.drawIcon(gl, "test.png", 0, 0)



            //spray stars all over the screen//

            val max: Int = 3000
            var i: Int = 0
            var rand1: Random
            var rand2: Random


            for (i in 0..STARS_COUNT) {
                rand1 = Random(System.nanoTime());
                rand2 = Random(System.nanoTime());

                star.drawIcon(gl, "star_cyan.png", rand1.nextInt(max), rand2.nextInt(max))
            }





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





            disable2d(gl)


        } catch (e: Exception) {
            val stringBuilder3 = StringBuilder()
            stringBuilder3.append("Unhandled Exception in Renderer ")
            stringBuilder3.append(e.toString())
            Log.e(TAG, stringBuilder3.toString())
            e.printStackTrace()
        }

    }


    override fun onSurfaceChanged(gl10: GL10, width: Int, height: Int) {
        var stringBuilder: StringBuilder
        stringBuilder = StringBuilder()
        stringBuilder.append("onSurfaceChanged ")
        stringBuilder.append(width)
        stringBuilder.append("/")
        stringBuilder.append(height)
        Log.v(TAG, stringBuilder.toString())

        System.gc()
        gl10.glViewport(0, 0, width, height)

        if (height != 0) {
            aspectratio = width.toFloat() / height.toFloat()
        }
        if (aspectratio < 1.0f) {
            aspectratio = 1.0f
        }


        stringBuilder = StringBuilder()
        stringBuilder.append("Aspect Ratio ")
        stringBuilder.append(aspectratio)
        stringBuilder.append(" Width ")
        stringBuilder.append(width)
        stringBuilder.append(" Height ")
        stringBuilder.append(height)
        Log.d(TAG, stringBuilder.toString())

        val ratio = width.toFloat() / height.toFloat()
        gl10.glMatrixMode(GL10.GL_PROJECTION)
        gl10.glLoadIdentity()
        GLU.gluPerspective(gl10, 45.0f, ratio, 1.0f, 1000.0f)

        Log.d(TAG, "Ratio set to $ratio")
        Log.i(TAG, "Surface Changed Rerender")

    }


    fun setTranslate(f: Float, f2: Float) {
        val f3 = aspectratio * 0.724f / zoom
        centerx += f * f3
        centery -= f2 * f3
    }


    private fun enable2d(gl: GL10) {
        gl.glMatrixMode(GL10.GL_PROJECTION)
        gl.glPushMatrix()
        gl.glLoadIdentity()
        gl.glMatrixMode(GL10.GL_MODELVIEW)
        gl.glPushMatrix()
        gl.glLoadIdentity()
    }

    private fun disable2d(gl: GL10) {
        gl.glMatrixMode(GL10.GL_PROJECTION)
        gl.glPopMatrix()
        gl.glMatrixMode(GL10.GL_MODELVIEW)
        gl.glPopMatrix()
    }


    private fun unloadTextures(gl: GL10) {

        //conus.unloadTextures(gl);
        test.unloadTextures(gl)
        star.unloadTextures(gl)

    }


}