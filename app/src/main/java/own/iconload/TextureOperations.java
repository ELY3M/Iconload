package own.iconload;

import android.graphics.Bitmap;
import android.opengl.GLES10;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.util.Log;
import javax.microedition.khronos.opengles.GL10;

public class TextureOperations {
    private static String TAG = "TextureOperations";
    private static boolean verbose = false;

    public static boolean invalidateTexture(GL10 gl, int[] textures) {
        if (gl != null) {
            int len = textures.length;
            gl.glDeleteTextures(len, textures, 0);
            for (int i = 0; i < len; i++) {
                if (textures[i] != 0) {
                    if (verbose) {
                        Log.d(TAG, "GLDELTEX [" + textures[i] + "] ");
                    }
                    textures[i] = 0;
                }
            }
        }
        return false;
    }

    public static boolean invalidateTexture(GL10 gl, int[][] textures) {
        if (gl != null) {
            int len = textures[0].length;
            for (int k = 0; k < textures.length; k++) {
                gl.glDeleteTextures(len, textures[k], 0);
                for (int i = 0; i < len; i++) {
                    if (textures[k][i] != 0) {
                        if (verbose) {
                            Log.d(TAG, "GLDELTEX [" + textures[k][i] + "] ");
                        }
                        textures[k][i] = 0;
                    }
                }
            }
        }
        return false;
    }

    public static int loadTextureFromBitmapFast(GL10 gl, Bitmap bitmap) {
        int[] tex = new int[1];
        gl.glGenTextures(1, tex, 0);
        gl.glPixelStorei(GL10.GL_UNPACK_ALIGNMENT, 1);
        gl.glBindTexture(GL10.GL_TEXTURE_2D, tex[0]);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_NEAREST);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE);
        gl.glTexEnvf(GL10.GL_TEXTURE_ENV, GLES10.GL_TEXTURE_ENV_MODE, GL10.GL_REPLACE);
        GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0);
        if (verbose) {
            Log.d(TAG, "GLGENTEX [" + tex[0] + "] " + " Width=" + bitmap.getWidth() + " Height=" + bitmap.getHeight());
        }
        return tex[0];
    }

    public static int loadTextureFromBitmapFastSmoothed(GL10 gl, Bitmap bitmap) {
        int[] tex = new int[1];
        gl.glGenTextures(1, tex, 0);
        gl.glPixelStorei(GL10.GL_UNPACK_ALIGNMENT, 1);
        gl.glBindTexture(GL10.GL_TEXTURE_2D, tex[0]);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE);
        gl.glTexEnvf(GL10.GL_TEXTURE_ENV, GL10.GL_TEXTURE_ENV_MODE, GL10.GL_REPLACE);
        GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0);
        if (verbose) {
            Log.d(TAG, "GLGENTEX [" + tex[0] + "] " + " Width=" + bitmap.getWidth() + " Height=" + bitmap.getHeight());
        }
        return tex[0];
    }

    //public TextureOperations(String logID) {
    //    LOG_TAG = logID;
    //}

    public static Bitmap restoreGifTransparency(Bitmap bmp) {
        System.gc();
        int length = bmp.getWidth() * bmp.getHeight();
        int[] pixels = new int[length];
        bmp.getPixels(pixels, 0, bmp.getWidth(), 0, 0, bmp.getWidth(), bmp.getHeight());
        int i = 0;
        while (i < length) {
            if (pixels[i] == -1) {
                pixels[i] = 0;
            }
            if (pixels[i] != 0 && colorCheck(pixels[i])) {
                pixels[i] = 1879048192 + (pixels[i] & ViewCompat.MEASURED_SIZE_MASK);
            }
            i++;
        }
        bmp.setPixels(pixels, 0, bmp.getWidth(), 0, 0, bmp.getWidth(), bmp.getHeight());
        System.gc();
        return bmp;
    }

    static boolean colorCheck(int value) {
        if (withinRange((16711680 & value) / 65536, 232, 10) && withinRange((MotionEventCompat.ACTION_POINTER_INDEX_MASK & value) / 256, 232, 10) && withinRange(value & 255, 232, 10)) {
            return true;
        }
        return false;
    }

    static boolean withinRange(int value, int checkvalue, int variance) {
        if (Math.abs(value - checkvalue) <= variance) {
            return true;
        }
        return false;
    }
}
